package com.timo.firstmod.client.renderer.entity;

import com.timo.firstmod.client.model.entity.RifleBulletModel;
import com.timo.firstmod.common.entity.projectile.bullet.RifleBullet;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class RifleBulletRenderer extends GeoProjectilesRenderer<RifleBullet> {

	public RifleBulletRenderer(Context renderManager) {
		super(renderManager, new RifleBulletModel());
		this.shadowRadius = 0f;
	}
}
