package com.timo.firstmod.common.block.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.timo.firstmod.core.init.BlockEntityInit;
import com.timo.firstmod.recipe.LightningBlockRecipe;
import com.timo.firstmod.screen.LightningBlockMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class LightningBlockEntity extends BlockEntity implements IAnimatable, MenuProvider {
	
	public final Map<UUID, Integer> playerUses = new HashMap<>();
	protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;
	
	public static final int TE_INVENTORY_SLOT_COUNT = 4;

	public LightningBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.LIGHTNING_BLOCK.get(), pos, state);
        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0: return LightningBlockEntity.this.progress;
                    case 1: return LightningBlockEntity.this.maxProgress;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch(index) {
                    case 0: LightningBlockEntity.this.progress = value; break;
                    case 1: LightningBlockEntity.this.maxProgress = value; break;
                }
            }

            public int getCount() {
                return 2;
            }
        };
    }

	//Data
	
	@Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        final net.minecraft.nbt.ListTag playerUses = nbt.getList("playerUseMap", Tag.TAG_COMPOUND);
        playerUses.forEach(player -> {
            if (player instanceof final CompoundTag tag) {
                final UUID uuid = tag.getUUID("uuid");
                final int uses = tag.getInt("uses");
                this.playerUses.put(uuid, uses);
            }
        });
        
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("lightning_block.progress");
    }
	
	@Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);

        final var playerUses = new net.minecraft.nbt.ListTag();
        this.playerUses.forEach((uuid, uses) -> {
            final var playerTag = new CompoundTag();
            playerTag.putUUID("uuid", uuid);
            playerTag.putInt("uses", uses);
            playerUses.add(playerTag);
        });
        
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.put("playerUseMap", playerUses);
        nbt.putInt("lightning_block.progress", progress);
        
        super.saveAdditional(nbt);
    }
	
	//GUI stuff
	
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new LightningBlockMenu(id, inv, this, this.data);
	}

	@Override
	public Component getDisplayName() {
		return new TextComponent("Lightning Block");
	}
	
	private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
	
	//Gecko-lib
    
    private AnimationFactory factory = new AnimationFactory(this);
    
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
    
    //CRAFTING
    
    public static void tick(Level level, BlockPos pPos, BlockState pState, LightningBlockEntity pBlockEntity) {
        if(hasRecipe(pBlockEntity)) {
            pBlockEntity.progress++;
            setChanged(level, pPos, pState);
            if(pBlockEntity.progress > pBlockEntity.maxProgress) {
                craftItem(pBlockEntity);
            }
        } else {
            pBlockEntity.resetProgress();
            setChanged(level, pPos, pState);
        }
    }

    private static boolean hasRecipe(LightningBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<LightningBlockRecipe> match = level.getRecipeManager()
                .getRecipeFor(LightningBlockRecipe.Type.INSTANCE, inventory, level);

        return match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
                && canInsertItemIntoOutputSlot(inventory, match.get().getResultItem())
                && hasFlintInSlot(entity) && hasFeatherInSlot(entity);
    }

    private static boolean hasFlintInSlot(LightningBlockEntity entity) {
        return entity.itemHandler.getStackInSlot(0).getItem() == Items.FLINT;
    }

    private static boolean hasFeatherInSlot(LightningBlockEntity entity) {
        return entity.itemHandler.getStackInSlot(2).getItem() == Items.FEATHER;
    }

    private static void craftItem(LightningBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<LightningBlockRecipe> match = level.getRecipeManager()
                .getRecipeFor(LightningBlockRecipe.Type.INSTANCE, inventory, level);

        if(match.isPresent()) {
            entity.itemHandler.extractItem(0,1, false);
            entity.itemHandler.extractItem(1,1, false);
            entity.itemHandler.extractItem(2,1, false);
            //entity.itemHandler.getStackInSlot(2).hurt(1, new Random(), null);

            entity.itemHandler.setStackInSlot(3, new ItemStack(match.get().getResultItem().getItem(),
                    entity.itemHandler.getStackInSlot(3).getCount() + 8));

            entity.resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack output) {
        return inventory.getItem(3).getItem() == output.getItem() || inventory.getItem(3).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(3).getMaxStackSize() > inventory.getItem(3).getCount();
    }
}