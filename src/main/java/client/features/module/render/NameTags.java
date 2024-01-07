package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.RenderingUtils;
import client.utils.font.Fonts;
import client.utils.font.TTFFontRenderer;
import client.utils.math.BillboardPos;
import client.utils.math.MatrixUtil;
import io.netty.util.internal.MathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import client.utils.math.Vec2d;


import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.opengl.GL11.*;


public class NameTags extends Module {

    BooleanSetting health;

    NumberSetting scaleFactor;
    public static TTFFontRenderer customFontRenderer = Fonts.font;
    public static TTFFontRenderer enchantFontRenderer = Fonts.font;
    BooleanSetting renderSelf;
    BooleanSetting armor;
    BooleanSetting ench;

    public NameTags() {
        super("NameTags", 0, Category.RENDER);
    }


    public void init() {
        renderSelf = new BooleanSetting("Render Self", false);
        armor = new BooleanSetting("Show Items", true);
        ench = new BooleanSetting("show Enchant", true);
        health = new BooleanSetting("Health", true);
        scaleFactor = new NumberSetting("Factor", 1.0F,1.0, 10, 1);
        addSetting(renderSelf, armor, ench,scaleFactor, health);
        super.init();
    }
    private float spaceWidth = 0F;



    public void onEvent(Event<?> e) {
        if(e instanceof EventRender2D) {

            Vec3d camPos = mc.gameRenderer.getCamera().getPos();

            spaceWidth = customFontRenderer.getStringWidth(" ");
            ;

            for (PlayerEntity entity : mc.world.getPlayers()) {
                if (entity == mc.player && !renderSelf.isEnable()) continue;

                Vec3d iPos = lerpEntity(entity);
                BillboardPos projectedPos = MatrixUtil.getBillboardPos(iPos.add(0, entity.getHeight() + 0.3, 0));
                if (projectedPos == null) continue;
                Vec2d screenPos = projectedPos.getProjectedPos();
                if (screenPos == null) continue;


                drawNametag(entity, camPos, iPos, screenPos);

            }

        }
    }
    public static Vec3d lerpEntity(Entity entity) {
        double[] lerped = lerp(entity);
        return new Vec3d(lerped[0], lerped[1], lerped[2]);
    }
    // Middle Level
    public static double[] lerp(Entity entity) {
        if (entity.prevX == 0D && entity.prevY == 0D && entity.prevZ == 0D) {
            entity.prevX = entity.getX();
            entity.prevY = entity.getY();
            entity.prevZ = entity.getZ();
        }

        double posX = lerp(entity.lastRenderX, entity.getX(), MinecraftClient.getInstance().getTickDelta());
        double posY = lerp(entity.lastRenderY, entity.getY(), MinecraftClient.getInstance().getTickDelta());
        double posZ = lerp(entity.lastRenderZ, entity.getZ(), MinecraftClient.getInstance().getTickDelta());

        return new double[] { posX, posY, posZ };
    }
    public static float lerp(float a, float b, float partial) {
        return (a * (1f - partial)) + (b * partial);
    }

    /**
     * Applies a linear interpolation between the two values
     */
    public static double lerp(double a, double b, float partial) {
        return (a * (1.0 - partial)) + (b * partial);
    }

