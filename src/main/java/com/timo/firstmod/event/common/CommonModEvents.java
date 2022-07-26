package com.timo.firstmod.event.common;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.Dshinni;
import com.timo.firstmod.common.entity.SnowmanCannon;
import com.timo.firstmod.core.init.EntityInit;
import com.timo.firstmod.core.init.PacketHandler;
import com.timo.firstmod.recipe.LightningBlockRecipe;

import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = FirstMod.MODID, bus = Bus.MOD)
public class CommonModEvents {
	
	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(PacketHandler::init);
	}
	
	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put( EntityInit.DSHINNI.get(), Dshinni.createAttributes().build() );
		event.put( EntityInit.SNOWMAN_CANNON.get(), SnowmanCannon.createAttributes().build() );
	}
	
	@SubscribeEvent
    public static void registerRecipeTypes(final RegistryEvent.Register<RecipeSerializer<?>> event) {
        Registry.register(Registry.RECIPE_TYPE, LightningBlockRecipe.Type.ID, LightningBlockRecipe.Type.INSTANCE);
    }
}
