package net.gegy1000.statue.server.provider.game;

import net.gegy1000.statue.client.model.game.GameModelLoader;
import net.gegy1000.statue.server.api.TextureProvider;
import net.gegy1000.statue.server.provider.DefaultStatueTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GameTextureProvider implements TextureProvider<DefaultStatueTexture, GameModelReference> {
    @Override
    public Map<String, GameModelReference> getTextures() {
        Map<String, GameModelReference> textures = new HashMap<>();
        Map<String, ResourceLocation> gameTextures = GameModelLoader.INSTANCE.getTextures();
        for (Map.Entry<String, ResourceLocation> entry : gameTextures.entrySet()) {
            String name = entry.getKey();
            ResourceLocation texture = entry.getValue();
            InputStream in = GameModelLoader.class.getResourceAsStream("/assets/" + texture.getResourceDomain() + "/" + texture.getResourcePath());
            if (in != null) {
                textures.put(name, new GameModelReference(name, null, texture));
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return textures;
    }

    @Override
    public DefaultStatueTexture getTexture(GameModelReference model) {
        try {
            ResourceLocation texture = model.getTexture();
            BufferedImage image = ImageIO.read(GameModelLoader.class.getResourceAsStream("/assets/" + texture.getResourceDomain() + "/" + texture.getResourcePath()));
            return new DefaultStatueTexture(image, model.getName());
        } catch (Exception e) {
            System.err.println("Failed to load Game texture: \"" + model.getName() + "\"");
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
        return "Game";
    }
}
