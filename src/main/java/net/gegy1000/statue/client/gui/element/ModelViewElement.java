package net.gegy1000.statue.client.gui.element;

import net.gegy1000.statue.client.ClientProxy;
import net.gegy1000.statue.client.gui.ModelViewGUI;
import net.gegy1000.statue.server.block.entity.StatueProperty;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.IElementGUI;
import net.ilexiconn.llibrary.client.util.ClientUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

@SideOnly(Side.CLIENT)
public class ModelViewElement<T extends IElementGUI & ModelViewGUI> extends Element<T> {
    private float cameraOffsetX = 0.0F;
    private float cameraOffsetY = 0.0F;
    private float rotationYaw = 225.0F;
    private float rotationPitch = -15.0F;
    private float prevRotationYaw = this.rotationYaw;
    private float prevRotationPitch = this.rotationPitch;
    private float prevCameraOffsetX;
    private float prevCameraOffsetY;
    private float zoom = 1.0F;
    private float zoomVelocity;
    private float prevMouseX;
    private float prevMouseY;

    private float partialTicks;

    private boolean dragged = false;

    public ModelViewElement(T gui, float posX, float posY, int width, int height) {
        super(gui, posX, posY, width, height);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        if (partialTicks < this.partialTicks) {
            this.prevMouseX = mouseX;
            this.prevMouseY = mouseY;
        }
        this.startScissor();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        T gui = this.gui;
        ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
        if (gui.getSelectedModel() != null) {
            this.renderModel(partialTicks, scaledResolution);
        }
        this.endScissor();
        GlStateManager.enableTexture2D();
        this.partialTicks = partialTicks;
        this.zoom += this.zoomVelocity;
        this.zoomVelocity *= 0.6F;
        if (this.zoom < 0.5F) {
            this.zoom = 0.5F;
        } else if (this.zoom > 10.0F) {
            this.zoom = 10.0F;
        }
    }

    @Override
    public void update() {
        super.update();
        if (!this.dragged) {
            this.updatePrevious();
        }
        this.dragged = false;
    }

    private void renderModel(float partialTicks, ScaledResolution scaledResolution) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableNormalize();
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GLU.gluPerspective(30.0F, (float) this.getWidth() / this.getHeight(), 1.0F, 10000.0F);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        int color = LLibrary.CONFIG.getTertiaryColor();
        float backgroundRed = (float) (color >> 16 & 0xFF) / 255.0F;
        float backgroundGreen = (float) (color >> 8 & 0xFF) / 255.0F;
        float backgroundBlue = (float) (color & 0xFF) / 255.0F;
        GlStateManager.clearColor(backgroundRed, backgroundGreen, backgroundBlue, 1.0F);
        GlStateManager.enableLighting();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
        GlStateManager.translate((this.getPosX() * 0.15) / scaledResolution.getScaleFactor(), (this.getPosY() * 0.15) / scaledResolution.getScaleFactor(), 0.0F);
        this.setupCamera(10.0F, partialTicks);

