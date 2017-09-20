package net.crazysnailboy.mods.magnetpistons.init;

import net.crazysnailboy.mods.magnetpistons.block.BlockMagnetPistonBase;
import net.crazysnailboy.mods.magnetpistons.block.BlockMagnetPistonExtension;
import net.crazysnailboy.mods.magnetpistons.block.BlockMagnetPistonMoving;
import net.crazysnailboy.mods.magnetpistons.block.BlockPistonBase;
import net.crazysnailboy.mods.magnetpistons.block.BlockPistonExtension;
import net.crazysnailboy.mods.magnetpistons.block.BlockPistonMoving;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPiston;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@EventBusSubscriber
public class ModBlocks
{

	public static final Block PISTON = new BlockPistonBase(false).setRegistryName("minecraft:piston").setUnlocalizedName("pistonBase");
	public static final Block STICKY_PISTON = new BlockPistonBase(true).setRegistryName("minecraft:sticky_piston").setUnlocalizedName("pistonStickyBase");
	public static final Block PISTON_HEAD = new BlockPistonExtension().setRegistryName("minecraft:piston_head");
	public static final Block PISTON_EXTENSION = new BlockPistonMoving().setRegistryName("minecraft:piston_extension");

	public static final Block MAGNET_PISTON1 = new BlockMagnetPistonBase(1).setRegistryName("magnet_piston1").setUnlocalizedName("magnet_piston");
	public static final Block MAGNET_PISTON2 = new BlockMagnetPistonBase(2).setRegistryName("magnet_piston2").setUnlocalizedName("magnet_piston");
	public static final Block MAGNET_PISTON3 = new BlockMagnetPistonBase(3).setRegistryName("magnet_piston3").setUnlocalizedName("magnet_piston");
	public static final Block MAGNET_PISTON4 = new BlockMagnetPistonBase(4).setRegistryName("magnet_piston4").setUnlocalizedName("magnet_piston");

	public static final Block MAGNET_PISTON_HEAD = new BlockMagnetPistonExtension().setRegistryName("magnet_piston_head");
	public static final Block MAGNET_PISTON_EXTENSION = new BlockMagnetPistonMoving().setRegistryName("magnet_piston_extension");


	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(
			PISTON, STICKY_PISTON, PISTON_HEAD, PISTON_EXTENSION,
			MAGNET_PISTON1, MAGNET_PISTON2, MAGNET_PISTON3, MAGNET_PISTON4,
			MAGNET_PISTON_HEAD, MAGNET_PISTON_EXTENSION
		);

//		System.out.println("----------");
//		System.out.println( Block.REGISTRY.getClass().getName() );
//		System.out.println("----------");
//		forceIt();
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new ItemPiston(PISTON).setRegistryName(PISTON.getRegistryName()));
		event.getRegistry().register(new ItemPiston(STICKY_PISTON).setRegistryName(STICKY_PISTON.getRegistryName()));

		event.getRegistry().register(new ItemPiston(MAGNET_PISTON1).setRegistryName(MAGNET_PISTON1.getRegistryName()));
		event.getRegistry().register(new ItemPiston(MAGNET_PISTON2).setRegistryName(MAGNET_PISTON2.getRegistryName()));
		event.getRegistry().register(new ItemPiston(MAGNET_PISTON3).setRegistryName(MAGNET_PISTON3.getRegistryName()));
		event.getRegistry().register(new ItemPiston(MAGNET_PISTON4).setRegistryName(MAGNET_PISTON4.getRegistryName()));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(final ModelRegistryEvent event)
	{
		Item item = Item.getItemFromBlock(MAGNET_PISTON1);
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	private static void rgeisterModel(Block block)
	{
		Item item = Item.getItemFromBlock(block);
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}


//	private static void forceIt()
//	{
//		Field lockedField = ReflectionHelper.getDeclaredField("net.minecraftforge.registries.NamespacedDefaultedWrapper", "locked");
//
//		ReflectionHelper.setFieldValue(lockedField, Block.REGISTRY, false);
//		Block.REGISTRY.putObject(new ResourceLocation("minecraft:piston"), PISTON);
//		Block.REGISTRY.putObject(new ResourceLocation("minecraft:sticky_piston"), STICKY_PISTON);
//		ReflectionHelper.setFieldValue(lockedField, Block.REGISTRY, true);
//
//		Field pistonField = ReflectionHelper.getDeclaredField(net.minecraft.init.Blocks.class, "PISTON");
//		Field stickyPistonField = ReflectionHelper.getDeclaredField(net.minecraft.init.Blocks.class, "STICKY_PISTON");
//
//		ReflectionHelper.setFinalStatic(pistonField, (net.minecraft.block.BlockPistonBase)PISTON);
//	}

}
