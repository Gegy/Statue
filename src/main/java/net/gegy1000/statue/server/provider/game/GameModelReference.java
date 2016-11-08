package net.gegy1000.statue.server.provider.game;

import net.gegy1000.statue.server.api.ImportFile;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.minecraft.util.ResourceLocation;

public class GameModelReference implements ImportFile {
    private final String name;
    private final QubbleModel model;
    private final ResourceLocation texture;

    public GameModelReference(String name, QubbleModel model, ResourceLocation texture) {
        this.name = name;
        this.model = model;
        this.texture = texture;
    }

    public QubbleModel getModel() {
        return this.model;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
