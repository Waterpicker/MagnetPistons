package net.crazysnailboy.mods.magnetpistons.init;

import net.crazysnailboy.mods.magnetpistons.MagnetPistons;
import net.crazysnailboy.mods.magnetpistons.client.renderer.tileentity.TileEntityMagnetPistonRenderer;
import net.crazysnailboy.mods.magnetpistons.tileentity.TileEntityMagnetPiston;
import net.crazysnailboy.mods.magnetpistons.tileentity.TileEntityPiston;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ModTileEntities
{

	public static void registerTileEntities()
	{
		registerTileEntity(TileEntityMagnetPiston.class);
		registerTileEntity(TileEntityPiston.class);
	}

	private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass)
	{
		GameRegistry.registerTileEntity(tileEntityClass, MagnetPistons.MODID + ":" + tileEntityClass.getSimpleName());
	}


	@SideOnly(Side.CLIENT)
	public static void registerRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMagnetPiston.class, new TileEntityMagnetPistonRenderer());
	}

}
