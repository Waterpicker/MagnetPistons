package net.crazysnailboy.mods.magnetpistons.tileentity;

import net.crazysnailboy.mods.magnetpistons.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ShapeUtils;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import java.util.Iterator;
import java.util.List;


public class TileEntityMagnetPiston extends TileEntity implements ITickable {
	private IBlockState state;
	private EnumFacing pistonFacing;
	private boolean extending;
	private boolean shouldHeadBeRendered;
	private static final ThreadLocal<EnumFacing> MOVING_ENTITY = ThreadLocal.withInitial(() -> null);
	private float progress;
	private float lastProgress;
	private long worldTime;

	public TileEntityMagnetPiston() {
		super(TileEntityType.PISTON);
	}

	public TileEntityMagnetPiston(IBlockState state, EnumFacing facing, boolean extending, boolean shouldHeadBeRendered) {
		this();
		this.state = state;
		this.pistonFacing = facing;
		this.extending = extending;
		this.shouldHeadBeRendered = shouldHeadBeRendered;
	}

	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	public boolean isExtending() {
		return this.extending;
	}

	public boolean shouldPistonHeadBeRendered() {
		return this.shouldHeadBeRendered;
	}

	public float getProgress(float progress) {
		if (progress > 1.0F) {
			progress = 1.0F;
		}

		return this.lastProgress + (this.progress - this.lastProgress) * progress;
	}

	public float getOffsetX(float p_getOffsetX_1_) {
		return (float)this.pistonFacing.getXOffset() * this.getExtendedProgress(this.getProgress(p_getOffsetX_1_));
	}

	public float getOffsetY(float p_getOffsetY_1_) {
		return (float)this.pistonFacing.getYOffset() * this.getExtendedProgress(this.getProgress(p_getOffsetY_1_));
	}

	public float getOffsetZ(float p_getOffsetZ_1_) {
		return (float)this.pistonFacing.getZOffset() * this.getExtendedProgress(this.getProgress(p_getOffsetZ_1_));
	}

	private float getExtendedProgress(float p_getExtendedProgress_1_) {
		return this.extending ? p_getExtendedProgress_1_ - 1.0F : 1.0F - p_getExtendedProgress_1_;
	}

	private IBlockState getCollisionRelatedBlockState() {
		return !this.isExtending() && this.shouldPistonHeadBeRendered() ? ModBlocks.MAGNET_PISTON_HEAD.getDefaultState().withProperty(BlockPistonExtension.FACING, this.state.getValue(BlockPistonBase.FACING)) : this.state;
	}

	private void moveCollidedEntities(float p_moveCollidedEntities_1_) {
		EnumFacing lvt_2_1_ = this.getFacing();
		double lvt_3_1_ = (double)(p_moveCollidedEntities_1_ - this.progress);
		VoxelShape lvt_5_1_ = this.getCollisionRelatedBlockState().getCollisionShape(this.world, this.getPos());
		if (!lvt_5_1_.isEmpty()) {
			List<AxisAlignedBB> lvt_6_1_ = lvt_5_1_.toBoundingBoxList();
			AxisAlignedBB lvt_7_1_ = this.moveByPositionAndProgress(this.getMinMaxPiecesAABB(lvt_6_1_));
			List<Entity> lvt_8_1_ = this.world.getEntitiesWithinAABBExcludingEntity(null, this.getMovementArea(lvt_7_1_, lvt_2_1_, lvt_3_1_).union(lvt_7_1_));
			if (!lvt_8_1_.isEmpty()) {
				boolean lvt_9_1_ = this.state.getBlock() == Blocks.SLIME_BLOCK;

				for (Entity entity : lvt_8_1_) {
					if (entity.getPushReaction() != EnumPushReaction.IGNORE) {
						if (lvt_9_1_) {
							switch (lvt_2_1_.getAxis()) {
								case X:
									entity.motionX = (double) lvt_2_1_.getXOffset();
									break;
								case Y:
									entity.motionY = (double) lvt_2_1_.getYOffset();
									break;
								case Z:
									entity.motionZ = (double) lvt_2_1_.getZOffset();
							}
						}

						double lvt_12_1_ = 0.0D;

						for (AxisAlignedBB aLvt_6_1_ : lvt_6_1_) {
							AxisAlignedBB lvt_15_1_ = this.getMovementArea(this.moveByPositionAndProgress(aLvt_6_1_), lvt_2_1_, lvt_3_1_);
							AxisAlignedBB lvt_16_1_ = entity.getEntityBoundingBox();
							if (lvt_15_1_.intersects(lvt_16_1_)) {
								lvt_12_1_ = Math.max(lvt_12_1_, this.getMovement(lvt_15_1_, lvt_2_1_, lvt_16_1_));
								if (lvt_12_1_ >= lvt_3_1_) {
									break;
								}
							}
						}

						if (lvt_12_1_ > 0.0D) {
							lvt_12_1_ = Math.min(lvt_12_1_, lvt_3_1_) + 0.01D;
							MOVING_ENTITY.set(lvt_2_1_);
							entity.move(MoverType.PISTON, lvt_12_1_ * (double) lvt_2_1_.getXOffset(), lvt_12_1_ * (double) lvt_2_1_.getYOffset(), lvt_12_1_ * (double) lvt_2_1_.getZOffset());
							MOVING_ENTITY.set(null);
							if (!this.extending && this.shouldHeadBeRendered) {
								this.fixEntityWithinPistonBase(entity, lvt_2_1_, lvt_3_1_);
							}
						}
					}
				}

			}
		}
	}

