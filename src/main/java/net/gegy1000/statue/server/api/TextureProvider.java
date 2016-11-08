package net.gegy1000.statue.server.api;

import java.awt.image.BufferedImage;
import java.util.Map;

public interface TextureProvider<T extends StatueTexture, F extends ImportFile> {
    Map<String, F> getTextures();

    T getTexture(F file);

    T create(BufferedImage image, String name);

    String getName();

    default T getTextureBase(ImportFile file) {
        return this.getTexture((F) file);
    }
}
