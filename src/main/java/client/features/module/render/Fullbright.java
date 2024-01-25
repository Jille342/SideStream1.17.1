package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;

public class Fullbright extends Module {
	public Fullbright() {
		super("Fullbright", 0,	Category.RENDER);
	}

	double lastGamma;

    @Override
    public void onEvent(Event<?> e) {
    	if(e instanceof EventUpdate) {
		mc.options.gamma= 10000;
    	}
    	super.onEvent(e);
    }

	@Override
	public void onEnable() {
		lastGamma = (Double) mc.options.gamma;
		super.onEnable();
	}

	@Override
	public void onDisable() {
		mc.options.gamma = lastGamma;
		super.onDisable();
	}
}
