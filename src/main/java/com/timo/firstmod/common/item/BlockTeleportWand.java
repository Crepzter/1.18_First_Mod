package com.timo.firstmod.common.item;

import java.util.List;

import com.timo.firstmod.FirstMod;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockTeleportWand extends Item {
	
	public BlockTeleportWand() {
		super(new Item.Properties().tab(FirstMod.FIRSTMOD_TAB).stacksTo(1));
	}
	
	public boolean canInteract(Player player, BlockPos pos) {
		float speed = player.getDigSpeed(player.level.getBlockState(pos), pos);
		return player.isCreative() || (player.mayBuild() && speed > 0 && speed < Float.MAX_VALUE);
	}
	
	@Override
	public boolean isEnchantable(ItemStack itemStack) {
		return false;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag pIsAdvanced) {
		if(Screen.hasShiftDown()) {
			tooltipComponents.add(new TranslatableComponent("tooltip.firstmod.block_tp_wand_shift"));
		} else {
			if(isFull(stack)) tooltipComponents.add(new TranslatableComponent("tooltip.firstmod.block_tp_wand_full"));
			else tooltipComponents.add(new TranslatableComponent("tooltip.firstmod.block_tp_wand_empty"));
		}
		
		super.appendHoverText(stack, level, tooltipComponents, pIsAdvanced);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		var player = context.getPlayer();
		var level = player.level;
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		
		if(!canInteract(player,pos)) return InteractionResult.FAIL;
		
		if(!stack.getOrCreateTag().contains(FirstMod.MODID)) { //if tag/obergruppe Mod nicht vorhanden
			stack.getOrCreateTag().put(FirstMod.MODID, new CompoundTag());
		}
		
		CompoundTag nbt = stack.getTag().getCompound(FirstMod.MODID); //die obergruppe mod
		if(!nbt.contains("ContainedBlock")) { //wenn og. containedblock nicht hat -> Einsammeln
			if(!state.isAir()) { //solange nicht air angeklickt
				level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				nbt.put("ContainedBlock", NbtUtils.writeBlockState(state));
				setFull(stack,true);
				return InteractionResult.SUCCESS;
			}
		} else if(state.canBeReplaced(new BlockPlaceContext(context))) {  //wenn og. containedblock hat -> Platzieren
			level.setBlockAndUpdate(pos, NbtUtils.readBlockState(nbt.getCompound("ContainedBlock")));
			nbt.remove("ContainedBlock");
			setFull(stack,false);
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.FAIL;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockHitResult result = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
		BlockState state = level.getBlockState(result.getBlockPos());
		
		if(!canInteract(player, result.getBlockPos()) || !state.isAir() || 
		   !state.canBeReplaced(new BlockPlaceContext(player, hand, stack, result))) { 
			return InteractionResultHolder.fail(stack); 
		}
		if(!stack.getOrCreateTag().contains(FirstMod.MODID)) {
			stack.getOrCreateTag().put(FirstMod.MODID, new CompoundTag());
			return InteractionResultHolder.fail(stack);
		}
		CompoundTag nbt = stack.getOrCreateTag().getCompound(FirstMod.MODID);
		if(!nbt.contains("ContainedBlock")) return InteractionResultHolder.fail(stack); 
		
		BlockState toPlace = NbtUtils.readBlockState(nbt.getCompound("ContainedBlock"));
		level.setBlockAndUpdate(result.getBlockPos(), toPlace);
		nbt.remove("ContainedBlock");
		setFull(stack,false);
		return  InteractionResultHolder.success(stack);
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		CompoundTag nbt = stack.getOrCreateTag();
		return nbt.contains(FirstMod.MODID) && nbt.getCompound(FirstMod.MODID).contains("ContainedBlock"); 
	}
	
	public void setFull(ItemStack stack, boolean full) {
		if(!stack.getOrCreateTag().contains(FirstMod.MODID)) {
			stack.getOrCreateTag().put(FirstMod.MODID, new CompoundTag());
		}
		CompoundTag nbt = stack.getOrCreateTag().getCompound(FirstMod.MODID);
		//CompoundTag tag = new CompoundTag();
		//tag.putBoolean("full", full);
		nbt.putBoolean("full", full);
	}
	
	public boolean isFull(ItemStack stack) {
		if(!stack.getOrCreateTag().contains(FirstMod.MODID)) {
			stack.getOrCreateTag().put(FirstMod.MODID, new CompoundTag());
		}
		CompoundTag nbt = stack.getOrCreateTag().getCompound(FirstMod.MODID);
		if(!nbt.contains("full") || !nbt.getBoolean("full")) return false;
		else return true;
	}
	
	
}
