package com.timo.firstmod.common.entity.projectile.bullet;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

public class RifleBullet extends AbstractBulletProjectile {

	public RifleBullet(EntityType<? extends ThrowableProjectile> entity, Level level) {
		super(entity, level);
	}

}
