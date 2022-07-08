package com.timo.firstmod.core.event;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.SnowmanCannon;
import com.timo.firstmod.utils.SheepUtils;

import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
	
	@SubscribeEvent
	public static void onPlayerEntityInteractEvent(PlayerInteractEvent.EntityInteract event) {
		if(!event.getPlayer().level.isClientSide() && event.getTarget() instanceof Sheep sheep && event.getPlayer().getItemInHand(event.getHand()).is(Items.SHEARS) && sheep.readyForShearing()) {
			if(sheep.hasCustomName() && "jeb_".equals(sheep.getName().getContents())) {
				sheep.setSheared(true);
				
				Level level = sheep.level;
				Item wool = SheepUtils.woolForJeb(sheep);
				
				int i = 1 + level.random.nextInt(3);

			    for(int j = 0; j < i; j++) {
			       ItemEntity itementity = sheep.spawnAtLocation(wool, 1);
			       if (itementity != null) {
			          itementity.setDeltaMovement(itementity.getDeltaMovement().add((double)((level.random.nextFloat() - level.random.nextFloat()) * 0.1F), (double)(level.random.nextFloat() * 0.05F), (double)((level.random.nextFloat() - level.random.nextFloat()) * 0.1F)));
			       }
			    }
			}
			
			event.setCanceled(true);
		}
	}
}
