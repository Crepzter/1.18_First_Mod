package com.timo.firstmod.core.datagen;

import com.timo.firstmod.FirstMod;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

//@Mod.EventBusSubscriber(modid = FirstMod.MODID, bus = Bus.MOD)
public class DataGenerators {

	//@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		if(event.includeServer()) {
			//generator.addProvider(new Provider());
		}
		if(event.includeClient()) {
			//generator.addProvider(new Provider());
		}
	}
}
