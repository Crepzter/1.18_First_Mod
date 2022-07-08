package com.timo.firstmod.client.model.item;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.item.gun.Rifle;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RifleModel extends AnimatedGeoModel<Rifle> {

	@Override
	public ResourceLocation getAnimationFileLocation(Rifle animatable) {
		return new ResourceLocation(FirstMod.MODID, "animations/items/rifle.json");
	}

	@Override
	public ResourceLocation getModelLocation(Rifle object) {
		return new ResourceLocation(FirstMod.MODID, "geo/items/rifle.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Rifle object) {
		return new ResourceLocation(FirstMod.MODID, "textures/items/rifle.png");
	}

}
