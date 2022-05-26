package com.timo.firstmod.client.model.item;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.item.RocketLauncher;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RocketLauncherModel extends AnimatedGeoModel<RocketLauncher> {

	@Override
	public ResourceLocation getAnimationFileLocation(RocketLauncher animatable) {
		return new ResourceLocation(FirstMod.MODID, "animations/items/rocket_launcher.json");
	}

	@Override
	public ResourceLocation getModelLocation(RocketLauncher object) {
		return new ResourceLocation(FirstMod.MODID, "geo/items/rocket_launcher.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(RocketLauncher object) {
		return new ResourceLocation(FirstMod.MODID, "textures/items/rocket_launcher.png");
	}

}
