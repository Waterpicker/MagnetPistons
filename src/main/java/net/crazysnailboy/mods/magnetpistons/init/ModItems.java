package net.crazysnailboy.mods.magnetpistons.init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@EventBusSubscriber
public class ModItems
{

	public static final Item MAGNET = new Item().setRegistryName("magnet").setUnlocalizedName("magnet").setCreativeTab(CreativeTabs.REDSTONE);


	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(MAGNET);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(final ModelRegistryEvent event)
	{
		ModelLoader.setCustomModelResourceLocation(MAGNET, 0, new ModelResourceLocation(MAGNET.getRegistryName(), "inventory"));
	}

}
