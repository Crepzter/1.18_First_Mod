package com.timo.firstmod.common.block;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.timo.firstmod.FirstMod;
import com.timo.firstmod.common.block.entity.LightningBlockEntity;
import com.timo.firstmod.core.init.BlockEntityInit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LightningBlock extends Block implements EntityBlock {
	
	public LightningBlock(Properties properties) {
		super(properties);
	}

	@Override //when block is right clicked
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
		final var lightningBE = (LightningBlockEntity)level.getBlockEntity(blockPos);
		
		if(!level.isClientSide() && !player.isShiftKeyDown()) {
			lightningBE.playerUses.put(player.getUUID(),
					lightningBE.playerUses.containsKey(player.getUUID())
                            ? lightningBE.playerUses.get(player.getUUID()) + 1
                            : 1);
            level.blockUpdated(blockPos, this);
            lightningBE.setChanged();
			
			LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
			lightning.setPos(player.position());
			level.addFreshEntity(lightning);

			return InteractionResult.SUCCESS;
		}

		if (player.isShiftKeyDown() && !level.isClientSide() && interactionHand == InteractionHand.MAIN_HAND) {
            player.displayClientMessage(new TextComponent("Player has clicked " + lightningBE.playerUses.get(player.getUUID()) + " times!"), false);
        }
        return InteractionResult.PASS;
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> type) {
		return level.isClientSide ? null : ($0, $1, $2, blockEntity) -> ((LightningBlockEntity)blockEntity).tick();
	}
	
	
	private static final VoxelShape SHAPE = Stream.of(
			Block.box(1, 0, 1, 15, 1, 15),
			Block.box(7, 1, 8, 8, 9, 9),
			Block.box(8, 1, 7, 9, 3, 8)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlockEntityInit.LIGHTNING_BLOCK.get().create(pos, state);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
			CollisionContext context) {
		return SHAPE;
	}
	
	//gecko lib
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
	
}
