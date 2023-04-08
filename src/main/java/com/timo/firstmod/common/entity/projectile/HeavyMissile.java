package com.timo.firstmod.common.entity.projectile;

import java.util.List;
import java.util.Map;

import com.timo.firstmod.core.init.SoundInit;
import com.timo.firstmod.utils.ExplosionUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HeavyMissile extends ThrowableProjectile implements IAnimatable {
	// Data
	public static double SPEED = 2.5d;
	
	private int blocksHit = 0;
	private Vec3 lastSpeed = new Vec3(0,0,0);
	private LivingEntity shooter = null;
	
	// List of Blocks the Missile will fly through
	private static final List<Block> WEAK_BLOCKS = List.of(
		Blocks.SNOW_BLOCK, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW,
		Blocks.ICE, Blocks.FROSTED_ICE,
		Blocks.ACACIA_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES,
		Blocks.GRAVEL
	);

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
	public void explode(Level level, BlockPos pos) {
		if(!level.isClientSide()) {
			level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundInit.HEAVY_EXPLOSION.get(), SoundSource.BLOCKS, 8.0F, 1.0F);
		}
		if(!level.isClientSide()) {
			ExplosionUtils e = new ExplosionUtils(level, pos, this, shooter, 50, 0.25f);
			e.explode();
		}
		level.gameEvent(this, GameEvent.RING_BELL, pos);
		discard();
	}
	
	// Other
	public void setShotBy(LivingEntity shooter) {
		this.shooter = shooter;
	}
	
	@Override
	protected void onHitBlock(BlockHitResult result) {
		BlockPos pos  = result.getBlockPos();
		Block block = level.getBlockState(result.getBlockPos()).getBlock();
		
		if(WEAK_BLOCKS.contains(block)) {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		} else {
			if(blocksHit < 2) { level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()); blocksHit++; }
			else explode(level,result.getBlockPos().above().above());
		}
	}
	
	@Override
	protected void onHitEntity(EntityHitResult p_37259_) {
		super.onHitEntity(p_37259_);
	}

	// geckolib
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
	
	// other stuff
	
	public Vec3 getNullPos() {
		return new Vec3(this.getX(),this.getY(),this.getZ());
	}
	
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	@Override
	protected void defineSynchedData() {
		
	}
	
	@Override
	public boolean isNoGravity() {
		return true;
	}
}
