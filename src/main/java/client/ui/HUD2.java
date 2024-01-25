package client.ui;

import client.features.module.Module;
import client.features.module.ModuleManager;
import client.utils.Colors;
import client.utils.RenderingUtils;
import client.utils.Translate;
import client.Client;
import client.utils.font.Fonts;
import client.utils.font.TTFFontRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.stat.Stat;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;

import java.awt.*;
import java.util.*;
import java.util.List;

public class HUD2 {

    protected MinecraftClient mc = MinecraftClient.getInstance();

    public HUD2() {
    }

    public void draw() {
        int[] counter = {1};
        int color = -1;
        switch (client.features.module.render.HUD2.namecolormode.getMode()) {
            case "Default":
                color = new Color(50, 200, 255).getRGB();
                break;
            case "Rainbow":
                color = Colors.rainbow((counter[0] * 15) * 7, 0.8f, 1.0f);
                break;
            case "Pulsing":
                color = TwoColoreffect(new Color(50, 200, 255), new Color(9, 9, 79), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                break;
            case "Test":
                color = TwoColoreffect(new Color(65, 179, 255), new Color(248, 54, 255), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                break;
        }
        Window scaledResolution = mc.getWindow();
        float height = 10;
        String name = Client.NAME;
        String fps = "FPS: \2477" + mc.fpsDebugString;
        String coord = "XYZ: \2477" + MathHelper.floor(this.mc.player.getX()) + " / " + MathHelper.floor(this.mc.player.getY()) + " / " + MathHelper.floor(this.mc.player.getZ());String
                build = "Build: \2477" + Client.VERSION;
        //  name = name.substring(0, 1).replaceAll(name.substring(0, 1), "\247c" + name.substring(0, 1)) + name.substring(1).replaceAll(name.substring(1), "\247f" + name.substring(1));
        name = name.substring(0, 1).replaceAll(name.substring(0, 1),  name.substring(0, 1)) + name.substring(1).replaceAll(name.substring(1), "\247f" + name.substring(1));

        if (!mc.options.debugEnabled) {
         Fonts.font.drawString(name, 3, 4, color);
            if (client.features.module.render.HUD2.info.isEnable()) {
                Fonts.font.drawString(fps, 3, scaledResolution.getScaledHeight() - height, -1);
                Fonts.font.drawString(coord, Fonts.font.getStringWidth(fps) + 6, scaledResolution.getScaledHeight() - height, -1);
                           }
            Fonts.font.drawString(build, 5, 16, -1);
            if(client.features.module.render.HUD2.armor.isEnable()) {
                drawArmorStatus(scaledResolution);
            }
            if(client.features.module.render.HUD2.effects.isEnable()) {
                drawPotionStatus(scaledResolution);
            }

            this.drawGaeHud();
        }

    }
    private void drawPotionStatus(Window sr) {
        float pY = (mc.currentScreen != null ) ? -26 : -12;
        assert mc.player != null;
        Collection<StatusEffectInstance> collection = mc.player.getStatusEffects();

        for (StatusEffectInstance effect : collection) {
            String name = I18n.translate(effect.getEffectType().getTranslationKey(), new Object[0]);
            String PType = "";
            if (effect.getAmplifier() == 1) {
                name = name + " II";
            } else if (effect.getAmplifier() == 2) {
                name = name + " III";
            } else if (effect.getAmplifier() == 3) {
                name = name + " IV";
            }
            if ((effect.getDuration() < 600) && (effect.getDuration() > 300)) {
                PType = PType + "\2476 " + effect.getDuration();
            } else if (effect.getDuration() < 300) {
                PType = PType + "\247c " + effect.getDuration();
            } else if (effect.getDuration() > 600) {
                PType = PType + "\2477 " + effect.getDuration();
            }
            Fonts.font.drawStringWithShadow(name, sr.getScaledWidth() - Fonts.font.getStringWidth(name + PType) - 3, sr.getScaledHeight() - 9 + pY, -1);
            Fonts.font.drawStringWithShadow(PType, sr.getScaledWidth() - Fonts.font.getStringWidth(PType) - 2, sr.getScaledHeight() - 9 + pY, -1);
            pY -= 9;
        }
    }
    private void drawArmorStatus(Window scaledRes) {
        assert mc.player != null;
        MatrixStack matrixStack = new MatrixStack();
        if (!mc.player.isCreative()) {
            int x = 15;
            matrixStack.push();
            for (int index = 3; index >= 0; index--) {
                ItemStack stack = mc.player.getInventory().armor.get(index);
                if (stack != null) {
                    mc.getItemRenderer().renderInGui(stack, scaledRes.getScaledWidth() / 2 + x - 1, scaledRes.getScaledHeight() - (mc.player.isInsideWaterOrBubbleColumn() ? 65 : 55) - 2);
                    mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, stack, scaledRes.getScaledWidth() / 2 + x - 1, scaledRes.getScaledHeight() - (mc.player.isInsideWaterOrBubbleColumn() ? 65 : 55) - 2);
                    x += 18;
                }
            }
           matrixStack.pop();
        }
    }

    private void drawGaeHud() {
        Window scaledResolution = mc.getWindow();
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();
        ArrayList<Module> sortedList = getSortedModules(Fonts.font);
        int listOffset = 10, y = 1;
        int[] counter = {1};

        GL11.glEnable(3042);
        for (int i = 0, sortedListSize = sortedList.size(); i < sortedListSize; i++) {
            Module module = sortedList.get(i);
            Translate translate = module.getTranslate();

            String moduleLabel = module.getDisplayName();
            float length = (float) Fonts.font.getStringWidth(moduleLabel);
            float featureX = width - length - 3.0F;
            boolean enable = module.isEnable();
            if (enable) {
                translate.interpolate(featureX, y, 7);
            } else {
                translate.interpolate(width + 3, y, 7);
            }
            double translateX = translate.getX();
            double translateY = translate.getY();
            boolean visible = ((translateX > -listOffset));
            if (visible) {
                int color = -1;
                switch (client.features.module.render.HUD2.colormode.getMode()) {
                    case "Default":
                        color = new Color(50   , 100, 255).getRGB();
                        break;
                    case "Rainbow":
                        color = Colors.rainbow((counter[0] * 15) * 7, 0.8f, 1.0f);
                        break;
                    case "Pulsing":
                        color = TwoColoreffect(new Color(50, 200, 255), new Color(9, 9, 79), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                        break;
                    case "Test":
                        color = TwoColoreffect(new Color(65, 179, 255), new Color(248, 54, 255), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                        break;
                }
                int nextIndex = sortedList.indexOf(module) + 1;
                Module nextModule = null;
                if (sortedList.size() > nextIndex)
                    nextModule = getNextEnabledModule(sortedList, nextIndex);
                if ((Boolean) client.features.module.render.HUD2.background.enable)
                    RenderingUtils.fill((int) (translateX - 2.0D), (int) ((int)translateY - 1.0D), width, (int) (translateY + listOffset - 1.0D), Colors.getColor(0,0,0,50));
                if ((Boolean) client.features.module.render.HUD2.OUTLINE.enable) {
                    RenderingUtils.drawRect(translateX - 2.6D, translateY - 1.0D, translateX - 2.0D, translateY + listOffset - 1.0D, color);
                    double offsetY = listOffset;
                    if (nextModule != null) {
                        double dif = (length - Fonts.font.getStringWidth(nextModule.getDisplayName()));
                        RenderingUtils.drawRect(translateX - 2.6D, translateY + offsetY - 1.0D, translateX - 2.6D + dif, translateY + offsetY - 0.5D, color);
                    } else {
                        RenderingUtils.drawRect(translateX - 2.6D, translateY + offsetY - 1.0D, width, translateY + offsetY - 0.6D, color);
                    }
                }

                Fonts.font.drawString(moduleLabel, (float) translateX, (float) translateY + 2, color);

                if (module.isEnable()) {
                    y += listOffset;
                    counter[0] -= 1F;
                }
            }
        }
    }


    private Module getNextEnabledModule(ArrayList<Module> modules, int startingIndex) {
        for (int i = startingIndex, modulesSize = modules.size(); i < modulesSize; i++) {
            Module module = modules.get(i);
            if (module.isEnable())
                return module;
        }
        return null;
    }
    private ArrayList<Module> getSortedModules(TTFFontRenderer fr) {
        ArrayList<Module> sortedList = new ArrayList<>(ModuleManager.modules);
        sortedList.sort(Comparator.comparingDouble(e -> -fr.getStringWidth(e.getDisplayName())));
        return sortedList;
    }
    public static Color TwoColoreffect(final Color color, final Color color2, double delay) {
        if (delay > 1.0) {
            final double n2 = delay % 1.0;
            delay = (((int) delay % 2 == 0) ? n2 : (1.0 - n2));
        }
        final double n3 = 1.0 - delay;
        return new Color((int) (color.getRed() * n3 + color2.getRed() * delay), (int) (color.getGreen() * n3 + color2.getGreen() * delay), (int) (color.getBlue() * n3 + color2.getBlue() * delay), (int) (color.getAlpha() * n3 + color2.getAlpha() * delay));
    }
    public static Color fade(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0F + index / count * 2.0F) % 2.0F - 1.0F);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }
    public double getDistTraveled() {
        double total = 0;
        for (double d : client.features.module.render.HUD2.distances) {
            total += d;
        }
        return total;
    }
}