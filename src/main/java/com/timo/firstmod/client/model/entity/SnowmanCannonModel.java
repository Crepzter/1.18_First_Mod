package com.timo.firstmod.client.model.entity;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.SnowmanCannon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class SnowmanCannonModel extends AnimatedGeoModel<SnowmanCannon> {

	@Override
	public ResourceLocation getAnimationFileLocation(SnowmanCannon object) {
		return new ResourceLocation(FirstMod.MODID, "animations/entities/snowman_cannon.json");
	}

	@Override
	public ResourceLocation getModelLocation(SnowmanCannon object) {
		return new ResourceLocation(FirstMod.MODID, "geo/entities/snowman_cannon.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(SnowmanCannon object) {
		return new ResourceLocation(FirstMod.MODID, "textures/entities/snowman_cannon.png");
	}
	
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	@Override
	public void setLivingAnimations(SnowmanCannon entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("head");
		
		LivingEntity entityIn = (LivingEntity) entity;
		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
		head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
	}
}
