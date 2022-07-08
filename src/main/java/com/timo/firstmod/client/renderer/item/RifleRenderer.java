package com.timo.firstmod.client.renderer.item;

import com.timo.firstmod.client.model.item.RifleModel;
import com.timo.firstmod.common.item.gun.Rifle;

import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class RifleRenderer extends GeoItemRenderer<Rifle> {

	public RifleRenderer() {
		super(new RifleModel());
	}
}
