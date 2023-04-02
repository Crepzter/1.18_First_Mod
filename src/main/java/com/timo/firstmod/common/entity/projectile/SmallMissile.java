package com.timo.firstmod.common.entity.projectile;

import java.util.UUID;

import javax.annotation.Nullable;

import com.timo.firstmod.core.init.PacketHandler;
import com.timo.firstmod.core.init.SoundInit;
import com.timo.firstmod.core.network.ClientBoundRocketProjectileExplosionPacket;
import com.timo.firstmod.utils.ExplosionUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class SmallMissile extends ThrowableProjectile implements IAnimatable {
	private static final double SPEED = 0.625D;
	private static final int EXPLOSION_RADIUS = 3;
	private static final int EXPLOSION_STRENGTH = 2;
	private static final int EXPLOSION_DAMAGE = 12;
	
	//target stuff
	private static final EntityDataAccessor<Float> TARGET_X = SynchedEntityData.defineId(SmallMissile.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> TARGET_Y = SynchedEntityData.defineId(SmallMissile.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> TARGET_Z = SynchedEntityData.defineId(SmallMissile.class, EntityDataSerializers.FLOAT);
	
	@Nullable
	private UUID targetId;
	@Nullable
	private Vec3 targetB;
	
	private LivingEntity shotBy;
	
	private int delay = 20;
	
	public SmallMissile(EntityType<? extends ThrowableProjectile> entity, Level level) {
		super(entity, level);
		this.setNoGravity(true);
	}
	
	@Override
	public void tick() {
		super.tick();		
		//Movement Calculation
		Vec3 target = this.getTargetPos();
		if(target != null) {
			Vec3 dir = target.subtract(this.getNullPos()).normalize().multiply(SPEED,SPEED,SPEED);
			this.setDeltaMovement(dir);
		} else if(delay < 0) {
			this.explode(new BlockPos(this.getNullPos()));
		}
		if(delay > -1) delay--;
		//remove when target reached
		if(target != null && target.subtract(this.getNullPos()).lengthSqr() < 0.2) {
			this.explode(new BlockPos(this.getNullPos()));
		}
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
		//Update and sync Target Pos, get Entity from uuid
		if(!level.isClientSide()) {
			//level.playSound(null, this, SoundInit.SMALL_MISSILE_FLYING.get(), SoundSource.NEUTRAL, 2.0F, 1.0F);
			updateTargetPos(((ServerLevel)level).getEntity(targetId));
		}
	}
	
	@Override
	protected void updateRotation() {
		if(!level.isClientSide()) {
			Vec3 vec3 = this.getDeltaMovement();
			double d0 = vec3.horizontalDistance();
			
			setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI))));
			setYRot(lerpRotation(this.yRotO, (float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI))));
		}
	}
	
	public void firstRotation(Vec3 vec3) {
		double d0 = vec3.horizontalDistance();
		
		//this.setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI))));
		//this.setYRot(lerpRotation(this.yRotO, (float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI))));
		
		xRotO = (float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI));
		yRotO = (float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI));
		setXRot(xRotO);
		setYRot(yRotO);
	}
	
	public void explode(BlockPos pos) {
		if(!level.isClientSide() && shotBy != null) {
			//world damage
			ExplosionUtils e = new ExplosionUtils(level, pos, EXPLOSION_RADIUS, EXPLOSION_STRENGTH);
			e.sMissileExplode(true, EXPLOSION_DAMAGE, this, shotBy);
			//ExplosionUtils e = new ExplosionUtils(level, pos, 30, EXPLOSION_STRENGTH);
			//e.tExplode3();

			//particles and sound --> Client Side
			PacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)),
										new ClientBoundRocketProjectileExplosionPacket(pos));
		}
		this.discard();
	}
	
	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		this.explode(new BlockPos(result.getLocation()));
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		this.explode(new BlockPos(result.getLocation()));
	}
	
	@Override
	protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
		this.playSound(SoundInit.SMALL_MISSILE_FLYING.get(), 0.15F, 1.0F);
	}
	
	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.hasUUID("TargetEId")) {
	       targetId = tag.getUUID("TargetEId");
	    } else if(tag.contains("TargetVX")) {
	    	targetB = new Vec3(tag.getDouble("TargetVX"),
	    					   tag.getDouble("TargetVY"),
	    					   tag.getDouble("TargetVZ"));
	    }
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
	    if (targetId != null) {
	    	tag.putUUID("TargetEId", targetId);
	    } else if(targetB != null) {
	    	tag.putDouble("TargetVX", targetB.x);
	    	tag.putDouble("TargetVY", targetB.y);
	    	tag.putDouble("TargetVZ", targetB.z);
	    }
	}
	
	//getter & setter
	public void setRotation(float xRot, float yRot) {
		this.setXRot(xRot);
		this.xRotO = xRot;
		this.setYRot(xRot);
		this.yRotO = yRot;
	}
	
	public void setTarget(BlockPos pos) {
		targetB = new Vec3(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
	}
	
	public void setTarget(UUID targetId) {
		targetB = null;
		this.targetId = targetId;
	}
	
	public void setShotBy(LivingEntity shotBy) {
		this.shotBy = shotBy;
	}
	
	public void updateTargetPos(Entity entity) {
		Vec3 pos;
		if(targetB != null) {
			pos = targetB;
		} else if(entity != null) {
			pos = entity.getEyePosition();
		} else {
			entityData.set(TARGET_X, null);
			entityData.set(TARGET_Y, null);
			entityData.set(TARGET_Z, null);
			return;
		}
		entityData.set(TARGET_X, (float)pos.x);
		entityData.set(TARGET_Y, (float)pos.y);
		entityData.set(TARGET_Z, (float)pos.z);
	}
	
	public Vec3 getTargetPos() {
		if(entityData.get(TARGET_X) == null || entityData.get(TARGET_Y) == null || entityData.get(TARGET_Z) == null) {
			return null;
		}
		return new Vec3(this.entityData.get(TARGET_X), this.entityData.get(TARGET_Y), this.entityData.get(TARGET_Z));
	}

	//geckolib
	private final AnimationFactory factory = new AnimationFactory(this);
		
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<SmallMissile>(this, "empty", 0, this::empty));
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
	public boolean isNoGravity() {
		return true;
	}
	
	@Override
	protected void defineSynchedData() {
	 	this.entityData.define(TARGET_X, null);
	 	this.entityData.define(TARGET_Y, null);
	 	this.entityData.define(TARGET_Z, null);
	}
	
	@Override
	public boolean fireImmune() {
		return true;
	}
}


