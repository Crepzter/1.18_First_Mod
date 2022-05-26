package com.timo.firstmod.core.init;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.block.entity.LightningBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityInit {

	private BlockEntityInit() {}
	
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, FirstMod.MODID);
	
	public static final RegistryObject<BlockEntityType<LightningBlockEntity>> LIGHTNING_BLOCK = BLOCK_ENTITIES.register("lightning_block", () -> 
		BlockEntityType.Builder.of(LightningBlockEntity::new, BlockInit.LIGHTNING_BLOCK.get()).build(null) );
}
