package com.timo.firstmod.client.model.entity;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.projectile.HeavyBomb;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HeavyBombModel extends AnimatedGeoModel<HeavyBomb> {

	@Override
	public ResourceLocation getAnimationFileLocation(HeavyBomb object) {
		return new ResourceLocation(FirstMod.MODID, "animations/entities/projectiles/heavy_bomb.json");
	}

	@Override
	public ResourceLocation getModelLocation(HeavyBomb object) {
		return new ResourceLocation(FirstMod.MODID, "geo/entities/projectiles/heavy_bomb.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(HeavyBomb object) {
		return new ResourceLocation(FirstMod.MODID, "textures/entities/projectiles/heavy_bomb.png");
	}	
}