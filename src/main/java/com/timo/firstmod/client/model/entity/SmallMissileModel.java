package com.timo.firstmod.client.model.entity;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.projectile.SmallMissile;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SmallMissileModel extends AnimatedGeoModel<SmallMissile> {

	@Override
	public ResourceLocation getAnimationFileLocation(SmallMissile object) {
		return new ResourceLocation(FirstMod.MODID, "animations/entities/projectiles/small_missile.json");
	}

	@Override
	public ResourceLocation getModelLocation(SmallMissile object) {
		return new ResourceLocation(FirstMod.MODID, "geo/entities/projectiles/small_missile.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(SmallMissile object) {
		return new ResourceLocation(FirstMod.MODID, "textures/entities/projectiles/small_missile.png");
	}	
}