package com.timo.firstmod.core.init;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.event.loot.ColoredWoolFromJebAdditionModifier;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LootModifierInit {
	private LootModifierInit() {}
	
	public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, FirstMod.MODID);
	
	public static final RegistryObject<ColoredWoolFromJebAdditionModifier.Serializer> WOOL_JEB = GLM.register("colored_wool_from_jeb", ColoredWoolFromJebAdditionModifier.Serializer::new);
}
