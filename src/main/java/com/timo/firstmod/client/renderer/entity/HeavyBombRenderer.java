package com.timo.firstmod.client.renderer.entity;

import com.timo.firstmod.client.model.entity.HeavyBombModel;
import com.timo.firstmod.common.entity.projectile.HeavyBomb;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class HeavyBombRenderer extends GeoProjectilesRenderer<HeavyBomb> {

	public HeavyBombRenderer(Context renderManager) {
		super(renderManager, new HeavyBombModel());
		this.shadowRadius = 0f;
	}
}
