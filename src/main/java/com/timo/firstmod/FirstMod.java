package com.timo.firstmod;

import com.timo.firstmod.config.FirstModCommonConfigs;
import com.timo.firstmod.core.init.BlockEntityInit;
import com.timo.firstmod.core.init.BlockInit;
import com.timo.firstmod.core.init.EntityInit;
import com.timo.firstmod.core.init.ItemInit;
import com.timo.firstmod.core.init.LootModifierInit;
import com.timo.firstmod.core.init.MenuInit;
import com.timo.firstmod.core.init.RecipeInit;

import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib3.GeckoLib;

@Mod(FirstMod.MODID)
public class FirstMod {
	public static final String MODID = "firstmod";
	public static boolean DISABLE_IN_DEV = false;
	
	public static final CreativeModeTab FIRSTMOD_TAB = new CreativeModeTab(CreativeModeTab.getGroupCountSafe(), "firstmod_tab") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemInit.F.get());
		}	
	};
	
	public FirstMod() {
		GeckoLib.initialize();
		
		MinecraftForge.EVENT_BUS.register(this);
		
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		BlockInit.BLOCKS.register(bus);
		BlockEntityInit.BLOCK_ENTITIES.register(bus);
		ItemInit.ITEMS.register(bus);
		EntityInit.ENTITIES.register(bus);
		LootModifierInit.GLM.register(bus);
		RecipeInit.RECIPES.register(bus);
		MenuInit.MENUS.register(bus);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FirstModCommonConfigs.SPEC, "firstmod-common.toml");
	}
	
	//Voxel Shapes 
	public static VoxelShape calculateShapes(Direction to, VoxelShape shape) {
        final VoxelShape[] buffer = { shape, Shapes.empty() };

        final int times = (to.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
                    Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }
	/*
    @Nonnull
    public Block retreiveBlock(ResourceLocation name) {
        final Optional<Block> block = ForgeRegistries.BLOCKS.getEntries().stream()
                .filter(entry -> entry.getKey().getRegistryName().equals(name)).map(java.util.Map.Entry::getValue)
                .findFirst();
        return block.orElse(Blocks.AIR);
    }*/
}
