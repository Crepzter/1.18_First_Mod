package com.timo.firstmod.common.entity.projectile;

import com.timo.firstmod.utils.ExplosionUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
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
	public static double SPEED = 2.5d;
	
	private int blocksHit = 0;

	public HeavyMissile(EntityType<? extends ThrowableProjectile> entity, Level level) {
		super(entity, level);
	}

	@Override
	public void tick() {
		super.tick();
		
		//Particle Spawning Client Side
		if(level.isClientSide()) {
			Vec3 pos = getNullPos().add(getDeltaMovement().normalize().multiply(-0.75, -0.75, -0.75));
			Vec3 mov = getDeltaMovement().normalize().multiply(-0.4,-0.4,-0.4);
			if (isInWater()) {
				if(random.nextDouble() < 0.7) level.addParticle(ParticleTypes.BUBBLE, pos.x, pos.y, pos.z, mov.x, mov.y, mov.z);
	        } else {
	        	if(random.nextDouble() < 0.7) level.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, mov.x, mov.y, mov.z);
	        }
	    }
		
		//Acceleration
		setDeltaMovement(getDeltaMovement().multiply(1.01d,1.01d,1.01d));
	}
	
	public void explode(Level level, BlockPos pos) {
		hitEffect(pos);
		ExplosionUtils.hBombExplode(level, pos, this);
		discard();
	}
	
	@Override
	protected void onHitBlock(BlockHitResult result) {
		if(level.isClientSide()) return;
		//fly through dirt, leaves...
		BlockPos pos  = result.getBlockPos();
		BlockState state = level.getBlockState(result.getBlockPos());
		Block block = level.getBlockState(result.getBlockPos()).getBlock();
		if(state.isSolidRender(level, pos)) {
			if(block != Blocks.BEDROCK) {
				if(blocksHit > 2) explode(level,result.getBlockPos());
				else {
					level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
					blocksHit++;
				}
			} else {
				explode(level,result.getBlockPos());
			}
		} else {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}
	
	public void hitEffect(BlockPos pos) {
		int radius = 25;
		int duration = 20;
		
		AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
		Entity entity = this.getOwner();
		if (entity instanceof LivingEntity) {
		   areaeffectcloud.setOwner((LivingEntity)entity);
		}
		areaeffectcloud.setParticle(ParticleTypes.EXPLOSION);
		areaeffectcloud.setRadius(radius);
		areaeffectcloud.setDuration(duration);
		areaeffectcloud.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 160, 1));
		areaeffectcloud.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 1));
		
		areaeffectcloud.setPos(pos.getX(), pos.getY(), pos.getZ());
		
		this.level.addFreshEntity(areaeffectcloud);
	}
	
	@Override
	protected void onHitEntity(EntityHitResult p_37259_) {
		super.onHitEntity(p_37259_);
	}

	//geckolib
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
	
	//other stuff
	
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
