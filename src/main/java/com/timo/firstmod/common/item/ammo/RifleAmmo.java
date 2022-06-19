package com.timo.firstmod.common.item.ammo;

import com.timo.firstmod.FirstMod;

import net.minecraft.world.item.Item;

public class RifleAmmo extends Item {

	public RifleAmmo() {
		super(new Properties().tab(FirstMod.FIRSTMOD_TAB).stacksTo(64));
	}
}
