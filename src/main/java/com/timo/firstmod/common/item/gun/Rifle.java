package com.timo.firstmod.common.item.gun;

import java.util.function.Consumer;

import com.timo.firstmod.client.renderer.item.RifleRenderer;
import com.timo.firstmod.client.renderer.item.RocketLauncherRenderer;
import com.timo.firstmod.common.item.RocketLauncher;
import com.timo.firstmod.common.item.ammo.AbstractAmmo;
import com.timo.firstmod.common.item.ammo.RifleAmmo;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

public class Rifle extends AbstractGun implements IAnimatable, ISyncable {

	public Rifle() {
		super();
		GeckoLibNetwork.registerSyncable(this);
	}
	
	@Override	
	public AbstractAmmo getAmmo() {
		return null;
	}
	
	//geckolib
	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IItemRenderProperties() {
			private final BlockEntityWithoutLevelRenderer renderer = new RifleRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return renderer;
			}
		});
	}

	@Override
	public void registerControllers(AnimationData data) {
		AnimationController<Rifle> controller = new AnimationController<Rifle>(this, CONTROLLER_NAME, 5, this::predicate);
		data.addAnimationController(controller);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if(player.isShiftKeyDown()) playReloadAnim(player.getItemInHand(hand), level, player);
		else if(player.isSprinting()) playShootAnim(player.getItemInHand(hand), level, player);
		return super.use(level, player, hand);
	}
	
	//geckolib
	public void playShootAnim(ItemStack stack, Level level, Player player) {
		if(level.isClientSide()) return;
		final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerLevel) level);
		final PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player);
		GeckoLibNetwork.syncAnimation(target, this, id, ANIM_SHOOT);
	}
	
	public void playReloadAnim(ItemStack stack, Level level, Player player) {
		if(level.isClientSide()) return;
		final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerLevel) level);
		final PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player);
		GeckoLibNetwork.syncAnimation(target, this, id, ANIM_RELOAD);
	}

	protected <E extends Item & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		return PlayState.CONTINUE;
	}
	
	protected final AnimationFactory factory = new AnimationFactory(this);

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void onAnimationSync(int id, int state) {
		if (state == ANIM_SHOOT) {
			final AnimationController controller = GeckoLibUtil.getControllerForID(factory, id, CONTROLLER_NAME);
			if (controller.getAnimationState() == AnimationState.Stopped) {
				controller.markNeedsReload();
				controller.setAnimation(new AnimationBuilder().addAnimation("shoot", false));
			}
		} else if (state == ANIM_RELOAD) {
			final AnimationController controller = GeckoLibUtil.getControllerForID(factory, id, CONTROLLER_NAME);
			if (controller.getAnimationState() == AnimationState.Stopped) {
				controller.markNeedsReload();
				controller.setAnimation(new AnimationBuilder().addAnimation("reload", false));
			}
		}
	}
}
