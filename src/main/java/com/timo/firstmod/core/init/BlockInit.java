package com.timo.firstmod.core.init;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.block.LightningBlock;
import com.timo.firstmod.common.block.TestBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BlockInit {
	private BlockInit() {}
	
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FirstMod.MODID);
	
	//Blocks
	public static final RegistryObject<Block> RGB_BLOCK = BLOCKS.register("rgb_block",() -> new Block( BlockBehaviour.Properties.copy(Blocks.GLOWSTONE).requiresCorrectToolForDrops() ) );
	
	//Advanced Blocks
	public static final RegistryObject<TestBlock> TEST_BLOCK = BLOCKS.register("test_block",() -> 
		new TestBlock( BlockBehaviour.Properties.of(Material.METAL, MaterialColor.TERRACOTTA_LIGHT_BLUE).strength(8.0f, 30f) ) );
	
	public static final RegistryObject<LightningBlock> LIGHTNING_BLOCK = BLOCKS.register("lightning_block",() -> 
		new LightningBlock( BlockBehaviour.Properties.of(Material.METAL, MaterialColor.TERRACOTTA_LIGHT_BLUE).strength(10.0f, 25f).noOcclusion() ) ); 
}
