/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package client.mixin.client;

import client.Client;
import client.event.listeners.EventEntityRender;
import client.event.listeners.EventRenderWorld;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.profiler.Profiler;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

	@Shadow private void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState) {}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
	private void render_swap(Profiler profiler, String string) {
		profiler.swap(string);

		if (string.equals("entities")) {
			Client.onEvent(new EventEntityRender.PreAll());
		} else if (string.equals("blockentities")) {
			Client.onEvent(new EventEntityRender.PostAll());
		}
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void render_head(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
							 LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo callback) {
		EventRenderWorld event = new EventRenderWorld(tickDelta, matrices);
		Client.onEvent(event);

		if (event.isCancelled()) {
			callback.cancel();
		}
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void render_return(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
							   LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo callback) {
		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
		EventRenderWorld event = new EventRenderWorld(tickDelta, matrices);
		Client.onEvent(event);
	}

	@Redirect(method = "renderEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
	private <E extends Entity> void renderEntity_render(EntityRenderDispatcher dispatcher, E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		EventEntityRender.Single.Pre event = new EventEntityRender.Single.Pre(entity, matrices, vertexConsumers);
		Client.onEvent(event);

		if (!event.isCancelled()) {
			dispatcher.render(event.getEntity(), x, y, z, yaw, tickDelta, event.getMatrix(), event.getVertex(), light);
		}
	}

}
