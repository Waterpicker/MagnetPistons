package net.crazysnailboy.mods.magnetpistons.client.renderer.tileentity;

import net.crazysnailboy.mods.magnetpistons.init.ModBlocks;
import net.crazysnailboy.mods.magnetpistons.tileentity.TileEntityMagnetPiston;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class TileEntityMagnetPistonRenderer extends TileEntityRenderer<TileEntityMagnetPiston> {
	private final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

	public TileEntityMagnetPistonRenderer() {
	}

	public void render(TileEntityMagnetPiston piston, double p_render_2_, double p_render_4_, double p_render_6_, float p_render_8_, int p_render_9_) {
		BlockPos pos = piston.getPos().offset(piston.getFacing().getOpposite());
		IBlockState state = piston.getState();
		if (!state.isAir() && piston.getProgress(p_render_8_) < 1.0F) {
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buffer = tess.getBuffer();
			this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.disableCull();
			if (Minecraft.isAmbientOcclusionEnabled()) {
				GlStateManager.shadeModel(7425);
			} else {
				GlStateManager.shadeModel(7424);
			}

			buffer.begin(7, DefaultVertexFormats.BLOCK);
			buffer.setTranslation(p_render_2_ - (double)pos.getX() + (double)piston.getOffsetX(p_render_8_), p_render_4_ - (double)pos.getY() + (double)piston.getOffsetY(p_render_8_), p_render_6_ - (double)pos.getZ() + (double)piston.getOffsetZ(p_render_8_));
			World lvt_14_1_ = this.getWorld();
			if (state.getBlock() == ModBlocks.MAGNET_PISTON_HEAD && piston.getProgress(p_render_8_) <= 4.0F) {
				state = state.withProperty(BlockPistonExtension.SHORT, true);
				this.renderStateModel(pos, state, buffer, lvt_14_1_, false);
			} else if (piston.shouldPistonHeadBeRendered() && !piston.isExtending()) {
				IBlockState lvt_16_1_ = ModBlocks.MAGNET_PISTON_HEAD.getDefaultState().withProperty(BlockPistonExtension.FACING, state.getValue(BlockPistonBase.FACING));
				lvt_16_1_ = lvt_16_1_.withProperty(BlockPistonExtension.SHORT, piston.getProgress(p_render_8_) >= 0.5F);
				this.renderStateModel(pos, lvt_16_1_, buffer, lvt_14_1_, false);
				BlockPos offset = pos.offset(piston.getFacing());
				buffer.setTranslation(p_render_2_ - (double)offset.getX(), p_render_4_ - (double)offset.getY(), p_render_6_ - (double)offset.getZ());
				state = state.withProperty(BlockPistonBase.EXTENDED, true);
				this.renderStateModel(offset, state, buffer, lvt_14_1_, true);
			} else {
				this.renderStateModel(pos, state, buffer, lvt_14_1_, false);
			}

			buffer.setTranslation(0.0D, 0.0D, 0.0D);
			tess.draw();
			RenderHelper.enableStandardItemLighting();
		}
	}

	private boolean renderStateModel(BlockPos pos, IBlockState state, BufferBuilder buffer, World world, boolean p_renderStateModel_5_) {
		return this.blockRenderer.getBlockModelRenderer().func_199324_a(world, this.blockRenderer.getModelForState(state), state, pos, buffer, p_renderStateModel_5_, new Random(), state.getPositionRandom(pos));
	}
}