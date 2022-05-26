package com.timo.firstmod.core.event;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.SnowmanCannon;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = FirstMod.MODID, bus = Bus.FORGE)
public class CommonForgeEvents {
	/*
    @SubscribeEvent
	public static void registerSnowmanAttack(LivingAttackEvent event) {
		if(event.getSource().getEntity() instanceof SnowmanCannon entity) {
	        entity.setDataTargeting(true);
		}
	} */
}
