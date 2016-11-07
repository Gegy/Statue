package net.gegy1000.statue.client.gui;

import net.gegy1000.statue.client.gui.element.ModelViewElement;
import net.gegy1000.statue.client.gui.property.CheckboxProperty;
import net.gegy1000.statue.client.gui.property.RotationProperty;
import net.gegy1000.statue.client.gui.property.TransformProperty;
import net.gegy1000.statue.server.api.StatueModel;
import net.gegy1000.statue.server.api.StatueTexture;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.gegy1000.statue.server.block.entity.StatueProperty;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.CheckboxElement;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.PropertyInputElement;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.llibrary.client.gui.element.StateButtonElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class StatueModelGUI extends BackgroundElementGUI implements ModelViewGUI {
    private StatueBlockEntity entity;

    public StatueModelGUI(World world, BlockPos pos) {
        this.entity = (StatueBlockEntity) world.getTileEntity(pos);
    }

    private ModelViewElement<StatueModelGUI> viewElement;

    private RotationProperty propertyRotationX, propertyRotationY, propertyRotationZ;
    private TransformProperty propertyOffsetX, propertyOffsetY, propertyOffsetZ;
    private TransformProperty propertyScaleX, propertyScaleY, propertyScaleZ;
    private CheckboxProperty propertyLocked;

    private SliderElement<StatueModelGUI, RotationProperty> rotationX, rotationY, rotationZ;
    private SliderElement<StatueModelGUI, TransformProperty> offsetX, offsetY, offsetZ;
    private SliderElement<StatueModelGUI, TransformProperty> scaleX, scaleY, scaleZ;
    
    private StatueModel model;

    private boolean locked;

    private boolean drawBackground = true;

    @Override
    protected void initElements() {
        this.updateProperties();

        this.clearElements();

        int width = this.width - 122;
        int height = (int) (width / 1.893);

        boolean enabled = this.entity.canInteract(this.mc.thePlayer);

        this.addElement(this.viewElement = new ModelViewElement<>(this, (this.width - 122.0F) / 2 - width / 2 + 122.0F, (this.height - 32) / 2 - height / 2 + 14, width, height));
        this.viewElement.setVisible(this.drawBackground);

        List<String> states = new ArrayList<>();
        states.add("World");
        states.add("GUI");

        this.addElement(new StateButtonElement<>(this, this.width - 100.0F, this.height - 18, 50, 18, states, (button) -> {
            this.drawBackground = !this.drawBackground;
            this.viewElement.setVisible(this.drawBackground);
            return true;
        }).withState(this.drawBackground ? 1 : 0));

        this.addElement(new ButtonElement<>(this, "Okay", this.width - 50.0F, this.height - 18, 50, 18, (button) -> {
            this.mc.displayGuiScreen(null);
            return true;
        }));
        this.addElement(new ButtonElement<>(this, "Edit", 0.0F, this.height - 18, 61, 18, (button) -> {
            this.mc.displayGuiScreen(new SelectModelGUI(this.entity, this));
            return true;
        }).setEnabled(enabled));
        this.addElement(new ButtonElement<>(this, "Texture", 61.0F, this.height - 18, 61, 18, (button) -> {
            if (this.getSelectedModel() != null) {
                this.mc.displayGuiScreen(new SelectTextureGUI(this.entity, this));
                return true;
            }
            return false;
        }).setEnabled(enabled));

        this.addElement(new LabelElement<>(this, "Rotation", 3.0F, 20.0F));
        this.addElement(this.rotationX = (SliderElement<StatueModelGUI, RotationProperty>) new SliderElement<>(this, 3.0F, 30.0F, 79.0F, this.propertyRotationX, 0.1F).setEnabled(enabled));
        this.addElement(this.rotationY = (SliderElement<StatueModelGUI, RotationProperty>) new SliderElement<>(this, 3.0F, 43.0F, 79.0F, this.propertyRotationY, 0.1F).setEnabled(enabled));
        this.addElement(this.rotationZ = (SliderElement<StatueModelGUI, RotationProperty>) new SliderElement<>(this, 3.0F, 56.0F, 79.0F, this.propertyRotationZ, 0.1F).setEnabled(enabled));

        this.addElement(new LabelElement<>(this, "Offset", 3.0F, 75.0F));
        this.addElement(this.offsetX = (SliderElement<StatueModelGUI, TransformProperty>) new SliderElement<>(this, 3.0F, 85.0F, this.propertyOffsetX, 0.1F).setEnabled(enabled));
        this.addElement(this.offsetY = (SliderElement<StatueModelGUI, TransformProperty>) new SliderElement<>(this, 42.0F, 85.0F, this.propertyOffsetY, 0.1F).setEnabled(enabled));
        this.addElement(this.offsetZ = (SliderElement<StatueModelGUI, TransformProperty>) new SliderElement<>(this, 81.0F, 85.0F, this.propertyOffsetZ, 0.1F).setEnabled(enabled));

        this.addElement(new LabelElement<>(this, "Scale", 3.0F, 104.0F));
        this.addElement(this.scaleX = (SliderElement<StatueModelGUI, TransformProperty>) new SliderElement<>(this, 3.0F, 114.0F, this.propertyScaleX, 0.1F).setEnabled(enabled));
        this.addElement(this.scaleY = (SliderElement<StatueModelGUI, TransformProperty>) new SliderElement<>(this, 42.0F, 114.0F, this.propertyScaleY, 0.1F).setEnabled(enabled));
        this.addElement(this.scaleZ = (SliderElement<StatueModelGUI, TransformProperty>) new SliderElement<>(this, 81.0F, 114.0F, this.propertyScaleZ, 0.1F).setEnabled(enabled));

        this.addElement(new LabelElement<>(this, "Locked", 3.0F, 140.0F));
        this.addElement(new CheckboxElement<>(this, 3.0F, 150.0F, this.propertyLocked).setEnabled(this.entity.canInteract(this.mc.thePlayer, true)));

        if (Loader.isModLoaded("qubble")) {
            this.addElement(new ButtonElement<>(this, "Qubble", this.width - 50.0F, 0.0F, 50, 14, button -> {
                try {
                    Class<?> guiClass = Class.forName("net.ilexiconn.qubble.client.gui.QubbleGUI");
                    Object gui = guiClass.getDeclaredConstructor(GuiScreen.class).newInstance(this);
                    if (gui instanceof GuiScreen) {
                        this.mc.displayGuiScreen((GuiScreen) gui);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }));
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.getSelectedModel() != this.model || this.entity.isLocked() != this.locked) {
            this.locked = this.entity.isLocked();
            this.model = this.getSelectedModel();
            this.initElements();
        }
    }

    protected void updateProperties() {
        this.propertyRotationX = new RotationProperty(value -> this.entity.setProperty(StatueProperty.ROTATION_X, value));
        this.propertyRotationX.set(this.entity.getProperty(StatueProperty.ROTATION_X));
        this.propertyRotationY = new RotationProperty(value -> this.entity.setProperty(StatueProperty.ROTATION_Y, value));
        this.propertyRotationY.set(this.entity.getProperty(StatueProperty.ROTATION_Y));
        this.propertyRotationZ = new RotationProperty(value -> this.entity.setProperty(StatueProperty.ROTATION_Z, value));
        this.propertyRotationZ.set(this.entity.getProperty(StatueProperty.ROTATION_Z));

        this.propertyOffsetX = new TransformProperty(value -> this.entity.setProperty(StatueProperty.OFFSET_X, value));
        this.propertyOffsetX.set(this.entity.getProperty(StatueProperty.OFFSET_X));
        this.propertyOffsetY = new TransformProperty(value -> this.entity.setProperty(StatueProperty.OFFSET_Y, value));
        this.propertyOffsetY.set(this.entity.getProperty(StatueProperty.OFFSET_Y));
        this.propertyOffsetZ = new TransformProperty(value -> this.entity.setProperty(StatueProperty.OFFSET_Z, value));
        this.propertyOffsetZ.set(this.entity.getProperty(StatueProperty.OFFSET_Z));

        this.propertyScaleX = new TransformProperty(value -> {
            this.entity.setProperty(StatueProperty.SCALE_X, value);
            if (isCtrlKeyDown()) {
                this.entity.setProperty(StatueProperty.SCALE_Y, value);
                this.entity.setProperty(StatueProperty.SCALE_Z, value);
                this.propertyScaleY.set(this.entity.getProperty(StatueProperty.SCALE_Y));
                this.propertyScaleZ.set(this.entity.getProperty(StatueProperty.SCALE_Z));
                ((PropertyInputElement) this.scaleY.getValueInput()).readValue();
                ((PropertyInputElement) this.scaleZ.getValueInput()).readValue();
            }
        });
        this.propertyScaleX.set(this.entity.getProperty(StatueProperty.SCALE_X));
        this.propertyScaleY = new TransformProperty(value -> {
            this.entity.setProperty(StatueProperty.SCALE_Y, value);
            if (isCtrlKeyDown()) {
                this.entity.setProperty(StatueProperty.SCALE_X, value);
                this.entity.setProperty(StatueProperty.SCALE_Z, value);
                this.propertyScaleX.set(this.entity.getProperty(StatueProperty.SCALE_X));
                this.propertyScaleZ.set(this.entity.getProperty(StatueProperty.SCALE_Z));
                ((PropertyInputElement) this.scaleX.getValueInput()).readValue();
                ((PropertyInputElement) this.scaleZ.getValueInput()).readValue();
            }
        });
        this.propertyScaleY.set(this.entity.getProperty(StatueProperty.SCALE_Y));
        this.propertyScaleZ = new TransformProperty(value -> {
            this.entity.setProperty(StatueProperty.SCALE_Z, value);
            if (isCtrlKeyDown()) {
                this.entity.setProperty(StatueProperty.SCALE_X, value);
                this.entity.setProperty(StatueProperty.SCALE_Y, value);
                this.propertyScaleX.set(this.entity.getProperty(StatueProperty.SCALE_X));
                this.propertyScaleY.set(this.entity.getProperty(StatueProperty.SCALE_Y));
                ((PropertyInputElement) this.scaleX.getValueInput()).readValue();
                ((PropertyInputElement) this.scaleY.getValueInput()).readValue();
            }
        });
        this.propertyScaleZ.set(this.entity.getProperty(StatueProperty.SCALE_Z));

        this.propertyLocked = new CheckboxProperty(state -> {
            if (this.entity.canInteract(this.mc.thePlayer, true)) {
                this.locked = state;
                this.entity.setLocked(state, true);
            }
        });
        this.propertyLocked.setBoolean(this.entity.isLocked());
    }

    @Override
    public void drawScreen(float mouseX, float mouseY, float partialTicks) {
        this.drawRectangle(0, 0, this.width, 14, LLibrary.CONFIG.getPrimaryColor());
        this.drawRectangle(0, this.height - 18, this.width, 18, LLibrary.CONFIG.getPrimaryColor());
        this.drawRectangle(0, 0, 122, this.height, LLibrary.CONFIG.getPrimaryColor());

        this.fontRendererObj.drawString("Statue", 3, 3, LLibrary.CONFIG.getTextColor());

        StatueModel selectedModel = this.getSelectedModel();
        if (selectedModel != null) {
            String displayName;
            String name = selectedModel.getName();
            String author = selectedModel.getAuthor();
            if (name == null || name.trim().length() == 0) {
                name = "Unnamed";
            }
            if (author == null) {
                displayName = name;
            } else {
                displayName = "\"" + name + "\" by " + author;
            }
            this.fontRendererObj.drawString(displayName, (this.width - 122) / 2 + 122 - this.fontRendererObj.getStringWidth(displayName) / 2, 3.0F, LLibrary.CONFIG.getTextColor(), false);
        }
    }

    @Override
    public boolean drawBackground() {
        return this.drawBackground;
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
        return this.entity.getStatueTexture();
    }

    @Override
    public ResourceLocation getSelectedRenderTexture() {
        return this.entity.getTexture();
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
