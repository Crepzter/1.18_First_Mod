package com.timo.firstmod.core.network;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.timo.firstmod.client.ClientAccess;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class ClientBoundRocketProjectileExplosionPacket {
	@Nullable
	public final BlockPos pos;
	
	public ClientBoundRocketProjectileExplosionPacket(BlockPos pos) { //constructor for blockpos target
		this.pos = pos;
	}
	
	public ClientBoundRocketProjectileExplosionPacket(FriendlyByteBuf buffer) { //"decode constructor"
		this(buffer.readBlockPos());
	}
	
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> ctx) {
		final var success = new AtomicBoolean(false);
		ctx.get().enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn( Dist.CLIENT, () -> () -> success.set(ClientAccess.rocketProjectileExplode(pos)) );
		});
		ctx.get().setPacketHandled(true);
		return success.get();
	}
}
