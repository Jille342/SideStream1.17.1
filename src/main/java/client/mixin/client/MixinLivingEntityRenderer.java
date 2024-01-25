package client.mixin.client;

import client.Client;
import client.event.listeners.EventRenderGUI;
import client.features.module.ModuleManager;
import client.features.module.render.Chams;
import net.minecraft.client.Keyboard;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntityRenderer.class})
public class MixinLivingEntityRenderer {

    @Redirect(method = "getRenderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    public Identifier onGetTexture(LivingEntityRenderer livingEntityRenderer, Entity entity) {
        if (ModuleManager.getModulebyClass(Chams.class).isEnable() && !Chams.shouldRenderTexture(entity)) {
            return Chams.EMPTY_TEXTURE;
        }
        return livingEntityRenderer.getTexture(entity);
    }
}
