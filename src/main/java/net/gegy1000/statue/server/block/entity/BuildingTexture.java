package net.gegy1000.statue.server.block.entity;

import net.gegy1000.statue.server.api.TextureProvider;

import java.awt.image.BufferedImage;

public class BuildingTexture {
    public static int currentID;

    private int id;
    private String name;
    private TextureProvider<?> provider;
    private int width;
    private int height;
    private int[] texture;
    private boolean complete;
    private int total;
    private int count;

    public BuildingTexture(int id, String name, TextureProvider<?> provider, int width, int height, int count) {
        this.id = id;
        this.name = name;
        this.provider = provider;
        this.width = width;
        this.height = height;
        this.texture = new int[width * height];
        this.total = count;
    }

    public void load(int x, int y, int width, int[] data) {
        for (int i = 0; i < data.length; i++) {
            int pixel = data[i];
            int textureX = x + this.getX(i, width);
            int textureY = y + this.getY(i, width);
            this.set(textureX, textureY, pixel);
        }
        if (++this.count >= this.total) {
            this.complete = true;
        }
    }

    protected int getIndex(int x, int y) {
        return x + (y * this.width);
    }

    protected void set(int x, int y, int pixel) {
        this.texture[this.getIndex(x, y)] = pixel;
    }

    protected int getX(int index, int width) {
        return index % width;
    }

    protected int getY(int index, int width) {
        return index / width;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void complete(StatueBlockEntity entity) {
        BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, this.width, this.height, this.texture, 0, this.width);
        entity.loadTexture(this.provider, this.provider.create(image, this.name));
    }

    public int getID() {
        return this.id;
    }
}
