package com.timo.firstmod.screen;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.timo.firstmod.common.block.entity.LightningBlockEntity;
import com.timo.firstmod.core.init.BlockInit;
import com.timo.firstmod.core.init.MenuInit;
import com.timo.firstmod.screen.slot.ModResultSlot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class LightningBlockMenu extends AbstractContainerMenu {
	private final LightningBlockEntity blockEntity;
	private final Level level;
	private final ContainerData data;
	
	//MENU STRUCTURE AND GENERATING
	
	public LightningBlockMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
		this(containerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2)); //MATCH WITH LIGHTNINGBLOCKENTITY getCount
	}
	
	public LightningBlockMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
		super(MenuInit.LIGHTNING_BLOCK_MENU.get(), containerId);
		checkContainerSize(inv, TE_INVENTORY_SLOT_COUNT); //MATCH WITH LIGHTNINGBLOCKENTITY
		blockEntity = ((LightningBlockEntity) entity);
		
		this.level = inv.player.level;
		this.data = data;
		
		addPlayerInventory(inv);
		addPlayerHotbar(inv);
		
		this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
			this.addSlot(new LightningBlockCraftSlot(handler, 0, 44, 18, (i) -> i.is(Items.FLINT)));
            //this.addSlot(new SlotItemHandler(handler, 0, 44, 18));
            this.addSlot(new LightningBlockCraftSlot(handler, 1, 44, 36, (i) -> i.is(Items.STICK)));
            //this.addSlot(new SlotItemHandler(handler, 1, 44, 36));
            this.addSlot(new LightningBlockCraftSlot(handler, 2, 44, 54, (i) -> i.is(Items.FEATHER)));
            //this.addSlot(new SlotItemHandler(handler, 2, 44, 54));
            this.addSlot(new ModResultSlot(handler, 3, 116, 36));
        });
		
		addDataSlots(data); //--> synchronized
	}
	
	public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);  // Max Progress
        int progressArrowSize = 30; // This is the width in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }
	
	// CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    public static final int TE_INVENTORY_SLOT_COUNT = LightningBlockEntity.TE_INVENTORY_SLOT_COUNT;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

	@Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, BlockInit.LIGHTNING_BLOCK.get());
    }

	private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }
}

class FlintSlot extends SlotItemHandler {

	public FlintSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}
	
	@Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        if (stack.isEmpty() || !stack.is(Items.FLINT)) return false;
        return true;
    }
}

class LightningBlockCraftSlot extends SlotItemHandler {
	private final Predicate<ItemStack> testItem;

	public LightningBlockCraftSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Predicate<ItemStack> testItem) {
		super(itemHandler, index, xPosition, yPosition);
		this.testItem = testItem;
	}
	
	@Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        if (stack.isEmpty() || !testItem.test(stack)) return false;
        return true;
    }
}
