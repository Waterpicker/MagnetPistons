package net.crazysnailboy.mods.magnetpistons.block;

import net.crazysnailboy.mods.magnetpistons.tileentity.TileEntityPiston;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class BlockPistonMoving extends net.minecraft.block.BlockPistonMoving
{

	public BlockPistonMoving() {
		super(Builder.from(Blocks.MOVING_PISTON));
		this.setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, PistonType.DEFAULT));
	}


	public static TileEntity createTilePiston(IBlockState state, EnumFacing facing, boolean extending, boolean shouldHeadBeRendered) {
		return new TileEntityPiston(state, facing, extending, shouldHeadBeRendered);
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState state1, boolean a) {
		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof TileEntityPiston) {
			((TileEntityPiston)tileentity).clearPistonTileEntity();
		} else {
			super.onReplaced(state, world, pos, state1, a);
		}
	}

	@Override
	public void onPlayerDestroy(IWorld world, BlockPos pos, IBlockState state) {
		BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());
		IBlockState iblockstate = world.getBlockState(blockpos);

		if (iblockstate.getBlock() instanceof net.minecraft.block.BlockPistonBase && (iblockstate.getValue(BlockPistonBase.EXTENDED))) {
			world.removeBlock(blockpos);
		}
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && world.getTileEntity(pos) == null) {
			world.removeBlock(pos);
			return true;
		} else
		{
			return false;
		}
	}

	@Nullable
	private TileEntityPiston getTilePistonAt(IBlockReader world, BlockPos pos) {
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity instanceof TileEntityPiston ? (TileEntityPiston)tileentity : null;
	}

    @Override
    public void dropBlockAsItemWithChance(IBlockState state, World world, BlockPos pos, float i, int fortune) {
        TileEntityPiston tileentitypiston = this.getTilePistonAt(world, pos);
        if (tileentitypiston != null) {
			tileentitypiston.func_200230_i().dropBlockAsItemWithChance(world, pos, i, fortune);
        }
    }

}