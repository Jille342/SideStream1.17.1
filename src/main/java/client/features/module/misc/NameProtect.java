package client.features.module.misc;

import client.Client;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import client.utils.ChatUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class NameProtect extends Module {

    public NameProtect() {
        super("NameProtect", 0, Category.MISC);
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventPacket) {
            EventPacket event = ((EventPacket)e);
            if(event.isIncoming()) {
                Packet<?> p = event.getPacket();

                if(p instanceof GameMessageS2CPacket) {

                    GameMessageS2CPacket packet = (GameMessageS2CPacket) event.getPacket();
                    if (packet.getMessage().getString().contains( mc.player.getName().getString())) {
                        String temp = packet.getMessage().toString();
                        ChatUtils.printChatNoName(temp.replaceAll(String.valueOf(mc.player.getName().getString()), "\247d" + Client.NAME + "User" + "\247r"));
                        event.setCancelled(true);
                    } else {
                        String[] list = new String[]{"join", "left", "leave", "leaving", "lobby", "server", "fell", "died", "slain", "burn", "void", "disconnect", "kill", "by", "was", "quit", "blood", "game"};
                        for (String str : list) {
                            if (packet.getMessage().toString().toLowerCase().contains(str)) {
                                event.setCancelled(true);
                                break;
                            }
                        }
                    }
                }

            }
        }
        super.onEvent(e);
    }

}
