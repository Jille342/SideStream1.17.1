package client.features.module.render;

import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.Colors;
import client.utils.RenderingUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

public class NameTagsTest extends Module {

    public static NumberSetting scale;
    public static BooleanSetting health;
    public static BooleanSetting showItems;

    public NameTagsTest() {
        super("NameTagsTest", 0, Category.RENDER);
    }

    public void init() {
        super.init();
        scale = new NumberSetting("Scale", 1, 0, 10, 1);
        health = new BooleanSetting("Health", false);
        showItems = new BooleanSetting("Show Items", true);
        addSetting(scale, health, showItems);
    }
    public static void
    renderTags(Entity e, Text t, MatrixStack m, VertexConsumerProvider v, int l)
    {
        if(t.getString().contains("Health")) return;
        boolean bl = true;
        float f = e.getHeight() + 0.5F;
        int y = "deadmau5".equals(t.getString()) ? -10 : 0;

        m.push();
        m.translate(0.D, f, 0.D);
        m.multiply(mc.getEntityRenderDispatcher().getRotation());

        float scale = (float) NameTagsTest.scale.getValue();
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        double distance = Math.sqrt(camera.getPos().squaredDistanceTo(e.getX(), e.getY(), e.getZ()));
        if(distance > 15)
            scale *= (float) (distance / 15);

        m.scale(-0.025F * scale, -0.025F * scale, 0.025F);
        scale += 2;

        Matrix4f m4f = m.peek().getModel();
        LivingEntity ent = (LivingEntity) e;

        String str = t.getString();
        str = str + " [" + (int) (ent.getHealth() + ent.getAbsorptionAmount()) + "/" + (int) ent.getMaxHealth() + "]";
        MinecraftClient mc = MinecraftClient.getInstance();

        float x1 = (-mc.textRenderer.getWidth(str) / 2);
        float x2 = x1 + mc.textRenderer.getWidth(str) * 1.5f;

        RenderingUtils.fill( (int) x1 - 2, y - 3 - (int) scale, (int) (x1 + x2) + 2, y + 11 - (int) scale, Colors.getColor(0,0,0,50));
        if(NameTagsTest.health.isEnable())
            mc.textRenderer.draw(str, x1, y - scale, -1, false, m4f, v, true, 0, l);
        else mc.textRenderer.draw(t.asOrderedText(), x1, y - scale, -1, false, m4f, v, true, 0, l);
        if (NameTagsTest.showItems.enable) {

            if (ent instanceof PlayerEntity) {
                ItemStack renderMainHand = ent.getMainHandStack();
                int xOffset = -8;
                for (ItemStack stack : ((PlayerEntity) ent).getInventory().armor) {
                    if (stack == null) continue;
                    xOffset -= 8;
                }
                xOffset -= 8;

                xOffset += 16;
                for (ItemStack stack : ((PlayerEntity) ent).getInventory().armor) {
                    if (stack == null) continue;
                    ItemStack armourStack = stack;
                    renderItemStack(armourStack, xOffset, -26);
                    xOffset += 16;
                }
                renderItemStack(renderMainHand, xOffset, -26);
            }
        }
        m.pop();
    }
    private static void renderItemStack(ItemStack stack, int x, int y) {

        MatrixStack matrices = RenderSystem.getModelViewStack();

        matrices.push();
        matrices.scale((float) scale.getValue(), (float) scale.getValue(), 1);

        mc.getItemRenderer().renderGuiItemIcon(stack, (int) (x / scale.getValue()), (int) (y / scale.getValue()));
      mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, stack, (int) (x / scale.getValue()), (int) (y / scale.getValue()), null);

        matrices.pop();

    }
}
