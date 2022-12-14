package com.timo.firstmod.event.common;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.SnowmanCannon;
import com.timo.firstmod.config.FirstModCommonConfigs;
import com.timo.firstmod.utils.SheepUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = FirstMod.MODID, bus = Bus.FORGE)
public class CommonForgeEvents {
	
	@SubscribeEvent
	public static void onPlayerEntityInteractEvent(PlayerInteractEvent.EntityInteract event) {
		if(!event.getPlayer().level.isClientSide() && event.getTarget() instanceof Sheep sheep && event.getPlayer().getItemInHand(event.getHand()).is(Items.SHEARS) && sheep.readyForShearing()) {
			if(sheep.hasCustomName() && "jeb_".equals(sheep.getName().getContents()) && FirstModCommonConfigs.JEB_DROP_COLORED_WOOL.get()) {
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
			    
			    level.playSound(null, sheep, SoundEvents.SHEEP_SHEAR, event.getPlayer() == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
			    
			    event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void onRightClickFletchingTable(PlayerInteractEvent.RightClickBlock event) {
		if(event.getPlayer().level.getBlockState(event.getPos()).is(Blocks.FLETCHING_TABLE) && !event.getPlayer().level.isClientSide()) {
			System.out.println("clicked");
			//event.getPlayer().openMenu(state.getMenuProvider(level, pos));
		}
	}
	
	//public MenuProvider getMenuProvider(BlockState p_52240_, Level p_52241_, BlockPos p_52242_) {
	//      return new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) -> {
	//         return new CraftingMenu(p_52229_, p_52230_, ContainerLevelAccess.create(p_52241_, p_52242_));
	//      }, CONTAINER_TITLE);
	//}
}
