package com.timo.firstmod.client.renderer.item;

import com.timo.firstmod.client.model.item.RocketLauncherModel;
import com.timo.firstmod.common.item.RocketLauncher;

import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class RocketLauncherRenderer extends GeoItemRenderer<RocketLauncher> {

	public RocketLauncherRenderer() {
		super(new RocketLauncherModel());
	}
}
