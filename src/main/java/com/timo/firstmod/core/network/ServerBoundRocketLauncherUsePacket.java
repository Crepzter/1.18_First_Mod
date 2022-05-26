package com.timo.firstmod.core.network;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.timo.firstmod.common.item.RocketLauncher;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class ServerBoundRocketLauncherUsePacket {
	@Nullable
	public final BlockPos targetB;
	@Nullable
	public final UUID targetId;
	
	public final int mode; //0 --> blockpos, 1 --> uuid
	
	public ServerBoundRocketLauncherUsePacket(BlockPos targetB) { //constructor for blockpos target
		this.targetB = targetB;
		targetId = null;
		mode = 0;
	}
	
	public ServerBoundRocketLauncherUsePacket(UUID targetId) { //constructor for entity target
		this.targetId = targetId;
		targetB = null;
		mode = 1;
	}
	
	/*public ServerBoundRocketLauncherUsePacket(FriendlyByteBuf buffer) { //"decode constructor"
		this(buffer.readUUID()); //TODO DECODE
	}*/
	
	public static ServerBoundRocketLauncherUsePacket decode(FriendlyByteBuf buffer) { //other decoder
		int pmode = buffer.readInt();
		
		if(pmode == 0) {
			return new ServerBoundRocketLauncherUsePacket(buffer.readBlockPos());
		} else /*if(pmode == 1)*/ {
			return new ServerBoundRocketLauncherUsePacket(buffer.readUUID());
		}
	}
	
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(mode);
		if(mode == 0) {
			buffer.writeBlockPos(targetB);
		}
		else if(mode == 1) {
			buffer.writeUUID(targetId);
		}
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> ctx) {
		final var success = new AtomicBoolean(false);
		ctx.get().enqueueWork(() -> {
			final ItemStack item = ctx.get().getSender().getItemInHand(InteractionHand.MAIN_HAND);
			if(item.getItem() instanceof RocketLauncher rocketLauncher) {
				if(mode == 1) rocketLauncher.shoot(ctx.get().getSender().level,ctx.get().getSender(),item,targetId);
				else if(mode == 0) rocketLauncher.shoot(ctx.get().getSender().level,ctx.get().getSender(),item,targetB);
			}
			success.set(true);
		});
		ctx.get().setPacketHandled(true);
		return success.get();
	}
}
