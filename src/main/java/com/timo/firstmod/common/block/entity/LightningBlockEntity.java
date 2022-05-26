package com.timo.firstmod.common.block.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.timo.firstmod.core.init.BlockEntityInit;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class LightningBlockEntity extends BlockEntity implements IAnimatable {
	
	private AnimationFactory factory = new AnimationFactory(this);
	public final Map<UUID, Integer> playerUses = new HashMap<>();

	public LightningBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityInit.LIGHTNING_BLOCK.get(), pos, state);
	}
	
	public void tick() {
		
	}

	@Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        final net.minecraft.nbt.ListTag playerUses = nbt.getList("PlayerUseMap", Tag.TAG_COMPOUND);
        playerUses.forEach(player -> {
            if (player instanceof final CompoundTag tag) {
                final UUID uuid = tag.getUUID("UUID");
                final int uses = tag.getInt("Uses");
                this.playerUses.put(uuid, uses);
            }
        });
    }
	
	@Override
    public void saveAdditional(CompoundTag nbt) { //CompoundTag not void
        super.saveAdditional(nbt);

        final var playerUses = new net.minecraft.nbt.ListTag();
        this.playerUses.forEach((uuid, uses) -> {
            final var playerTag = new CompoundTag();
            playerTag.putUUID("UUID", uuid);
            playerTag.putInt("Uses", uses);
            playerUses.add(playerTag);
        });

        nbt.put("PlayerUseMap", playerUses);
        //return nbt;
    }
	
	//gecko-lib
	@Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<LightningBlockEntity>(this, "main", 1, this::predicate));

    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.lightning_block.idle", true));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}