package net.crazysnailboy.mods.magnetpistons.block;

import net.crazysnailboy.mods.magnetpistons.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ShapeUtils;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import java.util.Random;


public class BlockMagnetPistonExtension extends BlockDirectional {
	public static final BooleanProperty SHORT;
	protected static final VoxelShape PISTON_EXTENSION_EAST_AABB;
	protected static final VoxelShape PISTON_EXTENSION_WEST_AABB;
	protected static final VoxelShape PISTON_EXTENSION_SOUTH_AABB;
	protected static final VoxelShape PISTON_EXTENSION_NORTH_AABB;
	protected static final VoxelShape PISTON_EXTENSION_UP_AABB;
	protected static final VoxelShape PISTON_EXTENSION_DOWN_AABB;
	protected static final VoxelShape UP_ARM_AABB;
	protected static final VoxelShape DOWN_ARM_AABB;
	protected static final VoxelShape SOUTH_ARM_AABB;
	protected static final VoxelShape NORTH_ARM_AABB;
	protected static final VoxelShape EAST_ARM_AABB;
	protected static final VoxelShape WEST_ARM_AABB;
	protected static final VoxelShape SHORT_UP_ARM_AABB;
	protected static final VoxelShape SHORT_DOWN_ARM_AABB;
	protected static final VoxelShape SHORT_SOUTH_ARM_AABB;
	protected static final VoxelShape SHORT_NORTH_ARM_AABB;
	protected static final VoxelShape SHORT_EAST_ARM_AABB;
	protected static final VoxelShape SHORT_WEST_ARM_AABB;

