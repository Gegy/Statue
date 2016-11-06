package net.gegy1000.statue.client;

import net.gegy1000.statue.client.model.OutlineRenderer;
import net.gegy1000.statue.server.block.StatueBlock;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.gegy1000.statue.server.block.entity.StatueProperty;
import net.ilexiconn.llibrary.LLibrary;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ClientEventHandler {
    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        float partialTicks = event.getPartialTicks();
        RayTraceResult target = event.getTarget();
        if (target != null && target.typeOfHit == RayTraceResult.Type.BLOCK) {
            EntityPlayer player = event.getPlayer();
            BlockPos pos = target.getBlockPos();
            IBlockState state = player.worldObj.getBlockState(pos);
            if (state.getBlock() instanceof StatueBlock) {
                TileEntity tile = player.worldObj.getTileEntity(pos);
                if (tile instanceof StatueBlockEntity) {
                    StatueBlockEntity entity = (StatueBlockEntity) tile;
                    ModelBase model = entity.getModel();
                    ResourceLocation texture = entity.getTexture();
                    if (model instanceof OutlineRenderer) {
                        EnumFacing facing = state.getValue(StatueBlock.FACING);
                        if (facing.getAxis() == EnumFacing.Axis.Z) {
                            facing = facing.getOpposite();
                        }
                        OutlineRenderer outlineRenderer = (OutlineRenderer) model;
                        GlStateManager.pushMatrix();
                        GlStateManager.disableTexture2D();
                        GlStateManager.disableBlend();
                        GlStateManager.enableRescaleNormal();
                        GlStateManager.enableCull();
                        RenderHelper.enableStandardItemLighting();
                        int light = player.worldObj.getCombinedLight(tile.getPos(), 0);
                        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light % 65536, light >> 16);
                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        double x = pos.getX() - TileEntityRendererDispatcher.staticPlayerX;
                        double y = pos.getY() - TileEntityRendererDispatcher.staticPlayerY;
                        double z = pos.getZ() - TileEntityRendererDispatcher.staticPlayerZ;
                        int accent = LLibrary.CONFIG.getAccentColor();
                        float r = (float) (accent >> 16 & 0xFF) / 255.0F;
                        float g = (float) (accent >> 8 & 0xFF) / 255.0F;
                        float b = (float) (accent & 0xFF) / 255.0F;
                        GlStateManager.disableLighting();
                        GlStateManager.color(r, g, b, 1.0F);
                        GlStateManager.translate(x + 0.5, y, z + 0.5);
                        GlStateManager.rotate(facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
                        double scale = 1.0;
                        GlStateManager.scale(-scale, -scale, scale);
                        GlStateManager.translate(entity.getInterpolatedProperty(StatueProperty.OFFSET_X, partialTicks), entity.getInterpolatedProperty(StatueProperty.OFFSET_Y, partialTicks), entity.getInterpolatedProperty(StatueProperty.OFFSET_Z, partialTicks));
                        GlStateManager.rotate(entity.getInterpolatedProperty(StatueProperty.ROTATION_X, partialTicks), 1.0F, 0.0F, 0.0F);
                        GlStateManager.rotate(entity.getInterpolatedProperty(StatueProperty.ROTATION_Y, partialTicks), 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(entity.getInterpolatedProperty(StatueProperty.ROTATION_Z, partialTicks), 0.0F, 0.0F, 1.0F);
                        GlStateManager.scale(entity.getInterpolatedProperty(StatueProperty.SCALE_X, partialTicks), entity.getInterpolatedProperty(StatueProperty.SCALE_Y, partialTicks), entity.getInterpolatedProperty(StatueProperty.SCALE_Z, partialTicks));
                        GlStateManager.translate(0.0F, -1.5F, 0.0F);
                        GlStateManager.depthMask(false);
                        outlineRenderer.renderOutlines(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
                        GlStateManager.depthMask(true);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.enableLighting();
                        ClientProxy.MINECRAFT.entityRenderer.enableLightmap();
                        if (texture != null) {
                            GlStateManager.enableTexture2D();
                            ClientProxy.MINECRAFT.getTextureManager().bindTexture(texture);
                        }
                        GlStateManager.enableBlend();
                        model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
                        GlStateManager.enableTexture2D();
                        event.setCanceled(true);
                        ClientProxy.MINECRAFT.entityRenderer.disableLightmap();
                        GlStateManager.popMatrix();
                        GlStateManager.disableRescaleNormal();
                        GlStateManager.disableCull();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        TileEntity tile = event.getWorld().getTileEntity(event.getPos());
        if (tile instanceof StatueBlockEntity) {
            StatueBlockEntity entity = (StatueBlockEntity) tile;
            entity.delete();
        }
    }
}
