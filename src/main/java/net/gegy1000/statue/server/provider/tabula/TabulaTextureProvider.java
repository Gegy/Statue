package net.gegy1000.statue.server.provider.tabula;

import net.gegy1000.statue.server.api.TextureProvider;
import net.gegy1000.statue.server.provider.DefaultStatueTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabulaTextureProvider implements TextureProvider<DefaultStatueTexture> {
    public static final File TABULA_DIRECTORY = new File(".", "mods" + File.separator + "tabula");
    public static final File TEXTURE_DIRECTORY = new File(TABULA_DIRECTORY, "textures");

    @Override
    public Map<String, File> getTextures() {
        Map<String, File> textures = new HashMap<>();
        List<File> textureFiles = this.getTextureFiles();
        for (File textureFile : textureFiles) {
            String name = textureFile.getName();
            if (name.contains(".")) {
                name = name.split("\\.")[0];
            }
            textures.put(name, textureFile);
        }
        return textures;
    }

    @Override
    public DefaultStatueTexture getTexture(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            return new DefaultStatueTexture(image, file.getName());
        } catch (Exception e) {
            System.err.println("Failed to load Tabula texture: \"" + file.getName() + "\"");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DefaultStatueTexture create(BufferedImage image, String name) {
        return new DefaultStatueTexture(image, name);
    }

    @Override
    public String getName() {
        return "Tabula";
    }

    protected List<File> getTextureFiles() {
        List<File> list = new ArrayList<>();
        for (File modelFile : TEXTURE_DIRECTORY.listFiles()) {
            if (modelFile.isFile() && modelFile.getName().endsWith(".png")) {
                list.add(modelFile);
            }
        }
        return list;
    }
}
