package net.gegy1000.statue.server.block.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

public class TextureColourPalette {
    private int[] palette;
    private Map<Integer, Integer> colours = new HashMap<>();

    public TextureColourPalette(Map<Integer, Integer> colours, int[] palette) {
        this.palette = palette;
        this.colours = colours;
    }

    public static TextureColourPalette create(int[] data) {
        Map<Integer, Integer> colours = new HashMap<>();
        int[] palette = new int[256];
        int index = 0;
        for (int colour : data) {
            Integer colourIndex = colours.get(colour);
            if (colourIndex == null) {
                colours.put(colour, index);
                palette[index] = colour;
                index++;
                if (index >= palette.length) {
                    return null;
                }
            }
        }
        return new TextureColourPalette(colours, palette);
    }

    public int getColour(int index) {
        return this.palette[index];
    }

    public int getIndex(int colour) {
        return this.colours.get(colour);
    }

    public int size() {
        return this.colours.size();
    }

    public void serialize(ByteBuf buf) {
        buf.writeByte(this.size() & 0xFF);
        for (int i = 0; i < this.size(); i++) {
            buf.writeInt(this.palette[i]);
        }
    }

    public void serialize(NBTTagCompound compound) {
        compound.setIntArray("TexturePalette", this.palette);
        compound.setByte("TexturePaletteSize", (byte) (this.size() & 0xFF));
    }

    public static TextureColourPalette deserialize(ByteBuf buf) {
        int size = buf.readByte() & 0xFF;
        if (size > 0) {
            Map<Integer, Integer> colours = new HashMap<>();
            int[] palette = new int[256];
            for (int i = 0; i < size; i++) {
                palette[i] = buf.readInt();
                colours.put(palette[i], i);
            }
            return new TextureColourPalette(colours, palette);
        }
        return null;
    }

    public static TextureColourPalette deserialize(NBTTagCompound compound) {
        int[] palette = compound.getIntArray("TexturePalette");
        int size = compound.getByte("TexturePaletteSize") & 0xFF;
        Map<Integer, Integer> colours = new HashMap<>();
        for (int i = 0; i < size; i++) {
            colours.put(palette[i], i);
        }
        return new TextureColourPalette(colours, palette);
    }
}
