package com.timo.firstmod.client.renderer.entity;

import com.timo.firstmod.client.model.entity.SnowmanCannonModel;
import com.timo.firstmod.common.entity.SnowmanCannon;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class SnowmanCannonRenderer extends GeoEntityRenderer<SnowmanCannon> {
	
	public SnowmanCannonRenderer(Context renderManager) {
		super(renderManager, new SnowmanCannonModel());
		this.shadowRadius = 0.7f;
	}
	
}
