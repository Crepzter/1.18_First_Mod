package com.timo.firstmod.utils;

import java.util.Map;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ExplosionUtils {
	private Random random = new Random();
	
	private Level level;
	private Entity source;
	private LivingEntity attacker;
	
	// Configuration
	private BlockPos pos;
	private Vec3 center;
	private int radius;
	private float strength;
	int maxHeight;
	int minHeight;
	
	private float[][][] blocks; // x|y|z
	
	// Damage Blocks Map
	private static final Map<Block, Block> BLOCKS_DAMAGED = Map.ofEntries(
		// Stone
		Map.entry(Blocks.STONE, Blocks.COBBLESTONE),
		Map.entry(Blocks.COBBLESTONE, Blocks.GRAVEL),
		Map.entry(Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS),
		// Logs
		Map.entry(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG),
		Map.entry(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG),
		Map.entry(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG),
		Map.entry(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG),
		Map.entry(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG),
		Map.entry(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG),
		Map.entry(Blocks.GRASS_BLOCK, Blocks.DIRT),
		// Others
		Map.entry(Blocks.GLASS, Blocks.AIR)
	);
	
	// Constructor
	public ExplosionUtils(Level level, BlockPos pos, Entity source, LivingEntity attacker, int radius, float strength) {
		this.level = level;
		this.source = source;
		this.attacker = attacker;
		
		this.pos = pos;
		this.radius = radius;
		this.strength = strength;
		this.center = new Vec3(pos.getX(),pos.getY(),pos.getZ());
		
		this.maxHeight = radius/3;
		this.minHeight = 2;
		this.blocks = new float[2*radius+1][minHeight+maxHeight+1][2*radius+1];
	}
	
	// Main Explosion
	
	public void explode() {
		blocks[radius][minHeight][radius] = 1; // Center of Explosion -> p = 1
		
		// Blocks
		if(getExplosionResistance(pos) < 11) {
			replaceBlock(pos, Blocks.AIR.defaultBlockState());
		}
		
		int dy = 0; // Beginning at height of center
		for(int k = 0; k <= maxHeight; k++) { // Moving upwards until maxHeight is reached
			process2dSlice(dy++);
		}
		
		dy = -1; // Now beginning from underneath the explosion
		for(int k = 0; k < minHeight; k++) { // Moving downwards until minHeight is reached, starting at height under center
			process2dSlice(dy--);
		}
		
		// Entities
		processEntities();
	}
	
	public void process2dSlice(int dy) {
		int dx = 0; // Starting at center of circle for every height
		int dz = 0;
		
		int yradius = radiusAtY(dy); // Calculating radius at given height, since radius declines when moving vertically away from center
		
		if(dy != 0) processAt(dx, dy, dz, yradius); // If center of circle isnt center of explosion, it should be calculated too
		
		for(int i = 1;i <= yradius; i++) { // Starting to move spirally around center of circle, always doing one ring further out. Always from:  O to x  ==>  O---x
			dx++;
			dz--;
			
			for(int j = 1;j <= 2*i; j++) {		//	_____
				dx--;							//	|	|
				processAt(dx, dy, dz, yradius);	//  |	|
			}									//	x---O
			for(int j = 1;j <= 2*i; j++) {		//  x____
				dz++;							//	|	|
				processAt(dx, dy, dz, yradius);	//	|	|
			}									//  O___|
			for(int j = 1;j <= 2*i; j++) {		//	O---x
				dx++;							//	|	|
				processAt(dx, dy, dz, yradius);	//  |	|
			}									//	|___|
			for(int j = 1;j <= 2*i; j++) {		//  ____O
				dz--;							//	|	|
				processAt(dx, dy, dz, yradius);	//	|	|
			}									//  |___x
		}
	}
	
	public void processAt(int dx, int dy, int dz, int yradius) {
		float p_rad = 1f - ( (dx*dx+dz*dz) / (float)(yradius*yradius) ); // The relative distance of the current position between the center of the circle and edge of the circle --> used for odds
		
		if(p_rad >= 0) 
		{
			BlockPos curPos = new BlockPos(center.add(dx,dy,dz)); // The position currently being dealt with => center + deltaVector
			
			float res = getExplosionResistance(curPos); // Returns the explosion-resistance of the block at current-position
			
			int[] t = xyz(dx,dy,dz); // Obtaining index of block explosion "meets" before this block, to use its probability in calculation
			
			float p_before = blocks[t[0]+radius][t[1]+minHeight][t[2]+radius];		
			float p_this = (p_rad < p_before) ? p_rad : p_before;
			
			if(res > 10) { // Probability to explode is 0
				p_this = 0;
			} else {
				p_this -= p_delta(res);
				
				if(p_this < 0) p_this = 0;
				if(p_this > 1) p_this = 1;
			}
			
			blocks[dx+radius][dy+minHeight][dz+radius] = p_this; // Current blocks p to save in array is (the calculated value * the "earlier" blocks p)
			
			remove(curPos, p_this, (p_rad < 0.6f) ? 0.1f*p_rad : 0.1f*p_this );
		}
	}
	
	public void processEntities() {
		for( Entity entityL : level.getEntities(source, new AABB(center.subtract(radius-1,minHeight-1,radius-1),center.add(radius-1,maxHeight-1,radius-1))) ) {
			if(entityL instanceof LivingEntity entity) 
			{
				Vec3 pos = entity.getEyePosition();
				int x = (int) (pos.x-center.x+radius);
				int y = (int) (pos.y-center.y+minHeight);
				int z = (int) (pos.z-center.z+radius);
				float p = blocks[x][y][z];
				
		        // fire
		        entity.setSecondsOnFire((int)(10f * p));
		        // damage
		        entity.hurt(DamageSource.explosion(entity), strength * radius * p);
		        // effects
		        int d = (int)(200f * p); //duration
		        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION,d,1,false,false,true), attacker);
		        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,d,1,false,false,true), attacker);
		        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,d*2,1,false,false,true), attacker);
		        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,d*2,1,false,false,true), attacker);
		        
		        Vec3 push = new Vec3(x-radius,y-minHeight,z-radius).normalize().multiply(0.05*radius*p, 0.05*radius*p, 0.05*radius*p);
		        entity.push(push.x,push.y,push.z);
		        entity.hurtMarked = true;
			}
		}
	}
	
	// Calculations
	
	public int radiusAtY(int dy) {
		if(dy > 0) 
		{
			float y = dy/(float)(maxHeight+1);
			return (int) ( radius * (1 - Math.pow(y, 3)) );
		} 
		else if(dy < 0) 
		{
			float y = -dy/(float)(minHeight+1);
			return (int) ( radius * (1 - Math.pow(y, 3)) );
		}
		else // if(dy == 0)
		{
			return radius;
		}
	}
	
	public float p_delta(float res) {
		// Probabilities not needed to calculate
		if(res > 11) return 1f;
		if(res <= 0) return 0f;
		if(res == 11) {
			return 3f / (radius * strength);
		}
		
		// fixed points: 
		//	- at min res destruction until: (radius * strength * 2) blocks
		// 		res = 0: p_delta = 1 / (radius * strength * 2)
		//	- at max res destruction until: (radius * strength) blocks
		// 		res = 10: p_delta = 1 / (radius * strength)
		
		float a = 1f / (radius * strength * 2); //p_d_min_res
		float b = 2f / (radius * strength);	 //p_d_max_res
		
		int max_r = 4;
		if(res > max_r) res = max_r;
		
		return (res)/(max_r) * (b-a) + a; // linear map
	}
	
	public int[] xyz(int dx, int dy, int dz) {
		Vec3 d = new Vec3(dx,dy,dz); // Create distance vector from center
		d = d.normalize(); // Normalize it
		
		dx = (int)(dx + 0.5 - d.x); if(dx < 0) dx--;
		dy = (int)(dy + 0.5 - d.y); if(dy < 0) dy--;
		dz = (int)(dz + 0.5 - d.z); if(dz < 0) dz--;
		
		return new int[] {dx, dy, dz};
	}
	
	// Utility
	
	// ExplosionResistance:  0 | 1 - 10 | 11 | 12
	public float getExplosionResistance(BlockPos pos) {
		float res = level.getBlockState(pos).getExplosionResistance(level, pos, null);
		
		if(res > 500) {
			return 12;
		} else if(res > 10) {
			return 11;
		} else if(res <= 0) {
			return 0;
		} else {
			return res;
		}
	}
	
	// Try to remove/damage block, given odds for destruction and fire
	public void remove(BlockPos pos, float p_d, float p_f) 
	{
		// Destroy block completely
		if(random.nextFloat() < p_d && p_d > 0.03) {
			// Replace block with fire
			if(random.nextFloat() < p_f) {
				replaceBlock(pos, Blocks.FIRE.defaultBlockState());
			// Replace block with air
			} else {
				replaceBlock(pos, Blocks.AIR.defaultBlockState());
			}
			return;
		} 
		
		// "Damage" block
		if(random.nextFloat() < 0.5*p_d && p_d > 0.05) {
			Block block = level.getBlockState(pos).getBlock();
			
			if(BLOCKS_DAMAGED.containsKey(block)) {
				level.setBlockAndUpdate(pos, BLOCKS_DAMAGED.get(block).defaultBlockState());
			}
		}
		
		// Launch Block
		if(random.nextFloat() < 0.5*p_d && p_d > 0.2) {
			Vec3 fly = new Vec3(pos.getX(),pos.getY(),pos.getZ()).subtract(center).normalize().multiply(p_d*1.2, p_d*1.2, p_d*1.2);
			
			FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(level,pos.getX(),pos.getY(),pos.getZ(),level.getBlockState(pos));
            fallingBlockEntity.setDeltaMovement(fly.x, fly.y, fly.z);
            
            level.addFreshEntity(fallingBlockEntity);
		}
	}
	
	// Optimized replacement of blocks
	public void replaceBlock(BlockPos pos, BlockState state) {
		level.setBlockAndUpdate(pos, state);
		if(!level.getBlockState(pos.above()).canSurvive(level, pos)) {
			replaceBlock(pos.above(), Blocks.AIR.defaultBlockState());
		} 
	}
}
