package net.crazysnailboy.mods.magnetpistons.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.crazysnailboy.mods.magnetpistons.block.state.BlockMagnetPistonStructureHelper;
import net.crazysnailboy.mods.magnetpistons.init.ModBlocks;
import net.crazysnailboy.mods.magnetpistons.tileentity.TileEntityMagnetPiston;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ShapeUtils;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.Set;


public class BlockMagnetPistonBase extends BlockDirectional {
	public static final BooleanProperty EXTENDED;
	public static final IntegerProperty STRENGTH;

	protected static final VoxelShape PISTON_BASE_EAST_AABB;
	protected static final VoxelShape PISTON_BASE_WEST_AABB;
	protected static final VoxelShape PISTON_BASE_SOUTH_AABB;
	protected static final VoxelShape PISTON_BASE_NORTH_AABB;
	protected static final VoxelShape PISTON_BASE_UP_AABB;
	protected static final VoxelShape PISTON_BASE_DOWN_AABB;

	private int strength;

	public BlockMagnetPistonBase(int strength) {
		super(Builder.from(Blocks.PISTON));
		this.setDefaultState(this.stateContainer.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EXTENDED, false).withProperty(STRENGTH, strength));
		this.strength = strength;
	}

	public boolean causesSuffocation(IBlockState state) {
		return !state.getValue(EXTENDED);
	}

	public VoxelShape getShape(IBlockState state, IBlockReader reader, BlockPos p_getShape_3_) {
		if (state.getValue(EXTENDED)) {
			switch(state.getValue(FACING)) {
				case DOWN:
					return PISTON_BASE_DOWN_AABB;
				case UP:
				default:
					return PISTON_BASE_UP_AABB;
				case NORTH:
					return PISTON_BASE_NORTH_AABB;
				case SOUTH:
					return PISTON_BASE_SOUTH_AABB;
				case WEST:
					return PISTON_BASE_WEST_AABB;
				case EAST:
					return PISTON_BASE_EAST_AABB;
			}
		} else {
			return ShapeUtils.fullCube();
		}
	}

	public boolean isTopSolid(IBlockState p_isTopSolid_1_) {
		return !p_isTopSolid_1_.getValue(EXTENDED) || p_isTopSolid_1_.getValue(FACING) == EnumFacing.DOWN;
	}

	public void onBlockPlacedBy(World p_onBlockPlacedBy_1_, BlockPos p_onBlockPlacedBy_2_, IBlockState p_onBlockPlacedBy_3_, EntityLivingBase p_onBlockPlacedBy_4_, ItemStack p_onBlockPlacedBy_5_) {
		if (!p_onBlockPlacedBy_1_.isRemote) {
			this.checkForMove(p_onBlockPlacedBy_1_, p_onBlockPlacedBy_2_, p_onBlockPlacedBy_3_);
		}

	}

	public void neighborChanged(IBlockState p_neighborChanged_1_, World p_neighborChanged_2_, BlockPos p_neighborChanged_3_, Block p_neighborChanged_4_, BlockPos p_neighborChanged_5_) {
		if (!p_neighborChanged_2_.isRemote) {
			this.checkForMove(p_neighborChanged_2_, p_neighborChanged_3_, p_neighborChanged_1_);
		}

	}

	public void onBlockAdded(IBlockState p_onBlockAdded_1_, World p_onBlockAdded_2_, BlockPos p_onBlockAdded_3_, IBlockState p_onBlockAdded_4_) {
		if (p_onBlockAdded_4_.getBlock() != p_onBlockAdded_1_.getBlock()) {
			if (!p_onBlockAdded_2_.isRemote && p_onBlockAdded_2_.getTileEntity(p_onBlockAdded_3_) == null) {
				this.checkForMove(p_onBlockAdded_2_, p_onBlockAdded_3_, p_onBlockAdded_1_);
			}

		}
	}

	public IBlockState getStateForPlacement(BlockItemUseContext p_getStateForPlacement_1_) {
		return this.getDefaultState().withProperty(FACING, p_getStateForPlacement_1_.func_196010_d().getOpposite()).withProperty(EXTENDED, false);
	}

	private void checkForMove(World world, BlockPos pos, IBlockState state) {
		EnumFacing facing = state.getValue(FACING);
		boolean shouldBeExtended = this.shouldBeExtended(world, pos, facing);
		if (shouldBeExtended && !state.getValue(EXTENDED)) {
			if ((new BlockMagnetPistonStructureHelper(world, pos, facing, true, strength)).canMove()) {
				world.addBlockEvent(pos, this, 0, facing.getIndex());
			}
		} else if (!shouldBeExtended && state.getValue(EXTENDED)) {
			BlockPos offset = pos.offset(facing, 2);
			IBlockState offsetState = world.getBlockState(offset);
			int lvt_8_1_ = 1;
			if (offsetState.getBlock() == ModBlocks.MAGNET_PISTON_MOVING && offsetState.getValue(FACING) == facing) {
				TileEntity tileEntity = world.getTileEntity(offset);
				if (tileEntity instanceof TileEntityMagnetPiston) {
                    TileEntityMagnetPiston piston = (TileEntityMagnetPiston) tileEntity;
					if (piston.isExtending() && (piston.getProgress(0.0F) < 0.5F || world.getTotalWorldTime() == piston.getWorldTime() || ((WorldServer)world).isInsideTick())) {
						lvt_8_1_ = 2;
					}
				}
			}

			world.addBlockEvent(pos, this, lvt_8_1_, facing.getIndex());
		}

	}

	private boolean shouldBeExtended(World p_shouldBeExtended_1_, BlockPos p_shouldBeExtended_2_, EnumFacing p_shouldBeExtended_3_) {
		EnumFacing[] var4 = EnumFacing.values();
		int var5 = var4.length;

		int var6;
		for(var6 = 0; var6 < var5; ++var6) {
			EnumFacing lvt_7_1_ = var4[var6];
			if (lvt_7_1_ != p_shouldBeExtended_3_ && p_shouldBeExtended_1_.isSidePowered(p_shouldBeExtended_2_.offset(lvt_7_1_), lvt_7_1_)) {
				return true;
			}
		}

		if (p_shouldBeExtended_1_.isSidePowered(p_shouldBeExtended_2_, EnumFacing.DOWN)) {
			return true;
		} else {
			BlockPos lvt_4_1_ = p_shouldBeExtended_2_.up();
			EnumFacing[] var10 = EnumFacing.values();
			var6 = var10.length;

			for(int var11 = 0; var11 < var6; ++var11) {
				EnumFacing lvt_8_1_ = var10[var11];
				if (lvt_8_1_ != EnumFacing.DOWN && p_shouldBeExtended_1_.isSidePowered(lvt_4_1_.offset(lvt_8_1_), lvt_8_1_)) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int params) {
		EnumFacing facing = state.getValue(FACING);
		if (!world.isRemote) {
			boolean shouldBeExtended = this.shouldBeExtended(world, pos, facing);
			if (shouldBeExtended && id == 1) {
				world.setBlockState(pos, state.withProperty(EXTENDED, true), 2);
				return false;
			}

			if (!shouldBeExtended && id == 0) {
				return false;
			}
		}

		if (id == 0) {
			if (!this.doMove(world, pos, facing, true)) {
				return false;
			}

			world.setBlockState(pos, state.withProperty(EXTENDED, true), 3);
			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
		} else if (id == 1 || id == 2) {
			TileEntity lvt_7_2_ = world.getTileEntity(pos.offset(facing));
			if (lvt_7_2_ instanceof TileEntityPiston) {
				((TileEntityPiston)lvt_7_2_).clearPistonTileEntity();
			}

			world.setBlockState(pos, ModBlocks.MAGNET_PISTON_MOVING.getDefaultState().withProperty(BlockPistonMoving.FACING, facing), 3);
			world.setTileEntity(pos, BlockMagnetPistonMoving.createPiston(this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(params & 7)), facing, false, true));
			BlockPos lvt_8_2_;
			if (id == 1) {
				lvt_8_2_ = pos.offset(facing);
				if (world.getBlockState(lvt_8_2_).getBlock() == ModBlocks.MAGNET_PISTON_HEAD) {
					world.setBlockState(lvt_8_2_, Blocks.AIR.getDefaultState(), 21);
				}

				this.doMove(world, pos, facing, false);
			} else {
				if (id == 2) {
					lvt_8_2_ = pos.offset(facing, 2);
					TileEntity lvt_9_1_ = world.getTileEntity(lvt_8_2_);
					if (lvt_9_1_ instanceof TileEntityPiston) {
						((TileEntityPiston)lvt_9_1_).clearPistonTileEntity();
					}
				}

				world.removeBlock(pos.offset(facing));
			}

			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
		}

		return true;
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}

	public static boolean canPush(IBlockState state, World world, BlockPos pos, EnumFacing facing1, boolean p_canPush_4_, EnumFacing facing2) {
		Block block = state.getBlock();
		if (block == Blocks.OBSIDIAN) {
			return false;
		} else if (!world.getWorldBorder().contains(pos)) {
			return false;
		} else if (pos.getY() < 0 || facing1 == EnumFacing.DOWN && pos.getY() == 0) {
			return false;
		} else if (pos.getY() <= world.getHeight() - 1 && (facing1 != EnumFacing.UP || pos.getY() != world.getHeight() - 1)) {
			if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON && !(block instanceof BlockMagnetPistonBase)) {
				if (state.getBlockHardness(world, pos) == -1.0F) {
					return false;
				}

				switch(state.getPushReaction().ordinal()) {
					case 1:
						return false;
					case 2:
						return p_canPush_4_;
					case 3:
						return facing1 == facing2;
				}
			} else if (state.getValue(EXTENDED)) {
				return false;
			}

			return !block.hasTileEntity();
		} else {
			return false;
		}
	}

	private boolean doMove(World world, BlockPos pos, EnumFacing facing, boolean extending) {
		BlockMagnetPistonStructureHelper structure = new BlockMagnetPistonStructureHelper(world, pos, facing, extending, strength);
		if (!structure.canMove()) {
			return false;
		} else {
			List<BlockPos> list = structure.getBlocksToMove();
			List<IBlockState> list1 = Lists.newArrayList();

			for (BlockPos lvt_9_1_ : list) {
				list1.add(world.getBlockState(lvt_9_1_));
			}

			List<BlockPos> list2 = structure.getBlocksToDestroy();
			int k = list.size() + list2.size();
			IBlockState[] aiblockstate  = new IBlockState[k];
			EnumFacing lvt_11_1_ = extending ? facing : facing.getOpposite();
			Set<BlockPos> lvt_12_1_ = Sets.newHashSet(list);

			int j;
			BlockPos lvt_14_2_;
			IBlockState state;
			for(int lvt_13_2_ = list2.size() - 1; lvt_13_2_ >= 0; --lvt_13_2_) {
				lvt_14_2_ = list2.get(lvt_13_2_);
				state = world.getBlockState(lvt_14_2_);
				state.dropBlockAsItem(world, lvt_14_2_, 0);
				world.setBlockState(lvt_14_2_, Blocks.AIR.getDefaultState(), 18);
				--k;
				aiblockstate [k] = state;
			}

			for(j = list.size() - 1; j >= 0; --j) {
				lvt_14_2_ = list.get(j);
				state = world.getBlockState(lvt_14_2_);
				lvt_14_2_ = lvt_14_2_.offset(lvt_11_1_);
				lvt_12_1_.remove(lvt_14_2_);
				world.setBlockState(lvt_14_2_, ModBlocks.MAGNET_PISTON_MOVING.getDefaultState().withProperty(FACING, facing), 84);
				world.setTileEntity(lvt_14_2_, BlockMagnetPistonMoving.createPiston(list1.get(j), facing, extending, false));
				--k;
				aiblockstate [k] = state;
			}

			BlockPos lvt_13_3_ = pos.offset(facing);
			if (extending) {
				state = ModBlocks.MAGNET_PISTON_HEAD.getDefaultState().withProperty(BlockPistonExtension.FACING, facing);
				IBlockState lvt_16_1_ = ModBlocks.MAGNET_PISTON_MOVING.getDefaultState().withProperty(BlockPistonMoving.FACING, facing);
				lvt_12_1_.remove(lvt_13_3_);
				world.setBlockState(lvt_13_3_, lvt_16_1_, 4);
				world.setTileEntity(lvt_13_3_, BlockMagnetPistonMoving.createPiston(state, facing, true, true));
			}

			for (BlockPos lvt_15_4_ : lvt_12_1_) {
				world.setBlockState(lvt_15_4_, Blocks.AIR.getDefaultState(), 2);
			}

			int lvt_14_5_;
			for(lvt_14_5_ = list2.size() - 1; lvt_14_5_ >= 0; --lvt_14_5_) {
				state = aiblockstate [k++];
				BlockPos lvt_16_2_ = list2.get(lvt_14_5_);
				state.validatePlacement(world, lvt_16_2_, 2);
				world.notifyNeighborsOfStateChange(lvt_16_2_, state.getBlock());
			}

			for(lvt_14_5_ = list.size() - 1; lvt_14_5_ >= 0; --lvt_14_5_) {
				world.notifyNeighborsOfStateChange(list.get(lvt_14_5_), aiblockstate [k++].getBlock());
			}

			if (extending) {
				world.notifyNeighborsOfStateChange(lvt_13_3_, ModBlocks.MAGNET_PISTON_HEAD);
			}

			return true;
		}
	}

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
	    if(facing != state.getValue(FACING)) {
	        int newStrength = (this.strength == 16 ? 1 : this.strength + 1);

	        Block block = ModBlocks.STRENGTH[newStrength-1];

	        if(block == null) return false;

	        IBlockState newState = block.getDefaultState();

	        newState = newState.withProperty(FACING, state.getValue(FACING)).withProperty(EXTENDED, state.getValue(EXTENDED)).withProperty(STRENGTH, newStrength);
            world.setBlockState(pos, newState);
            return true;
        } else return false;
    }

    @Override
    public ItemStack getItem(IBlockReader p_getItem_1_, BlockPos p_getItem_2_, IBlockState p_getItem_3_) {
        return new ItemStack(ModBlocks.MAGNET_PISTON1);
    }

    @Override
    public IItemProvider getItemDropped(IBlockState p_getItemDropped_1_, World p_getItemDropped_2_, BlockPos p_getItemDropped_3_, int p_getItemDropped_4_) {
        return Item.getItemFromBlock(ModBlocks.MAGNET_PISTON1);
    }

    public IBlockState withRotation(IBlockState p_withRotation_1_, Rotation p_withRotation_2_) {
		return p_withRotation_1_.withProperty(FACING, p_withRotation_2_.rotate(p_withRotation_1_.getValue(FACING)));
	}

	public IBlockState withMirror(IBlockState p_withMirror_1_, Mirror p_withMirror_2_) {
		return p_withMirror_1_.withRotation(p_withMirror_2_.toRotation(p_withMirror_1_.getValue(FACING)));
	}

	protected void fillStateContainer(net.minecraft.state.StateContainer.Builder<Block, IBlockState> p_fillStateContainer_1_) {
		p_fillStateContainer_1_.add(FACING, EXTENDED, STRENGTH);
	}

	public BlockFaceShape getBlockFaceShape(IBlockReader p_getBlockFaceShape_1_, IBlockState p_getBlockFaceShape_2_, BlockPos p_getBlockFaceShape_3_, EnumFacing p_getBlockFaceShape_4_) {
		return p_getBlockFaceShape_2_.getValue(FACING) != p_getBlockFaceShape_4_.getOpposite() && p_getBlockFaceShape_2_.getValue(EXTENDED) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
	}

	public int getOpacity(IBlockState p_getOpacity_1_, IBlockReader p_getOpacity_2_, BlockPos p_getOpacity_3_) {
		return 0;
	}

	public boolean allowsMovement(IBlockState p_allowsMovement_1_, IBlockReader p_allowsMovement_2_, BlockPos p_allowsMovement_3_, PathType p_allowsMovement_4_) {
		return false;
	}

	static {
		EXTENDED = BlockStateProperties.EXTENDED;
		STRENGTH = IntegerProperty.create("strength", 1, 16);
		PISTON_BASE_EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
		PISTON_BASE_WEST_AABB = Block.makeCuboidShape(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		PISTON_BASE_SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
		PISTON_BASE_NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D);
		PISTON_BASE_UP_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
		PISTON_BASE_DOWN_AABB = Block.makeCuboidShape(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	}
}
