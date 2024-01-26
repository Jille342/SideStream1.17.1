//Deobfuscated By Mouath#2221 | ????#2221 D:\Game\private 2\False"!

package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.PlayerHelper;
import client.utils.RotationUtils;
import client.utils.ServerHelper;
import net.minecraft.entity.*;

import java.util.*;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class HitBoxes extends Module
{
    private Entity pointedEntity;
    private NumberSetting expand;
    public static float hitBoxMultiplier;
    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    BooleanSetting ignoreTeamsSetting;
    NumberSetting fov;
    NumberSetting rangeSetting;

    ModeSetting sortmode;
    static NumberSetting size;
    private final List<LivingEntity> validated = new ArrayList<>();


    public HitBoxes() {
        super("HitBoxes", 0, Category.COMBAT);
    }

    public void init(){
        super.init();
        sortmode = new ModeSetting("SortMode", "Angle", new String[]{"Angle","Distance"});
        size = new NumberSetting("HitBox", 0.08 , 0, 1,0.01F);
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        this.rangeSetting = new NumberSetting("Range", 5.0, 1, 8.0, 0.1);
        this.fov = new NumberSetting("FOV", 90.0D, 15.0D, 360.0D, 1.0D);
        addSetting(rangeSetting,size, sortmode, targetAnimalsSetting, targetMonstersSetting, ignoreTeamsSetting, fov);

    }



    @Override
    public void onDisable() {
    }
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate)  {
            setTag(sortmode.getMode());
            LivingEntity entity = findTarget();
            if(entity != null)  {
                float width = entity.getWidth();
                float height = entity.getHeight();
                float expandValue = (float) size.getValue()-0.30F;
                entity.setBoundingBox(new Box(entity.getX() - width - expandValue, entity.getY() , entity.getZ() + width + expandValue, entity.getX() + width + expandValue, entity.getY() + height , entity.getZ() - width - expandValue));


            }
        }

    }


    private LivingEntity findTarget() {
        validated.clear();
        for (Entity entity : mc.world.getEntities()) {

            if (entity instanceof LivingEntity && entity != mc.player) {
                if (((LivingEntity) entity).isDead() || !entity.isAlive() || entity.age < 10) {
                    continue;
                }

                if (!PlayerHelper.fov(entity, fov.value))
                    continue;
                double focusRange = mc.player.canSee(entity) ? rangeSetting.value : 3.5;
                if (mc.player.distanceTo(entity) > focusRange) continue;
                if (entity instanceof PlayerEntity) {

                    if (ignoreTeamsSetting.enable && ServerHelper.isTeammate((PlayerEntity) entity)) {
                        continue;
                    }
                    if(  ((PlayerEntity) entity).getHealth() ==0)
                        continue;
                    validated.add((LivingEntity) entity);
                } else if (entity instanceof AnimalEntity && targetAnimalsSetting.enable) {
                    if( ((AnimalEntity) entity).getHealth() ==0)
                        continue;
                    validated.add((LivingEntity) entity);
                } else if (entity instanceof MobEntity && targetMonstersSetting.enable) {
                    if(((MobEntity) entity).getHealth() == 0)
                        continue;
                    validated.add((LivingEntity) entity);
                }
            }
        }

        if (validated.isEmpty()) return null;
        switch (sortmode.getMode()) {
            case "Angle":
                validated.sort(Comparator.comparingDouble(RotationUtils::calculateYawChangeToDst));
                break;
            case "Distance":
                validated.sort((o1, o2) -> (int) (o1.distanceTo(mc.player) - o2.distanceTo(mc.player)));
                break;
        }
        this.validated.sort(Comparator.comparingInt(o -> o.hurtTime));

        return validated.get(0);
    }


}
