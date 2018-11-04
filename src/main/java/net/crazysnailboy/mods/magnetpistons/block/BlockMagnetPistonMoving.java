package net.crazysnailboy.mods.magnetpistons.block;

import net.crazysnailboy.mods.magnetpistons.tileentity.TileEntityMagnetPiston;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ShapeUtils;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class BlockMagnetPistonMoving extends BlockContainer {
	public static final DirectionProperty FACING;
	public static final EnumProperty<PistonType> TYPE;

	public BlockMagnetPistonMoving(Builder builder) {
		super(builder);
		this.setDefaultState(this.stateContainer.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, PistonType.DEFAULT));
	}

	@Nullable
	public TileEntity createNewTileEntity(IBlockReader reader) {
		return null;
	}

	public static TileEntity createPiston(IBlockState state, EnumFacing facing, boolean p_196343_2_, boolean p_196343_3_) {
		return new TileEntityMagnetPiston(state, facing, p_196343_2_, p_196343_3_);
	}

	public void onReplaced(IBlockState p_onReplaced_1_, World p_onReplaced_2_, BlockPos p_onReplaced_3_, IBlockState p_onReplaced_4_, boolean p_onReplaced_5_) {
		if (p_onReplaced_1_.getBlock() != p_onReplaced_4_.getBlock()) {
			TileEntity lvt_6_1_ = p_onReplaced_2_.getTileEntity(p_onReplaced_3_);
			if (lvt_6_1_ instanceof TileEntityMagnetPiston) {
				((TileEntityMagnetPiston)lvt_6_1_).clearPistonTileEntity();
			} else {
				super.onReplaced(p_onReplaced_1_, p_onReplaced_2_, p_onReplaced_3_, p_onReplaced_4_, p_onReplaced_5_);
			}

		}
	}

	public void onPlayerDestroy(IWorld p_onPlayerDestroy_1_, BlockPos p_onPlayerDestroy_2_, IBlockState p_onPlayerDestroy_3_) {
		BlockPos lvt_4_1_ = p_onPlayerDestroy_2_.offset(p_onPlayerDestroy_3_.getValue(FACING).getOpposite());
		IBlockState lvt_5_1_ = p_onPlayerDestroy_1_.getBlockState(lvt_4_1_);
		if (lvt_5_1_.getBlock() instanceof BlockPistonBase && lvt_5_1_.getValue(BlockPistonBase.EXTENDED)) {
			p_onPlayerDestroy_1_.removeBlock(lvt_4_1_);
		}

	}

	public boolean isSolid(IBlockState p_isSolid_1_) {
		return false;
	}

	public boolean isFullCube(IBlockState p_isFullCube_1_) {
		return false;
	}

	public boolean onBlockActivated(IBlockState p_onBlockActivated_1_, World p_onBlockActivated_2_, BlockPos p_onBlockActivated_3_, EntityPlayer p_onBlockActivated_4_, EnumHand p_onBlockActivated_5_, EnumFacing p_onBlockActivated_6_, float p_onBlockActivated_7_, float p_onBlockActivated_8_, float p_onBlockActivated_9_) {
		if (!p_onBlockActivated_2_.isRemote && p_onBlockActivated_2_.getTileEntity(p_onBlockActivated_3_) == null) {
			p_onBlockActivated_2_.removeBlock(p_onBlockActivated_3_);
			return true;
		} else {
			return false;
		}
	}

	public IItemProvider getItemDropped(IBlockState p_getItemDropped_1_, World p_getItemDropped_2_, BlockPos p_getItemDropped_3_, int p_getItemDropped_4_) {
		return Items.AIR;
	}

	public void dropBlockAsItemWithChance(IBlockState p_dropBlockAsItemWithChance_1_, World p_dropBlockAsItemWithChance_2_, BlockPos p_dropBlockAsItemWithChance_3_, float p_dropBlockAsItemWithChance_4_, int p_dropBlockAsItemWithChance_5_) {
		if (!p_dropBlockAsItemWithChance_2_.isRemote) {
			TileEntityMagnetPiston lvt_6_1_ = this.func_196342_a(p_dropBlockAsItemWithChance_2_, p_dropBlockAsItemWithChance_3_);
			if (lvt_6_1_ != null) {
				lvt_6_1_.getState().dropBlockAsItem(p_dropBlockAsItemWithChance_2_, p_dropBlockAsItemWithChance_3_, 0);
			}
		}
	}

	public VoxelShape getShape(IBlockState p_getShape_1_, IBlockReader p_getShape_2_, BlockPos p_getShape_3_) {
		return ShapeUtils.empty();
	}

	public VoxelShape getCollisionShape(IBlockState p_getCollisionShape_1_, IBlockReader p_getCollisionShape_2_, BlockPos p_getCollisionShape_3_) {
		TileEntityMagnetPiston lvt_4_1_ = this.func_196342_a(p_getCollisionShape_2_, p_getCollisionShape_3_);
		return lvt_4_1_ != null ? lvt_4_1_.func_195508_a(p_getCollisionShape_2_, p_getCollisionShape_3_) : ShapeUtils.empty();
	}

	@Nullable
	private TileEntityMagnetPiston func_196342_a(IBlockReader p_196342_1_, BlockPos p_196342_2_) {
		TileEntity lvt_3_1_ = p_196342_1_.getTileEntity(p_196342_2_);
		return lvt_3_1_ instanceof TileEntityMagnetPiston ? (TileEntityMagnetPiston)lvt_3_1_ : null;
	}

	public ItemStack getItem(IBlockReader p_getItem_1_, BlockPos p_getItem_2_, IBlockState p_getItem_3_) {
		return ItemStack.EMPTY;
	}

	public IBlockState withRotation(IBlockState p_withRotation_1_, Rotation p_withRotation_2_) {
		return p_withRotation_1_.withProperty(FACING, p_withRotation_2_.rotate(p_withRotation_1_.getValue(FACING)));
	}

	public IBlockState withMirror(IBlockState p_withMirror_1_, Mirror p_withMirror_2_) {
		return p_withMirror_1_.withRotation(p_withMirror_2_.toRotation(p_withMirror_1_.getValue(FACING)));
	}

	protected void fillStateContainer(net.minecraft.state.StateContainer.Builder<Block, IBlockState> p_fillStateContainer_1_) {
		p_fillStateContainer_1_.add(FACING, TYPE);
	}

	public BlockFaceShape getBlockFaceShape(IBlockReader p_getBlockFaceShape_1_, IBlockState p_getBlockFaceShape_2_, BlockPos p_getBlockFaceShape_3_, EnumFacing p_getBlockFaceShape_4_) {
		return BlockFaceShape.UNDEFINED;
	}

	public boolean allowsMovement(IBlockState p_allowsMovement_1_, IBlockReader p_allowsMovement_2_, BlockPos p_allowsMovement_3_, PathType p_allowsMovement_4_) {
		return false;
	}

	static {
		FACING = BlockPistonExtension.FACING;
		TYPE = BlockPistonExtension.TYPE;
	}
}
