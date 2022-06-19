package com.timo.firstmod.client.renderer.entity;

import com.timo.firstmod.client.model.entity.SmallMissileModel;
import com.timo.firstmod.common.entity.projectile.SmallMissile;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class SmallMissileRenderer extends GeoProjectilesRenderer<SmallMissile> {

	public SmallMissileRenderer(Context renderManager) {
		super(renderManager, new SmallMissileModel());
		this.shadowRadius = 0f;
	}
}
