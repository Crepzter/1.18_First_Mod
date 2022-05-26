package com.timo.firstmod.client.event;

import com.timo.firstmod.FirstMod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = FirstMod.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {
	private ClientForgeEvents() {
	}

	@SubscribeEvent
	public static void clientTick(ClientTickEvent event) {
		//final var player = Minecraft.getInstance().player;
	}
}