    private void drawNametag(PlayerEntity player, Vec3d camPos, Vec3d iPos, Vec2d screenPos)  {
        double height = 0;

        double dist = 1 - iPos.distanceTo(camPos) * (0.1D * scaleFactor.getValue());

        if (dist < 0.3D) {
            dist = 0.3D;
        }

        glPushMatrix();
        glTranslated(screenPos.x, screenPos.y, 0);
        glScaled(dist, dist, 1D);

        ColoredString[] strings = new ColoredString[5];





        if (health.isEnable()) {
            float playerHealth = player.getHealth();

            if (player.getEntityName().equalsIgnoreCase("antiflame") || player.getEntityName().equalsIgnoreCase("0851_")) {
                playerHealth += 0.69F;
            }

            String health = Float.toString(playerHealth).replace(".0", "");

            if (playerHealth < 5) {
                strings[3] = new ColoredString(health, 0xFFFF4848);
            } else if (playerHealth < 20) {
                strings[3] = new ColoredString(health, 0xFFFFCE48);
            } else {
                strings[3] = new ColoredString(health, 0xFF27CC00);
            }
        }


        String total = "";
        boolean first = true;
        for (ColoredString part : strings) {
            if (part == null) continue;
            if (!first) {
                total += " ";
            }
            first = false;
            total += part.string;
        }

        float widthOffst = (customFontRenderer.getStringWidth(total) / 2F) + 2;

            RenderingUtils.drawRect2(-widthOffst + 2, -(customFontRenderer.getFontHeight() + 2 * 2),(2 + widthOffst) * 2,  customFontRenderer.getFontHeight() + 2 * 2, 0x13144);



        widthOffst = -(widthOffst - 3);

        first = true;
        for (ColoredString part : strings) {
            if (part == null) continue;
            if (!first) {
                widthOffst += spaceWidth;
            }
            first = false;
            customFontRenderer.drawString(part.string, widthOffst, -(customFontRenderer.getFontHeight() + 2), part.color);
            widthOffst += customFontRenderer.getStringWidth(part.string);
        }

        height += customFontRenderer.getFontHeight() + 2 * 2;

        if (ench.isEnable() || armor.isEnable()) {


            double xOffset = 0;

            for (ItemStack stack : mc.player.getInventory().armor) {
                if (stack != null && !stack.isEmpty()) {
                    xOffset -= 35 / 2D;
                }
            }

            if (mc.player.getMainHandStack() != null && !mc.player.getMainHandStack().isEmpty()) {
                xOffset -= 35 / 2D;
            }

            if (mc.player.getOffHandStack() != null && !mc.player.getOffHandStack().isEmpty()) {
                xOffset -= 35 / 2D;
            }

            float maxOffset = 0F;

            if (mc.player.getMainHandStack() != null && !mc.player.getMainHandStack().isEmpty()) {
                maxOffset = Math.max(maxOffset, renderItem(mc.player.getMainHandStack(), xOffset, -height));
                xOffset += 35;
            }

            for (ItemStack stack : mc.player.getInventory().armor) {
                if (stack != null && !stack.isEmpty()) {
                    maxOffset = Math.max(maxOffset, renderItem(stack, xOffset, -height));
                    xOffset += 35;
                }
            }

            if (mc.player.getOffHandStack() != null && !mc.player.getOffHandStack().isEmpty()) {
                maxOffset = Math.max(maxOffset, renderItem(mc.player.getOffHandStack(), xOffset, -height));
            }

            if (mc.player.getMainHandStack() != null && !mc.player.getMainHandStack().isEmpty()) {
                maxOffset = Math.max(29, maxOffset) + 2;
                enchantFontRenderer.drawCenteredString(mc.player.getMainHandStack().getName().getString(), 0, (float) -(maxOffset + height), -1);
            }
        }

        glPopMatrix();
    }

    private float renderItem(ItemStack stack, double xPosition, double yPosition) {
        if (!stack.isEmpty()) {
            AtomicReference<Float> yOffset = new AtomicReference<>((float) 0);

            if (armor.isEnable()) {
                glPushMatrix();
                glTranslated(xPosition, yPosition - 29, 0);
                glScaled(2, 2, 1);

                mc.getItemRenderer().renderGuiItemIcon(stack, 0, 0);
                mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, stack, 0, 0);

                glPopMatrix();
            }

            if (ench.isEnable()) {
                glTranslated(xPosition + 2D, yPosition, 0);

                EnchantmentHelper.get(stack).forEach((enc, level) -> {
                    try {
                        String encName = enc.isCursed()
                                ? enc.getName(0).getString().substring(9).substring(0, 1).toLowerCase()
                                : enc.getName(0).getString().substring(0, 1).toLowerCase();
                        encName = encName + level;

                        yOffset.set(yOffset.get() + enchantFontRenderer.getFontHeight(encName));

                        enchantFontRenderer.drawStringWithShadow(encName, 0, -yOffset.get(), -1);
                    } catch (IndexOutOfBoundsException exception) {

                    }

                });

                glTranslated(-(xPosition + 2D), -(yPosition), 0);
            }
            return yOffset.get();
        }
        return 0F;
    }

    private static class ColoredString {
        public final String string;
        public final int color;

        public ColoredString(String string, int color) {
            this.string = string;
            this.color = color;
        }
    }
}
