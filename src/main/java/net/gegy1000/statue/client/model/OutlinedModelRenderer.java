package net.gegy1000.statue.client.model;

import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class OutlinedModelRenderer extends AdvancedModelRenderer {
    public int sizeX, sizeY, sizeZ;
    private int displayList;
    private boolean compiled;

    public OutlinedModelRenderer(AdvancedModelBase model, String name, int textureX, int textureY) {
        super(model, name);
        this.setTextureOffset(textureX, textureY);
    }

    @Override
    public void addBox(float offsetX, float offsetY, float offsetZ, int width, int height, int depth, float scaleFactor) {
        super.addBox(offsetX, offsetY, offsetZ, width, height, depth, scaleFactor);
        this.sizeX = width;
        this.sizeY = height;
        this.sizeZ = depth;
    }

    @Override
    public void postRender(float scale) {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!this.compiled) {
                    this.compileDisplayList(scale);
                }
                GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                if (this.rotateAngleZ != 0.0F) {
                    GlStateManager.rotate((float) Math.toDegrees(this.rotateAngleZ), 0.0F, 0.0F, 1.0F);
                }
                if (this.rotateAngleY != 0.0F) {
                    GlStateManager.rotate((float) Math.toDegrees(this.rotateAngleY), 0.0F, 1.0F, 0.0F);
                }
                if (this.rotateAngleX != 0.0F) {
                    GlStateManager.rotate((float) Math.toDegrees(this.rotateAngleX), 1.0F, 0.0F, 0.0F);
                }
                if (this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F) {
                    GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
                }
            }
        }
    }

    @Override
    public void render(float scale) {
        if (!this.isHidden) {
            if (this.showModel) {
                GlStateManager.pushMatrix();
                if (!this.compiled) {
                    this.compileDisplayList(scale);
                }
                GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);
                GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                if (this.rotateAngleZ != 0.0F) {
                    GlStateManager.rotate((float) Math.toDegrees(this.rotateAngleZ), 0.0F, 0.0F, 1.0F);
                }
                if (this.rotateAngleY != 0.0F) {
                    GlStateManager.rotate((float) Math.toDegrees(this.rotateAngleY), 0.0F, 1.0F, 0.0F);
                }
                if (this.rotateAngleX != 0.0F) {
                    GlStateManager.rotate((float) Math.toDegrees(this.rotateAngleX), 1.0F, 0.0F, 0.0F);
                }
                if (this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F) {
                    GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
                }
                GlStateManager.callList(this.displayList);
                if (this.childModels != null) {
                    for (ModelRenderer childModel : this.childModels) {
                        childModel.render(scale);
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }

    public void renderOutline(float scale) {
        if (!this.isHidden) {
            if (this.showModel) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);
                GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                if (this.rotateAngleZ != 0.0F) {
                    GlStateManager.rotate((float) Math.toDegrees(this.rotateAngleZ), 0.0F, 0.0F, 1.0F);
                }
                if (this.rotateAngleY != 0.0F) {
                    GlStateManager.rotate((float) Math.toDegrees(this.rotateAngleY), 0.0F, 1.0F, 0.0F);
                }
                if (this.rotateAngleX != 0.0F) {
                    GlStateManager.rotate((float) Math.toDegrees(this.rotateAngleX), 1.0F, 0.0F, 0.0F);
                }
                if (this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F) {
                    GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
                }

                ModelBox box = this.cubeList.get(0);
                Tessellator tessellator = Tessellator.getInstance();
                GlStateManager.glLineWidth(16.0F);
                VertexBuffer buffer = tessellator.getBuffer();
                buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
                buffer.pos(box.posX1 * scale, box.posY1 * scale, box.posZ1 * scale).endVertex();
                buffer.pos(box.posX2 * scale, box.posY1 * scale, box.posZ1 * scale).endVertex();
                buffer.pos(box.posX2 * scale, box.posY1 * scale, box.posZ2 * scale).endVertex();
                buffer.pos(box.posX1 * scale, box.posY1 * scale, box.posZ2 * scale).endVertex();
                buffer.pos(box.posX1 * scale, box.posY1 * scale, box.posZ1 * scale).endVertex();
                tessellator.draw();
                buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
                buffer.pos(box.posX1 * scale, box.posY2 * scale, box.posZ1 * scale).endVertex();
                buffer.pos(box.posX2 * scale, box.posY2 * scale, box.posZ1 * scale).endVertex();
                buffer.pos(box.posX2 * scale, box.posY2 * scale, box.posZ2 * scale).endVertex();
                buffer.pos(box.posX1 * scale, box.posY2 * scale, box.posZ2 * scale).endVertex();
                buffer.pos(box.posX1 * scale, box.posY2 * scale, box.posZ1 * scale).endVertex();
                tessellator.draw();
                buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
                buffer.pos(box.posX1 * scale, box.posY1 * scale, box.posZ1 * scale).endVertex();
                buffer.pos(box.posX1 * scale, box.posY2 * scale, box.posZ1 * scale).endVertex();
                tessellator.draw();
                buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
                buffer.pos(box.posX2 * scale, box.posY1 * scale, box.posZ1 * scale).endVertex();
                buffer.pos(box.posX2 * scale, box.posY2 * scale, box.posZ1 * scale).endVertex();
                tessellator.draw();
                buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
                buffer.pos(box.posX1 * scale, box.posY1 * scale, box.posZ2 * scale).endVertex();
                buffer.pos(box.posX1 * scale, box.posY2 * scale, box.posZ2 * scale).endVertex();
                buffer.pos(box.posX2 * scale, box.posY1 * scale, box.posZ2 * scale).endVertex();
                buffer.pos(box.posX2 * scale, box.posY2 * scale, box.posZ2 * scale).endVertex();
                tessellator.draw();

                if (this.childModels != null) {
                    for (ModelRenderer childModel : this.childModels) {
                        if (childModel instanceof OutlinedModelRenderer) {
                            ((OutlinedModelRenderer) childModel).renderOutline(scale);
                        }
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }

    public void compileDisplayList(float scale) {
        if (this.compiled) {
            GLAllocation.deleteDisplayLists(this.displayList);
            this.compiled = false;
        }
        this.displayList = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(this.displayList, GL11.GL_COMPILE);
        VertexBuffer buffer = Tessellator.getInstance().getBuffer();
        for (ModelBox box : this.cubeList) {
            box.render(buffer, scale);
        }
        GlStateManager.glEndList();
        this.compiled = true;
    }
}
