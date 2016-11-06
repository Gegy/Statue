package net.gegy1000.statue.client.render.block;

import net.gegy1000.statue.server.block.BlockRegistry;
import net.gegy1000.statue.server.block.StatueBlock;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.gegy1000.statue.server.block.entity.StatueProperty;
import net.ilexiconn.llibrary.client.model.VoxelModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StatueRenderer extends TileEntitySpecialRenderer<StatueBlockEntity> {
    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();

    private ModelBase voxelModel = new VoxelModel();

    @Override
    public void renderTileEntityAt(StatueBlockEntity entity, double x, double y, double z, float partialTicks, int destroyStage) {
        EnumFacing facing = EnumFacing.NORTH;
        ModelBase model = null;
        ResourceLocation texture = null;
        if (entity != null) {
            model = entity.getModel();
            texture = entity.getTexture();
            IBlockState state = this.getWorld().getBlockState(entity.getPos());
            if (state.getBlock() == BlockRegistry.STATUE) {
                facing = state.getValue(StatueBlock.FACING);
                if (facing.getAxis() == EnumFacing.Axis.X) {
                    facing = facing.getOpposite();
                }
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        if (model == null) {
            GlStateManager.disableTexture2D();
            GlStateManager.pushMatrix();
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            GlStateManager.translate(0.0F, -3.175F, 0.0F);
            this.renderModel(this.voxelModel, 2.8F, 0.23F);
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.translate(0.0F, -3.0F, 0.0F);
            this.renderModel(this.voxelModel, 2.666F, 1.0F);
            GlStateManager.enableTexture2D();
        } else if (MINECRAFT.objectMouseOver == null || (!entity.getPos().equals(MINECRAFT.objectMouseOver.getBlockPos()) || MINECRAFT.gameSettings.hideGUI)) {
            GlStateManager.rotate(facing.getHorizontalAngle(), 0.0F, 90.0F, 0.0F);
            if (texture == null) {
                GlStateManager.disableTexture2D();
            } else {
                GlStateManager.enableTexture2D();
                MINECRAFT.getTextureManager().bindTexture(texture);
            }
            GlStateManager.enableRescaleNormal();
            GlStateManager.disableCull();
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(entity.getInterpolatedProperty(StatueProperty.OFFSET_X, partialTicks), entity.getInterpolatedProperty(StatueProperty.OFFSET_Y, partialTicks), entity.getInterpolatedProperty(StatueProperty.OFFSET_Z, partialTicks));
            GlStateManager.rotate(entity.getInterpolatedProperty(StatueProperty.ROTATION_Y, partialTicks), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(entity.getInterpolatedProperty(StatueProperty.ROTATION_X, partialTicks), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(entity.getInterpolatedProperty(StatueProperty.ROTATION_Z, partialTicks), 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(entity.getInterpolatedProperty(StatueProperty.SCALE_X, partialTicks), entity.getInterpolatedProperty(StatueProperty.SCALE_Y, partialTicks), entity.getInterpolatedProperty(StatueProperty.SCALE_Z, partialTicks));
            GlStateManager.translate(0.0F, -1.5F, 0.0F);
            this.renderModel(model, 1.0F, 1.0F);
            GlStateManager.disableRescaleNormal();
            GlStateManager.enableCull();
        }
        GlStateManager.popMatrix();
        if (entity != null && MINECRAFT.getRenderManager().isDebugBoundingBox()) {
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.pushMatrix();
            RenderGlobal.drawBoundingBox(x, y, z, x + 1, y + 1, z + 1, 1.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.popMatrix();
        }
        GlStateManager.enableTexture2D();
    }

    private void renderModel(ModelBase model, float scale, float lightness) {
        GlStateManager.pushMatrix();
        GlStateManager.color(lightness, lightness, lightness, 1.0F);
        GlStateManager.scale(scale, scale, scale);
        model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
    }
}
