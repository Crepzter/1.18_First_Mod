package com.timo.firstmod.event.client;

import com.timo.firstmod.FirstMod;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = FirstMod.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {
	private ClientForgeEvents() {
	}

	@SubscribeEvent
	public static void clientTick(ClientTickEvent event) {
		//final var player = Minecraft.getInstance().player;
	}
	
	@SubscribeEvent
	public static void renderPlayer(RenderPlayerEvent event) {
		Player player = event.getPlayer();
		Level level = player.getLevel();
		
		for(ItemStack stack : player.getArmorSlots()) 
		{
			if(stack.is(Items.NETHERITE_CHESTPLATE) && event.getPartialTick() > 0.8d && player.getDeltaMovement().lengthSqr() < 0.02d && !player.isSprinting())
			{
				double anim_length = 40;
				double delta_pi = (player.tickCount % anim_length) / anim_length * 6.283184d;
				Vec3 pos = player.getPosition(0F).add(0,0.05,0);
				
				/*
				// Diagonal Particles
				// 1
				Vec2 xz = rotate( new Vec2(0,1) , delta_pi );
				double y = (Math.sin(delta_pi) + 1) / 2.0d * 1.8d;
				
				level.addParticle(ParticleTypes.FLAME, pos.x+xz.x, pos.y+y, pos.z+xz.y, 0.0d, 0.03d, 0.0d);
				
				// 2
				xz = rotate( xz , 3.14159d );
				
				level.addParticle(ParticleTypes.FLAME, pos.x+xz.x, pos.y+y, pos.z+xz.y, 0.0d, 0.03d, 0.0d);
				
				// Base
				// 1
				xz = rotate( new Vec2((float)Math.sin(0.785398d),(float)Math.cos(0.785398d)) , delta_pi );
				
				level.addParticle(ParticleTypes.SMALL_FLAME, pos.x+xz.x, pos.y, pos.z+xz.y, 0.0d, 0.005d, 0.0d);
				level.addParticle(ParticleTypes.SMOKE, pos.x+xz.x, pos.y-0.02, pos.z+xz.y, 0.0d, 0.005d, 0.0d);
				
				// 2
				xz = rotate( xz , 3.14159d );
				
				level.addParticle(ParticleTypes.SMALL_FLAME, pos.x+xz.x, pos.y, pos.z+xz.y, 0.0d, 0.005d, 0.0d);
				level.addParticle(ParticleTypes.SMOKE, pos.x+xz.x, pos.y-0.02, pos.z+xz.y, 0.0d, 0.005d, 0.0d);
				*/
				Vec2 xz = rotate( new Vec2(0.6f,0f) , delta_pi );
				
				level.addParticle(ParticleTypes.FLAME, pos.x+xz.x, pos.y, pos.z+xz.y, xz.x*0.01d, 0.25d, xz.y*0.01d);
				level.addParticle(ParticleTypes.SMOKE, pos.x+xz.x, pos.y-0.02, pos.z+xz.y, 0.0d, -0.005d, 0.0d);
				
				// 2
				xz = rotate( xz , 3.14159d );
				
				level.addParticle(ParticleTypes.FLAME, pos.x+xz.x, pos.y, pos.z+xz.y, xz.x*0.01d, 0.25d, xz.y*0.01d);
				level.addParticle(ParticleTypes.SMOKE, pos.x+xz.x, pos.y-0.02, pos.z+xz.y, 0.0d, -0.005d, 0.0d);
			}
		}
	}
	
	public static Vec3 particle(int ticks, Vec3 pos) {
		double anim_length = 40;
		double delta_pi = (ticks % anim_length) / anim_length *6.283184d;
		
		//System.out.println("ticks: "+ticks+"  modulo: " + (ticks % anim_length) + "  delta: "+delta_pi);
		
		//X,Z
		double x = Math.sin(delta_pi);
		double z = Math.cos(delta_pi);
		
		//Y
		double y = (Math.sin(delta_pi) + 1) / 2.0d * 2.0d;
		
		return new Vec3(pos.x+x,pos.y+y,pos.z+z);
	}
	
	public static Vec2 rotate(Vec2 vec, double ang) {
		double sin = Math.sin(ang);
		double cos = Math.cos(ang);
		float x = (float)(cos*vec.x - sin*vec.y);
		float y = (float)(sin*vec.x + cos*vec.y);
		return new Vec2(x, y);
	}
}
