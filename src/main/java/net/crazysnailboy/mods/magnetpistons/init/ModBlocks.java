package net.crazysnailboy.mods.magnetpistons.init;

import net.crazysnailboy.mods.magnetpistons.MagnetPistons;
import net.crazysnailboy.mods.magnetpistons.block.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import org.dimdev.rift.listener.BlockAdder;

import java.util.function.Function;

public class ModBlocks implements BlockAdder {
	private static Function<String, ResourceLocation> convert = id -> new ResourceLocation(MagnetPistons.MODID, id);

	public static final Block PISTON = new BlockPistonBase(false);
	public static final Block STICKY_PISTON = new BlockPistonBase(true);
	public static final Block PISTON_HEAD = new BlockPistonExtension();
	public static final Block PISTON_MOVING = new BlockPistonMoving();

	public static final Block MAGNET_PISTON1 = new BlockMagnetPistonBase(1);
	public static final Block MAGNET_PISTON2 = new BlockMagnetPistonBase(2);
	public static final Block MAGNET_PISTON3 = new BlockMagnetPistonBase(3);
	public static final Block MAGNET_PISTON4 = new BlockMagnetPistonBase(4);

	public static final Block MAGNET_PISTON_HEAD = new BlockMagnetPistonExtension();
	public static final Block MAGNET_PISTON_MOVING = new BlockMagnetPistonMoving();

	public static void registerItems() {
		Item.register(MAGNET_PISTON1, ItemGroup.REDSTONE);
		Item.register(MAGNET_PISTON2, ItemGroup.REDSTONE);
		Item.register(MAGNET_PISTON3, ItemGroup.REDSTONE);
		Item.register(MAGNET_PISTON4, ItemGroup.REDSTONE);
	}


	@Override
	public void registerBlocks() {
		Block.register("minecraft:piston", PISTON);
		Block.register("minecraft:sticky_piston", STICKY_PISTON);
		Block.register("minecraft:piston_head", PISTON_HEAD);
		Block.register("minecraft:moving_piston", PISTON_MOVING);

		Block.register(convert.apply("magnet_piston1"), MAGNET_PISTON1);
		Block.register(convert.apply("magnet_piston2"), MAGNET_PISTON2);
		Block.register(convert.apply("magnet_piston3"), MAGNET_PISTON3);
		Block.register(convert.apply("magnet_piston4"), MAGNET_PISTON4);
	}
}
