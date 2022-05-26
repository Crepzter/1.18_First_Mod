package com.timo.firstmod.common.entity;

import com.timo.firstmod.utils.ExplosionUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
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
	private int blocksHit = 0;

	public HeavyMissile(EntityType<? extends ThrowableProjectile> entity, Level level) {
		super(entity, level);
	}

	@Override
	public void tick() {
		super.tick();
		
		//Particle Spawning Client Side
		if(level.isClientSide()) {
			Vec3 pos = this.getNullPos().add(this.getDeltaMovement().normalize().multiply(-0.45, -0.45, -0.45));
			Vec3 mov = this.getDeltaMovement().normalize().multiply(-0.4,-0.4,-0.4);
			if (this.isInWater()) {
				if(random.nextDouble() < 0.6) level.addParticle(ParticleTypes.BUBBLE, pos.x, pos.y, pos.z, mov.x, mov.y, mov.z);
	        } else {
	        	if(random.nextDouble() < 0.6) level.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, mov.x, mov.y, mov.z);
	        }
	    }
	}
	
	public void explode(Level level, BlockPos pos) {
		ExplosionUtils.hBombExplode(level, pos, this);
		this.discard();
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
