package net.crazysnailboy.mods.magnetpistons.tileentity;

import net.crazysnailboy.mods.magnetpistons.block.BlockPistonBase;
import net.crazysnailboy.mods.magnetpistons.block.BlockPistonExtension;
import net.crazysnailboy.mods.magnetpistons.init.ModBlocks;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ShapeUtils;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import java.util.List;


public class TileEntityPiston extends net.minecraft.tileentity.TileEntityPiston
{
	private IBlockState pistonState;
	private EnumFacing pistonFacing;
	private boolean extending;
	private boolean shouldHeadBeRendered;
	private static final ThreadLocal<EnumFacing> MOVING_ENTITY = ThreadLocal.withInitial(() -> null);
	private float progress;
	private float lastProgress;

	public TileEntityPiston()
	{
		super();
	}

	public TileEntityPiston(IBlockState pistonState, EnumFacing pistonFacing, boolean extending, boolean shouldHeadBeRendered) {
		super(pistonState, pistonFacing, extending, shouldHeadBeRendered);
		this.pistonState = pistonState;
		this.pistonFacing = pistonFacing;
		this.extending = extending;
		this.shouldHeadBeRendered = shouldHeadBeRendered;
	}

	@Override
	public IBlockState func_200230_i()
	{
		return this.pistonState;
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public boolean isExtending()
	{
		return this.extending;
	}

	@Override
	public boolean shouldPistonHeadBeRendered()
	{
		return this.shouldHeadBeRendered;
	}

	@Override
	public float getProgress(float ticks) {
		if (ticks > 1.0F) {
			ticks = 1.0F;
		}

		return this.lastProgress + (this.progress - this.lastProgress) * ticks;
	}

	@Override
	public float getOffsetX(float ticks) {
		return (float)this.pistonFacing.getXOffset() * this.getExtendedProgress(this.getProgress(ticks));
	}

	@Override
	public float getOffsetY(float ticks) {
		return (float)this.pistonFacing.getYOffset() * this.getExtendedProgress(this.getProgress(ticks));
	}

	@Override
	public float getOffsetZ(float ticks) {
		return (float)this.pistonFacing.getZOffset() * this.getExtendedProgress(this.getProgress(ticks));
	}

	private float getExtendedProgress(float ticks)
	{
		return this.extending ? ticks - 1.0F : 1.0F - ticks;
	}


	private IBlockState getCollisionRelatedBlockState()
	{
		return !this.isExtending() && this.shouldPistonHeadBeRendered() ? ModBlocks.PISTON_HEAD.getDefaultState().withProperty(BlockPistonExtension.TYPE, this.pistonState.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT).withProperty(BlockPistonExtension.FACING, this.pistonState.getValue(BlockPistonBase.FACING)) : this.pistonState;
	}

	private void moveCollidedEntities(float p_184322_1_)
	{
		EnumFacing enumfacing = this.extending ? this.pistonFacing : this.pistonFacing.getOpposite();
		double d0 = (double)(p_184322_1_ - this.progress);
		VoxelShape shape = this.getCollisionRelatedBlockState().getCollisionShape(this.world, this.getPos());

		if (!shape.isEmpty()) {
			List<AxisAlignedBB> list = shape.toBoundingBoxList();
			AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(this.getMinMaxPiecesAABB(list));
			List<Entity> list1 = this.world.getEntitiesWithinAABBExcludingEntity((Entity)null, this.getMovementArea(axisalignedbb, enumfacing, d0).union(axisalignedbb));

			if (!list1.isEmpty()) {
				boolean flag = this.pistonState.getBlock() == Blocks.SLIME_BLOCK;

				for (Entity entity : list1) {
					if (entity.getPushReaction() != EnumPushReaction.IGNORE) {
						if (flag) {
							switch (enumfacing.getAxis()) {
								case X:
									entity.motionX = (double) enumfacing.getXOffset();
									break;
								case Y:
									entity.motionY = (double) enumfacing.getYOffset();
									break;
								case Z:
									entity.motionZ = (double) enumfacing.getZOffset();
							}
						}

						double d1 = 0.0D;

						for (AxisAlignedBB aList : list) {
							AxisAlignedBB axisalignedbb1 = this.getMovementArea(this.moveByPositionAndProgress(aList), enumfacing, d0);
							AxisAlignedBB axisalignedbb2 = entity.getEntityBoundingBox();

							if (axisalignedbb1.intersects(axisalignedbb2)) {
								d1 = Math.max(d1, this.getMovement(axisalignedbb1, enumfacing, axisalignedbb2));
								if (d1 >= d0) {
									break;
								}
							}
						}

						if (d1 > 0.0D) {
							d1 = Math.min(d1, d0) + 0.01D;
							MOVING_ENTITY.set(enumfacing);
							entity.move(MoverType.PISTON, d1 * (double) enumfacing.getXOffset(), d1 * (double) enumfacing.getYOffset(), d1 * (double) enumfacing.getZOffset());
							MOVING_ENTITY.set(null);

							if (!this.extending && this.shouldHeadBeRendered) {
								this.fixEntityWithinPistonBase(entity, enumfacing, d0);
							}
						}
					}
				}
			}
		}
	}

	private AxisAlignedBB getMinMaxPiecesAABB(List<AxisAlignedBB> list) {
		double d0 = 0.0D;
		double d1 = 0.0D;
		double d2 = 0.0D;
		double d3 = 1.0D;
		double d4 = 1.0D;
		double d5 = 1.0D;

		for (AxisAlignedBB axisalignedbb : list)
		{
			d0 = Math.min(axisalignedbb.minX, d0);
			d1 = Math.min(axisalignedbb.minY, d1);
			d2 = Math.min(axisalignedbb.minZ, d2);
			d3 = Math.max(axisalignedbb.maxX, d3);
			d4 = Math.max(axisalignedbb.maxY, d4);
			d5 = Math.max(axisalignedbb.maxZ, d5);
		}

		return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
	}

	private double getMovement(AxisAlignedBB p_190612_1_, EnumFacing facing, AxisAlignedBB p_190612_3_)
	{
		switch (facing.getAxis())
		{
			case X:
				return getDeltaX(p_190612_1_, facing, p_190612_3_);
			case Y:
			default:
				return getDeltaY(p_190612_1_, facing, p_190612_3_);
			case Z:
				return getDeltaZ(p_190612_1_, facing, p_190612_3_);
		}
	}

	private AxisAlignedBB moveByPositionAndProgress(AxisAlignedBB p_190607_1_)
	{
		double d0 = (double)this.getExtendedProgress(this.progress);
		return p_190607_1_.offset((double)this.pos.getX() + d0 * (double)this.pistonFacing.getXOffset(), (double)this.pos.getY() + d0 * (double)this.pistonFacing.getYOffset(), (double)this.pos.getZ() + d0 * (double)this.pistonFacing.getHorizontalAngle());
	}

	private AxisAlignedBB getMovementArea(AxisAlignedBB p_190610_1_, EnumFacing p_190610_2_, double p_190610_3_)
	{
		double d0 = p_190610_3_ * (double)p_190610_2_.getAxisDirection().getOffset();
		double d1 = Math.min(d0, 0.0D);
		double d2 = Math.max(d0, 0.0D);

		switch (p_190610_2_)
		{
			case WEST:
				return new AxisAlignedBB(p_190610_1_.minX + d1, p_190610_1_.minY, p_190610_1_.minZ, p_190610_1_.minX + d2, p_190610_1_.maxY, p_190610_1_.maxZ);
			case EAST:
				return new AxisAlignedBB(p_190610_1_.maxX + d1, p_190610_1_.minY, p_190610_1_.minZ, p_190610_1_.maxX + d2, p_190610_1_.maxY, p_190610_1_.maxZ);
			case DOWN:
				return new AxisAlignedBB(p_190610_1_.minX, p_190610_1_.minY + d1, p_190610_1_.minZ, p_190610_1_.maxX, p_190610_1_.minY + d2, p_190610_1_.maxZ);
			case UP:
			default:
				return new AxisAlignedBB(p_190610_1_.minX, p_190610_1_.maxY + d1, p_190610_1_.minZ, p_190610_1_.maxX, p_190610_1_.maxY + d2, p_190610_1_.maxZ);
			case NORTH:
				return new AxisAlignedBB(p_190610_1_.minX, p_190610_1_.minY, p_190610_1_.minZ + d1, p_190610_1_.maxX, p_190610_1_.maxY, p_190610_1_.minZ + d2);
			case SOUTH:
				return new AxisAlignedBB(p_190610_1_.minX, p_190610_1_.minY, p_190610_1_.maxZ + d1, p_190610_1_.maxX, p_190610_1_.maxY, p_190610_1_.maxZ + d2);
		}
	}

	private void fixEntityWithinPistonBase(Entity p_190605_1_, EnumFacing p_190605_2_, double p_190605_3_)
	{
		AxisAlignedBB axisalignedbb = p_190605_1_.getEntityBoundingBox();
		AxisAlignedBB axisalignedbb1 = ShapeUtils.fullCube().getBoundingBox().offset(this.pos);

		if (axisalignedbb.intersects(axisalignedbb1)) {
			EnumFacing enumfacing = p_190605_2_.getOpposite();
			double d0 = this.getMovement(axisalignedbb1, enumfacing, axisalignedbb) + 0.01D;
			double d1 = this.getMovement(axisalignedbb1, enumfacing, axisalignedbb.intersect(axisalignedbb1)) + 0.01D;

			if (Math.abs(d0 - d1) < 0.01D)
			{
				d0 = Math.min(d0, p_190605_3_) + 0.01D;
				MOVING_ENTITY.set(p_190605_2_);
				p_190605_1_.move(MoverType.PISTON, d0 * (double) enumfacing.getXOffset(), d0 * (double) enumfacing.getYOffset(), d0 * (double) enumfacing.getZOffset());
				MOVING_ENTITY.set(null);
			}
		}
	}

	private static double getDeltaX(AxisAlignedBB p_190611_0_, EnumFacing facing, AxisAlignedBB p_190611_2_)
	{
		return facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? p_190611_0_.maxX - p_190611_2_.minX : p_190611_2_.maxX - p_190611_0_.minX;
	}

	private static double getDeltaY(AxisAlignedBB p_190608_0_, EnumFacing facing, AxisAlignedBB p_190608_2_)
	{
		return facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? p_190608_0_.maxY - p_190608_2_.minY : p_190608_2_.maxY - p_190608_0_.minY;
	}

	private static double getDeltaZ(AxisAlignedBB p_190604_0_, EnumFacing facing, AxisAlignedBB p_190604_2_)
	{
		return facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? p_190604_0_.maxZ - p_190604_2_.minZ : p_190604_2_.maxZ - p_190604_0_.minZ;
	}

	/**
	 * removes a piston's tile entity (and if the piston is moving, stops it)
	 */
	@Override
	public void clearPistonTileEntity()
	{
		if (this.lastProgress < 1.0F && this.world != null)
		{
			this.progress = 1.0F;
			this.lastProgress = this.progress;
			this.world.removeTileEntity(this.pos);
			this.invalidate();

			if (this.world.getBlockState(this.pos).getBlock() instanceof net.minecraft.block.BlockPistonMoving)
			{
				this.world.setBlockState(this.pos, this.pistonState, 3);
				this.world.neighborChanged(this.pos, this.pistonState.getBlock(), this.pos);
			}
		}
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	@Override
	public void update()
	{
		this.lastProgress = this.progress;

		if (this.lastProgress >= 1.0F)
		{
			this.world.removeTileEntity(this.pos);
			this.invalidate();

			if (this.world.getBlockState(this.pos).getBlock() instanceof net.minecraft.block.BlockPistonMoving)
			{
				this.world.setBlockState(this.pos, this.pistonState, 3);
				this.world.neighborChanged(this.pos, this.pistonState.getBlock(), this.pos);
			}
		}
		else
		{
			float f = this.progress + 0.5F;
			this.moveCollidedEntities(f);
			this.progress = f;

			if (this.progress >= 1.0F)
			{
				this.progress = 1.0F;
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.pistonState = NBTUtil.readBlockState(compound.getCompoundTag("blockState"));
		this.pistonFacing = EnumFacing.byIndex(compound.getInteger("facing"));
		this.progress = compound.getFloat("progress");
		this.lastProgress = this.progress;
		this.extending = compound.getBoolean("extending");
		this.shouldHeadBeRendered = compound.getBoolean("source");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("blockState", NBTUtil.writeBlockState(this.pistonState));
		compound.setInteger("facing", this.pistonFacing.getIndex());
		compound.setFloat("progress", this.lastProgress);
		compound.setBoolean("extending", this.extending);
		compound.setBoolean("source", this.shouldHeadBeRendered);
		return compound;
	}

	@Override
	public VoxelShape func_195508_a(IBlockReader reader, BlockPos pos) {
		VoxelShape shape;

		if (!this.extending && this.shouldHeadBeRendered) {
			shape = this.pistonState.withProperty(BlockPistonBase.EXTENDED, Boolean.TRUE).getCollisionShape(reader, pos);
		} else {
			shape = ShapeUtils.empty();
		}

		EnumFacing enumfacing = MOVING_ENTITY.get();

		if ((double)this.progress >= 1.0D || enumfacing == this.func_195509_h()) {
			return shape;

		} else {
			IBlockState iblockstate;

			if (this.shouldPistonHeadBeRendered())
			{
				iblockstate = ModBlocks.PISTON_HEAD.getDefaultState().withProperty(BlockPistonExtension.FACING, this.pistonFacing).withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(this.extending != 1.0F - this.progress < 0.25F));
			} else {
				iblockstate = this.pistonState;
			}

			float f = this.getExtendedProgress(this.progress);
			double d0 = (double)((float)this.pistonFacing.getXOffset() * f);
			double d1 = (double)((float)this.pistonFacing.getYOffset() * f);
			double d2 = (double)((float)this.pistonFacing.getZOffset() * f);
			return ShapeUtils.or(shape, iblockstate.getCollisionShape(reader, pos).withOffset(d0, d1, d2));
		}
	}
}
