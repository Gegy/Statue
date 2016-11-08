package net.gegy1000.statue.server.provider.tabula;

import net.gegy1000.statue.server.api.ImportableFile;
import net.gegy1000.statue.server.api.TextureProvider;
import net.gegy1000.statue.server.provider.DefaultStatueTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabulaTextureProvider implements TextureProvider<DefaultStatueTexture, ImportableFile> {
    public static final File TABULA_DIRECTORY = new File(".", "mods" + File.separator + "tabula");
    public static final File TEXTURE_DIRECTORY = new File(TABULA_DIRECTORY, "textures");

    @Override
    public Map<String, ImportableFile> getTextures() {
        Map<String, ImportableFile> textures = new HashMap<>();
        List<File> textureFiles = this.getTextureFiles();
        for (File textureFile : textureFiles) {
            String name = textureFile.getName();
            if (name.contains(".")) {
                name = name.split("\\.")[0];
            }
            textures.put(name, new ImportableFile(textureFile));
        }
        return textures;
    }

    @Override
    public DefaultStatueTexture getTexture(ImportableFile file) {
        try {
            BufferedImage image = ImageIO.read(file.get());
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
        List<File> textureFiles = new ArrayList<>();
        File[] files = TEXTURE_DIRECTORY.listFiles();
        if (files != null) {
            for (File modelFile : files) {
                if (modelFile.isFile() && modelFile.getName().endsWith(".png")) {
                    textureFiles.add(modelFile);
                }
            }
        }
        return textureFiles;
    }
}
