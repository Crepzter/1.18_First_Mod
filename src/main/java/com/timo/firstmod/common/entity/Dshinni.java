package com.timo.firstmod.common.entity;

import com.timo.firstmod.core.init.EntityInit;
import com.timo.firstmod.core.init.ItemInit;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class Dshinni extends Animal {

	public Dshinni(EntityType<? extends Animal> entityType, Level level) {
		super(entityType, level);
	}
	
	@Override
	protected void registerGoals() {
		super.registerGoals();
	    this.goalSelector.addGoal(0, new FloatGoal(this));
	    this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
	    this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
	    this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	    this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
	    this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(ItemInit.MAGIC_LAMP.get()), false));
	}
	
	public static AttributeSupplier.Builder createAttributes() {
	   return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, (double)0.65F);
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
		return EntityInit.DSHINNI.get().create(level);
	}

}
