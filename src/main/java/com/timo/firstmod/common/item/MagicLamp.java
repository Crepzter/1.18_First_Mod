package com.timo.firstmod.common.item;

import java.util.List;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.Dshinni;
import com.timo.firstmod.core.init.EntityInit;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class MagicLamp extends Item {
	
	public MagicLamp() {
		super(new Item.Properties().tab(FirstMod.FIRSTMOD_TAB).stacksTo(1).fireResistant());
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag pIsAdvanced) {
		if(wasUsed(stack)) tooltipComponents.add(new TranslatableComponent("tooltip.firstmod.magic_lamp_used"));
		else tooltipComponents.add(new TranslatableComponent("tooltip.firstmod.magic_lamp_unused"));
		
		super.appendHoverText(stack, level, tooltipComponents, pIsAdvanced);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand); 
		
		if(!wasUsed(stack)) {
			Dshinni dshinni = new Dshinni(EntityInit.DSHINNI.get(), level);
			dshinni.setPos(player.getX(),player.getY(),player.getZ());
			level.addFreshEntity(dshinni);
			setUsed(stack);
			return  InteractionResultHolder.success(stack);
		} else {
			return  InteractionResultHolder.fail(stack);
		}
	}
	
	public void setUsed(ItemStack stack) {
		if(!stack.getOrCreateTag().contains(FirstMod.MODID)) {
			stack.getOrCreateTag().put(FirstMod.MODID, new CompoundTag());
		}
		CompoundTag nbt = stack.getOrCreateTag().getCompound(FirstMod.MODID);
		nbt.putBoolean("used", true);
	}
	
	public boolean wasUsed(ItemStack stack) {
		if(!stack.getOrCreateTag().contains(FirstMod.MODID)) {
			stack.getOrCreateTag().put(FirstMod.MODID, new CompoundTag());
		}
		CompoundTag nbt = stack.getOrCreateTag().getCompound(FirstMod.MODID);
		if(!nbt.contains("used")) return false;
		else return true;
	}
}
