package net.gegy1000.statue.client.gui;

import net.gegy1000.statue.client.gui.element.ModelViewElement;
import net.gegy1000.statue.server.api.ImportFile;
import net.gegy1000.statue.server.api.ProviderHandler;
import net.gegy1000.statue.server.api.StatueModel;
import net.gegy1000.statue.server.api.StatueTexture;
import net.gegy1000.statue.server.api.TextureProvider;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.gegy1000.statue.server.block.entity.StatueProperty;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.ElementGUI;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.ListElement;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class SelectTextureGUI extends ElementGUI implements ModelViewGUI {
    private Map<String, ImportFile> textures = new HashMap<>();
    private List<String> textureNames = new ArrayList<>();

    private ListElement<SelectTextureGUI> textureList;

    private TextureProvider<?, ?> selectedProvider;
    private StatueTexture selectedTexture;
    private ResourceLocation selectedRenderTexture;

    private StatueBlockEntity entity;
    private StatueModelGUI parent;

    public SelectTextureGUI(StatueBlockEntity entity, StatueModelGUI parent) {
        this.entity = entity;
        this.parent = parent;
    }

    @Override
    protected void initElements() {
        if (this.selectedTexture != null) {
            this.selectedTexture.delete(this.entity.getWorld());
        }

        this.selectedProvider = null;
        this.selectedTexture = null;
        this.selectedRenderTexture = null;

        this.clearElements();

        List<String> providers = new ArrayList<>();

        for (TextureProvider<?, ?> provider : ProviderHandler.getTextureProviders()) {
            String name = provider.getName();
            providers.add(name);
        }

        int width = this.width - 175;
        int height = (int) (width / 1.893);

        this.addElement(new ModelViewElement<>(this, (this.width - 175.0F) / 2 - width / 2 + 175.0F, (this.height - 32) / 2 - height / 2 + 14, width, height));

        this.addElement(new ListElement<>(this, 0.0F, 14.0F, 85, this.height - 32, providers, (list) -> {
            TextureProvider<?, ?> provider = ProviderHandler.getTexture(list.getSelectedIndex());
            if (provider != null) {
                this.selectedProvider = provider;
                this.createModelList();
                return true;
            }
            return false;
        }));

        this.addElement(new ButtonElement<>(this, "Cancel", 0.0F, this.height - 18, 50, 18, (button) -> {
            this.mc.displayGuiScreen(this.parent);
            return true;
        }));

        this.addElement(new ButtonElement<>(this, "Select", this.width - 50, this.height - 18, 50, 18, (button) -> {
            this.entity.setTexture(this.selectedTexture == null ? null : this.selectedProvider, this.selectedTexture);
            this.mc.displayGuiScreen(this.parent);
            return true;
        }));

        this.createModelList();
    }

    private void createModelList() {
        this.textures.clear();
        this.textureNames.clear();

        if (this.selectedProvider != null) {
            Map<String, ? extends ImportFile> textures = this.selectedProvider.getTextures();
            for (Map.Entry<String, ? extends ImportFile> texture : textures.entrySet()) {
                String name = texture.getKey();
                this.textures.put(name, texture.getValue());
                this.textureNames.add(name);
            }
            this.textureNames.sort((s1, s2) -> s1.compareTo(s2));
        }

        if (this.textureList != null) {
            this.removeElement(this.textureList);
        }

        this.addElement(this.textureList = new ListElement<>(this, 90.0F, 14.0F, 85, this.height - 32, this.textureNames, (list) -> {
            StatueTexture texture = this.selectedProvider.getTextureBase(this.textures.get(list.getSelectedEntry()));
            if (texture != null) {
                if (this.selectedTexture != null) {
                    this.selectedTexture.delete(this.entity.getWorld());
                }
                this.selectedTexture = texture;
                this.selectedRenderTexture = texture.get(this.entity.getWorld());
                return true;
            }
            return false;
        }));

        this.textureList.setEnabled(this.selectedProvider != null);
    }

    @Override
    public void drawScreen(float mouseX, float mouseY, float partialTicks) {
        this.drawRectangle(0, 0, this.width, 14, LLibrary.CONFIG.getPrimaryColor());
        this.drawRectangle(0, this.height - 18, this.width, 18, LLibrary.CONFIG.getPrimaryColor());

        this.fontRendererObj.drawString("Texture Types", 4, 3, LLibrary.CONFIG.getTextColor());
        this.fontRendererObj.drawString("Textures", 92, 3, LLibrary.CONFIG.getTextColor());
    }

    protected void drawRectangle(double x, double y, double width, double height, int color) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        float a = (float) (color >> 24 & 0xFF) / 255.0F;
        float r = (float) (color >> 16 & 0xFF) / 255.0F;
        float g = (float) (color >> 8 & 0xFF) / 255.0F;
        float b = (float) (color & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(x, y + height, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x + width, y + height, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x + width, y, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x, y, 0.0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public StatueModel getSelectedModel() {
        return this.entity.getStatueModel();
    }

    @Override
    public ModelBase getSelectedRenderModel() {
        return this.entity.getModel();
    }

    @Override
    public StatueTexture getSelectedTexture() {
        return this.selectedTexture;
    }

    @Override
    public ResourceLocation getSelectedRenderTexture() {
        return this.selectedRenderTexture;
    }

    @Override
    public float getProperty(StatueProperty property) {
        return this.entity.getProperty(property);
    }

    @Override
    public float getInterpolatedProperty(StatueProperty property, float partialTicks) {
        return this.entity.getInterpolatedProperty(property, partialTicks);
    }
}