        ModelBase model = this.gui.getSelectedRenderModel();
        if (model != null) {
            TextureManager textureManager = ClientProxy.MINECRAFT.getTextureManager();
            ResourceLocation texture = this.gui.getSelectedRenderTexture();

            if (texture != null) {
                GlStateManager.enableTexture2D();
                textureManager.bindTexture(texture);
            }

            if (LLibrary.CONFIG.getColorMode().equals("light")) {
                GlStateManager.color(0.75F, 0.75F, 0.75F, 1.0F);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(this.gui.getInterpolatedProperty(StatueProperty.OFFSET_X, partialTicks), this.gui.getInterpolatedProperty(StatueProperty.OFFSET_Y, partialTicks), this.gui.getInterpolatedProperty(StatueProperty.OFFSET_Z, partialTicks));
            GlStateManager.rotate(this.gui.getInterpolatedProperty(StatueProperty.ROTATION_X, partialTicks), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(this.gui.getInterpolatedProperty(StatueProperty.ROTATION_Y, partialTicks), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.gui.getInterpolatedProperty(StatueProperty.ROTATION_Z, partialTicks), 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(this.gui.getInterpolatedProperty(StatueProperty.SCALE_X , partialTicks), this.gui.getInterpolatedProperty(StatueProperty.SCALE_Y, partialTicks), this.gui.getInterpolatedProperty(StatueProperty.SCALE_Z, partialTicks));
            GlStateManager.translate(0.0F, -1.5F, 0.0F);
            model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -1.5F, 0.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            this.drawGrid(tessellator, buffer, 0.25F);
            this.drawGrid(tessellator, buffer, 0.5F);
            this.drawGrid(tessellator, buffer, 1.0F);
            this.drawGrid(tessellator, buffer, 2.0F);
            this.drawGrid(tessellator, buffer, 4.0F);
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();
        }

        GlStateManager.enableTexture2D();
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, -5000.0D, 5000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.disableBlend();
    }

    private void drawGrid(Tessellator tessellator, VertexBuffer buffer, float size) {
        float scale = size / 4.0F;
        float lineWidth = Math.min(1.25F, 16.0F * (this.zoom / 4.0F) * (scale / 2.0F));
        GlStateManager.glLineWidth(lineWidth);
        float gridY = 24.0F * 0.0625F;
        size /= scale;
        int color = LLibrary.CONFIG.getTextColor();
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        float a = Math.max(0.0F, Math.min(1.0F, lineWidth - 0.35F));
        for (float x = -size; x < size + scale; x += scale) {
            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(x, gridY, -size).color(r, g, b, a).endVertex();
            buffer.pos(x, gridY, size).color(r, g, b, a).endVertex();
            tessellator.draw();
        }
        for (float z = -size; z < size + scale; z += scale) {
            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(-size, gridY, z).color(r, g, b, a).endVertex();
            buffer.pos(size, gridY, z).color(r, g, b, a).endVertex();
            tessellator.draw();
        }
    }

    private void setupCamera(float scale, float partialTicks) {
        GlStateManager.disableTexture2D();
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(0.0F, -2.0F, -10.0F);
        GlStateManager.scale(this.zoom, this.zoom, this.zoom);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.translate(ClientUtils.interpolate(this.prevCameraOffsetX, this.cameraOffsetX, partialTicks), ClientUtils.interpolate(this.prevCameraOffsetY, this.cameraOffsetY, partialTicks), 0.0F);
        GlStateManager.rotate(ClientUtils.interpolate(this.prevRotationPitch, this.rotationPitch, partialTicks), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(ClientUtils.interpolate(this.prevRotationYaw, this.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.isSelected(mouseX, mouseY)) {
            if (!this.dragged) {
                this.updatePrevious();
            }
            this.dragged = true;
            float xMovement = (mouseX - this.prevMouseX) * 0.15F;
            float yMovement = (mouseY - this.prevMouseY) * 0.15F;
            if (button == 0) {
                this.rotationYaw += xMovement / this.zoom;
                if ((this.rotationPitch > -90.0F || yMovement < 0.0F) && (this.rotationPitch < 90.0F || yMovement > 0.0F)) {
                    this.rotationPitch -= yMovement / this.zoom;
                }
                return true;
            } else if (button == 1) {
                this.cameraOffsetX = this.cameraOffsetX + (xMovement / this.zoom) * 0.016F;
                this.cameraOffsetY = this.cameraOffsetY + (yMovement / this.zoom) * 0.016F;
                return true;
            }
        }
        return false;
    }

    private void updatePrevious() {
        this.prevCameraOffsetX = this.cameraOffsetX;
        this.prevCameraOffsetY = this.cameraOffsetY;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    @Override
    public boolean mouseScrolled(float mouseX, float mouseY, int amount) {
        this.zoomVelocity += (amount / 120.0F) * 0.05F;
        return true;
    }
}
