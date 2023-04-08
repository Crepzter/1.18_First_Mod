package com.timo.firstmod.common.item;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.utils.ExplosionUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class GravityWand extends Item {

	public GravityWand() {
		super(new Item.Properties().tab(FirstMod.FIRSTMOD_TAB).stacksTo(143));
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		Level level = player.level;
		BlockPos hitPos = context.getClickedPos();
		
		if(level.isClientSide()) {
			level.setBlock(hitPos, Blocks.RED_CONCRETE.defaultBlockState(), 4);
		}

		return InteractionResult.SUCCESS;
	}
}
