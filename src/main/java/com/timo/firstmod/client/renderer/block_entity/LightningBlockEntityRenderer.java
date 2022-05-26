package com.timo.firstmod.client.renderer.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.timo.firstmod.client.model.block_entity.LightningBlockEntityModel;
import com.timo.firstmod.common.block.entity.LightningBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class LightningBlockEntityRenderer extends GeoBlockRenderer<LightningBlockEntity> {
	public LightningBlockEntityRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn,  new LightningBlockEntityModel());
    }

	@Override
	public RenderType getRenderType(LightningBlockEntity animatable, float partialTicks, PoseStack stack,
			MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.entitySolid(getTextureLocation(animatable));
	}
}