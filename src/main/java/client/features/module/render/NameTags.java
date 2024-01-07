package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventRender2D;
import client.event.listeners.RenderEvent;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.font.Fonts;
import client.utils.font.TTFFontRenderer;
import client.utils.math.BillboardPos;
import client.utils.math.MatrixUtil;
import client.utils.math.Vec2d;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicReference;

import static client.utils.math.InterpolationUtil.lerp;


public class NameTags extends Module {



    public static TTFFontRenderer customFontRenderer = Fonts.font;
    public static TTFFontRenderer enchantFontRenderer = Fonts.font3;
    NumberSetting scaledFactor;
    NumberSetting maxScale;
    BooleanSetting self;
    NumberSetting gap;
    NumberSetting margin;
    NumberSetting armorSpacing;
    NumberSetting spacing;
    BooleanSetting enchants;
    BooleanSetting armor;
    BooleanSetting health;
    public NameTags() {
        super("NameTags", 0, Category.RENDER);
    }

    private float spaceWidth = 0F;
    public void init(){
        super.init();
        scaledFactor = new NumberSetting("Factor",0.6D, 1D, 0.01D, 0.01D);
        maxScale = new NumberSetting("Max Scale", 0.3D, 1D, 0.1D, 0.05D);
        self = new BooleanSetting("Self", false);
        gap = new NumberSetting("Gap", 5, 20, 0, 1);
       margin = new NumberSetting("Margin",2, 10, 0, 1 );
       armorSpacing = new NumberSetting("Armor Spacing",35, 100, 10, 1);
        spacing = new NumberSetting("Spacing",0.3D, 1D, 0D, 0.05D);
        enchants = new BooleanSetting("Enchants", true);
        armor = new BooleanSetting("Armor", true);
        health = new BooleanSetting("Health", true);
        addSetting(scaledFactor,maxScale,self,gap,margin,armorSpacing,spacing,enchants,armor,health);
    }



    public void onEvent(Event<?> e) {
        if (e instanceof EventRender2D) {
            Vec3d camPos = mc.gameRenderer.getCamera().getPos();

            spaceWidth = customFontRenderer.getStringWidth(" ");
            MatrixStack stack = new MatrixStack();
            RenderEvent event = new RenderEvent();
            MatrixUtil.onRender(event);

            for (PlayerEntity entity : mc.world.getPlayers()) {
                if (entity == mc.player && !self.isEnable()) continue;
                Vec3d iPos = lerpEntity(entity);
                BillboardPos projectedPos = MatrixUtil.getBillboardPos(iPos.add(0, entity.getHeight() + spacing.getValue(), 0));
                if (projectedPos == null) continue;
                Vec2d screenPos = projectedPos.getProjectedPos();
                if (screenPos == null) continue;


                drawNametag(entity, camPos, iPos, screenPos);

            }

        }
    }
    public static Vec3d lerpEntity(PlayerEntity entity) {
        double[] lerped = lerp(entity);
        return new Vec3d(lerped[0], lerped[1], lerped[2]);
    }
    public static float roundFloat(float value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
    private void drawNametag(PlayerEntity player, Vec3d camPos, Vec3d iPos, Vec2d screenPos)  {
        MatrixStack stack = new MatrixStack();
        double height = 0;

        double dist = 1 - iPos.distanceTo(camPos) * (0.1D * scaledFactor.getValue());

        if (dist < maxScale.getValue()) {
            dist = maxScale.getValue();
        }

      stack.push();
        stack.translate(screenPos.x, screenPos.y, 0);
        stack.scale((float) dist, (float) dist, 1F);

        String[] strings = new String[5];



        strings[2] = player.getName().toString();

        if (health.getValue()) {
            float playerHealth = roundFloat(player.getHealth() + player.getAbsorptionAmount(), 1);

            if (player.getEntityName().equalsIgnoreCase("antiflame") || player.getEntityName().equalsIgnoreCase("0851_")) {
                playerHealth += 0.69F;
            }

            String health = Float.toString(playerHealth).replace(".0", "");


                strings[3] = health;

        }

        String total = "";
        boolean first = true;
        for (String part : strings) {
            if (part == null) continue;
            if (!first) {
                total += " ";
            }
            first = false;
            total += part.toString();
        }

        float widthOffst = (customFontRenderer.getStringWidth(total) / 2F) + 2;


        widthOffst = -(widthOffst - 3);

        first = true;
        for (String part : strings) {
            if (part == null) continue;
            if (!first) {
                widthOffst += spaceWidth;
            }
            first = false;
            customFontRenderer.drawString(part, widthOffst, -(customFontRenderer.getFontHeight() + margin.getValue()), -1);
            widthOffst += customFontRenderer.getStringWidth(part);
        }

        height += customFontRenderer.getFontHeight() + margin.getValue() * 2;

        if (enchants.getValue() || armor.getValue()) {
            height += gap.getValue();

            double xOffset = 0;

            for (ItemStack itemStack : mc.player.getInventory().armor) {
                if (itemStack != null && !itemStack.isEmpty()) {
                    xOffset -= armorSpacing.getValue() / 2D;
                }
            }

            if (mc.player.getMainHandStack() != null && !mc.player.getMainHandStack().isEmpty()) {
                xOffset -= armorSpacing.getValue() / 2D;
            }

            if (mc.player.getOffHandStack() != null && !mc.player.getOffHandStack().isEmpty()) {
                xOffset -= armorSpacing.getValue() / 2D;
            }

            float maxOffset = 0F;

            if (mc.player.getMainHandStack() != null && !mc.player.getMainHandStack().isEmpty()) {
                maxOffset = Math.max(maxOffset, renderItem(mc.player.getMainHandStack(), xOffset, -height));
                xOffset += armorSpacing.getValue();
            }

            for (ItemStack itemStackstack : mc.player.getInventory().armor) {
                if (itemStackstack != null && !stack.isEmpty()) {
                    maxOffset = Math.max(maxOffset, renderItem(itemStackstack, xOffset, -height));
                    xOffset += armorSpacing.getValue();
                }
            }

            if (mc.player.getOffHandStack() != null && !mc.player.getOffHandStack().isEmpty()) {
                maxOffset = Math.max(maxOffset, renderItem(mc.player.getOffHandStack(), xOffset, -height));
            }


        }

        stack.pop();
    }

    private float renderItem(ItemStack stack, double xPosition, double yPosition) {
        MatrixStack matrixStack = new MatrixStack();
        if (!stack.isEmpty()) {
            AtomicReference<Float> yOffset = new AtomicReference<>((float) 0);

            if (armor.getValue()) {
                matrixStack.push();
                matrixStack.translate(xPosition, yPosition - 29, 0);
                matrixStack.scale(2, 2, 1);

                mc.getItemRenderer().renderGuiItemIcon(stack, 0, 0);
                mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, stack, 0, 0);

                matrixStack.pop();
            }

            if (enchants.getValue()) {
                matrixStack.translate(xPosition + 2D, yPosition, 0);
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

                matrixStack.translate(-(xPosition + 2D), -(yPosition), 0);
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