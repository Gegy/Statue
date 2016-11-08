package net.gegy1000.statue.server.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.Tuple;

import java.awt.image.BufferedImage;
import java.util.Map;

public interface ModelProvider<M extends StatueModel, F extends ImportFile> {
    Map<String, F> getModels();
    M getModel(F file, String name);
    Tuple<BufferedImage, TextureProvider<?, ?>> getTexture(F file, String name);

    default M getModelBase(ImportFile file, String name) {
        return this.getModel((F) file, name);
    }

    default Tuple<BufferedImage, TextureProvider<?, ?>> getTextureBase(ImportFile file, String name) {
        return this.getTexture((F) file, name);
    }

    void serialize(M model, ByteBuf buf);

    M deserialize(ByteBuf buf);

    String getName();
}
