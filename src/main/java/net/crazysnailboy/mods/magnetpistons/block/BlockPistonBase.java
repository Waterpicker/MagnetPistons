package net.crazysnailboy.mods.magnetpistons.block;

import com.google.common.collect.Lists;
import net.crazysnailboy.mods.magnetpistons.block.state.BlockPistonStructureHelper;
import net.crazysnailboy.mods.magnetpistons.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;


public class BlockPistonBase extends net.minecraft.block.BlockPistonBase {
	private final boolean isSticky;
	public BlockPistonBase(boolean isSticky) {
		super(isSticky, Builder.create(Material.PISTON).sound(SoundType.STONE).hardnessAndResistance(0.5f, 0.5f));
		this.setDefaultState(this.stateContainer.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EXTENDED, Boolean.FALSE));
		this.isSticky = isSticky;
		//this.setCreativeTab(CreativeTabs.REDSTONE);
	}


	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		//world.setBlockState(pos, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)), 2);
		if (!world.isRemote) {
			this.checkForMove(world, pos, state);
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!world.isRemote) {
			this.checkForMove(world, pos, state);
		}
	}

	@Override
	public void onBlockAdded(IBlockState state, World world, BlockPos pos, IBlockState state2) {
		if (!world.isRemote && world.getTileEntity(pos) == null) {
			this.checkForMove(world, pos, state);
		}
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().withProperty(FACING, context.func_196010_d().getOpposite()).withProperty(EXTENDED, false);
	}

	private void checkForMove(World world, BlockPos pos, IBlockState state)
	{
		EnumFacing enumfacing = state.getValue(FACING);
		boolean shouldBeExtended = this.shouldBeExtended(world, pos, enumfacing);

		if (shouldBeExtended && !state.getValue(EXTENDED))
		{
			if (new BlockPistonStructureHelper(world, pos, enumfacing, true).canMove())
			{
				world.addBlockEvent(pos, this, 0, enumfacing.getIndex());
			}
		}
		else if (!shouldBeExtended && state.getValue(EXTENDED))
		{
			world.addBlockEvent(pos, this, 1, enumfacing.getIndex());
		}
	}

	private boolean shouldBeExtended(World world, BlockPos pos, EnumFacing facing)
	{
		for (EnumFacing enumfacing : EnumFacing.values())
		{
			if (enumfacing != facing && world.isSidePowered(pos.offset(enumfacing), enumfacing))
			{
				return true;
			}
		}
		if (world.isSidePowered(pos, EnumFacing.DOWN))
		{
			return true;
		}
		else
		{
			BlockPos blockpos = pos.up();
			for (EnumFacing enumfacing1 : EnumFacing.values())
			{
				if (enumfacing1 != EnumFacing.DOWN && world.isSidePowered(blockpos.offset(enumfacing1), enumfacing1))
				{
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
		EnumFacing enumfacing = state.getValue(FACING);

		if (!world.isRemote) {
			boolean shouldBeExtended = this.shouldBeExtended(world, pos, enumfacing);

			if (shouldBeExtended && id == 1) {
				world.setBlockState(pos, state.withProperty(EXTENDED, true), 2);
				return false;
			}

			if (!shouldBeExtended && id == 0) {
				return false;
			}
		}

		if (id == 0)
		{
			if (!this.doMove(world, pos, enumfacing, true))
			{
				return false;
			}

			world.setBlockState(pos, state.withProperty(EXTENDED, true), 3);
			world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
		}
		else if (id == 1)
		{
			TileEntity tileentity1 = world.getTileEntity(pos.offset(enumfacing));
			if (tileentity1 instanceof net.minecraft.tileentity.TileEntityPiston) {
				((net.minecraft.tileentity.TileEntityPiston)tileentity1).clearPistonTileEntity();
			}

			world.setBlockState(pos, ModBlocks.PISTON_MOVING.getDefaultState().withProperty(BlockPistonMoving.FACING, enumfacing).withProperty(BlockPistonMoving.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT), 3);
			world.setTileEntity(pos, BlockPistonMoving.createTilePiston(state, enumfacing, false, true));

			if (this.isSticky) {
				BlockPos blockpos = pos.add(enumfacing.getXOffset() * 2, enumfacing.getYOffset() * 2, enumfacing.getZOffset() * 2);
				IBlockState iblockstate = world.getBlockState(blockpos);
				Block block = iblockstate.getBlock();
				boolean flag1 = false;

				if (block instanceof net.minecraft.block.BlockPistonMoving) {
					TileEntity tileentity = world.getTileEntity(blockpos);
					if (tileentity instanceof net.minecraft.tileentity.TileEntityPiston) {
						net.minecraft.tileentity.TileEntityPiston tileentitypiston = (net.minecraft.tileentity.TileEntityPiston)tileentity;

						if (tileentitypiston.func_195509_h() == enumfacing && tileentitypiston.isExtending()) {
							tileentitypiston.clearPistonTileEntity();
							flag1 = true;
						}
					}
				}

				if (!flag1 && !iblockstate.getBlock().isAir(iblockstate) && canPush(iblockstate, world, blockpos, enumfacing.getOpposite(), false, enumfacing) && (iblockstate.getPushReaction() == EnumPushReaction.NORMAL || block instanceof net.minecraft.block.BlockPistonBase))
				{
					this.doMove(world, pos, enumfacing, false);
				}
			}
			else
			{
				world.removeBlock(pos.offset(enumfacing));
			}

			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
		}

		return true;
	}

	@SuppressWarnings("incomplete-switch")
	public static boolean canPush(IBlockState state, World world, BlockPos pos, EnumFacing facing, boolean destroyBlocks, EnumFacing p_185646_5_)
	{
		Block block = state.getBlock();

		if (block == Blocks.OBSIDIAN)
		{
			return false;
		}
		else if (!world.getWorldBorder().contains(pos))
		{
			return false;
		}
		else if (pos.getY() >= 0 && (facing != EnumFacing.DOWN || pos.getY() != 0))
		{
			if (pos.getY() <= world.getHeight() - 1 && (facing != EnumFacing.UP || pos.getY() != world.getHeight() - 1))
			{
				if (!(block instanceof net.minecraft.block.BlockPistonBase))
				{
					if (state.getBlockHardness(world, pos) == -1.0F)
					{
						return false;
					}

					switch (state.getPushReaction())
					{
						case BLOCK:
							return false;
						case DESTROY:
							return destroyBlocks;
						case PUSH_ONLY:
							return facing == p_185646_5_;
					}
				}
				else if (state.getValue(EXTENDED))
				{
					return false;
				}

				return !block.hasTileEntity();
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	private boolean doMove(World world, BlockPos pos, EnumFacing direction, boolean extending) {
		BlockPistonStructureHelper blockpistonstructurehelper = new BlockPistonStructureHelper(world, pos, direction, extending);
		if (!blockpistonstructurehelper.canMove()) {
			return false;
		} else {
			List<BlockPos> list = blockpistonstructurehelper.getBlocksToMove();
			List<IBlockState> list1 = Lists.<IBlockState>newArrayList();

			for (BlockPos blockpos : list) {
				list1.add(world.getBlockState(blockpos));
			}

			List<BlockPos> list2 = blockpistonstructurehelper.getBlocksToDestroy();
			int k = list.size() + list2.size();
			IBlockState[] aiblockstate = new IBlockState[k];
			EnumFacing enumfacing = extending ? direction : direction.getOpposite();

			for (int j = list2.size() - 1; j >= 0; --j) {
				BlockPos blockpos1 = list2.get(j);
				IBlockState iblockstate = world.getBlockState(blockpos1);
				// Forge: With our change to how snowballs are dropped this needs to disallow to mimic vanilla behavior.
				float chance = iblockstate.getBlock() instanceof BlockSnow ? -1.0f : 1.0f;
				iblockstate.getBlock().dropBlockAsItemWithChance(iblockstate, world, blockpos1, chance, 0);
				world.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 4);
				--k;
				aiblockstate[k] = iblockstate;
			}

			for (int l = list.size() - 1; l >= 0; --l)
			{
				BlockPos blockpos3 = list.get(l);
				IBlockState iblockstate2 = world.getBlockState(blockpos3);
				world.setBlockState(blockpos3, Blocks.AIR.getDefaultState(), 2);
				blockpos3 = blockpos3.offset(enumfacing);
				world.setBlockState(blockpos3, ModBlocks.PISTON_MOVING.getDefaultState().withProperty(FACING, direction), 4);
				world.setTileEntity(blockpos3, BlockPistonMoving.createTilePiston(list1.get(l), direction, extending, false));
				--k;
				aiblockstate[k] = iblockstate2;
			}

			BlockPos blockpos2 = pos.offset(direction);

			if (extending)
			{
				PistonType pistonType = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
				IBlockState iblockstate3 = ModBlocks.PISTON_HEAD.getDefaultState().withProperty(BlockPistonExtension.FACING, direction).withProperty(BlockPistonExtension.TYPE, pistonType);
				IBlockState iblockstate1 = ModBlocks.PISTON_MOVING.getDefaultState().withProperty(BlockPistonMoving.FACING, direction).withProperty(BlockPistonMoving.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
				world.setBlockState(blockpos2, iblockstate1, 4);
				world.setTileEntity(blockpos2, BlockPistonMoving.createTilePiston(iblockstate3, direction, true, true));
			}

			for (int i1 = list2.size() - 1; i1 >= 0; --i1)
			{
				world.notifyNeighborsOfStateChange(list2.get(i1), aiblockstate[k++].getBlock());
			}

			for (int j1 = list.size() - 1; j1 >= 0; --j1)
			{
				world.notifyNeighborsOfStateChange(list.get(j1), aiblockstate[k++].getBlock());
			}

			if (extending)
			{
				world.notifyNeighborsOfStateChange(blockpos2, ModBlocks.PISTON_HEAD);
			}

			return true;
		}
	}
}