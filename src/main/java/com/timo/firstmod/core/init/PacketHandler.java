package com.timo.firstmod.core.init;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.core.network.ClientBoundRocketLauncherUsePacket;
import com.timo.firstmod.core.network.ClientBoundRocketProjectileExplosionPacket;
import com.timo.firstmod.core.network.ServerBoundRocketLauncherUsePacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
	private PacketHandler() {
	}
	
	private static final String PROTOCOL_VERSION = "1";
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(FirstMod.MODID,"main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	
	public static void init() {
		int index = 0;
		INSTANCE.messageBuilder(ServerBoundRocketLauncherUsePacket.class, index++, NetworkDirection.PLAY_TO_SERVER).encoder(ServerBoundRocketLauncherUsePacket::encode)
																											     //.decoder(ServerBoundRocketLauncherUsePacket::new)
																												   .decoder(ServerBoundRocketLauncherUsePacket::decode)
																												   .consumer(ServerBoundRocketLauncherUsePacket::handle)
																												   .add();
		
		INSTANCE.messageBuilder(ClientBoundRocketLauncherUsePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT).encoder(ClientBoundRocketLauncherUsePacket::encode)
																												   .decoder(ClientBoundRocketLauncherUsePacket::new)
																												   .consumer(ClientBoundRocketLauncherUsePacket::handle)
																												   .add();
		
		INSTANCE.messageBuilder(ClientBoundRocketProjectileExplosionPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT).encoder(ClientBoundRocketProjectileExplosionPacket::encode)
																														   .decoder(ClientBoundRocketProjectileExplosionPacket::new)
																														   .consumer(ClientBoundRocketProjectileExplosionPacket::handle)
																														   .add();
	}
}
