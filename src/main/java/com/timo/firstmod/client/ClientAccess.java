package com.timo.firstmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class ClientAccess {
	public static boolean updateRocketLauncher() {
		return true;
	}
	
	@SuppressWarnings("resource")
	public static boolean rocketProjectileExplode(BlockPos pos) {
		Level level = Minecraft.getInstance().level;
		
		level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, 1F, false);
		
		level.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.getX(), pos.getY(), pos.getZ(), 1.0D, 0.0D, 0.0D);
		
		return true;
	}
}
