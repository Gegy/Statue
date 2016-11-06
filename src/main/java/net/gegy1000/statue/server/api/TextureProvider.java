package net.gegy1000.statue.server.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

public interface TextureProvider<T extends StatueTexture> {
    Map<String, File> getTextures();
    T getTexture(File file);

    T create(BufferedImage image, String name);

    String getName();
}
