package com.timo.firstmod.core.init;

import com.timo.firstmod.FirstMod;

import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StructureInit {
private StructureInit() {}
	
	public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, FirstMod.MODID);
	
	//Structures
	//public static final RegistryObject<StructureFeature<JigsawConfiguration>> RGB_BLOCK = STRUCTURES.register("old_shed",() ->  );
}
