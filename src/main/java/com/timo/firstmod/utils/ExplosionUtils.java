package com.timo.firstmod.utils;

import java.util.Random;

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
                   
                    System.out.println("x: "+x+"  y: "+y+"  z: "+z+"  r: "+r+"   odds: "+((0d+x*x+z*z)/(1d*r*r)));
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
}
