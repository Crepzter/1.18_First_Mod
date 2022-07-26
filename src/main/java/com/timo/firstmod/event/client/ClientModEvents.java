package com.timo.firstmod.event.client;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.client.model.entity.DshinniModel;
import com.timo.firstmod.client.renderer.block_entity.LightningBlockEntityRenderer;
import com.timo.firstmod.client.renderer.entity.DshinniRenderer;
import com.timo.firstmod.client.renderer.entity.HeavyBombRenderer;
import com.timo.firstmod.client.renderer.entity.HeavyMissileRenderer;
import com.timo.firstmod.client.renderer.entity.RifleBulletRenderer;
import com.timo.firstmod.client.renderer.entity.SmallMissileRenderer;
import com.timo.firstmod.client.renderer.entity.SnowmanCannonRenderer;
import com.timo.firstmod.core.init.BlockEntityInit;
import com.timo.firstmod.core.init.BlockInit;
import com.timo.firstmod.core.init.EntityInit;
import com.timo.firstmod.core.init.MenuInit;
import com.timo.firstmod.screen.LightningBlockScreen;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = FirstMod.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
	
	private ClientModEvents() {}
	
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(BlockInit.TEST_BLOCK.get(), RenderType.cutout()); //wenn löcher int textur
		
		MenuScreens.register(MenuInit.LIGHTNING_BLOCK_MENU.get(), LightningBlockScreen::new);
	}
	
	@SubscribeEvent
	public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(DshinniModel.LAYER_LOCATION, DshinniModel::createBodyLayer);
	}
	
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityInit.DSHINNI.get(), DshinniRenderer::new);
		event.registerEntityRenderer(EntityInit.SNOWMAN_CANNON.get(), SnowmanCannonRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityInit.LIGHTNING_BLOCK.get(), LightningBlockEntityRenderer::new);
		
		event.registerEntityRenderer(EntityInit.SMALL_MISSILE.get(), SmallMissileRenderer::new);
		event.registerEntityRenderer(EntityInit.HEAVY_MISSILE.get(), HeavyMissileRenderer::new);
		event.registerEntityRenderer(EntityInit.HEAVY_BOMB.get(), HeavyBombRenderer::new);
		
		event.registerEntityRenderer(EntityInit.RIFLE_BULLET.get(), RifleBulletRenderer::new);
	}
}
