package com.timo.firstmod.common.item;

import javax.annotation.Nullable;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.entity.HeavyBomb;
import com.timo.firstmod.common.entity.HeavyMissile;
import com.timo.firstmod.core.init.EntityInit;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WalkieTalkie extends Item {

	public WalkieTalkie() {
		super(new Item.Properties().tab(FirstMod.FIRSTMOD_TAB).stacksTo(1));
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
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
		Vec3 start = player.getEyePosition().add(0,124,0);
		BlockPos targetB = getTargetBlock(player, level, 124, 0);
		
		if(start.y > 180 && targetB != null) {
			level.setBlockAndUpdate(targetB, Blocks.RED_CONCRETE.defaultBlockState());
			
			Vec3 target = new Vec3(targetB.getX(),targetB.getY(),targetB.getZ());
			
			HeavyMissile projectile = new HeavyMissile(EntityInit.HEAVY_MISSILE.get(), level);
			projectile.setPos(start);
			
			projectile.setDeltaMovement(target.subtract(start).normalize().multiply(2.5,2.5,2.5));
			
			level.addFreshEntity(projectile);
		}
		
		return InteractionResultHolder.pass(player.getItemInHand(pUsedHand));
	}

}
