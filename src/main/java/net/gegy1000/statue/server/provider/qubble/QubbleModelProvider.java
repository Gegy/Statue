package net.gegy1000.statue.server.provider.qubble;

import io.netty.buffer.ByteBuf;
import net.gegy1000.statue.server.api.ModelProvider;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QubbleModelProvider implements ModelProvider<StatueQubbleModel> {
    public static final File QUBBLE_DIRECTORY = new File(".", "llibrary" + File.separator + "qubble");
    public static final File MODEL_DIRECTORY = new File(QUBBLE_DIRECTORY, "models");

    @Override
    public Map<String, File> getModels() {
        Map<String, File> models = new HashMap<>();
        List<File> modelFiles = this.getModelFiles();
        for (File modelFile : modelFiles) {
            String name = modelFile.getName();
            if (name.contains(".")) {
                name = name.split("\\.")[0];
            }
            models.put(name, modelFile);
        }
        return models;
    }

    @Override
    public StatueQubbleModel getModel(File file, String name) {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            NBTTagCompound compound = CompressedStreamTools.readCompressed(in);
            in.close();
            QubbleModel model = QubbleModel.deserialize(compound);
            return new StatueQubbleModel(model);
        } catch (Exception e) {
            System.err.println("Failed to load Qubble model: \"" + file.getName() + "\"");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void serialize(StatueQubbleModel statueModel, ByteBuf buf) {
        QubbleModel model = statueModel.get();

        ByteBufUtils.writeUTF8String(buf, model.getName());
        ByteBufUtils.writeUTF8String(buf, model.getAuthor());
        buf.writeShort(model.getTextureWidth());
        buf.writeShort(model.getTextureHeight());

        List<QubbleCuboid> cuboids = model.getCuboids();
        buf.writeShort(cuboids.size());

        for (QubbleCuboid cuboid : cuboids) {
            this.serializeCuboid(buf, cuboid);
        }
    }

    @Override
    public StatueQubbleModel deserialize(ByteBuf buf) {
        String name = ByteBufUtils.readUTF8String(buf);
        String author = ByteBufUtils.readUTF8String(buf);
        int textureWidth = buf.readShort();
        int textureHeight = buf.readShort();
        int cuboidCount = buf.readShort();

        QubbleModel model = QubbleModel.create(name, author, textureWidth, textureHeight);
        List<QubbleCuboid> cuboids = model.getCuboids();

        for (int i = 0; i < cuboidCount; i++) {
            cuboids.add(this.deserializeCuboid(buf, null));
        }

        if (model.getCuboids().isEmpty()) {
            return null;
        }

        return new StatueQubbleModel(model);
    }

    @Override
    public String getName() {
        return "Qubble";
    }

    private void serializeCuboid(ByteBuf buf, QubbleCuboid cuboid) {
        ByteBufUtils.writeUTF8String(buf, cuboid.getName());

        buf.writeByte(cuboid.getDimensionX() & 0xFF);
        buf.writeByte(cuboid.getDimensionY() & 0xFF);
        buf.writeByte(cuboid.getDimensionZ() & 0xFF);
        buf.writeShort(cuboid.getTextureX());
        buf.writeShort(cuboid.getTextureY());
        buf.writeFloat(cuboid.getPositionX());
        buf.writeFloat(cuboid.getPositionY());
        buf.writeFloat(cuboid.getPositionZ());
        buf.writeFloat(cuboid.getOffsetX());
        buf.writeFloat(cuboid.getOffsetY());
        buf.writeFloat(cuboid.getOffsetZ());
        buf.writeFloat(cuboid.getRotationX());
        buf.writeFloat(cuboid.getRotationY());
        buf.writeFloat(cuboid.getRotationZ());
        buf.writeFloat(cuboid.getScaleX());
        buf.writeFloat(cuboid.getScaleY());
        buf.writeFloat(cuboid.getScaleZ());
        buf.writeBoolean(cuboid.isTextureMirrored());
        buf.writeShort(cuboid.getChildren().size());

        for (QubbleCuboid child : cuboid.getChildren()) {
            this.serializeCuboid(buf, child);
        }
    }

    private QubbleCuboid deserializeCuboid(ByteBuf buf, QubbleCuboid parent) {
        String cuboidName = ByteBufUtils.readUTF8String(buf);
        int sizeX = buf.readByte() & 0xFF;
        int sizeY = buf.readByte() & 0xFF;
        int sizeZ = buf.readByte() & 0xFF;
        int textureX = buf.readShort();
        int textureY = buf.readShort();
        float x = buf.readFloat();
        float y = buf.readFloat();
        float z = buf.readFloat();
        float offsetX = buf.readFloat();
        float offsetY = buf.readFloat();
        float offsetZ = buf.readFloat();
        float rotationX = buf.readFloat();
        float rotationY = buf.readFloat();
        float rotationZ = buf.readFloat();
        float scaleX = buf.readFloat();
        float scaleY = buf.readFloat();
        float scaleZ = buf.readFloat();
        boolean textureMirrored = buf.readBoolean();
        int childCount = buf.readShort();

        QubbleCuboid cuboid = QubbleCuboid.create(cuboidName);
        cuboid.setPosition(x, y, z);
        cuboid.setRotation(rotationX, rotationY, rotationZ);
        cuboid.setTexture(textureX, textureY);
        cuboid.setDimensions(sizeX, sizeY, sizeZ);
        cuboid.setOffset(offsetX, offsetY, offsetZ);
        cuboid.setScale(scaleX, scaleY, scaleZ);
        cuboid.setTextureMirrored(textureMirrored);

        if (parent != null) {
            parent.getChildren().add(cuboid);
        }

        for (int i = 0; i < childCount; i++) {
            this.deserializeCuboid(buf, cuboid);
        }

        return cuboid;
    }

    protected List<File> getModelFiles() {
        List<File> list = new ArrayList<>();
        for (File modelFile : MODEL_DIRECTORY.listFiles()) {
            if (modelFile.isFile() && modelFile.getName().endsWith(".qbl")) {
                list.add(modelFile);
            }
        }
        return list;
    }
}
