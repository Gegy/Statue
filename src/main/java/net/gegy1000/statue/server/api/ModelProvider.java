package net.gegy1000.statue.server.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.Tuple;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

public interface ModelProvider<M extends StatueModel> {
    Map<String, File> getModels();
    M getModel(File file, String name);
    Tuple<BufferedImage, TextureProvider<?>> getTexture(File file, String name);

    void serialize(M model, ByteBuf buf);

    M deserialize(ByteBuf buf);

    String getName();
}
