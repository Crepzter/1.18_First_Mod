package com.timo.firstmod.common.block;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.timo.firstmod.FirstMod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TestBlock extends HorizontalDirectionalBlock {

	public TestBlock(Properties p_49795_) {
		super(p_49795_);
		registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
		runCalculation(SHAPE.orElse(Shapes.block()));
	}

	@Override //when block is right clicked
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
		if(!level.isClientSide()) {
			if(player.experienceLevel < 5 && !player.isCreative()) {
				level.playSound(player, blockPos, SoundEvents.ANVIL_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);
				return InteractionResult.FAIL;
			}
			if(!player.isCreative()) {
				player.giveExperienceLevels(-4);
				
			}
			level.playSound(player, blockPos, SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundSource.BLOCKS, 1.0f, 1.0f);
			for (int index = 0; index < this.RANDOM.nextInt(10) + 7; index++) {
				Pig pig = EntityType.PIG.create(level);
				pig.setPos(blockPos.getX() + this.RANDOM.nextInt(10) - 5, blockPos.getY(),
						blockPos.getZ() + this.RANDOM.nextInt(10) - 5);
				level.addFreshEntity(pig);
			}
			return InteractionResult.SUCCESS;	
		}
		return InteractionResult.FAIL;
	}
	
	private static final Map<Direction,VoxelShape> SHAPES = new EnumMap<>(Direction.class);
	
	private static final Optional<VoxelShape> SHAPE = Stream.of(
			Block.box(6, 7, 6, 10, 8, 10),
			Block.box(3, 9, 3, 4, 10, 13),
			Block.box(12, 9, 3, 13, 10, 13),
			Block.box(4, 9, 12, 12, 10, 13),
			Block.box(4, 9, 3, 12, 10, 4),
			Block.box(4, 0.009999999999999787, 14.99, 12, 11.01, 15.99),
			Block.box(0.010000000000000009, 0.009999999999999787, 4, 1.01, 11.01, 12),
			Block.box(4, 0.009999999999999787, 0.010000000000000009, 12, 11.01, 1.01),
			Block.box(14.99, 0.009999999999999787, 4, 15.99, 11.01, 12),
			Block.box(0, 0, 0, 16, 1, 16),
			Block.box(5, 0.009999999999999787, 5, 11, 7.01, 11)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR));
	
	protected void runCalculation(VoxelShape shape) {
		for(Direction dir : Direction.values()) {
			SHAPES.put(dir, FirstMod.calculateShapes(dir, shape));
		}
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		// TODO Auto-generated method stub
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
			CollisionContext context) {
		return SHAPES.get(state.getValue(FACING));
	}
}