	public EnumFacing getFacing() {
		return this.extending ? this.pistonFacing : this.pistonFacing.getOpposite();
	}

	private AxisAlignedBB getMinMaxPiecesAABB(List<AxisAlignedBB> p_getMinMaxPiecesAABB_1_) {
		double lvt_2_1_ = 0.0D;
		double lvt_4_1_ = 0.0D;
		double lvt_6_1_ = 0.0D;
		double lvt_8_1_ = 1.0D;
		double lvt_10_1_ = 1.0D;
		double lvt_12_1_ = 1.0D;

		AxisAlignedBB lvt_15_1_;
		for(Iterator var14 = p_getMinMaxPiecesAABB_1_.iterator(); var14.hasNext(); lvt_12_1_ = Math.max(lvt_15_1_.maxZ, lvt_12_1_)) {
			lvt_15_1_ = (AxisAlignedBB)var14.next();
			lvt_2_1_ = Math.min(lvt_15_1_.minX, lvt_2_1_);
			lvt_4_1_ = Math.min(lvt_15_1_.minY, lvt_4_1_);
			lvt_6_1_ = Math.min(lvt_15_1_.minZ, lvt_6_1_);
			lvt_8_1_ = Math.max(lvt_15_1_.maxX, lvt_8_1_);
			lvt_10_1_ = Math.max(lvt_15_1_.maxY, lvt_10_1_);
		}

		return new AxisAlignedBB(lvt_2_1_, lvt_4_1_, lvt_6_1_, lvt_8_1_, lvt_10_1_, lvt_12_1_);
	}

	private double getMovement(AxisAlignedBB p_getMovement_1_, EnumFacing p_getMovement_2_, AxisAlignedBB p_getMovement_3_) {
		switch(p_getMovement_2_.getAxis()) {
			case X:
				return getDeltaX(p_getMovement_1_, p_getMovement_2_, p_getMovement_3_);
			case Y:
			default:
				return getDeltaY(p_getMovement_1_, p_getMovement_2_, p_getMovement_3_);
			case Z:
				return getDeltaZ(p_getMovement_1_, p_getMovement_2_, p_getMovement_3_);
		}
	}

	private AxisAlignedBB moveByPositionAndProgress(AxisAlignedBB p_moveByPositionAndProgress_1_) {
		double lvt_2_1_ = (double)this.getExtendedProgress(this.progress);
		return p_moveByPositionAndProgress_1_.offset((double)this.pos.getX() + lvt_2_1_ * (double)this.pistonFacing.getXOffset(), (double)this.pos.getY() + lvt_2_1_ * (double)this.pistonFacing.getYOffset(), (double)this.pos.getZ() + lvt_2_1_ * (double)this.pistonFacing.getZOffset());
	}

