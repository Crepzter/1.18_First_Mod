package com.timo.firstmod.utils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileUtils {
	
	@Nullable
	public static LivingEntity getEntityLookAt(Level level, Entity source, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter, double range) {
		LivingEntity entity = null;
		range = range*range;
		
		List<Entity> entities = level.getEntities(source, boundingBox, filter);
		for(Entity entity1 : entities)
		{
			AABB aabb = entity1.getBoundingBox();
			
			aabb = aabb.inflate( scaleAABB(aabb.getXsize()) , scaleAABB(aabb.getYsize()) , scaleAABB(aabb.getZsize()) );
			
			Optional<Vec3> optional = aabb.clip(startVec, endVec);
			if (optional.isPresent()) {
				double disSqr = startVec.distanceToSqr(optional.get());
				
				if (disSqr < range && entity1 instanceof LivingEntity) {
					entity = (LivingEntity)entity1;
					range = disSqr;
				}
			}
		}
		
		return entity;
	}
	
	public static double scaleAABB(double a) {
		if(a > 6) return 0;
		if(a > 0.6) return a * -0.16666 + 1.1666;
		if(a > 0.125) return a * -12 + 7;
		else return 5.5;
	}
}
