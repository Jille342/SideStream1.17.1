package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventEntityRender;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventRenderWorld;
import client.features.module.Module;
import client.setting.NumberSetting;
import client.utils.render.RenderUtils;
import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NameTagsTest2 extends Module {

    public static NumberSetting scale;

    public NameTagsTest2() {
        super("NameTagsTest2", 0, Category.RENDER);
    }

    @Override
    public void init() {
        super.init();
        scale  =new NumberSetting("Scale", 1, 0, 10,1);
        addSetting(scale);
    }

    @Override
    public void onEvent(Event event){
        if(event instanceof EventRenderWorld){

            for (PlayerEntity entity : mc.world.getPlayers()) {
                if (entity == mc.player || entity.hasPassenger(mc.player) || mc.player.hasPassenger(entity)) {
                    continue;
                }
                Vec3d rPos = entity.getPos().subtract(this.getInterpolationOffset(entity)).add(0, entity.getHeight() + 0.25, 0);

                double scale = Math.max(NameTagsTest2.scale.getValue() * (mc.cameraEntity.distanceTo(entity) / 20), 1);

                List<Text> lines = getPlayerLines((PlayerEntity) entity);

                drawItems(rPos.x, rPos.y + (lines.size() + 1) * 0.25 * scale, rPos.z, scale, getMainEquipment(entity));
                drawLines(rPos.x, rPos.y, rPos.z, scale, lines);

                System.out.println("draw");
            }
        }
        if(event instanceof EventEntityRender.Single.Label){
            event.setCancelled(true);
        }
    }

    private void drawLines(double x, double y, double z, double scale, List<Text> lines) {
        double offset = lines.size() * 0.25 * scale;

        for (Text t: lines) {

            RenderUtils.drawText(t, x, y + offset, z, scale, true);
            offset -= 0.25 * scale;
        }
    }

    private void drawItems(double x, double y, double z, double scale, List<ItemStack> items) {
        double lscale = scale * 0.4;

        for (int i = 0; i < items.size(); i++) {
            drawItem(x, y, z, i + 0.5 - items.size() / 2d, 0, lscale, items.get(i));
        }
    }

    private void drawItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
        if (item.isEmpty())
            return;

        RenderUtils.drawGuiItem(x, y, z, offX * scale, offY * scale, scale, item);

        double w = mc.textRenderer.getWidth("x" + item.getCount()) / 52d;
        RenderUtils.drawText(new LiteralText("x" + item.getCount()),
                x, y, z, (offX - w) * scale, (offY - 0.07) * scale, scale * 1.75, false);

        int c = 0;
        for (Map.Entry<Enchantment, Integer> m : EnchantmentHelper.get(item).entrySet()) {
            String text = I18n.translate(m.getKey().getName(2).getString());

            if (text.isEmpty())
                continue;

            text = WordUtils.capitalizeFully(text.replaceFirst("Curse of (.)", "C$1"));

            String subText = text.substring(0, Math.min(text.length(), 2)) + m.getValue();

            RenderUtils.drawText(new LiteralText(subText).styled(s -> s.withColor(TextColor.fromRgb(m.getKey().isCursed() ? 0xff5050 : 0xffb0e0))),
                    x, y, z, (offX + 0.02) * scale, (offY + 0.75 - c * 0.34) * scale, scale * 1.4, false);
            c--;
        }
    }

    public List<Text> getPlayerLines(PlayerEntity player) {
        List<Text> lines = new ArrayList<>();
        List<Text> mainText = new ArrayList<>();

        PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(player.getGameProfile().getId());


        if(player.getDisplayName().getStyle().getColor() == null){
            mainText.add(((MutableText) player.getName()));
        }else{
            mainText.add(((MutableText) player.getName()).formatted(Formatting.byName(player.getDisplayName().getStyle().getColor().getName())));
        }

        if (!mainText.isEmpty()){
            lines.add(Texts.join(mainText, new LiteralText(" ")));
        }

        return lines;
    }

    private List<ItemStack> getMainEquipment(Entity e) {
        List<ItemStack> list = Lists.newArrayList(e.getItemsEquipped());
        list.add(list.remove(1));
        return list;
    }

    public static Vec3d getInterpolationOffset(Entity e) {
        if (MinecraftClient.getInstance().isPaused()) {
            return Vec3d.ZERO;
        }

        double tickDelta = MinecraftClient.getInstance().getTickDelta();
        return new Vec3d(
                e.getX() - MathHelper.lerp(tickDelta, e.lastRenderX, e.getX()),
                e.getY() - MathHelper.lerp(tickDelta, e.lastRenderY, e.getY()),
                e.getZ() - MathHelper.lerp(tickDelta, e.lastRenderZ, e.getZ()));
    }
}
