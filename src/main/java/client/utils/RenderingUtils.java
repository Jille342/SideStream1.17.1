package client.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.renderer.Renderer2d;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.NumberFormat;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;
import static org.lwjgl.opengl.GL11.*;

public class RenderingUtils implements MCUtil {

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }


    public static int[] getFractionIndicies(float[] fractions, float progress) {
        int[] range = new int[2];
        int startPoint = 0;
        while (startPoint < fractions.length && fractions[startPoint] <= progress) {
            startPoint++;
        }

        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }

        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }


    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        Color color = null;
        if (fractions.length == colors.length) {
            int[] indicies = getFractionIndicies(fractions, progress);
            if (indicies[0] < 0 || indicies[0] >= fractions.length || indicies[1] < 0 || indicies[1] >= fractions.length) {
                return colors[0];
            }

            float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
            Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            color = blend(colorRange[0], colorRange[1], 1f - weight);
        }
        return color;
    }
    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = (float) 1.0 - r;

        float rgb1[] = new float[3];
        float rgb2[] = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);

        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;

        if (red < 0) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }

        Color color = null;
        try {
            color = new Color(red, green, blue);
        } catch (IllegalArgumentException exp) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color;
    }

















    public static void drawGradient(double x, double y, double x2, double y2, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;

        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);

        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);

        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);

        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);

        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
        GL11.glColor4d(255, 255, 255, 255);
    }



    public static void drawFilledTriangle(float x, float y, float r, int c, int borderC) {
        enableGL2D();
        glColor(c);
        glEnable(GL_POLYGON_SMOOTH);
        glBegin(GL_TRIANGLES);
        glVertex2f(x + r / 2, y + r / 2);
        glVertex2f(x + r / 2, y - r / 2);
        glVertex2f(x - r / 2, y);
        glEnd();
        glLineWidth(1.3f);
        glColor(borderC);
        glBegin(GL_LINE_STRIP);
        glVertex2f(x + r / 2, y + r / 2);
        glVertex2f(x + r / 2, y - r / 2);
        glEnd();
        glBegin(GL_LINE_STRIP);
        glVertex2f(x - r / 2, y);
        glVertex2f(x + r / 2, y - r / 2);
        glEnd();
        glBegin(GL_LINE_STRIP);
        glVertex2f(x + r / 2, y + r / 2);
        glVertex2f(x - r / 2, y);
        glEnd();
        glDisable(GL_POLYGON_SMOOTH);
        disableGL2D();
    }


    public static void drawImage(final Identifier image, final int x, final int y, final int width, final int height) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        final MatrixStack matrixStack =  new MatrixStack();
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(image);
        drawTexture(matrixStack,x, y, 0.0f, 0.0f, width, height, (int) width, (int) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }





    public static void glColor(final int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255F;
        float red = (hex >> 16 & 0xFF) / 255F;
        float green = (hex >> 8 & 0xFF) / 255F;
        float blue = (hex & 0xFF) / 255F;
        glColor4f(red, green, blue, alpha);
    }

    public static void glColor(final int hex, final float alpha) {
        float red = (hex >> 16 & 0xFF) / 255F;
        float green = (hex >> 8 & 0xFF) / 255F;
        float blue = (hex & 0xFF) / 255F;
        glColor4f(red, green, blue, alpha);
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
    public static void drawRect(double left, double top, double right, double bottom, int color) {
        MatrixStack matrixStack = new MatrixStack();
        Color color2 = new Color(color);

        Renderer2d.renderQuad(matrixStack, color2,left,top,right,bottom);
    }
    public static  void drawRect(MatrixStack matrixStack ,int color, float left, float top, float right, float bottom) {
        Color color2 = new Color(color);
        Renderer2d.renderQuad(matrixStack, color2,left,top,right,bottom);
    }
    public static void drawRect2(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance().getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager._enableBlend();
        GlStateManager._disableTexture();
        GlStateManager._blendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferbuilder.vertex(x, h, 0.0).color(red, green, blue, alpha).next();
        bufferbuilder.vertex(w, h, 0.0).color(red, green, blue, alpha).next();
        bufferbuilder.vertex(w, y, 0.0).color(red, green, blue, alpha).next();
        bufferbuilder.vertex(x, y, 0.0).color(red, green, blue, alpha).next();
        bufferbuilder.end();
        BufferRenderer.draw(bufferbuilder);
    }



    private static void innerFill(Matrix4f matrix, int x1, int y1, int x2, int y2,  int color)
    {
        int i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        float f = (float)(color >> 24 & 255) / 255.0F;
        float g = (float)(color >> 16 & 255) / 255.0F;
        float h = (float)(color >> 8 & 255) / 255.0F;
        float j = (float)(color & 255) / 255.0F;
        BufferBuilder bufferBuilder = new BufferBuilder(3);
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0.0F).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0.0F).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0.0F).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0.0F).color(g, h, j, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
    public static void fill(int x1, int y1, int x2, int y2, int color){
        MatrixStack stack = new MatrixStack();
        Screen.fill(stack,x1,y1,x2,y2, color);
    }
    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glHint(3154, 4354);
    }
    public static boolean began =false;
    public static void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }





    public static double getDiff(double lastI, double i, float ticks, double ownI) { return lastI + (i - lastI) * ticks - ownI; }

}