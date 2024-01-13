package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.PlayerHelper;
import client.utils.ServerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AimAssist extends Module {








    private final List<Entity> validated = new ArrayList<>();
    private Entity primary;
    private int breakTick;
    BooleanSetting ignoreTeamsSetting;
    BooleanSetting notHolding;
    NumberSetting aimSpeedSetting;
    NumberSetting rangeSetting;
    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    NumberSetting fov;

    public AimAssist() {
        super("Aim Assist",  0,Category.COMBAT);
    }
    @Override
    public void init() {
        super.init();
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        this.notHolding = new BooleanSetting("not Holding", false);
        this.aimSpeedSetting = new NumberSetting("AimSpeed", 0.45, 0.1, 1.0, 0.1);
        this.rangeSetting = new NumberSetting("Range", 5.0, 3.0, 8.0, 0.1);
        this.fov = new NumberSetting("FOV", 90.0D, 15.0D, 360.0D, 1.0D);


        addSetting(notHolding, ignoreTeamsSetting, aimSpeedSetting, rangeSetting,  targetAnimalsSetting, targetMonstersSetting, fov);
    }

    @Override
    public void onDisable() {
        validated.clear();
        primary = null;
        breakTick = 0;
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventRender2D) {
            primary = findTarget();
            if (e.isPost() || primary == null || !canAssist()) {
                return;
            }

            float diff = calculateYawChangeToDst(primary);
            float aimSpeed = (float) aimSpeedSetting.value;
            aimSpeed = (float) MathHelper.clamp(RandomUtils.nextFloat(aimSpeed - 0.2f, aimSpeed + 1.8f), aimSpeedSetting.minimum, aimSpeedSetting.maximum);
            aimSpeed -= (float) (aimSpeed % getSensitivity());

            if (diff < -6) {
                aimSpeed -= diff / 12f;
                mc.player.setYaw(mc.player.getYaw()- aimSpeed);
            } else if (diff > 6) {
                aimSpeed += diff / 12f;
                mc.player.setYaw(mc.player.getYaw() + aimSpeed);
            }
        }

    }

    public double getSensitivity() {
        double sensitivity = mc.options.mouseSensitivity * 0.3 + 0.2;
        return sensitivity * sensitivity * sensitivity * RandomUtils.nextFloat(2f, 3f);
    }
    private boolean canAssist() {
        if (mc.currentScreen != null) {
            return false;
        }

        if (!notHolding.enable && !mc.options.keyAttack.isPressed()) {
            return false;
        }

        if (mc.player.isUsingItem()) {
            return false;
        }

        return true;
    }

    private Entity findTarget() {
        validated.clear();

        assert mc.world != null;
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof Entity && entity != mc.player) {
                if ( !entity.isAlive() || entity.age < 10) {
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

                    validated.add(entity);
                } else if (entity instanceof AnimalEntity && targetAnimalsSetting.enable) {
                    validated.add( entity);
                } else if (entity instanceof MobEntity && targetMonstersSetting.enable) {
                    validated.add( entity);
                }
            }
        }

        if (validated.isEmpty()) return null;
        validated.sort(Comparator.comparingDouble(this::calculateYawChangeToDst));
        return validated.get(0);
    }

    public float calculateYawChangeToDst(Entity entity) {
        double diffX = entity.getX() - mc.player.getX();
        double diffZ = entity.getZ() - mc.player.getZ();
        double deg = Math.toDegrees(Math.atan(diffZ / diffX));
        if (diffZ < 0.0 && diffX < 0.0) {
            return (float) MathHelper.wrapDegrees(-(mc.player.getYaw() - (90 + deg)));
        } else if (diffZ < 0.0 && diffX > 0.0) {
            return (float) MathHelper.wrapDegrees(-(mc.player.getYaw() - (-90 + deg)));
        } else {
            return (float) MathHelper.wrapDegrees(-(mc.player.getYaw() - Math.toDegrees(-Math.atan(diffX / diffZ))));
        }
    }
}
