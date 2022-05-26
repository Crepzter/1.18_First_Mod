package com.timo.firstmod.client.model.block_entity;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.block.entity.LightningBlockEntity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LightningBlockEntityModel extends AnimatedGeoModel<LightningBlockEntity> {
	@Override
	public ResourceLocation getAnimationFileLocation(LightningBlockEntity object) {
		return new ResourceLocation(FirstMod.MODID, "animations/block_entities/lightning_block.json");
	}

	@Override
	public ResourceLocation getModelLocation(LightningBlockEntity object) {
		return new ResourceLocation(FirstMod.MODID, "geo/block_entities/lightning_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(LightningBlockEntity object) {
		return new ResourceLocation(FirstMod.MODID, "textures/block_entities/lightning_block.png");
	}
}
