package net.crazysnailboy.mods.magnetpistons.init;

import net.crazysnailboy.mods.magnetpistons.MagnetPistons;
import net.crazysnailboy.mods.magnetpistons.block.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import org.dimdev.rift.listener.BlockAdder;

import java.util.function.Function;

public class ModBlocks implements BlockAdder {
	private static Function<String, ResourceLocation> convert = id -> new ResourceLocation(MagnetPistons.MODID, id);

	public static final Block MAGNET_PISTON1 = new BlockMagnetPistonBase(1);
	public static final Block MAGNET_PISTON2 = new BlockMagnetPistonBase(2);
	public static final Block MAGNET_PISTON3 = new BlockMagnetPistonBase(3);
	public static final Block MAGNET_PISTON4 = new BlockMagnetPistonBase(4);
	public static final Block MAGNET_PISTON5 = new BlockMagnetPistonBase(5);
	public static final Block MAGNET_PISTON6 = new BlockMagnetPistonBase(6);
	public static final Block MAGNET_PISTON7 = new BlockMagnetPistonBase(7);
	public static final Block MAGNET_PISTON8 = new BlockMagnetPistonBase(8);
	public static final Block MAGNET_PISTON9 = new BlockMagnetPistonBase(9);
	public static final Block MAGNET_PISTON10 = new BlockMagnetPistonBase(10);
	public static final Block MAGNET_PISTON11 = new BlockMagnetPistonBase(11);
	public static final Block MAGNET_PISTON12 = new BlockMagnetPistonBase(12);
	public static final Block MAGNET_PISTON13 = new BlockMagnetPistonBase(13);
	public static final Block MAGNET_PISTON14 = new BlockMagnetPistonBase(14);
	public static final Block MAGNET_PISTON15 = new BlockMagnetPistonBase(15);
	public static final Block MAGNET_PISTON16 = new BlockMagnetPistonBase(16);

	public static final Block MAGNET_PISTON_HEAD = new BlockMagnetPistonExtension(Block.Builder.from(Blocks.PISTON_HEAD));
	public static final Block MAGNET_PISTON_MOVING = new BlockMagnetPistonMoving(Block.Builder.from(Blocks.MOVING_PISTON));

	public static final Block[] STRENGTH;

	public static void registerItems() {
		Item.register(MAGNET_PISTON1, ItemGroup.REDSTONE);
	}


	@Override
	public void registerBlocks() {
		Block.register(convert.apply("magnet_piston1"), MAGNET_PISTON1);
		Block.register(convert.apply("magnet_piston2"), MAGNET_PISTON2);
		Block.register(convert.apply("magnet_piston3"), MAGNET_PISTON3);
		Block.register(convert.apply("magnet_piston4"), MAGNET_PISTON4);
		Block.register(convert.apply("magnet_piston5"), MAGNET_PISTON5);
		Block.register(convert.apply("magnet_piston6"), MAGNET_PISTON6);
		Block.register(convert.apply("magnet_piston7"), MAGNET_PISTON7);
		Block.register(convert.apply("magnet_piston8"), MAGNET_PISTON8);
		Block.register(convert.apply("magnet_piston9"), MAGNET_PISTON9);
		Block.register(convert.apply("magnet_piston10"), MAGNET_PISTON10);
		Block.register(convert.apply("magnet_piston11"), MAGNET_PISTON11);
		Block.register(convert.apply("magnet_piston12"), MAGNET_PISTON12);
		Block.register(convert.apply("magnet_piston13"), MAGNET_PISTON13);
		Block.register(convert.apply("magnet_piston14"), MAGNET_PISTON14);
		Block.register(convert.apply("magnet_piston15"), MAGNET_PISTON15);
		Block.register(convert.apply("magnet_piston16"), MAGNET_PISTON16);

		Block.register(convert.apply("magnet_piston_head"), MAGNET_PISTON_HEAD);
		Block.register(convert.apply("magnet_piston_moving"), MAGNET_PISTON_MOVING);
	}

	static {
		STRENGTH = new Block[]{
				MAGNET_PISTON1,
				MAGNET_PISTON2,
				MAGNET_PISTON3,
				MAGNET_PISTON4,
				MAGNET_PISTON5,
				MAGNET_PISTON6,
				MAGNET_PISTON7,
				MAGNET_PISTON8,
				MAGNET_PISTON9,
				MAGNET_PISTON10,
				MAGNET_PISTON11,
				MAGNET_PISTON12,
				MAGNET_PISTON13,
				MAGNET_PISTON14,
				MAGNET_PISTON15,
				MAGNET_PISTON16
		};
	}
}
