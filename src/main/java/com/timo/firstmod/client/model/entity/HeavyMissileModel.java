package com.timo.firstmod.client.model.entity;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.HeavyMissile;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HeavyMissileModel extends AnimatedGeoModel<HeavyMissile> {

	@Override
	public ResourceLocation getAnimationFileLocation(HeavyMissile object) {
		return new ResourceLocation(FirstMod.MODID, "animations/entities/heavy_missile.json");
	}

	@Override
	public ResourceLocation getModelLocation(HeavyMissile object) {
		return new ResourceLocation(FirstMod.MODID, "geo/entities/heavy_missile.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(HeavyMissile object) {
		return new ResourceLocation(FirstMod.MODID, "textures/entities/heavy_missile.png");
	}	
}