package com.timo.firstmod.common.item;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.utils.ExplosionUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class GravityWand extends Item {

	public GravityWand() {
		super(new Item.Properties().tab(FirstMod.FIRSTMOD_TAB).stacksTo(143));
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		//var player = context.getPlayer();
		//var level = player.level;
		BlockPos hitPos = context.getClickedPos();
		
		int r = 6;
		int strength = 3;
		
		ExplosionUtils e = new ExplosionUtils(context.getLevel(), hitPos, r, strength);
		e.sMissileExplode(false, 0, context.getPlayer(), context.getPlayer());
		return InteractionResult.SUCCESS;
	}
}
