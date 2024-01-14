package client.event.listeners;

import client.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public class EventRenderWorld extends Event<EventRenderWorld> {
	float partialTicks;
	MatrixStack matrices;
	
	public EventRenderWorld(float partialTicks, MatrixStack matrices) {
		this.partialTicks=partialTicks;
		this.matrices=matrices;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

	public MatrixStack getMatrices() {return  matrices;}

	public void setPartialTicks(float partialTicks) {
		this.partialTicks = partialTicks;
	}
}
