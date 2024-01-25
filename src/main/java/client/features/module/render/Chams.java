package client.features.module.render;

import client.features.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class Chams extends Module {

    public static final Identifier EMPTY_TEXTURE = new Identifier("textures/empty.png");
    public Chams() {
        super("Chams",0, Category.RENDER);
    }

    public static boolean shouldRenderTexture(Entity entity) {
        if (entity == null) {
            return false;
        }

        if (entity instanceof PlayerEntity) {
            return entity != MinecraftClient.getInstance().player;
        }


        switch (entity.getType().getSpawnGroup()) {
            case CREATURE:
            case WATER_AMBIENT:
            default: return false;
        }
    }
}
