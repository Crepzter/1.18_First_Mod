package com.timo.firstmod.core.init;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.recipe.LightningBlockRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeInit {
	private RecipeInit() {}
	
	public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.Keys.RECIPE_SERIALIZERS, FirstMod.MODID);
	
	public static final RegistryObject<LightningBlockRecipe.Serializer> LIGHTNING_BLOCK_RECIPE = RECIPES.register("craft_arrow_fletching", LightningBlockRecipe.Serializer::new);
}
