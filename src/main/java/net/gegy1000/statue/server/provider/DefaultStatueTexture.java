package net.gegy1000.statue.server.provider;

import net.gegy1000.statue.Statue;
import net.gegy1000.statue.client.ClientProxy;
import net.gegy1000.statue.server.api.StatueTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.awt.image.BufferedImage;

public class DefaultStatueTexture implements StatueTexture {
    private String name;
    private BufferedImage image;
    private ResourceLocation texture;

    public DefaultStatueTexture(BufferedImage image, String name) {
        this.image = image;
        this.name = name;
    }

    @Override
    public ResourceLocation get(World world) {
        if (this.texture == null) {
            if (world.isRemote) {
                this.createTexture();
            }
        }
        return this.texture;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getWidth() {
        if (this.image != null) {
            return this.image.getWidth();
        }
        return 0;
    }

    @Override
    public int getHeight() {
        if (this.image != null) {
            return this.image.getHeight();
        }
        return 0;
    }

    @Override
    public void delete(World world) {
        if (world.isRemote) {
            this.deleteTexture();
        }
    }

    @Override
    public int get(int x, int y) {
        return this.image.getRGB(x, y);
    }

    private void createTexture() {
        this.texture = ClientProxy.MINECRAFT.getTextureManager().getDynamicTextureLocation(Statue.MODID + "." + this.name, new DynamicTexture(this.image));
    }

    private void deleteTexture() {
        if (this.texture != null) {
            ClientProxy.MINECRAFT.getTextureManager().deleteTexture(this.texture);
            this.texture = null;
        }
    }
}
