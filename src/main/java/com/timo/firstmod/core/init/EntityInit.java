package com.timo.firstmod.core.init;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.Dshinni;
import com.timo.firstmod.common.entity.SnowmanCannon;
import com.timo.firstmod.common.entity.projectile.HeavyBomb;
import com.timo.firstmod.common.entity.projectile.HeavyMissile;
import com.timo.firstmod.common.entity.projectile.SmallMissile;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
	
	private EntityInit() {}
	
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, FirstMod.MODID);
	
	//mobs
	public static final RegistryObject<EntityType<SnowmanCannon>> SNOWMAN_CANNON = ENTITIES.register("snowman_cannon", () -> EntityType.Builder.of(SnowmanCannon::new, MobCategory.CREATURE)
			.sized(1f, 2f)
			.build( new ResourceLocation(FirstMod.MODID,"snowman_cannon").toString() )  );
	
	public static final RegistryObject<EntityType<Dshinni>> DSHINNI = ENTITIES.register("dshinni", () -> EntityType.Builder.of(Dshinni::new, MobCategory.CREATURE)
			.sized(2f, 2f)
			.build( new ResourceLocation(FirstMod.MODID,"dshinni").toString() )  );
	
	//projectiles
	public static final RegistryObject<EntityType<SmallMissile>> SMALL_MISSILE = ENTITIES.register("small_missile", () -> EntityType.Builder.of(SmallMissile::new, MobCategory.MISC)
			.sized(0.7f, 0.7f)
			.setUpdateInterval(1)
			.build( new ResourceLocation(FirstMod.MODID,"small_missile").toString() )  );
	
	public static final RegistryObject<EntityType<HeavyMissile>> HEAVY_MISSILE = ENTITIES.register("heavy_missile", () -> EntityType.Builder.of(HeavyMissile::new, MobCategory.MISC)
			.sized(0.7f, 0.7f)
			.setUpdateInterval(1)
			.build( new ResourceLocation(FirstMod.MODID,"heavy_missile").toString() )  );
	
	public static final RegistryObject<EntityType<HeavyBomb>> HEAVY_BOMB = ENTITIES.register("heavy_bomb", () -> EntityType.Builder.of(HeavyBomb::new, MobCategory.MISC)
			.sized(0.7f, 0.7f)
			.setUpdateInterval(1)
			.build( new ResourceLocation(FirstMod.MODID,"heavy_bomb").toString() )  );

}
