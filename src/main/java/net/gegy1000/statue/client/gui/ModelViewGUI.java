package net.gegy1000.statue.client.gui;

import net.gegy1000.statue.server.api.StatueModel;
import net.gegy1000.statue.server.api.StatueTexture;
import net.gegy1000.statue.server.block.entity.StatueProperty;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;

public interface ModelViewGUI {
    StatueModel getSelectedModel();
    ModelBase getSelectedRenderModel();

    StatueTexture getSelectedTexture();
    ResourceLocation getSelectedRenderTexture();

    float getProperty(StatueProperty property);
    float getInterpolatedProperty(StatueProperty property, float partialTicks);
}
