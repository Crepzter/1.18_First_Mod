package com.timo.firstmod.core.network;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.timo.firstmod.client.ClientAccess;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class ClientBoundRocketLauncherUsePacket {
public final boolean shoot;
	
	public ClientBoundRocketLauncherUsePacket(boolean s) {
		shoot = s;
	}
	
	public ClientBoundRocketLauncherUsePacket(FriendlyByteBuf buffer) { //"decode constructor"
		this(buffer.readBoolean());
	}
	
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBoolean(shoot);
	}
	
	/*
	public ClientBoundRocketLauncherUsePacket decode(FriendlyByteBuf buffer) {
		return new ClientBoundRocketLauncherUsePacket(buffer.readBoolean());
	}*/
	
	public boolean handle(Supplier<NetworkEvent.Context> ctx) {
		final var success = new AtomicBoolean(false);
		ctx.get().enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn( Dist.CLIENT, () -> () -> success.set(ClientAccess.updateRocketLauncher()) );
		});
		ctx.get().setPacketHandled(true);
		return success.get();
	}
}