	public BlockMagnetPistonExtension(Builder p_i48280_1_) {
		super(p_i48280_1_);
		this.setDefaultState(this.stateContainer.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	private VoxelShape func_196424_i(IBlockState p_196424_1_) {
		switch(p_196424_1_.getValue(FACING)) {
			case DOWN:
			default:
				return PISTON_EXTENSION_DOWN_AABB;
			case UP:
				return PISTON_EXTENSION_UP_AABB;
			case NORTH:
				return PISTON_EXTENSION_NORTH_AABB;
			case SOUTH:
				return PISTON_EXTENSION_SOUTH_AABB;
			case WEST:
				return PISTON_EXTENSION_WEST_AABB;
			case EAST:
				return PISTON_EXTENSION_EAST_AABB;
		}
	}

	public VoxelShape getShape(IBlockState p_getShape_1_, IBlockReader p_getShape_2_, BlockPos p_getShape_3_) {
		return ShapeUtils.or(this.func_196424_i(p_getShape_1_), this.func_196425_x(p_getShape_1_));
	}

	private VoxelShape func_196425_x(IBlockState p_196425_1_) {
		boolean lvt_2_1_ = p_196425_1_.getValue(SHORT);
		switch(p_196425_1_.getValue(FACING)) {
			case DOWN:
			default:
				return lvt_2_1_ ? SHORT_DOWN_ARM_AABB : DOWN_ARM_AABB;
			case UP:
				return lvt_2_1_ ? SHORT_UP_ARM_AABB : UP_ARM_AABB;
			case NORTH:
				return lvt_2_1_ ? SHORT_NORTH_ARM_AABB : NORTH_ARM_AABB;
			case SOUTH:
				return lvt_2_1_ ? SHORT_SOUTH_ARM_AABB : SOUTH_ARM_AABB;
			case WEST:
				return lvt_2_1_ ? SHORT_WEST_ARM_AABB : WEST_ARM_AABB;
			case EAST:
				return lvt_2_1_ ? SHORT_EAST_ARM_AABB : EAST_ARM_AABB;
		}
	}

	public boolean isTopSolid(IBlockState p_isTopSolid_1_) {
		return p_isTopSolid_1_.getValue(FACING) == EnumFacing.UP;
	}

	public void onBlockHarvested(World p_onBlockHarvested_1_, BlockPos p_onBlockHarvested_2_, IBlockState p_onBlockHarvested_3_, EntityPlayer p_onBlockHarvested_4_) {
		if (!p_onBlockHarvested_1_.isRemote && p_onBlockHarvested_4_.capabilities.isCreativeMode) {
			BlockPos lvt_5_1_ = p_onBlockHarvested_2_.offset(p_onBlockHarvested_3_.getValue(FACING).getOpposite());
			Block lvt_6_1_ = p_onBlockHarvested_1_.getBlockState(lvt_5_1_).getBlock();
			if (lvt_6_1_ == Blocks.PISTON || lvt_6_1_ == Blocks.STICKY_PISTON || lvt_6_1_ instanceof BlockMagnetPistonBase) {
				p_onBlockHarvested_1_.removeBlock(lvt_5_1_);
			}
		}

		super.onBlockHarvested(p_onBlockHarvested_1_, p_onBlockHarvested_2_, p_onBlockHarvested_3_, p_onBlockHarvested_4_);
	}

	public void onReplaced(IBlockState p_onReplaced_1_, World p_onReplaced_2_, BlockPos p_onReplaced_3_, IBlockState p_onReplaced_4_, boolean p_onReplaced_5_) {
		if (p_onReplaced_1_.getBlock() != p_onReplaced_4_.getBlock()) {
			super.onReplaced(p_onReplaced_1_, p_onReplaced_2_, p_onReplaced_3_, p_onReplaced_4_, p_onReplaced_5_);
			EnumFacing lvt_6_1_ = p_onReplaced_1_.getValue(FACING).getOpposite();
			p_onReplaced_3_ = p_onReplaced_3_.offset(lvt_6_1_);
			IBlockState lvt_7_1_ = p_onReplaced_2_.getBlockState(p_onReplaced_3_);
			if ((lvt_7_1_.getBlock() == Blocks.PISTON || lvt_7_1_.getBlock() == Blocks.STICKY_PISTON || lvt_7_1_.getBlock() instanceof BlockMagnetPistonBase) && lvt_7_1_.getValue(BlockPistonBase.EXTENDED)) {
				lvt_7_1_.dropBlockAsItem(p_onReplaced_2_, p_onReplaced_3_, 0);
				p_onReplaced_2_.removeBlock(p_onReplaced_3_);
			}

		}
	}

	public boolean isFullCube(IBlockState p_isFullCube_1_) {
		return false;
	}

	public int quantityDropped(IBlockState p_quantityDropped_1_, Random p_quantityDropped_2_) {
		return 0;
	}

	public IBlockState updatePostPlacement(IBlockState p_updatePostPlacement_1_, EnumFacing p_updatePostPlacement_2_, IBlockState p_updatePostPlacement_3_, IWorld p_updatePostPlacement_4_, BlockPos p_updatePostPlacement_5_, BlockPos p_updatePostPlacement_6_) {
		return p_updatePostPlacement_2_.getOpposite() == p_updatePostPlacement_1_.getValue(FACING) && !p_updatePostPlacement_1_.isValidPosition(p_updatePostPlacement_4_, p_updatePostPlacement_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_updatePostPlacement_1_, p_updatePostPlacement_2_, p_updatePostPlacement_3_, p_updatePostPlacement_4_, p_updatePostPlacement_5_, p_updatePostPlacement_6_);
	}

	public boolean isValidPosition(IBlockState p_isValidPosition_1_, IWorldReaderBase p_isValidPosition_2_, BlockPos p_isValidPosition_3_) {
		Block block = p_isValidPosition_2_.getBlockState(p_isValidPosition_3_.offset(p_isValidPosition_1_.getValue(FACING).getOpposite())).getBlock();
		return block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.MOVING_PISTON || block == ModBlocks.MAGNET_PISTON_MOVING || block instanceof BlockMagnetPistonBase;
	}

	public void neighborChanged(IBlockState p_neighborChanged_1_, World p_neighborChanged_2_, BlockPos p_neighborChanged_3_, Block p_neighborChanged_4_, BlockPos p_neighborChanged_5_) {
		if (p_neighborChanged_1_.isValidPosition(p_neighborChanged_2_, p_neighborChanged_3_)) {
			BlockPos lvt_6_1_ = p_neighborChanged_3_.offset(p_neighborChanged_1_.getValue(FACING).getOpposite());
			p_neighborChanged_2_.getBlockState(lvt_6_1_).neighborChanged(p_neighborChanged_2_, lvt_6_1_, p_neighborChanged_4_, p_neighborChanged_5_);
		}

	}

	public ItemStack getItem(IBlockReader p_getItem_1_, BlockPos p_getItem_2_, IBlockState p_getItem_3_) {
		return new ItemStack(ModBlocks.MAGNET_PISTON1);
	}

	public IBlockState withRotation(IBlockState p_withRotation_1_, Rotation p_withRotation_2_) {
		return p_withRotation_1_.withProperty(FACING, p_withRotation_2_.rotate(p_withRotation_1_.getValue(FACING)));
	}

	public IBlockState withMirror(IBlockState p_withMirror_1_, Mirror p_withMirror_2_) {
		return p_withMirror_1_.withRotation(p_withMirror_2_.toRotation(p_withMirror_1_.getValue(FACING)));
	}

	protected void fillStateContainer(net.minecraft.state.StateContainer.Builder<Block, IBlockState> p_fillStateContainer_1_) {
		p_fillStateContainer_1_.add(FACING, SHORT);
	}

	public BlockFaceShape getBlockFaceShape(IBlockReader p_getBlockFaceShape_1_, IBlockState p_getBlockFaceShape_2_, BlockPos p_getBlockFaceShape_3_, EnumFacing p_getBlockFaceShape_4_) {
		return p_getBlockFaceShape_4_ == p_getBlockFaceShape_2_.getValue(FACING) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	public boolean allowsMovement(IBlockState p_allowsMovement_1_, IBlockReader p_allowsMovement_2_, BlockPos p_allowsMovement_3_, PathType p_allowsMovement_4_) {
		return false;
	}

	static {
		SHORT = BlockStateProperties.SHORT;
		PISTON_EXTENSION_EAST_AABB = Block.makeCuboidShape(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		PISTON_EXTENSION_WEST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
		PISTON_EXTENSION_SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
		PISTON_EXTENSION_NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
		PISTON_EXTENSION_UP_AABB = Block.makeCuboidShape(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		PISTON_EXTENSION_DOWN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
		UP_ARM_AABB = Block.makeCuboidShape(6.0D, -4.0D, 6.0D, 10.0D, 12.0D, 10.0D);
		DOWN_ARM_AABB = Block.makeCuboidShape(6.0D, 4.0D, 6.0D, 10.0D, 20.0D, 10.0D);
		SOUTH_ARM_AABB = Block.makeCuboidShape(6.0D, 6.0D, -4.0D, 10.0D, 10.0D, 12.0D);
		NORTH_ARM_AABB = Block.makeCuboidShape(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 20.0D);
		EAST_ARM_AABB = Block.makeCuboidShape(-4.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
		WEST_ARM_AABB = Block.makeCuboidShape(4.0D, 6.0D, 6.0D, 20.0D, 10.0D, 10.0D);
		SHORT_UP_ARM_AABB = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
		SHORT_DOWN_ARM_AABB = Block.makeCuboidShape(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
		SHORT_SOUTH_ARM_AABB = Block.makeCuboidShape(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 12.0D);
		SHORT_NORTH_ARM_AABB = Block.makeCuboidShape(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 16.0D);
		SHORT_EAST_ARM_AABB = Block.makeCuboidShape(0.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
		SHORT_WEST_ARM_AABB = Block.makeCuboidShape(4.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
	}
}
