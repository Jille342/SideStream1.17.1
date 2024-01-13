package client.mixin.client;

import client.features.module.ModuleManager;
import client.Client;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventRenderGUI;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinIngameHud {

    @Inject(method = "renderHotbar", at = @At("HEAD"))
    private void renderHotbar(float tickDelta, MatrixStack context, CallbackInfo ci) {
        final EventRender2D eventRender2D = new EventRender2D(tickDelta);
        Client.onEvent(eventRender2D);
        if(ModuleManager.getModulebyName("HUD2").enable)
            Client.hud2.draw();

    }
    @Inject(at = @At("HEAD"),
            method = {"render"})
    private void onRender(MatrixStack context, float tickDelta, CallbackInfo ci)
    {
        final EventRenderGUI eventRenderGUI = new EventRenderGUI();
        Client.onEvent(eventRenderGUI);
    }


}
