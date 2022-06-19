package com.timo.firstmod.client.renderer.entity;

import com.timo.firstmod.client.model.entity.HeavyMissileModel;
import com.timo.firstmod.common.entity.projectile.HeavyMissile;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class HeavyMissileRenderer extends GeoProjectilesRenderer<HeavyMissile> {

	public HeavyMissileRenderer(Context renderManager) {
		super(renderManager, new HeavyMissileModel());
		this.shadowRadius = 0f;
	}
}
