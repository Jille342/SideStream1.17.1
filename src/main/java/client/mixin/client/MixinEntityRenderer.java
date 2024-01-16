package client.mixin.client;


import client.Client;
import client.event.listeners.EventEntityRender;
import client.features.module.ModuleManager;
import client.features.module.render.NameTagsTest;
import client.utils.Colors;
import client.utils.RenderingUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity>
{

    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
    {
        EventEntityRender.Single.Label event = new EventEntityRender.Single.Label(entity, matrices, vertexConsumers);
        Client.onEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }

        if(ModuleManager.getModulebyClass(NameTagsTest.class).isEnable())
        {
            if(entity instanceof LivingEntity)
            {
                NameTagsTest.renderTags(entity, entity.getDisplayName(), matrices, vertexConsumers, light);
                ci.cancel();
            }
        }
    }

}
