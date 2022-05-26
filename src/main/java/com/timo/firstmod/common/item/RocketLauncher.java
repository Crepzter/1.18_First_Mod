package com.timo.firstmod.common.item;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.client.renderer.item.RocketLauncherRenderer;
import com.timo.firstmod.common.entity.SmallMissile;
import com.timo.firstmod.core.init.EntityInit;
import com.timo.firstmod.core.init.PacketHandler;
import com.timo.firstmod.core.network.ServerBoundRocketLauncherUsePacket;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
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
	private static final String CONTROLLER_NAME = "shootController";
	public static final int ANIM_OPEN = 0;
	
	public static final int RANGE = 169;
	
	private int entityTick;
	@Nullable
	private UUID entityTargetId;

	public RocketLauncher() {
		super(new Item.Properties().tab(FirstMod.FIRSTMOD_TAB).stacksTo(1));
		GeckoLibNetwork.registerSyncable(this);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int i) {
		if (entity instanceof Player player) {
			if (level.isClientSide()	&& player.getDeltaMovement().length() < 0.1d && player.isShiftKeyDown() && !player.getCooldowns().isOnCooldown(this)) {
				BlockPos p = getTargetBlock(player, level, RANGE, 0);
				
				if (entityTargetId != null && entityTick > 15) {
					PacketHandler.INSTANCE.sendToServer(new ServerBoundRocketLauncherUsePacket(entityTargetId));
					entityTargetId = null;
					entityTick = 0;
				} else if (p != null) {
					PacketHandler.INSTANCE.sendToServer(new ServerBoundRocketLauncherUsePacket(p));
				} else return;
				
				player.getCooldowns().addCooldown(this, 35);

				Vec3 dir = player.getLookAngle();
				player.push(-3*dir.x,0.15,-3*dir.z);
			}
		}
	}
	
	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int i) {
		if (entity instanceof Player player) {
			if(level.isClientSide()	&& player.getDeltaMovement().length() < 0.1d && player.isShiftKeyDown() && !player.getCooldowns().isOnCooldown(this)) {
				LivingEntity entityTarget = getTargetEntity(player, level, RANGE, 0);
				if(entityTarget != null) {
					UUID id = entityTarget.getUUID();
					if(id.equals(entityTargetId)) {
						entityTick++;
						if(entityTick > 20) {
							player.playSound(SoundEvents.ANVIL_BREAK, 1.0F, 5.0F);
							player.displayClientMessage(new TextComponent("Entity targeted: " + entityTarget.getType() ), true);
							
							level.addParticle(ParticleTypes.LANDING_LAVA, entityTarget.getX(), entityTarget.getY(1f), entityTarget.getZ(), 0.5d, 0.5d, 0.5d);
						}
					}
					else {
						entityTargetId = id;
						entityTick = 0;
					}
				} else entityTargetId = null;
			}
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

	@Nullable
	public BlockPos getTargetBlock(Player player, Level level, double range, float p_19909_) { // ..., 20, 0, false
		Vec3 eye = player.getEyePosition(p_19909_);
		Vec3 view = player.getViewVector(p_19909_);
		Vec3 sight = eye.add(view.x * range, view.y * range, view.z * range);
		HitResult h = level.clip(new ClipContext(eye, sight, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
		if (h.getType() == HitResult.Type.BLOCK) {
			return ((BlockHitResult)h).getBlockPos();
		} else {
			return null;
		}
	}

	@Nullable
	public LivingEntity getTargetEntity(Player player, Level level, double range, float p_19909_) { // ..., 20, 0, false
		Vec3 eyePos = player.getEyePosition();
		Vec3 rangeV = new Vec3(RANGE,RANGE,RANGE);
		
		for( Entity entity1 : level.getEntities(player, new AABB(eyePos.subtract(rangeV),eyePos.add(rangeV)) ) ) {
			if(entity1 instanceof LivingEntity) {
				//enderman approach
				Vec3 vec3 = player.getViewVector(1.0F).normalize();
		        Vec3 dis = new Vec3(entity1.getX() - player.getX(), entity1.getEyeY() - player.getEyeY(), entity1.getZ() - player.getZ());
		        double disLen = dis.length();
		        dis = dis.normalize();
		        double dot = vec3.dot(dis);
		        if(dot > 1.0D - 0.035D / disLen && player.hasLineOfSight(entity1)) return (LivingEntity)entity1;
		        //TODO boundingbox approach?
			}
		}
		return null;
	}

	public void shoot(Level level, Player player, ItemStack stack, Object targetO) {
		if (!level.isClientSide()) {
			SmallMissile projectile = new SmallMissile(EntityInit.SMALL_MISSILE.get(), level);
			projectile.setPos(player.getEyePosition());
			projectile.setShotBy(player);
			
			if (targetO instanceof UUID targetEId) {
				projectile.setTarget(targetEId);
			} else if (targetO instanceof BlockPos targetV) {
				projectile.setTarget(targetV);
			} else {
				return;
			}
			projectile.firstRotation(player.getViewVector(1f));

			final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerLevel) level);
			final PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player);
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

	// geckolib
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

	//other stuff
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
		return 40;
	}

	@Override
	public boolean useOnRelease(ItemStack p_41464_) {
		return true;
	}
}
