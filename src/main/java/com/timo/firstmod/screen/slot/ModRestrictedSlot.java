package com.timo.firstmod.screen.slot;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ModRestrictedSlot extends SlotItemHandler {
	private final Predicate<ItemStack> testPre;

	public ModRestrictedSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Item testItem) {
		super(itemHandler, index, xPosition, yPosition);
		this.testPre = (i) -> i.is(testItem);
	}
	
	public ModRestrictedSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, TagKey<Item> testItem) {
		super(itemHandler, index, xPosition, yPosition);
		this.testPre = (i) -> i.is(testItem);
	}
	
	@Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        if (stack.isEmpty() || !testPre.test(stack)) return false;
        return true;
    }
}
