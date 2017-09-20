package net.crazysnailboy.mods.magnetpistons;

import net.crazysnailboy.mods.magnetpistons.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


@Mod(modid = MagnetPistons.MODID, name = MagnetPistons.NAME, version = MagnetPistons.VERSION)
public class MagnetPistons
{

	public static final String MODID = "magnetpistons";
	public static final String NAME = "Magnet Pistons";
	public static final String VERSION = "${version}";

	private static final String CLIENT_PROXY_CLASS = "net.crazysnailboy.mods.magnetpistons.proxy.ClientProxy";
	private static final String SERVER_PROXY_CLASS = "net.crazysnailboy.mods.magnetpistons.proxy.CommonProxy";


	@SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = SERVER_PROXY_CLASS)
	public static CommonProxy proxy;


	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}

}
