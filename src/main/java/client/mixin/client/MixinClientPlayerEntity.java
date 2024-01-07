package client.mixin.client;

import client.Client;
import client.event.EventType;
import client.event.listeners.EventUpdate;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerEntity.class})
public class MixinClientPlayerEntity {
    @Inject(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
            ordinal = 0), method = "tick()V")
    private void onTick(CallbackInfo ci)
    {
        EventUpdate eventUpdate = new EventUpdate();
        eventUpdate.setType(EventType.PRE);
        Client.onEvent(eventUpdate);
    }
}
