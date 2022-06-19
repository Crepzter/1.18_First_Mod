package com.timo.firstmod.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

public class WeaponsProjectile extends ThrowableProjectile {

	public WeaponsProjectile(EntityType<? extends ThrowableProjectile> entity, Level level) {
		super(entity, level);
	}

	@Override
	protected void defineSynchedData() {
		
	}

}
