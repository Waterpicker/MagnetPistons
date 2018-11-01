package net.crazysnailboy.mods.magnetpistons.block;

import net.crazysnailboy.mods.magnetpistons.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;


public class BlockMagnetPistonExtension extends BlockPistonExtension {
	public BlockMagnetPistonExtension() {
		super();
		this.setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, PistonType.DEFAULT).withProperty(SHORT, false));
	}


	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode) {
			BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());
			Block block = world.getBlockState(blockpos).getBlock();
			if (block instanceof net.minecraft.block.BlockPistonBase) {
				world.removeBlock(blockpos);
			}
		}
		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState state1, boolean a) {
		super.onReplaced(state, world, pos, state1, a);
		EnumFacing enumfacing = state.getValue(FACING).getOpposite();
		pos = pos.offset(enumfacing);
		IBlockState iblockstate = world.getBlockState(pos);

		if (iblockstate.getBlock() instanceof net.minecraft.block.BlockPistonBase && iblockstate.getValue(BlockPistonBase.EXTENDED))
		{
			iblockstate.dropBlockAsItem(world, pos, 0);
			world.removeBlock(pos);
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		EnumFacing enumfacing = state.getValue(FACING);
		BlockPos blockpos = pos.offset(enumfacing.getOpposite());
		IBlockState iblockstate = world.getBlockState(blockpos);

		if (!(iblockstate.getBlock() instanceof net.minecraft.block.BlockPistonBase)) {
			world.removeBlock(pos);
		} else {
			iblockstate.neighborChanged(world, blockpos, block, fromPos);
		}
	}

	@Override
	public ItemStack getItem(IBlockReader world, BlockPos pos, IBlockState state) {
		return new ItemStack(ModBlocks.MAGNET_PISTON1);
	}

}
