package net.crazysnailboy.mods.magnetpistons.block;

import net.crazysnailboy.mods.magnetpistons.tileentity.TileEntityMagnetPiston;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class BlockMagnetPistonMoving extends BlockPistonMoving
{

//	public static final PropertyDirection FACING = BlockMagnetPistonExtension.FACING;
//	public static final PropertyEnum<net.minecraft.block.BlockPistonExtension.EnumPistonType> TYPE = BlockMagnetPistonExtension.TYPE;

	public BlockMagnetPistonMoving()
	{
		super(Builder.from(Blocks.MOVING_PISTON));
		this.setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, PistonType.DEFAULT));
	}

	@Override
	@Nullable
	public TileEntity createNewTileEntity(IBlockReader reader) {
		return null;
	}

	public static TileEntity createTilePiston(IBlockState state, EnumFacing facing, boolean extending, boolean shouldHeadBeRendered)
	{
		return new TileEntityMagnetPiston(state, facing, extending, shouldHeadBeRendered);
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState state1, boolean a) {
		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof TileEntityMagnetPiston) {
			((TileEntityMagnetPiston)tileentity).clearPistonTileEntity();
		} else {
			super.onReplaced(state, world, pos, state1, a);
		}
	}

	@Override
	public void onPlayerDestroy(IWorld world, BlockPos pos, IBlockState state) {
		BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());
		IBlockState iblockstate = world.getBlockState(blockpos);

		if (iblockstate.getBlock() instanceof BlockMagnetPistonBase && iblockstate.getValue(BlockMagnetPistonBase.EXTENDED)) {
			world.removeBlock(blockpos);
		}
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && world.getTileEntity(pos) == null) {
			world.removeBlock(pos);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return Items.AIR;
	}

	@Override
	public void dropBlockAsItemWithChance(IBlockState state, World world, BlockPos pos, float chance, int fortune)
	{
//        if (false && !world.isRemote) // Forge: Noop this out
//        {
//            TileEntityMagnetPiston TileEntityMagnetPiston = this.getTilePistonAt(world, pos);
//
//            if (TileEntityMagnetPiston != null)
//            {
//                IBlockState iblockstate = TileEntityMagnetPiston.getPistonState();
//                iblockstate.getBlock().dropBlockAsItem(world, pos, iblockstate, 0);
//            }
//        }
		super.dropBlockAsItemWithChance(state, world, pos, 1, fortune); // mimic vanilla behavior from above and ignore chance
	}

	@Override
	@Nullable
	public VoxelShape getRaytraceShape(IBlockState blockState, IBlockReader world, BlockPos pos) {
		return null;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!world.isRemote) {
			world.getTileEntity(pos);
		}
	}

	@Nullable
	private TileEntityMagnetPiston getTilePistonAt(IBlockReader world, BlockPos pos) {
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity instanceof TileEntityMagnetPiston ? (TileEntityMagnetPiston)tileentity : null;
	}
}
