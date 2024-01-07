package client.features.module.movement;

import client.event.Event;
import client.features.module.Module;
import net.minecraft.client.option.KeyBinding;

public class Sprint extends Module {
    public Sprint () {
        super("Sprint", 0, Category.MOVEMENT);
    }
    public void onEvent(Event<?> e) {
        if(e.isPre()) {
            if(mc.options.keyForward.isPressed() && !(mc.player.isUsingItem()))
                KeyBinding.setKeyPressed(mc.options.keySprint.getDefaultKey(), true);
        }
    }

}
