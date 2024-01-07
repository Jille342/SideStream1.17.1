package client.mixin.client;

import client.features.module.ModuleManager;
import client.Client;
import client.event.listeners.EventTick;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {


    @Inject(method = {"stop"}, at = @At("HEAD"))
    public void shutdown(CallbackInfo ci)
    {
        ModuleManager.saveModuleSetting();
    }
    @Inject(method ="tick", at = @At("RETURN"))
    private void runTick(CallbackInfo ci) {
        EventTick eventTick = new EventTick();
        Client.onEvent(eventTick);
    }
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onResolutionChanged()V"))
   public void init(CallbackInfo ci) {
Client.init();
 }




}
