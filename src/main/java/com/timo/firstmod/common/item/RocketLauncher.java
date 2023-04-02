package com.timo.firstmod.common.item;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.client.renderer.item.RocketLauncherRenderer;
import com.timo.firstmod.common.entity.projectile.SmallMissile;
import com.timo.firstmod.core.init.EntityInit;
import com.timo.firstmod.core.init.PacketHandler;
import com.timo.firstmod.core.init.SoundInit;
import com.timo.firstmod.core.network.ServerBoundRocketLauncherUsePacket;
import com.timo.firstmod.utils.ProjectileUtils;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class RocketLauncher extends Item implements IAnimatable, ISyncable {
	public static final int RANGE = 250;
	public static final int LOCK_TARGET_TIME = 15;
	
	private int useTick = 0;
	private int targetTick = 0;
	private int targetDelay = -1;
	@Nullable
	private UUID entityTargetId = null;
	

	public RocketLauncher() {
		super(new Item.Properties().tab(FirstMod.FIRSTMOD_TAB).stacksTo(1));
		GeckoLibNetwork.registerSyncable(this);
	}
	
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}
	
	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int i) {
		if (entity instanceof Player player && !level.isClientSide() && !player.getCooldowns().isOnCooldown(this)) {
			LivingEntity entityTarget = getTargetEntity(player, level);
			
			if(entityTarget != null) {
				targetDelay = 0;
				UUID id = entityTarget.getUUID();
				
				if(id.equals(entityTargetId)) {
					targetTick++;
				}
				else {
					entityTargetId = id;
					targetTick = 0;
				}
			// Time Buffer to make locking target easier
			} else if (targetDelay > -1) {
				targetDelay++;
				if(targetDelay > 8) {
					targetDelay = -1;
					entityTargetId = null;
					targetTick = 0;
				}
			}
			
			// Play Sound indicating target locked
			if(entityTargetId != null && targetTick > LOCK_TARGET_TIME) {
				level.playSound(null, entity.getX(), entity.getEyeY(), entity.getZ(), SoundInit.ROCKET_LAUNCHER_TARGET_LOCKED.get(), SoundSource.PLAYERS, 0.6F, 1F);
				System.out.println("sound");
				// Mark Target
			}
			useTick++;
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity shooter, int i) {
		if( !level.isClientSide() && useTick >= 20 ) {
			// Reset usetick
			useTick = 0;
			
			// If player cooldown return
			if(shooter instanceof Player && ((Player)shooter).getCooldowns().isOnCooldown(this)) return;
			
			// Shoot at entity
			if (entityTargetId != null && targetTick > LOCK_TARGET_TIME) {
				shoot(level, shooter, stack, entityTargetId);
				
			// Reset targettick
			targetTick = 0;
			entityTargetId = null;
				
			// Shoot at blockpos
			} else  {
				BlockPos p = getTargetBlock(shooter, level);
				
				if(p != null) {
					shoot(level, shooter, stack, p);
				} else {
					return;
				}
			}
			
			// If player update cooldown
			if(shooter instanceof Player) ((Player)shooter).getCooldowns().addCooldown(this, 35);
			
			// Push shooter back
			Vec3 dir = shooter.getLookAngle();
			shooter.push(-3*dir.x,0.15,-3*dir.z);
			if(shooter instanceof Player) ((Player)shooter).hurtMarked = true;
		}
	}

	@Nullable
	public BlockPos getTargetBlock(LivingEntity shooter, Level level) {
		Vec3 eyePos = shooter.getEyePosition();
		Vec3 eyeVector = shooter.getViewVector(1.0F).multiply(RANGE, RANGE, RANGE);
		
		HitResult h = level.clip( new ClipContext(eyePos, eyePos.add(eyeVector), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, shooter) );
		
		if (h.getType() == HitResult.Type.BLOCK) {
			return ((BlockHitResult)h).getBlockPos();
		} else {
			return null;
		}
	}
	
	@Nullable
	public LivingEntity getTargetEntity(Entity shooter, Level level) {
		Vec3 eyePos = shooter.getEyePosition();
		Vec3 eyeVector = shooter.getViewVector(1.0F).multiply(RANGE, RANGE, RANGE);
		
		return ProjectileUtils.getEntityLookAt(level, shooter, eyePos, eyePos.add(eyeVector), shooter.getBoundingBox().expandTowards(eyeVector), (Entity) -> true, RANGE);
	}

	public void shoot(Level level, LivingEntity shooter, ItemStack stack, Object targetO) {
		if (!level.isClientSide()) {
			SmallMissile projectile = new SmallMissile(EntityInit.SMALL_MISSILE.get(), level);
			projectile.setPos(shooter.getEyePosition());
			projectile.setShotBy(shooter);
			
			if (targetO instanceof UUID targetEId) {
				projectile.setTarget(targetEId);
			} else if (targetO instanceof BlockPos targetV) {
				projectile.setTarget(targetV);
			} else {
				return;
			}
			projectile.firstRotation(shooter.getViewVector(1.0F));

			final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerLevel) level);
			final PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> shooter);
			GeckoLibNetwork.syncAnimation(target, this, id, ANIM_OPEN);

			level.addFreshEntity(projectile);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents,
			TooltipFlag pIsAdvanced) {
		// tooltipComponents.add(new
		// TranslatableComponent("tooltip.firstmod.magic_lamp_unused"));
		tooltipComponents.add(new TextComponent("rocket schooosch"));
		super.appendHoverText(stack, level, tooltipComponents, pIsAdvanced);
	}
	

	// Geckolib
	
	private static final String CONTROLLER_NAME = "shootController";
	public static final int ANIM_OPEN = 0;
	
	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IItemRenderProperties() {
			private final BlockEntityWithoutLevelRenderer renderer = new RocketLauncherRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return renderer;
			}
		});
	}

	private final AnimationFactory factory = new AnimationFactory(this);

	@Override
	public void registerControllers(AnimationData data) {
		AnimationController<RocketLauncher> controller = new AnimationController<RocketLauncher>(this, CONTROLLER_NAME, 5, this::predicate);
		data.addAnimationController(controller);
	}

	private <E extends Item & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		return PlayState.CONTINUE;
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void onAnimationSync(int id, int state) {
		if (state == ANIM_OPEN) {
			final AnimationController controller = GeckoLibUtil.getControllerForID(this.factory, id, CONTROLLER_NAME);
			if (controller.getAnimationState() == AnimationState.Stopped) {
				controller.markNeedsReload();
				controller.setAnimation(new AnimationBuilder().addAnimation("shoot", false));
			}
		}
	}
	

	// Other stuff
	
	@Override
	public boolean canAttackBlock(BlockState p_41441_, Level p_41442_, BlockPos p_41443_, Player p_41444_) {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack p_41456_) {
		return false;
	}

	@Override
	public boolean isFoil(ItemStack p_41453_) {
		return false;
	}

	@Override
	public int getUseDuration(ItemStack p_41454_) {
		return 20;
	}

	@Override
	public boolean useOnRelease(ItemStack p_41464_) {
		return true;
	}
}
