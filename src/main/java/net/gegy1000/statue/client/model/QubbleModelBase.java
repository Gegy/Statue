package net.gegy1000.statue.client.model;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class QubbleModelBase extends AdvancedModelBase implements OutlineRenderer {
    private List<OutlinedModelRenderer> rootCubes = new ArrayList<>();
    private Map<QubbleCuboid, OutlinedModelRenderer> cubes = new HashMap<>();

    public QubbleModelBase(QubbleModel model) {
        this.textureWidth = model.getTextureWidth();
        this.textureHeight = model.getTextureHeight();
        for (QubbleCuboid cube : model.getCuboids()) {
            this.parseCube(cube, null);
        }
    }

    private void parseCube(QubbleCuboid cube, OutlinedModelRenderer parent) {
        OutlinedModelRenderer box = this.createCube(cube);
        if (parent != null) {
            parent.addChild(box);
        } else {
            this.rootCubes.add(box);
        }
        for (QubbleCuboid child : cube.getChildren()) {
            this.parseCube(child, box);
        }
    }

    private OutlinedModelRenderer createCube(QubbleCuboid cube) {
        OutlinedModelRenderer box = new OutlinedModelRenderer(this, cube.getName(), cube.getTextureX(), cube.getTextureY());
        box.setRotationPoint(cube.getPositionX(), cube.getPositionY(), cube.getPositionZ());
        box.addBox(cube.getOffsetX(), cube.getOffsetY(), cube.getOffsetZ(), cube.getDimensionX(), cube.getDimensionY(), cube.getDimensionZ(), 0.0F);
        box.rotateAngleX = (float) Math.toRadians(cube.getRotationX());
        box.rotateAngleY = (float) Math.toRadians(cube.getRotationY());
        box.rotateAngleZ = (float) Math.toRadians(cube.getRotationZ());
        box.mirror = cube.isTextureMirrored();
        box.scaleX = cube.getScaleX();
        box.scaleY = cube.getScaleY();
        box.scaleZ = cube.getScaleZ();
        this.cubes.put(cube, box);
        return box;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
        GlStateManager.pushMatrix();
        for (OutlinedModelRenderer cube : this.rootCubes) {
            cube.render(scale);
        }
        GlStateManager.popMatrix();
    }

    public OutlinedModelRenderer getCube(QubbleCuboid cube) {
        return this.cubes.get(cube);
    }

    public Set<Map.Entry<QubbleCuboid, OutlinedModelRenderer>> getCubes() {
        return this.cubes.entrySet();
    }

    @Override
    public void renderOutlines(float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, null);
        GlStateManager.pushMatrix();
        for (OutlinedModelRenderer cube : this.rootCubes) {
            cube.renderOutline(scale);
        }
        GlStateManager.popMatrix();
    }
}
