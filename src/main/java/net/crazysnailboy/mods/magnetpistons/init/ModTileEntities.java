package net.crazysnailboy.mods.magnetpistons.init;

import net.crazysnailboy.mods.magnetpistons.MagnetPistons;
import net.crazysnailboy.mods.magnetpistons.client.renderer.tileentity.TileEntityMagnetPistonRenderer;
import net.crazysnailboy.mods.magnetpistons.tileentity.TileEntityMagnetPiston;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.dimdev.rift.listener.TileEntityTypeAdder;
import org.dimdev.rift.listener.client.TileEntityRendererAdder;

import java.util.Map;


public class ModTileEntities implements TileEntityRendererAdder, TileEntityTypeAdder {
	public static TileEntityType<TileEntityMagnetPiston> MAGNET_PISTON;

	@Override
	public void registerTileEntityTypes() {
		MAGNET_PISTON = (TileEntityType<TileEntityMagnetPiston>) register("magnet_piston", TileEntityType.Builder.create(TileEntityMagnetPiston::new));
	}

	@Override
	public void addTileEntityRenderers(Map<Class<? extends TileEntity>, TileEntityRenderer<? extends TileEntity>> renderers) {
        renderers.put(TileEntityMagnetPiston.class, new TileEntityMagnetPistonRenderer());
	}

	public TileEntityType<? extends TileEntity> register(String name, TileEntityType.Builder<? extends TileEntity> builder) {
		return TileEntityType.registerTileEntityType(MagnetPistons.MODID + name, builder);
	}
}
