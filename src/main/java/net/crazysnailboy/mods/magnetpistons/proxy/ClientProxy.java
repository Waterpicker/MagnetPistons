package net.crazysnailboy.mods.magnetpistons.proxy;

import net.crazysnailboy.mods.magnetpistons.init.ModTileEntities;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{

	@Override
	public void preInit()
	{
		super.preInit();
		ModTileEntities.registerRenderers();
	}

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public void postInit()
	{
		super.postInit();
	}

}
