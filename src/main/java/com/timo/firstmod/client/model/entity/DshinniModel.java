package com.timo.firstmod.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.Dshinni;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class DshinniModel<Type extends Dshinni> extends EntityModel<Type> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(FirstMod.MODID, "dshinni"), "main");
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart hitbox;

	public DshinniModel(ModelPart root) {
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.hitbox = root.getChild("hitbox");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(29, 7).addBox(-3.0F, -8.0F, -1.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 22).addBox(-4.0F, -6.0F, -2.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(16, 24).addBox(-3.0F, -5.0F, -3.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(28, 22).addBox(-3.0F, -3.0F, -3.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(29, 0).addBox(-2.0F, -1.0F, -6.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		partdefinition.addOrReplaceChild("hitbox", CubeListBuilder.create().texOffs(0, 42).addBox(-5.0F, -9.0F, -7.0F, 8.0F, 9.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Type entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, buffer, packedLight, packedOverlay);
		body.render(poseStack, buffer, packedLight, packedOverlay);
		hitbox.render(poseStack, buffer, packedLight, packedOverlay);
	}
}