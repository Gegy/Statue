package net.gegy1000.statue.server.provider.tabula;

import io.netty.buffer.ByteBuf;
import net.gegy1000.statue.server.api.ImportableFile;
import net.gegy1000.statue.server.api.ModelProvider;
import net.gegy1000.statue.server.api.ProviderHandler;
import net.gegy1000.statue.server.api.TextureProvider;
import net.ilexiconn.llibrary.client.model.tabula.TabulaModelHandler;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeGroupContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TabulaModelProvider implements ModelProvider<StatueTabulaModel, ImportableFile> {
    public static final File TABULA_DIRECTORY = new File(".", "mods" + File.separator + "tabula");
    public static final File MODEL_DIRECTORY = new File(TABULA_DIRECTORY, "saves");

    @Override
    public Map<String, ImportableFile> getModels() {
        Map<String, ImportableFile> models = new HashMap<>();
        List<File> modelFiles = this.getModelFiles();
        for (File modelFile : modelFiles) {
            String name = modelFile.getName();
            if (name.contains(".")) {
                name = name.split("\\.")[0];
            }
            models.put(name, new ImportableFile(modelFile));
        }
        return models;
    }

    @Override
    public StatueTabulaModel getModel(ImportableFile file, String name) {
        try {
            ZipFile zip = new ZipFile(file.get());
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().equals("model.json")) {
                    TabulaModelContainer model = TabulaModelHandler.INSTANCE.loadTabulaModel(zip.getInputStream(entry));
                    zip.close();
                    return new StatueTabulaModel(model);
                }
            }
            zip.close();
        } catch (Exception e) {
            System.err.println("Failed to load Tabula model: \"" + file.getName() + "\"");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Tuple<BufferedImage, TextureProvider<?, ?>> getTexture(ImportableFile file, String name) {
        try {
            ZipFile zip = new ZipFile(file.get());
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().equals("texture.png")) {
                    BufferedImage image = ImageIO.read(zip.getInputStream(entry));
                    zip.close();
                    return new Tuple<>(image, ProviderHandler.TABULA_TEXTURE_PROVIDER);
                }
            }
            zip.close();
        } catch (Exception e) {
            System.err.println("Failed to load Tabula model texture: \"" + file.getName() + "\"");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void serialize(StatueTabulaModel statueModel, ByteBuf buf) {
        TabulaModelContainer model = statueModel.get();

        ByteBufUtils.writeUTF8String(buf, model.getName());
        ByteBufUtils.writeUTF8String(buf, model.getAuthor());
        buf.writeShort(model.getTextureWidth());
        buf.writeShort(model.getTextureHeight());
        buf.writeByte(model.getProjectVersion() & 0xFF);

        List<TabulaCubeGroupContainer> groups = model.getCubeGroups();
        buf.writeShort(groups.size());

        for (TabulaCubeGroupContainer group : groups) {
            this.serializeCubeGroup(buf, group);
        }

        List<TabulaCubeContainer> cubes = model.getCubes();
        buf.writeShort(cubes.size());

        for (TabulaCubeContainer cube : cubes) {
            this.serializeCube(buf, cube);
        }
    }

    @Override
    public StatueTabulaModel deserialize(ByteBuf buf) {
        String name = ByteBufUtils.readUTF8String(buf);
        String author = ByteBufUtils.readUTF8String(buf);
        int textureWidth = buf.readShort();
        int textureHeight = buf.readShort();
        int version = buf.readByte() & 0xFF;

        TabulaModelContainer model = new TabulaModelContainer(name, author, textureWidth, textureHeight, new ArrayList<>(), version);

        int groupCount = buf.readShort();
        for (int i = 0; i < groupCount; i++) {
            model.getCubeGroups().add(this.deserializeCubeGroup(buf));
        }

        int cubeCount = buf.readShort();
        for (int i = 0; i < cubeCount; i++) {
            model.getCubes().add(this.deserializeCube(buf, null));
        }

        return new StatueTabulaModel(model);
    }

    protected void serializeCubeGroup(ByteBuf buf, TabulaCubeGroupContainer group) {
        List<TabulaCubeContainer> cubes = group.getCubes();
        buf.writeShort(cubes.size());
        for (TabulaCubeContainer cube : cubes) {
            this.serializeCube(buf, cube);
        }
        List<TabulaCubeGroupContainer> groups = group.getCubeGroups();
        buf.writeByte(groups.size() & 0xFF);
        for (TabulaCubeGroupContainer child : groups) {
            this.serializeCubeGroup(buf, child);
        }
    }

    protected TabulaCubeGroupContainer deserializeCubeGroup(ByteBuf buf) {
        TabulaCubeGroupContainer group = new TabulaCubeGroupContainer();
        int cubeCount = buf.readShort();
        for (int i = 0; i < cubeCount; i++) {
            group.getCubes().add(this.deserializeCube(buf, null));
        }
        int groupCount = buf.readByte() & 0xFF;
        for (int i = 0; i < groupCount; i++) {
            group.getCubeGroups().add(this.deserializeCubeGroup(buf));
        }
        return group;
    }

    protected void serializeCube(ByteBuf buf, TabulaCubeContainer cube) {
        ByteBufUtils.writeUTF8String(buf, cube.getName());
        ByteBufUtils.writeUTF8String(buf, cube.getIdentifier());

        buf.writeByte(cube.getDimensions()[0] & 0xFF);
        buf.writeByte(cube.getDimensions()[1] & 0xFF);
        buf.writeByte(cube.getDimensions()[2] & 0xFF);
        buf.writeShort(cube.getTextureOffset()[0]);
        buf.writeShort(cube.getTextureOffset()[1]);
        buf.writeFloat((float) cube.getPosition()[0]);
        buf.writeFloat((float) cube.getPosition()[1]);
        buf.writeFloat((float) cube.getPosition()[2]);
        buf.writeFloat((float) cube.getOffset()[0]);
        buf.writeFloat((float) cube.getOffset()[1]);
        buf.writeFloat((float) cube.getOffset()[2]);
        buf.writeFloat((float) cube.getRotation()[0]);
        buf.writeFloat((float) cube.getRotation()[1]);
        buf.writeFloat((float) cube.getRotation()[2]);
        buf.writeFloat((float) cube.getScale()[0]);
        buf.writeFloat((float) cube.getScale()[1]);
        buf.writeFloat((float) cube.getScale()[2]);
        buf.writeBoolean(cube.isTextureMirrorEnabled());

        buf.writeShort(cube.getChildren().size());
        for (TabulaCubeContainer child : cube.getChildren()) {
            this.serializeCube(buf, child);
        }
    }

    protected TabulaCubeContainer deserializeCube(ByteBuf buf, String parentIdentifier) {
        String name = ByteBufUtils.readUTF8String(buf);
        String identifier = ByteBufUtils.readUTF8String(buf);

        int[] dimensions = new int[] { buf.readByte() & 0xFF, buf.readByte() & 0xFF, buf.readByte() & 0xFF };
        int[] textureOffset = new int[] { buf.readShort(), buf.readShort() };
        double[] position = new double[] { buf.readFloat(), buf.readFloat(), buf.readFloat() };
        double[] offset = new double[] { buf.readFloat(), buf.readFloat(), buf.readFloat() };
        double[] rotation = new double[] { buf.readFloat(), buf.readFloat(), buf.readFloat() };
        double[] scale = new double[] { buf.readFloat(), buf.readFloat(), buf.readFloat() };
        boolean mirror = buf.readBoolean();

        TabulaCubeContainer cube = new TabulaCubeContainer(name, identifier, parentIdentifier, dimensions, position, offset, rotation, scale, textureOffset, mirror, 100.0, 1.0, false);

        int cubeCount = buf.readShort();
        for (int i = 0; i < cubeCount; i++) {
            cube.getChildren().add(this.deserializeCube(buf, identifier));
        }
        return cube;
    }

    @Override
    public String getName() {
        return "Tabula";
    }

    protected List<File> getModelFiles() {
        List<File> modelFiles = new ArrayList<>();
        File[] files = MODEL_DIRECTORY.listFiles();
        if (files != null) {
            for (File modelFile : files) {
                if (modelFile.isFile() && modelFile.getName().endsWith(".tbl")) {
                    modelFiles.add(modelFile);
                }
            }
        }
        return modelFiles;
    }
}
