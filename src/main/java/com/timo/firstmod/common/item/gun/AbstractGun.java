package com.timo.firstmod.common.item.gun;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.item.ammo.AbstractAmmo;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AbstractGun extends Item {
	
	public AbstractGun(Properties p_41383_) {
		super(new Properties().tab(FirstMod.FIRSTMOD_TAB).stacksTo(1));
	}
	
	public abstract AbstractAmmo getAmmo();
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
		// TODO Auto-generated method stub
		return super.use(p_41432_, p_41433_, p_41434_);
	}

}