	private AxisAlignedBB getMovementArea(AxisAlignedBB p_getMovementArea_1_, EnumFacing p_getMovementArea_2_, double p_getMovementArea_3_) {
		double lvt_5_1_ = p_getMovementArea_3_ * (double)p_getMovementArea_2_.getAxisDirection().getOffset();
		double lvt_7_1_ = Math.min(lvt_5_1_, 0.0D);
		double lvt_9_1_ = Math.max(lvt_5_1_, 0.0D);
		switch(p_getMovementArea_2_) {
			case DOWN:
				return new AxisAlignedBB(p_getMovementArea_1_.minX + lvt_7_1_, p_getMovementArea_1_.minY, p_getMovementArea_1_.minZ, p_getMovementArea_1_.minX + lvt_9_1_, p_getMovementArea_1_.maxY, p_getMovementArea_1_.maxZ);
			case UP:
				return new AxisAlignedBB(p_getMovementArea_1_.maxX + lvt_7_1_, p_getMovementArea_1_.minY, p_getMovementArea_1_.minZ, p_getMovementArea_1_.maxX + lvt_9_1_, p_getMovementArea_1_.maxY, p_getMovementArea_1_.maxZ);
			case NORTH:
				return new AxisAlignedBB(p_getMovementArea_1_.minX, p_getMovementArea_1_.minY + lvt_7_1_, p_getMovementArea_1_.minZ, p_getMovementArea_1_.maxX, p_getMovementArea_1_.minY + lvt_9_1_, p_getMovementArea_1_.maxZ);
			case SOUTH:
			default:
				return new AxisAlignedBB(p_getMovementArea_1_.minX, p_getMovementArea_1_.maxY + lvt_7_1_, p_getMovementArea_1_.minZ, p_getMovementArea_1_.maxX, p_getMovementArea_1_.maxY + lvt_9_1_, p_getMovementArea_1_.maxZ);
			case WEST:
				return new AxisAlignedBB(p_getMovementArea_1_.minX, p_getMovementArea_1_.minY, p_getMovementArea_1_.minZ + lvt_7_1_, p_getMovementArea_1_.maxX, p_getMovementArea_1_.maxY, p_getMovementArea_1_.minZ + lvt_9_1_);
			case EAST:
				return new AxisAlignedBB(p_getMovementArea_1_.minX, p_getMovementArea_1_.minY, p_getMovementArea_1_.maxZ + lvt_7_1_, p_getMovementArea_1_.maxX, p_getMovementArea_1_.maxY, p_getMovementArea_1_.maxZ + lvt_9_1_);
		}
	}

	private void fixEntityWithinPistonBase(Entity p_fixEntityWithinPistonBase_1_, EnumFacing p_fixEntityWithinPistonBase_2_, double p_fixEntityWithinPistonBase_3_) {
		AxisAlignedBB lvt_5_1_ = p_fixEntityWithinPistonBase_1_.getEntityBoundingBox();
		AxisAlignedBB lvt_6_1_ = ShapeUtils.fullCube().getBoundingBox().offset(this.pos);
		if (lvt_5_1_.intersects(lvt_6_1_)) {
			EnumFacing lvt_7_1_ = p_fixEntityWithinPistonBase_2_.getOpposite();
			double lvt_8_1_ = this.getMovement(lvt_6_1_, lvt_7_1_, lvt_5_1_) + 0.01D;
			double lvt_10_1_ = this.getMovement(lvt_6_1_, lvt_7_1_, lvt_5_1_.intersect(lvt_6_1_)) + 0.01D;
			if (Math.abs(lvt_8_1_ - lvt_10_1_) < 0.01D) {
				lvt_8_1_ = Math.min(lvt_8_1_, p_fixEntityWithinPistonBase_3_) + 0.01D;
				MOVING_ENTITY.set(p_fixEntityWithinPistonBase_2_);
				p_fixEntityWithinPistonBase_1_.move(MoverType.PISTON, lvt_8_1_ * (double)lvt_7_1_.getXOffset(), lvt_8_1_ * (double)lvt_7_1_.getYOffset(), lvt_8_1_ * (double)lvt_7_1_.getZOffset());
				MOVING_ENTITY.set(null);
			}
		}

	}

	private static double getDeltaX(AxisAlignedBB p_getDeltaX_0_, EnumFacing p_getDeltaX_1_, AxisAlignedBB p_getDeltaX_2_) {
		return p_getDeltaX_1_.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? p_getDeltaX_0_.maxX - p_getDeltaX_2_.minX : p_getDeltaX_2_.maxX - p_getDeltaX_0_.minX;
	}

	private static double getDeltaY(AxisAlignedBB p_getDeltaY_0_, EnumFacing p_getDeltaY_1_, AxisAlignedBB p_getDeltaY_2_) {
		return p_getDeltaY_1_.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? p_getDeltaY_0_.maxY - p_getDeltaY_2_.minY : p_getDeltaY_2_.maxY - p_getDeltaY_0_.minY;
	}

	private static double getDeltaZ(AxisAlignedBB p_getDeltaZ_0_, EnumFacing p_getDeltaZ_1_, AxisAlignedBB p_getDeltaZ_2_) {
		return p_getDeltaZ_1_.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? p_getDeltaZ_0_.maxZ - p_getDeltaZ_2_.minZ : p_getDeltaZ_2_.maxZ - p_getDeltaZ_0_.minZ;
	}

	public IBlockState getState() {
		return this.state;
	}

	public void clearPistonTileEntity() {
		if (this.lastProgress < 1.0F && this.world != null) {
			this.progress = 1.0F;
			this.lastProgress = this.progress;
			this.world.removeTileEntity(this.pos);
			this.invalidate();
			if (this.world.getBlockState(this.pos).getBlock() == ModBlocks.MAGNET_PISTON_MOVING) {
				IBlockState lvt_1_2_;
				if (this.shouldHeadBeRendered) {
					lvt_1_2_ = Blocks.AIR.getDefaultState();
				} else {
					lvt_1_2_ = Block.getValidBlockForPosition(this.state, this.world, this.pos);
				}

				this.world.setBlockState(this.pos, lvt_1_2_, 3);
				this.world.neighborChanged(this.pos, lvt_1_2_.getBlock(), this.pos);
			}
		}

	}

