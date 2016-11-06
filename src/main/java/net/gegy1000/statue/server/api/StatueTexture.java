package net.gegy1000.statue.server.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface StatueTexture {
    ResourceLocation get(World world);

    String getName();

    int getWidth();
    int getHeight();

    void delete(World world);

    int get(int x, int y);
}
