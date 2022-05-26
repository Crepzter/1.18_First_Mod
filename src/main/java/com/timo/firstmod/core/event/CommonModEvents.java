package com.timo.firstmod.core.event;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.Dshinni;
import com.timo.firstmod.common.entity.SnowmanCannon;
import com.timo.firstmod.core.init.EntityInit;
import com.timo.firstmod.core.init.PacketHandler;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = FirstMod.MODID, bus = Bus.MOD)
public class CommonModEvents {
	
	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(PacketHandler::init);
	}
	
	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put( EntityInit.DSHINNI.get(), Dshinni.createAttributes().build() );
		event.put( EntityInit.SNOWMAN_CANNON.get(), SnowmanCannon.createAttributes().build() );
	}
}
