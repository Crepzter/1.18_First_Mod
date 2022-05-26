package com.timo.firstmod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.timo.firstmod.FirstMod;
import com.timo.firstmod.client.model.entity.DshinniModel;
import com.timo.firstmod.common.entity.Dshinni;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DshinniRenderer<Type extends Dshinni> extends MobRenderer<Type, DshinniModel<Type>> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(FirstMod.MODID, "textures/entities/dshinni.png");
	
	public DshinniRenderer(Context context) {
		super(context, new DshinniModel<>(context.bakeLayer(DshinniModel.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(Type entity) {
		return TEXTURE;
	}
	
	@Override
	public void render(Type p_115455_, float p_115456_, float p_115457_, PoseStack stack,
			MultiBufferSource p_115459_, int p_115460_) {
		stack.pushPose();
		stack.scale(2.5f, 2.5f, 2.5f);
		super.render(p_115455_, p_115456_, p_115457_, stack, p_115459_, p_115460_);
		stack.popPose();
	}

}
