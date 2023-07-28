package com.timo.firstmod.common.entity.projectile;

import com.timo.firstmod.core.init.SoundInit;
import com.timo.firstmod.utils.ExplosionUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HeavyMissile extends AbstractAirProjectile { //ThrowableProjectile implements IAnimatable {
	// Data
	public static double SPEED = 2.5d;
	private Vec3 lastSpeed = new Vec3(0,0,0);
	
	// Constructor
	public HeavyMissile(EntityType<? extends ThrowableProjectile> entity, Level level) {
		super(entity, level);
	}

	// Tick
	@Override
	public void tick() {
		super.tick();
		
		// Particle Spawning Client Side
		if(level.isClientSide()) {
			Vec3 pos = getNullPos().add(getDeltaMovement().normalize().multiply(-0.75, -0.75, -0.75));
			Vec3 mov = getDeltaMovement().normalize().multiply(-0.4,-0.4,-0.4);
			if (isInWater()) {
				if(random.nextDouble() < 0.7) level.addParticle(ParticleTypes.BUBBLE, pos.x, pos.y, pos.z, mov.x, mov.y, mov.z);
	        } else {
	        	if(random.nextDouble() < 0.7) level.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, mov.x, mov.y, mov.z);
	        }
	    }
		
		// Acceleration
		if(!isInWater()) {
			lastSpeed = getDeltaMovement();
			setDeltaMovement(getDeltaMovement().multiply(1.01d,1.01d,1.01d));
		} else {
			if(getDeltaMovement().lengthSqr() < lastSpeed.multiply(0.7,0.7,0.7).lengthSqr()) {
				setDeltaMovement(lastSpeed.multiply(0.7,0.7,0.7));
			} else {
				setDeltaMovement(getDeltaMovement().multiply(1.4d,1.4d,1.4d));
			}
		}
	}
	
	// Explode
	@Override
	public void explode(BlockPos pos) {
		if(!level.isClientSide()) {
			level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundInit.HEAVY_EXPLOSION.get(), SoundSource.BLOCKS, 8.0F, 1.0F);
		}
		if(!level.isClientSide()) {
			ExplosionUtils e = new ExplosionUtils(level, pos, this, shooter, 55, 0.10f);
			e.explode();
		}
		level.gameEvent(this, GameEvent.RING_BELL, pos);
		discard();
	}
	
	// Other
	@Override
	public boolean destroysBlocks() {
		return true;
	}

	// Geckolib
	private final AnimationFactory factory = new AnimationFactory(this);
		
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<HeavyMissile>(this, "empty", 0, this::empty));
	}
	
	private <E extends IAnimatable> PlayState empty(AnimationEvent<E> event) {
		return PlayState.CONTINUE;
	}

	@Override
	public AnimationFactory getFactory() {
		return factory;
	}
}
