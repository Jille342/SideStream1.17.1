package me.x150.renderer.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
import org.jetbrains.annotations.Range;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * The rendering class for the 2nd dimension, used in the hud renderer or in screens
 */
public class Renderer2d {
    /**
     * Reference to the minecraft client
     */
    private static final MinecraftClient client = MinecraftClient.getInstance();

    /**
     * Begins scissoring the selected area<br>
     * Only pixels in this area will be rendered, until {@link #endScissor()} is called<br>
     * <strong>Do not call directly unless you have a good reason to, use
     * @param x The start X coordinate
     * @param y The start Y coordinate
     * @param endX The end X coordinate
     * @param endY The end Y coordinate
     * @deprecated for internal use only
     */
    @Deprecated
    public static void beginScissor(double x, double y, double endX, double endY) {
        double width = endX - x;
        double height = endY - y;
        width = Math.max(0, width);
        height = Math.max(0, height);
        float d = (float) client.getWindow().getScaleFactor();
        int ay = (int) ((client.getWindow().getScaledHeight() - (y + height)) * d);
        RenderSystem.enableScissor((int) (x * d), ay, (int) (width * d), (int) (height * d));
    }

    /**
     * Ends the scissor context<br>
     * Always call when you used {@link #beginScissor(double, double, double, double)} before, unless you want to break something<br>
     * <strong>Do not call directly unless you have a good reason to, use
     * @deprecated for internal use only
     */
    @Deprecated
    public static void endScissor() {
        RenderSystem.disableScissor();
    }

    /**
     * Renders a texture<br>
     * Make sure to link your texture using {@link RenderSystem#setShaderTexture(int, Identifier)} before using this
     * @param matrices The context MatrixStack
     * @param x0 The X coordinate
     * @param y0 The Y coordinate
     * @param width The width of the rendered area
     * @param height The height of the rendered area
     * @param u The U of the initial texture (0 for none)
     * @param v The V of the initial texture (0 for none)
     * @param regionWidth The UV Region width of the initial texture (can be width)
     * @param regionHeight The UV Region width of the initial texture (can be height)
     * @param textureWidth The texture width (can be width)
     * @param textureHeight The texture height (can be height)
     */
    public static void renderTexture(MatrixStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight) {
        double x1 = x0 + width;
        double y1 = y0 + height;
        double z = 0;
        renderTexturedQuad(matrices.peek().getModel(), x0, x1, y0, y1, z, (u + 0.0F) / (float) textureWidth, (u + (float) regionWidth) / (float) textureWidth, (v + 0.0F) / (float) textureHeight, (v + (float) regionHeight) / (float) textureHeight);
    }

    /**
     * Renders a texture<br>
     * Make sure to link your texture using {@link RenderSystem#setShaderTexture(int, Identifier)} before using this
     * @param matrices The context MatrixStack
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param width The width of the texture
     * @param height The height of the texture
     */
    public static void renderTexture(MatrixStack matrices, double x, double y, double width, double height) {
        renderTexture(matrices, x, y, width, height,0,0,width,height,width,height);
    }

    /**
     * Renders a circle<br>
     * Best used inside of {@link MSAAFramebuffer#use(int, Runnable)}
     * @param matrices The context MatrixStack
     * @param circleColor The color of the circle
     * @param originX The <b>center</b> X coordinate
     * @param originY The <b>center</b> Y coordinate
     * @param rad The radius of the circle
     * @param segments How many segments to use to render the circle (less = more performance, more = higher quality circle)
     */

    /**
     * Renders a regular colored quad
     * @param matrices The context MatrixStack
     * @param c The color of the quad
     * @param x1 The start X coordinate
     * @param y1 The start Y coordinate
     * @param x2 The end X coordinate
     * @param y2 The end Y coordinate
     */
    public static void renderQuad(MatrixStack matrices, java.awt.Color c, double x1, double y1, double x2, double y2) {
        int color = c.getRGB();
        double j;
        if (x1 < x2) {
            j = x1;
            x1 = x2;
            x2 = j;
        }

        if (y1 < y2) {
            j = y1;
            y1 = y2;
            y2 = j;
        }
        Matrix4f matrix = matrices.peek().getModel();
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RendererUtils.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }
    private static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double rad, double samples) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        double toX1 = toX - rad;
        double toY1 = toY - rad;
        double fromX1 = fromX + rad;
        double fromY1 = fromY + rad;
        double[][] map = new double[][]{new double[]{toX1, toY1}, new double[]{toX1, fromY1}, new double[]{fromX1, fromY1}, new double[]{fromX1, toY1}};
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            double max = (360 / 4d + i * 90d);
            for (double r = i * 90d; r < max; r += (90 / samples)) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).next();
            }
            // make sure we render the corner properly by adding one final vertex at the end
            float rad1 = (float) Math.toRadians(max);
            float sin = (float) (Math.sin(rad1) * rad);
            float cos = (float) (Math.cos(rad1) * rad);
            bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).next();
        }
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }
    /**
     * Renders a rounded rectangle<br>
     * Best used inside of {@link MSAAFramebuffer#use(int, Runnable)}
     * @param matrices The context MatrixStack
     * @param c The color of the rounded rectangle
     * @param fromX The start X coordinate
     * @param fromY The start Y coordinate
     * @param toX The end X coordinate
     * @param toY The end Y coordinate
     * @param rad The radius of the corners
     * @param samples How many samples to use for the corners
     * @throws IllegalArgumentException If height or width are below 1 px
     */
    public static void renderRoundedQuad(MatrixStack matrices, java.awt.Color c, double fromX, double fromY, double toX, double toY, double rad, double samples) {
        double height = toY-fromY;
        double width = toX-fromX;
        if (height <= 0) throw new IllegalArgumentException("Height should be > 0, got "+height);
        if (width <= 0) throw new IllegalArgumentException("Width should be > 0, got "+width);
        double smallestC = Math.min(height, width)/2d;
        rad = Math.min(rad, smallestC);
        int color = c.getRGB();
        Matrix4f matrix = matrices.peek().getModel();
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        RendererUtils.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        renderRoundedQuadInternal(matrix, g, h, k, f, fromX, fromY, toX, toY, rad, samples);
    }

    /**
     * Renders a regular line between 2 points
     * @param stack The context MatrixStack
     * @param c The color of the line
     * @param x The start X coordinate
     * @param y The start Y coordinate
     * @param x1 The end X coordinate
     * @param y1 The end Y coordinate
     */
    public static void renderLine(MatrixStack stack, Color c, double x, double y, double x1, double y1) {
        float g = c.getRed() / 255f;
        float h = c.getGreen() / 255f;
        float k = c.getBlue() / 255f;
        float f = c.getAlpha() / 255f;
        Matrix4f m = stack.peek().getModel();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RendererUtils.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(m, (float) x, (float) y, 0f).color(g, h, k, f).next();
        bufferBuilder.vertex(m, (float) x1, (float) y1, 0f).color(g, h, k, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    private static void renderTexturedQuad(Matrix4f matrix, double x0, double x1, double y0, double y1, double z, float u0, float u1, float v0, float v1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, (float) x0, (float) y1, (float) z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y0, (float) z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, (float) x0, (float) y0, (float) z).texture(u0, v0).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }
}