	public void update() {
		this.worldTime = this.world.getTotalWorldTime();
		this.lastProgress = this.progress;
		if (this.lastProgress >= 1.0F) {
			this.world.removeTileEntity(this.pos);
			this.invalidate();
			if (this.state != null && this.world.getBlockState(this.pos).getBlock() == ModBlocks.MAGNET_PISTON_MOVING) {
				IBlockState lvt_1_1_ = Block.getValidBlockForPosition(this.state, this.world, this.pos);
				if (lvt_1_1_.isAir()) {
					this.world.setBlockState(this.pos, this.state, 20);
					Block.replaceBlock(this.state, lvt_1_1_, this.world, this.pos, 3);
				} else {
					if (lvt_1_1_.hasProperty(BlockStateProperties.WATERLOGGED) && lvt_1_1_.getValue(BlockStateProperties.WATERLOGGED)) {
						lvt_1_1_ = lvt_1_1_.withProperty(BlockStateProperties.WATERLOGGED, false);
					}

					this.world.setBlockState(this.pos, lvt_1_1_, 3);
					this.world.neighborChanged(this.pos, lvt_1_1_.getBlock(), this.pos);
				}
			}

		} else {
			float lvt_1_2_ = this.progress + 0.5F;
			this.moveCollidedEntities(lvt_1_2_);
			this.progress = lvt_1_2_;
			if (this.progress >= 1.0F) {
				this.progress = 1.0F;
			}

		}
	}

	public void readFromNBT(NBTTagCompound p_readFromNBT_1_) {
		super.readFromNBT(p_readFromNBT_1_);
		this.state = NBTUtil.readBlockState(p_readFromNBT_1_.getCompoundTag("blockState"));
		this.pistonFacing = EnumFacing.byIndex(p_readFromNBT_1_.getInteger("facing"));
		this.progress = p_readFromNBT_1_.getFloat("progress");
		this.lastProgress = this.progress;
		this.extending = p_readFromNBT_1_.getBoolean("extending");
		this.shouldHeadBeRendered = p_readFromNBT_1_.getBoolean("source");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound p_writeToNBT_1_) {
		super.writeToNBT(p_writeToNBT_1_);
		p_writeToNBT_1_.setTag("blockState", NBTUtil.writeBlockState(this.state));
		p_writeToNBT_1_.setInteger("facing", this.pistonFacing.getIndex());
		p_writeToNBT_1_.setFloat("progress", this.lastProgress);
		p_writeToNBT_1_.setBoolean("extending", this.extending);
		p_writeToNBT_1_.setBoolean("source", this.shouldHeadBeRendered);
		return p_writeToNBT_1_;
	}

	public VoxelShape func_195508_a(IBlockReader p_195508_1_, BlockPos p_195508_2_) {
		VoxelShape lvt_3_2_;
		if (!this.extending && this.shouldHeadBeRendered) {
			lvt_3_2_ = this.state.withProperty(BlockPistonBase.EXTENDED, true).getCollisionShape(p_195508_1_, p_195508_2_);
		} else {
			lvt_3_2_ = ShapeUtils.empty();
		}

		EnumFacing lvt_4_1_ = MOVING_ENTITY.get();
		if ((double)this.progress < 1.0D && lvt_4_1_ == this.getFacing()) {
			return lvt_3_2_;
		} else {
			IBlockState lvt_5_2_;
			if (this.shouldPistonHeadBeRendered()) {
				lvt_5_2_ = ModBlocks.MAGNET_PISTON_HEAD.getDefaultState().withProperty(BlockPistonExtension.FACING, this.pistonFacing).withProperty(BlockPistonExtension.SHORT, this.extending != 1.0F - this.progress < 4.0F);
			} else {
				lvt_5_2_ = this.state;
			}

			float lvt_6_1_ = this.getExtendedProgress(this.progress);
			double lvt_7_1_ = (double)((float)this.pistonFacing.getXOffset() * lvt_6_1_);
			double lvt_9_1_ = (double)((float)this.pistonFacing.getYOffset() * lvt_6_1_);
			double lvt_11_1_ = (double)((float)this.pistonFacing.getZOffset() * lvt_6_1_);
			return ShapeUtils.or(lvt_3_2_, lvt_5_2_.getCollisionShape(p_195508_1_, p_195508_2_).withOffset(lvt_7_1_, lvt_9_1_, lvt_11_1_));
		}
	}

	public long getWorldTime() {
		return this.worldTime;
	}
}
