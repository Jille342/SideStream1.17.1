package client.event.listeners;

import client.event.Event;
import net.minecraft.client.util.Window;

/**
 * 2DRenderのEvent
 */
public class EventRender2D extends Event {
    private Window resolution;
    private float partialticks;

    public EventRender2D( float partialticks) {
        this.resolution = resolution;
        this.partialticks = partialticks;
    }

    public Window getResolution() {
        return resolution;
    }

    public float getPartialTicks() {
        return partialticks;
    }
}