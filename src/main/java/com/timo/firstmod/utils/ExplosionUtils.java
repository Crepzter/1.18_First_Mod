package com.timo.firstmod.utils;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ExplosionUtils {
	public static Random random = new Random();
	
	public static void sMissileExplode(int r, float strength, Level level, BlockPos pos, boolean fire, int damage, Entity entityExploding, LivingEntity entityAttacker) {
		double FIRE_ODDS = 0.15d;
		
		if(level.isClientSide()) return; //only server side
		
		Vec3 posV = new Vec3(pos.getX(),pos.getY(),pos.getZ());
		Vec3 rad = new Vec3(1.5*r,1.5*r,1.5*r);
		
		//entity damage
		for( Entity entity : level.getEntities(entityExploding, new AABB(posV.subtract(rad),posV.add(rad))) ) {
			if(entity instanceof LivingEntity entityL) {
				Vec3 dis = entityL.getEyePosition().subtract(posV);
		        double disLen = dis.length();
		        float damageR = (float) (damage*(1-disLen/(2*r))); //linear decrease
		        
		        if(entityL instanceof Player entityP && (entityP.isCreative() || entityP.isSpectator())) continue;
		        //fire
		        entityL.setSecondsOnFire(4);
		        //damage
		        entityL.hurt(DamageSource.explosion(entityAttacker), damageR);
		        //effects
		        int d = (int) (120*(1-disLen/(3*r))); //duration
		        entityL.addEffect(new MobEffectInstance(MobEffects.CONFUSION,d,1,false,false,false), entityAttacker);
		        entityL.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,d,1,false,false,false), entityAttacker);
		        entityL.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,d,1,false,false,false), entityAttacker);
		        entityL.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,d,1,false,false,false), entityAttacker);
			}
		}
		//world damage
		for (int y = r; y > -r - 1; y--) { //from bottom to top
            for (int x = -r; x < r + 1; x++) {
                for (int z = -r; z < r + 1; z++) {
                	BlockPos pPos = new BlockPos(posV.add(x,y,z));
                    BlockState state = level.getBlockState(pPos);
                    Block block = state.getBlock();
                    
                    if(x * x + y * y + z * z > r * r) { //if out of radius/sphere, only try to place fire
                    	if(block == Blocks.AIR && fire) {
                    		ignite(level,pPos,FIRE_ODDS);
                        }
                    	continue;
                    }
                    //blocks that shouldnt explode/be moved
                    if(state.is(BlockTags.NEEDS_IRON_TOOL) || block == Blocks.BEDROCK || block == Blocks.WATER || block == Blocks.LAVA || block == Blocks.AIR || block == Blocks.FIRE) continue;
                    
                    level.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
                    if(state.isSolidRender(level, pPos)) {
                    	float xMotion = random.nextFloat()* strength - strength / 2f;
                        float yMotion = (random.nextFloat()+0.1f) * strength * 1.2f;
                        float zMotion = random.nextFloat()* strength - strength / 2f;
                        
                        FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(level,pPos.getX(),pPos.getY(),pPos.getZ(),state);
                        fallingBlockEntity.setDeltaMovement(xMotion, yMotion, zMotion);
                        
                        level.addFreshEntity(fallingBlockEntity);
                    }
                    
                    if(fire) ignite(level,pPos,FIRE_ODDS);
                }
            }
        }
	}
	
	public static void hBombExplode(Level level, BlockPos pos, Entity entityExploding/*, LivingEntity entityAttacker*/) {
		double FIRE_ODDS = 0.15d;
		int RADIUS, r = 23;
		int depth, d = 6;
		float STRENGTH, s = 5;
		int DAMAGE = 20;
		
		if(level.isClientSide()) return; //only server side
		
		Vec3 posV = new Vec3(pos.getX(),pos.getY(),pos.getZ());
		Vec3 rad = new Vec3(1.5*r,1.5*r,1.5*r);
		
		//entity damage
		for( Entity entity : level.getEntities(entityExploding, new AABB(posV.subtract(rad),posV.add(rad))) ) {
			if(entity instanceof LivingEntity entityL) {
				Vec3 dis = entityL.getEyePosition().subtract(posV);
		        double disLen = dis.length();
		        float damageR = (float) (DAMAGE*(1-disLen/(2*r))); //linear decrease
		        //fire
		        entityL.setSecondsOnFire(4);
		        /*
		        //damage
		        entityL.hurt(DamageSource.explosion(entityAttacker), damageR);
		        //effects
		        int d1 = (int) (120*(1-disLen/(3*r))); //duration
		        entityL.addEffect(new MobEffectInstance(MobEffects.CONFUSION,d1,1,false,false,false), entityAttacker);
		        entityL.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,d1,1,false,false,false), entityAttacker);
		        entityL.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,d1,1,false,false,false), entityAttacker);
		        entityL.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,d1,1,false,false,false), entityAttacker);*/
			}
		}
		//world damage
		for (int y = d; y > -d - 1; y--) { //from bottom to top
            for (int x = -r; x < r + 1; x++) {
                for (int z = -r; z < r + 1; z++) {
                	BlockPos pPos = new BlockPos(posV.add(x,y,z));
                    BlockState state = level.getBlockState(pPos);
                    Block block = state.getBlock();
                    
                    if(x * x + z * z > r * r || !inHeight(Math.sqrt(x*x+z*z),y)) { //if out of radius/sphere, only try to place fire
                    	if(block == Blocks.AIR) {
                    		ignite(level,pPos,FIRE_ODDS);
                        }
                    	continue;
                    }
                    //blocks that shouldnt explode/be moved
                    if(block == Blocks.BEDROCK || block == Blocks.WATER || block == Blocks.LAVA || block == Blocks.AIR || block == Blocks.FIRE) continue;
                   
                    //System.out.println("x: "+x+"  y: "+y+"  z: "+z+"  r: "+r+"   odds: "+((0d+x*x+z*z)/(1d*r*r)));
                    if(random.nextDouble() > (0d+x*x+z*z)/(1d*r*r) && (y < 0 ? random.nextDouble() > -1d*y/(2d*d) : true)	) {
                    	level.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
                    }
                    /*
                    if(state.isSolidRender(level, pPos)) {
                    	float xMotion = random.nextFloat()* s - s / 2f;
                        float yMotion = (random.nextFloat()+0.1f) * s * 1.2f;
                        float zMotion = random.nextFloat()* s - s / 2f;
                        
                        FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(level,pPos.getX(),pPos.getY(),pPos.getZ(),state);
                        fallingBlockEntity.setDeltaMovement(xMotion, yMotion, zMotion);
                        
                        level.addFreshEntity(fallingBlockEntity);
                    }
                    */
                    ignite(level,pPos,FIRE_ODDS);
                }
            }
        }
	}
	
	public static boolean inHeight(double xz, double y) {
		return ( 0.00002*Math.pow(xz, 4)-5 <= y /*&& -0.00002*Math.pow(xz, 4)+5 >= y */);
	}
	
	//ignite a given block with a certain probability
	public static void ignite(Level level, BlockPos pPos, double odds) {
		BlockPos pos = pPos.below();
		BlockState state = level.getBlockState(pos);
		
		if((state.is(BlockTags.LEAVES) || state.isSolidRender(level, pos)) && random.nextDouble() < odds) {
        	level.setBlockAndUpdate(pPos, Blocks.FIRE.defaultBlockState());
        }
	}
	
	// Non Static
	
	private Random rrandom = new Random();
	
	private Level level;
	
	private BlockPos pos;
	private Vec3 center; // Vec3 of BlockPos pos
	private int radius;
	private float strength;
	private float[][] bblocks; // x|z
	
	private static final float FIRE_ODDS = 0.1f;
	
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
		Map.entry(Blocks.GRASS_BLOCK, Blocks.DIRT)
	);
	
	public ExplosionUtils(Level level, BlockPos pos, int radius, float strength) {
		this.level = level;
		this.pos = pos;
		this.radius = radius;
		this.strength = strength;
		this.center = new Vec3(pos.getX(),pos.getY(),pos.getZ());
		bblocks = new float[2*radius+1][2*radius+1];
		
		this.maxHeight = radius/3;
		this.minHeight = 2;
		this.bbblocks = new float[2*radius+1][minHeight+maxHeight+1][2*radius+1];
	}
	
	// Methods
	
	public void tExplode() {
		
		bblocks[radius][radius] = 1;
		
		if(!level.getBlockState(pos).is(Blocks.BEDROCK)) {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
		
		int dx = 0;
		int dz = 0;
		
		for(int i = 1;i <= radius; i++) {
			
			dx++;
			dz--;
			
			for(int j = 1;j <= 2*i; j++) {
				dx--;
				
				calc(dx, dz);
			}
			for(int j = 1;j <= 2*i; j++) {
				dz++;
				
				calc(dx, dz);
			}
			for(int j = 1;j <= 2*i; j++) {
				dx++;
				
				calc(dx, dz);
			}
			for(int j = 1;j <= 2*i; j++) {
				dz--;
				
				calc(dx, dz);
			}
		}
	}
	
	public void calc(int dx, int dz) {
		float rad = 1f - ( (dx*dx+dz*dz) / (float)(radius*radius) );
		
		if(rad >= 0) {
			BlockPos pPos = new BlockPos(center.add(dx,0,dz));

			float p = level.getBlockState(pPos).getExplosionResistance(level, pPos, null);
			
			if(p > 10) {
				p = 0;
			} else {
				p = 1-(float)(p / 10);
			}
			
			int[] t = xz(dx,dz);
			bblocks[dx+radius][dz+radius] = p * bblocks[t[0]+radius][t[1]+radius];
			
			//remove(level, pPos, bblocks[dx+radius][dz+radius]*rad, FIRE_ODDS );
			//color(level, pPos, bblocks[dx+radius][dz+radius]*rad);
			color2(level, pPos, bblocks[dx+radius][dz+radius]*rad);
		}
	}
	
	public int[] xz(int dx, int dz) {
		float ang = 0;
		if(dx != 0) ang = (float)(Math.atan(dz/(float)dx)*180/Math.PI);
		
		if(dx < 0 && dz > 0) ang+=180;
		else if(dx < 0 && dz < 0) ang-=180;
		else if(dx == 0 && dz > 0) ang=90;
		else if(dx == 0 && dz < 0) ang=-90;
		else if(dz == 0 && dx > 0) ang=0;
		else if(dz == 0 && dx < 0) ang=180;
		
		if(ang < -150) {
			dx++;
		} else if(ang < -120) {
			dz++;
			dx++;
		} else if(ang < -60) {
			dz++;
		} else if(ang < -30) {
			dz++;
			dx--;
		} else if(ang < 30) {
			dx--;
		} else if(ang < 60) {
			dz--;
			dx--;
		} else if(ang < 120) {
			dz--;
		} else if(ang < 150) {
			dz--;
			dx++;
		} else if(ang <= 180) {
			dx++;
		}
		
		return new int[] {dx, dz};
	}
	
	// Explode 2
	
	private float[][][] bbblocks; // x|y|z

	int maxHeight;
	int minHeight;
	
	public void tExplode2() {
		System.out.println("RUN EXPLODE 2");
		
		bbblocks[radius][minHeight][radius] = 1; // Center of Explosion
		
		if(!level.getBlockState(pos).is(Blocks.BEDROCK)) {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
		int dy = -minHeight;
		
		for(int k = 0; k <= minHeight+maxHeight; k++) {
			
			int dx = 0;
			int dz = 0;
			
			int yradius = radiusAtY(dy);
			
			for(int i = 1;i <= yradius; i++) {
				
				dx++;
				dz--;
				
				for(int j = 1;j <= 2*i; j++) {
					dx--;
					
					calc2(dx, dy, dz, yradius);
				}
				for(int j = 1;j <= 2*i; j++) {
					dz++;
					
					calc2(dx, dy, dz, yradius);
				}
				for(int j = 1;j <= 2*i; j++) {
					dx++;
					
					calc2(dx, dy, dz, yradius);
				}
				for(int j = 1;j <= 2*i; j++) {
					dz--;
					
					calc2(dx, dy, dz, yradius);
				}
				
			}

			dy++;
		}
	}
	
	public void calc2(int dx, int dy, int dz, int radius) {
		float rad = 1f - ( (dx*dx+dz*dz) / (float)(radius*radius) );
		
		if(rad >= 0) {
			BlockPos pPos = new BlockPos(center.add(dx,dy,dz));

			float p = level.getBlockState(pPos).getExplosionResistance(level, pPos, null);
			
			if(p > 10) {
				p = 0;
			} else {
				p = 1-(float)(p / 10);
			}
			
			int[] t = xyz(dx,dy,dz);
			bbblocks[dx+radius][dy+minHeight][dz+radius] = p * bbblocks[t[0]+radius][t[1]+minHeight][t[2]+radius];
			
			color2(level, pPos, bbblocks[dx+radius][dy+minHeight][dz+radius]*rad);
			//color(level, pPos, bblocks[dx+radius][dz+radius]*rad);;
		}
	}
	
	public int[] xyz(int dx, int dy, int dz) {
		float angxz = 0;
		if(dx != 0) angxz = (float)(Math.atan(dz/(float)dx)*180/Math.PI);
		
		if(dx < 0 && dz > 0) angxz+=180;
		else if(dx < 0 && dz < 0) angxz-=180;
		else if(dx == 0 && dz > 0) angxz=90;
		else if(dx == 0 && dz < 0) angxz=-90;
		else if(dz == 0 && dx > 0) angxz=0;
		else if(dz == 0 && dx < 0) angxz=180;
		
		if(angxz < -150) {
			dx++;
		} else if(angxz < -120) {
			dz++;
			dx++;
		} else if(angxz < -60) {
			dz++;
		} else if(angxz < -30) {
			dz++;
			dx--;
		} else if(angxz < 30) {
			dx--;
		} else if(angxz < 60) {
			dz--;
			dx--;
		} else if(angxz < 120) {
			dz--;
		} else if(angxz < 150) {
			dz--;
			dx++;
		} else if(angxz <= 180) {
			dx++;
		}
		
		
		float angy = 0;
		float dxz = (float)Math.sqrt(dx*dx+dz*dz);
		if(dx != 0 || dz != 0) angy = (float)(Math.atan(dy/dxz)*180/Math.PI);
		
		if(dxz == 0 && dy > 0) angy=90;
		else if(dxz == 0 && dy < 0) angy=-90;
		else if(dy == 0) angy=0;
		
		if(angy < -60) {
			dy+=2;
		} else if(angy < -30) {
			dy++;
		} else if(angy < 30) {
			//dy--;
		} else if(angy < 60) {
			dy--;
		} else if(angy < 90) {
			dy-=2;
		}
		
		return new int[] {dx, dy, dz};
	}
	
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
	
	// Explode 3
	
	public void tExplode3() {
		bbblocks[radius][minHeight][radius] = 1; // Center of Explosion -> p = 1
		
		if(!level.getBlockState(pos).is(Blocks.BEDROCK)) {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
		
		int dy = 0; // Beginning at height of center
		
		for(int k = 0; k <= maxHeight; k++) { // Moving upwards until maxHeight is reached
			
			int dx = 0; // Starting at center of circle for every height
			int dz = 0;
			
			int yradius = radiusAtY(dy); // Calculating radius at given height, since radius declines when moving vertically away from center
			
			if(dy != 0) { // If center of circle isnt center of explosion, it should be calculated too
				calc3(dx, dy, dz, yradius);
			}
			
			for(int i = 1;i <= yradius; i++) { // Starting to move spirally around center of circle, always doing one ring further out. Always from O to x / O---x
				
				dx++;
				dz--;
				
				for(int j = 1;j <= 2*i; j++) {		//	_____
					dx--;							//	|	|
													//  |	|
					calc3(dx, dy, dz, yradius);		//	x---O
				}
				for(int j = 1;j <= 2*i; j++) {		//  x____
					dz++;							//	|	|
													//	|	|
					calc3(dx, dy, dz, yradius);		//  O___|
				}
				for(int j = 1;j <= 2*i; j++) {		//	O---x
					dx++;							//	|	|
													//  |	|
					calc3(dx, dy, dz, yradius);		//	|___|
				}
				for(int j = 1;j <= 2*i; j++) {		//  ____O
					dz--;							//	|	|
													//	|	|
					calc3(dx, dy, dz, yradius);		//  |___x
				}
				
			}

			dy++; // Moving upwards by one
		}
		
		dy = -1; // Now beginning from underneath the explosion
		
		for(int k = 0; k < minHeight; k++) { // Moving downwards until minHeight is reached, starting at height under center
			
			int dx = 0;
			int dz = 0;
			
			int yradius = radiusAtY(dy);
			
			if(dy != 0) {
				calc3(dx, dy, dz, yradius);
			}
			
			for(int i = 1;i <= yradius; i++) {
				
				dx++;
				dz--;
				
				for(int j = 1;j <= 2*i; j++) {
					dx--;
					
					calc3(dx, dy, dz, yradius);
				}
				for(int j = 1;j <= 2*i; j++) {
					dz++;
					
					calc3(dx, dy, dz, yradius);
				}
				for(int j = 1;j <= 2*i; j++) {
					dx++;
					
					calc3(dx, dy, dz, yradius);
				}
				for(int j = 1;j <= 2*i; j++) {
					dz--;
					
					calc3(dx, dy, dz, yradius);
				}
				
			}

			dy--;
		}
	}
	
	public void calc3(int dx, int dy, int dz, int radius) {
		
		float rad = 1f - ( (dx*dx+dz*dz) / (float)(radius*radius) ); // The relative distance of the current position between the center of the circle and edge of the circle --> used for odds
		
		if(rad >= 0) {
			BlockPos curPos = new BlockPos(center.add(dx,dy,dz)); // The position currently being dealt with => center + deltaVector
			
			// Debug
			//System.out.println("-\npos hier: "+pPos.getX()+" "+pPos.getY()+" "+pPos.getZ());
			// E Debug
			
			float p = level.getBlockState(curPos).getExplosionResistance(level, curPos, null); // Returns the explosion-resistance of the block at current-position
			
			if(p > 10) { // Probability to explode is 0
				p = 0;
			} else {
				p = 1-(float)(p / 10); // Calculating probability using explosion-resistance
			}
			
			
			int[] t = xyz2(dx,dy,dz); // Obtaining index of block explosion "meets" before this block, to use its probability in calculation
			
			bbblocks[dx+radius][dy+minHeight][dz+radius] = p * bbblocks[t[0]+radius][t[1]+minHeight][t[2]+radius];
			
			BlockPos ppPos = new BlockPos(center.add(t[0],t[1],t[2]));
			System.out.println("Pos here: "+pPos.getX()+" "+pPos.getY()+" "+pPos.getZ()+"  Pos there: "+ppPos.getX()+" "+ppPos.getY()+" "+ppPos.getZ());
			System.out.println("Changed Value at: "+(dx+radius)+" "+(dy+radius)+" "+(dz+radius)+"  to  "+bbblocks[dx+radius][dy+minHeight][dz+radius]);
			// Debug
			//System.out.println("dxyz: " +dx+" "+dy+" "+dz+"  -> "+t[0]+" "+t[1]+" "+t[2]);
			//System.out.println("p hier: "+p+" ?= "+bbblocks[dx+radius][dy+minHeight][dz+radius]);
			//BlockPos ppPos = new BlockPos(center.add(t[0],t[1],t[2]));
			//System.out.println("pos da: "+ppPos.getX()+" "+ppPos.getY()+" "+ppPos.getZ());
			//System.out.println("p da: "+bbblocks[t[0]+radius][t[1]+minHeight][t[2]+radius]);
			// E Debug
			
			color2(level, pPos, bbblocks[dx+radius][dy+minHeight][dz+radius]*rad);
			//color(level, pPos, bblocks[dx+radius][dz+radius]*rad);;
		}
	}
	
	public int[] xyz2(int dx, int dy, int dz) {
		Vec3 d = new Vec3(dx,dy,dz);
		d = d.normalize();
		
		System.out.println("dxyz: " +dx+" "+dy+" "+dz+"  norm -> "+d.x+" "+d.y+" "+d.z);
		
		dx = (int)(dx + 0.5 - d.x); if(dx < 0) dx--;
		dy = (int)(dy + 0.5 - d.y); if(dy < 0) dy--;
		dz = (int)(dz + 0.5 - d.z); if(dz < 0) dz--;
		
		return new int[] {dx, dy, dz};
	}
	
	// Change Blocks with p
	
	public void color(Level level, BlockPos pos, float p) {
		pos = new BlockPos(pos.getX(), pos.getY()-5, pos.getZ());
		
		if(p < 0.03) {
			level.setBlockAndUpdate(pos, Blocks.BLACK_CONCRETE.defaultBlockState());
		} else if(p < 0.1) {
			level.setBlockAndUpdate(pos, Blocks.LIME_CONCRETE.defaultBlockState());
		} else if(p < 0.3) {
			level.setBlockAndUpdate(pos, Blocks.GREEN_CONCRETE.defaultBlockState());
		} else if(p < 0.5) {
			level.setBlockAndUpdate(pos, Blocks.YELLOW_CONCRETE.defaultBlockState());
		} else if(p < 0.7) {
			level.setBlockAndUpdate(pos, Blocks.ORANGE_CONCRETE.defaultBlockState());
		} else if(p < 1) {
			level.setBlockAndUpdate(pos, Blocks.RED_CONCRETE.defaultBlockState());
		} else {
			level.setBlockAndUpdate(pos, Blocks.BLUE_CONCRETE.defaultBlockState());
		}
	}
	
	public void color2(Level level, BlockPos pos, float p) {
		pos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
		if(!level.getBlockState(pos).is(Blocks.AIR)) return;
		
		if(p < 0.03) {
			level.setBlockAndUpdate(pos, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
		} else if(p < 0.1) {
			level.setBlockAndUpdate(pos, Blocks.LIME_STAINED_GLASS.defaultBlockState());
		} else if(p < 0.3) {
			level.setBlockAndUpdate(pos, Blocks.GREEN_STAINED_GLASS.defaultBlockState());
		} else if(p < 0.5) {
			level.setBlockAndUpdate(pos, Blocks.YELLOW_STAINED_GLASS.defaultBlockState());
		} else if(p < 0.7) {
			level.setBlockAndUpdate(pos, Blocks.ORANGE_STAINED_GLASS.defaultBlockState());
		} else if(p < 1) {
			level.setBlockAndUpdate(pos, Blocks.RED_STAINED_GLASS.defaultBlockState());
		} else if(p == 1){
			level.setBlockAndUpdate(pos, Blocks.WHITE_STAINED_GLASS.defaultBlockState());
		} else {
			level.setBlockAndUpdate(pos, Blocks.BLUE_STAINED_GLASS.defaultBlockState());
		}
	}
	
	public void remove(Level level, BlockPos pos, float p_d, float p_f) {
		if(rrandom.nextFloat() < p_d && p_d > 0.03) { // Destroy Block completely
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			if(rrandom.nextFloat() < p_f) {
				level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
			}
		} else if(rrandom.nextFloat() < p_d && p_d > 0.03) { // "Damage" Block
			Block block = level.getBlockState(pos).getBlock();
			
			if(BLOCKS_DAMAGED.containsKey(block)) {
				level.setBlockAndUpdate(pos, BLOCKS_DAMAGED.get(block).defaultBlockState());
			}
		}
	}
}
