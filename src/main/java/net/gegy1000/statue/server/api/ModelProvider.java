package net.gegy1000.statue.server.api;

import io.netty.buffer.ByteBuf;

import java.io.File;
import java.util.Map;

public interface ModelProvider<T extends StatueModel> {
    Map<String, File> getModels();
    T getModel(File file);

    void serialize(T model, ByteBuf buf);

    T deserialize(ByteBuf buf);

    String getName();
}
