package com.timo.firstmod.client.model.entity;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.projectile.bullet.RifleBullet;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RifleBulletModel extends AnimatedGeoModel<RifleBullet> {

	@Override
	public ResourceLocation getAnimationFileLocation(RifleBullet object) {
		return new ResourceLocation(FirstMod.MODID, "animations/entities/projectiles/bullets/rifle_bullet.json");
	}

	@Override
	public ResourceLocation getModelLocation(RifleBullet object) {
		return new ResourceLocation(FirstMod.MODID, "geo/entities/projectiles/bullets/rifle_bullet.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(RifleBullet object) {
		return new ResourceLocation(FirstMod.MODID, "textures/entities/projectiles/bullets/rifle_bullet.png");
	}	
}