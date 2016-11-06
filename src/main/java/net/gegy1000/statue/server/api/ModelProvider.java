package net.gegy1000.statue.server.api;

import io.netty.buffer.ByteBuf;

import java.io.File;
import java.util.Map;

public interface ModelProvider<M extends StatueModel> {
    Map<String, File> getModels();
    M getModel(File file, String name);

    void serialize(M model, ByteBuf buf);

    M deserialize(ByteBuf buf);

    String getName();
}
