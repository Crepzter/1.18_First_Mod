package com.timo.firstmod.common.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;

public abstract class AbstractAirProjectile  extends ThrowableProjectile implements IAnimatable  {
	// Data
	protected LivingEntity shooter = null;
	protected int blocksHit = 0;
	
	protected static final List<Block> WEAK_BLOCKS = List.of(
		Blocks.SNOW_BLOCK, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW,
		Blocks.ICE, Blocks.FROSTED_ICE,
		Blocks.ACACIA_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES,
		Blocks.GRAVEL
	);
	
	// Constructor
	public AbstractAirProjectile(EntityType<? extends ThrowableProjectile> entity, Level level) {
		super(entity, level);
	}
	
	// Main Methods
	public abstract void explode(BlockPos pos);
	
	@Override
	protected void onHitBlock(BlockHitResult result) {
		if(destroysBlocks()) 
		{
			BlockPos pos  = result.getBlockPos();
			Block block = level.getBlockState(result.getBlockPos()).getBlock();
			
			if(WEAK_BLOCKS.contains(block)) {
				level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			} else {
				if(blocksHit < 2) { level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()); blocksHit++; }
				else explode(result.getBlockPos().above().above());
			}
		} 
		else
		{
			explode(result.getBlockPos());
		}
	}
	
	// Other
	public void setShotBy(LivingEntity shooter) {
		this.shooter = shooter;
	}
	
	public Vec3 getNullPos() {
		return new Vec3(this.getX(),this.getY(),this.getZ());
	}
	
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	@Override
	protected void defineSynchedData() {
		
	}
	
	@Override
	public boolean isNoGravity() {
		return true;
	}
	
	public abstract boolean destroysBlocks();
}
