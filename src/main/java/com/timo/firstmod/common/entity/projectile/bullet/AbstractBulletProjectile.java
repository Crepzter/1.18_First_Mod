package com.timo.firstmod.common.entity.projectile.bullet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class AbstractBulletProjectile  extends ThrowableProjectile implements IAnimatable{

	protected AbstractBulletProjectile(EntityType<? extends ThrowableProjectile> entity, Level level) {
		super(entity, level);
	}
	
	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		BlockPos pos = result.getBlockPos();
		if(level.getBlockState(pos).is(net.minecraftforge.common.Tags.Blocks.GLASS)) {
			level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
		} else {
			this.discard();
		}
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if(result.getEntity() instanceof LivingEntity entity) {
			entity.hurt(DamageSource.playerAttack(this.getPlayer()), 8);
		}
		
	}

	//geckolib
	private final AnimationFactory factory = new AnimationFactory(this);
		
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<AbstractBulletProjectile>(this, "empty", 0, this::empty));
	}
	
	private <E extends IAnimatable> PlayState empty(AnimationEvent<E> event) {
		return PlayState.CONTINUE;
	}

	@Override
	public AnimationFactory getFactory() {
		return factory;
	}
	
	//other stuff
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	@Override
	protected void defineSynchedData() {
		
	}
	
	@Override
	public boolean isNoGravity() {
		return false;
	}
    
	@Override
	protected float getGravity() {
		return 0.02F;
	}

}
