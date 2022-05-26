package com.timo.firstmod.core.init;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.item.BlockTeleportWand;
import com.timo.firstmod.common.item.GravityWand;
import com.timo.firstmod.common.item.MagicLamp;
import com.timo.firstmod.common.item.RocketLauncher;
import com.timo.firstmod.common.item.WalkieTalkie;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ItemInit {
	private ItemInit() {}
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FirstMod.MODID);
	
	//Items
	public static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test_item", () -> new Item( new Item.Properties().tab(FirstMod.FIRSTMOD_TAB).fireResistant().stacksTo(17) ) );
	public static final RegistryObject<Item> F = ITEMS.register("f", () -> new Item( new Item.Properties().stacksTo(420).tab(FirstMod.FIRSTMOD_TAB) ) );
	public static final RegistryObject<ForgeSpawnEggItem> SNOWMAN_CANNON_SPAWN_EGG = ITEMS.register("snowman_cannon_spawn_egg", () -> new ForgeSpawnEggItem(EntityInit.SNOWMAN_CANNON,0xf7f7f7,0xf78307,new Item.Properties().tab(FirstMod.FIRSTMOD_TAB).stacksTo(64)) );
	
	//Advanced Items
	public static final RegistryObject<MagicLamp> MAGIC_LAMP = ITEMS.register("magic_lamp", () -> new MagicLamp() );
	public static final RegistryObject<BlockTeleportWand> BLOCK_TP_WAND = ITEMS.register("block_tp_wand", () -> new BlockTeleportWand() );
	public static final RegistryObject<GravityWand> GRAVITY_WAND = ITEMS.register("gravity_wand", () -> new GravityWand() );
	public static final RegistryObject<RocketLauncher> ROCKET_LAUNCHER = ITEMS.register("rocket_launcher", () -> new RocketLauncher() );
	public static final RegistryObject<WalkieTalkie> WALKIE_TALKIE = ITEMS.register("walkie_talkie", () -> new WalkieTalkie() );
	
	//Block Items
	public static final RegistryObject<BlockItem> RGB_BLOCK_ITEM = ITEMS.register("rgb_block", () -> new BlockItem(BlockInit.RGB_BLOCK.get(), new BlockItem.Properties().stacksTo(64).tab(FirstMod.FIRSTMOD_TAB) ) );
	
	//Advanced Block Items
	public static final RegistryObject<BlockItem> TEST_BLOCK_ITEM = ITEMS.register("test_block", () -> new BlockItem(BlockInit.TEST_BLOCK.get(), new BlockItem.Properties().stacksTo(64).tab(FirstMod.FIRSTMOD_TAB) ) );
	public static final RegistryObject<BlockItem> LIGHTNING_BLOCK_ITEM = ITEMS.register("lightning_block", () -> new BlockItem(BlockInit.LIGHTNING_BLOCK.get(), new BlockItem.Properties().stacksTo(64).tab(FirstMod.FIRSTMOD_TAB) ) );
}
