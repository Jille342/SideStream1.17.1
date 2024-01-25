package client;

import client.command.CommandManager;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.ModuleManager;
import client.ui.HUD2;
import client.ui.theme.ThemeManager;
import client.utils.WorldUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.network.Packet;

import java.io.File;

public class Client
{
    public static final String MOD_ID = "sidestream";
    public static final String NAME = "SideStream";
    public static final String VERSION = "0.1 Beta";
	public static HUD2 hud2 = new HUD2();
	public static Identifier background = new Identifier("client/background.png");


	public static ThemeManager themeManager = new ThemeManager();

	public static final File FOLDER = new File(FabricLoader.getInstance().getGameDir().toString(), "client");
	public static CommandManager commandManager = new CommandManager();
	public static MinecraftClient mc = MinecraftClient.getInstance();

    public static void init()
    {
		System.out.println("Starting " + NAME);

		commandManager.init();
		ModuleManager.registerModules();
		ModuleManager.loadModuleSetting();
	}

	public static Event<?> onEvent(Event<?> e) {
		if (e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			Packet p = event.getPacket();
			if (p instanceof WorldTimeUpdateS2CPacket) {
				WorldUtils.onTime((WorldTimeUpdateS2CPacket) p);
			}
		}
    	ModuleManager.onEvent(e);
		return e;
	}


}
