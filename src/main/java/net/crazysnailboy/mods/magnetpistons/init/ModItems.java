package net.crazysnailboy.mods.magnetpistons.init;

import net.crazysnailboy.mods.magnetpistons.MagnetPistons;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import org.dimdev.rift.listener.ItemAdder;


public class ModItems implements ItemAdder {

	public static final Item MAGNET = new Item(new Item.Builder().group(ItemGroup.REDSTONE));

	@Override
	public void registerItems() {
		Item.register(new ResourceLocation(MagnetPistons.MODID, "magnet"), MAGNET);
		ModBlocks.registerItems();
	}
}
