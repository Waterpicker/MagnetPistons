package net.crazysnailboy.mods.magnetpistons.block.state;

import com.google.common.collect.Lists;
import net.crazysnailboy.mods.magnetpistons.block.BlockMagnetPistonBase;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;


public class BlockMagnetPistonStructureHelper {
	private final World world;
	private final BlockPos pistonPos;
	private final boolean extending;
	private final BlockPos blockToMove;
	private final EnumFacing moveDirection;
	private final List<BlockPos> toMove = Lists.newArrayList();
	private final List<BlockPos> toDestroy = Lists.newArrayList();
	private final EnumFacing facing;
	private final int strength;

	public BlockMagnetPistonStructureHelper(World world, BlockPos pos, EnumFacing facing, boolean extending, int strength) {
		this.world = world;
		this.pistonPos = pos;
		this.facing = facing;
		this.extending = extending;
		this.strength = strength;

		if (extending) {
			this.moveDirection = facing;
			this.blockToMove = pos.offset(facing);
		} else {
			this.moveDirection = facing.getOpposite();
			this.blockToMove = pos.offset(facing, 2);
		}

	}

	public boolean canMove() {
		this.toMove.clear();
		this.toDestroy.clear();
		IBlockState state = this.world.getBlockState(this.blockToMove);
		if (!BlockMagnetPistonBase.canPush(state, this.world, this.blockToMove, this.moveDirection, false, this.facing)) {
			if (this.extending && state.getPushReaction() == EnumPushReaction.DESTROY) {
				this.toDestroy.add(this.blockToMove);
				return true;
			} else {
				return false;
			}
		} else if (!this.addBlockLine(this.blockToMove, this.moveDirection)) {
			return false;
		} else {
			for (BlockPos pos : this.toMove) {
				if (this.world.getBlockState(pos).getBlock() == Blocks.SLIME_BLOCK && !this.addBranchingBlocks(pos)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean addBlockLine(BlockPos origin, EnumFacing direction) {
		IBlockState state = this.world.getBlockState(origin);
		if (state.isAir()) {
			return true;
		} else if (!BlockMagnetPistonBase.canPush(state, this.world, origin, this.moveDirection, false, direction)) {
			return true;
		} else if (origin.equals(this.pistonPos)) {
			return true;
		} else if (this.toMove.contains(origin)) {
			return true;
		} else {
			int i = 1;
			if (i + this.toMove.size() > this.strength) {
				return false;
			} else {
				while(true) {
					BlockPos blockPos = origin.offset(this.moveDirection.getOpposite(), i);
					state = this.world.getBlockState(blockPos);
					if (state.isAir() || !BlockMagnetPistonBase.canPush(state, this.world, blockPos, this.moveDirection, false, this.moveDirection.getOpposite()) || blockPos.equals(this.pistonPos)) {
						break;
					}

					++i;
					if (i + this.toMove.size() > this.strength && this.extending) {
						return false;
					}
				}

				int i1 = 0;

				for(int j = (this.extending ? i - 1 : this.strength -1); j >= 0; --j) {
					this.toMove.add(origin.offset(this.moveDirection.getOpposite(), j));
					++i1;
				}

				int j1 = 1;

				while(true) {
					BlockPos blockpos1 = origin.offset(this.moveDirection, j1);
					int k = this.toMove.indexOf(blockpos1);
					if (k > -1) {
						this.reorderListAtCollision(i1, k);

						for(int l = 0; l <= k + i1; ++l) {
							BlockPos blockpos2 = this.toMove.get(l);
							if (this.world.getBlockState(blockpos2).getBlock() == Blocks.SLIME_BLOCK && !this.addBranchingBlocks(blockpos2)) {
								return false;
							}
						}

						return true;
					}

					state = this.world.getBlockState(blockpos1);
					if (state.isAir()) {
						return true;
					}

					if (!BlockMagnetPistonBase.canPush(state, this.world, blockpos1, this.moveDirection, true, this.moveDirection) || blockpos1.equals(this.pistonPos)) {
						return false;
					}

					if (state.getPushReaction() == EnumPushReaction.DESTROY) {
						this.toDestroy.add(blockpos1);
						return true;
					}

					if (this.toMove.size() >= this.strength) {
						return false;
					}

					this.toMove.add(blockpos1);
					++i1;
					++j1;
				}
			}
		}
	}

	private void reorderListAtCollision(int p_reorderListAtCollision_1_, int p_reorderListAtCollision_2_) {
		List<BlockPos> list = Lists.newArrayList();
		List<BlockPos> list1 = Lists.newArrayList();
		List<BlockPos> list2 = Lists.newArrayList();
		list.addAll(this.toMove.subList(0, p_reorderListAtCollision_2_));
		list1.addAll(this.toMove.subList(this.toMove.size() - p_reorderListAtCollision_1_, this.toMove.size()));
		list2.addAll(this.toMove.subList(p_reorderListAtCollision_2_, this.toMove.size() - p_reorderListAtCollision_1_));
		this.toMove.clear();
		this.toMove.addAll(list);
		this.toMove.addAll(list1);
		this.toMove.addAll(list2);
	}

	private boolean addBranchingBlocks(BlockPos pos) {
		for (EnumFacing facing : EnumFacing.values()) {
			if (facing.getAxis() != this.moveDirection.getAxis() && !this.addBlockLine(pos.offset(facing), facing)) {
				return false;
			}
		}

		return true;
	}

	public List<BlockPos> getBlocksToMove() {
		return this.toMove;
	}

	public List<BlockPos> getBlocksToDestroy() {
		return this.toDestroy;
	}
}
