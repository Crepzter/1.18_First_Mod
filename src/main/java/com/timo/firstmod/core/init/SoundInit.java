package com.timo.firstmod.core.init;

import com.timo.firstmod.FirstMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundInit {
	public SoundInit() {}
	
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FirstMod.MODID);

	//Sounds
	public static RegistryObject<SoundEvent> HEAVY_EXPLOSION = SOUNDS.register( "heavy_explosion", 
			() -> new SoundEvent(new ResourceLocation(FirstMod.MODID, "heavy_explosion")) );
	
	public static RegistryObject<SoundEvent> ROCKET_LAUNCHER_TARGET_LOCKED = SOUNDS.register( "rocket_launcher_target_locked", 
			() -> new SoundEvent(new ResourceLocation(FirstMod.MODID, "rocket_launcher_target_locked")) );
	
	public static RegistryObject<SoundEvent> SMALL_MISSILE_FLYING = SOUNDS.register( "small_missile_flying", 
			() -> new SoundEvent(new ResourceLocation(FirstMod.MODID, "small_missile_flying")) );
	
}