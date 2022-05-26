package com.timo.firstmod.common.entity;

import com.timo.firstmod.FirstMod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class SnowmanCannon extends Monster implements IAnimatable, RangedAttackMob {
	private static final EntityDataAccessor<Integer> DATA_ATTACKING_STATE = SynchedEntityData.defineId(SnowmanCannon.class, EntityDataSerializers.INT); //0: peaceful, 1: attacking
	private static final EntityDataAccessor<Integer> DATA_ANIM_STATE = SynchedEntityData.defineId(SnowmanCannon.class, EntityDataSerializers.INT); //0: no animation, 1: attack animation
	private static final EntityDataAccessor<Integer> DATA_ANIM_LENGTH = SynchedEntityData.defineId(SnowmanCannon.class, EntityDataSerializers.INT); //current anim length
	
	private int attackTickL = 5;
	
	private static final ResourceLocation LOOT_TABLE = new ResourceLocation(FirstMod.MODID,"entities/snowman_cannon");
	
	public SnowmanCannon(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.FOLLOW_RANGE, 30.0D).add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.ATTACK_DAMAGE, 3.0D);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 5, 20F));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
	    this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
	    this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
	    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
	    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
	}
	
	public void aiStep() {
      super.aiStep();
      if (!this.level.isClientSide) {
         if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
            return;
         }

         BlockState blockstate = Blocks.SNOW.defaultBlockState();

         for(int l = 0; l < 3; ++l) {
            double i = Mth.floor(this.getX() + (double)((float)(l % 2 * 2 - 1) * 0.25F));
            double j = Mth.floor(this.getY());
            double k = Mth.floor(this.getZ() + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos blockpos1 = new BlockPos(i, j, k);
            if (this.level.isEmptyBlock(blockpos1) && blockstate.canSurvive(this.level, blockpos1) && level.getBlockState(blockpos1.below()).getBlock() != Blocks.AIR ) {
               this.level.setBlockAndUpdate(blockpos1, blockstate);
            }
         }
      }
    }
	
	@Override
	public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
		LivingEntity targetentity = pTarget;
		
		Vec3 targetEye = new Vec3(targetentity.getX(),targetentity.getEyeY(),targetentity.getZ());
		Vec3 thisEye = new Vec3(getX(),getEyeY(),getZ());
		
		Vec3 dir = targetEye.subtract(thisEye);
		
		startAttackAnim();
		
		SnowmanCannonProjectile snowball = new SnowmanCannonProjectile(level, thisEye, this);
		
		double eY = targetEye.y - (double)1.1F;
		double dx = dir.x; //double dx = targetEye.x - thisEye.x;
	    double dy = eY - snowball.getY();
	    double dz = dir.z; //double dz = targetEye.z - thisEye.y;
	    double d4 = Math.sqrt(dx * dx + dz * dz) * (double)0.05F;
	    snowball.shoot(dx, dy + d4, dz, 4.5F, 0.1F);
		
		playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 1.0F);
	    
	    if(level.isClientSide()) level.addParticle(ParticleTypes.EXPLOSION, thisEye.x, thisEye.y, thisEye.z, 0.1d, 0.1d, 0.1d);
		
	    level.addFreshEntity(snowball);
		
	}
	
	protected void defineSynchedData() {
		this.entityData.define(DATA_ATTACKING_STATE, 0);
	 	this.entityData.define(DATA_ANIM_STATE, 0);
	 	this.entityData.define(DATA_ANIM_LENGTH, 0);
		super.defineSynchedData();
	}
	
	@Override
	public void setTarget(LivingEntity target) {
		super.setTarget(target);
		if(target != null) setAttackingState(1);
		else setAttackingState(0);
	}
	
	@Override
	public double getEyeY() {
		return getY() + 1.4f;
	}
	
	public void setAttackingState(int i) {
		entityData.set(DATA_ATTACKING_STATE, i);
	}
	
	public int getAttackingState() {
		return entityData.get(DATA_ATTACKING_STATE);
	}
	
	public void setAnimState(int i) {
		entityData.set(DATA_ANIM_STATE, i);
	}
	
	public int getAnimState() {
		return entityData.get(DATA_ANIM_STATE);
	}
	
	public boolean isInAnim() {
	    return getAnimState() != 0;
	}
	
	public int getAnimLength() {
		return entityData.get(DATA_ANIM_LENGTH);
	}
	
	public void setAnimLength(int i) {
		entityData.set(DATA_ANIM_LENGTH,i);
	}
	
	public void startAttackAnim() { //queue Attack Animation for start
		if(getAnimState() == 0) {
			setAnimState(1);
			setAnimLength(attackTickL);
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if(!level.isClientSide() && getAnimState() == 1) {
			if(getAnimLength() == 0) { setAnimState(0); }
			else { setAnimLength(getAnimLength()-1); }
		}
	}

	//geckolib
	private final AnimationFactory factory = new AnimationFactory(this);
	
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<SnowmanCannon>(this, "bodyController", 0, this::body));
		data.addAnimationController(new AnimationController<SnowmanCannon>(this, "headController", 0, this::head));
	}
	
	private <E extends IAnimatable> PlayState body(AnimationEvent<E> event) {
		if (this.getDeltaMovement().lengthSqr() < 0.07d){
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.snowman_cannon.idle", false));
			return PlayState.CONTINUE;
		} else {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.snowman_cannon.walk", false));
			return PlayState.CONTINUE;
		}
	}
	
	private <E extends IAnimatable> PlayState head(AnimationEvent<E> event) {
		if(getAnimState() == 1) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.snowman_cannon.attack", false));
			return PlayState.CONTINUE;
		} else if(getAttackingState() == 0){
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.snowman_cannon.head_peace", false));
			return PlayState.CONTINUE;
		} else {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.snowman_cannon.head_angry", false));
			return PlayState.CONTINUE;
		}
	}
	
	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
	
	//other stuff
	public boolean isSensitiveToWater() {
		return true;
	}
	
	@Override
	protected ResourceLocation getDefaultLootTable() {
		return LOOT_TABLE;
	}
	
	//snowball
	static class SnowmanCannonProjectile extends Snowball {
		private SnowmanCannon snowmanCannon;
		
		public SnowmanCannonProjectile(Level p_37399_, LivingEntity p_37400_, SnowmanCannon snowmanCannon) {
			super(p_37399_, p_37400_);
			this.snowmanCannon = snowmanCannon;
		}
		
		public SnowmanCannonProjectile(Level level, Vec3 pos, SnowmanCannon snowmanCannon) {
			super(level, pos.x, pos.y, pos.z);
			this.snowmanCannon = snowmanCannon;
		}
		
		@Override
		protected void onHitEntity(EntityHitResult p_37404_) {
		    super.onHitEntity(p_37404_);
		    Entity entity = p_37404_.getEntity();
		    entity.hurt(DamageSource.thrown(this, this.getOwner()), (float)8);
		    //if(entity instanceof LivingEntity) ((LivingEntity) entity).addEffect();
		    //if(entity.getClass().equals(LivingEntity.class)) ((LivingEntity) entity).addEffect();
		    AreaEffectCloud areaeffectcloudentity = new AreaEffectCloud(entity.level, this.getX(), this.getY(), this.getZ());
		    areaeffectcloudentity.setParticle(ParticleTypes.CRIMSON_SPORE);
			areaeffectcloudentity.setRadius(0.8F);
			areaeffectcloudentity.setDuration(1);
			areaeffectcloudentity.setPos(entity.getX(), entity.getEyeY(), entity.getZ());
			entity.level.addFreshEntity(areaeffectcloudentity);
		}
	}
}
