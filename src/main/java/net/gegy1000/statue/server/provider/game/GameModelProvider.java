package net.gegy1000.statue.server.provider.game;

import io.netty.buffer.ByteBuf;
import net.gegy1000.statue.client.model.game.GameModelLoader;
import net.gegy1000.statue.server.api.ModelProvider;
import net.gegy1000.statue.server.api.ProviderHandler;
import net.gegy1000.statue.server.api.TextureProvider;
import net.gegy1000.statue.server.provider.qubble.StatueQubbleModel;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameModelProvider implements ModelProvider<StatueQubbleModel, GameModelReference> {
    @Override
    public Map<String, GameModelReference> getModels() {
        Map<String, GameModelReference> models = new HashMap<>();
        Map<String, QubbleModel> gameModels = GameModelLoader.INSTANCE.getModels();
        Map<String, ResourceLocation> textures = GameModelLoader.INSTANCE.getTextures();
        for (Map.Entry<String, QubbleModel> entry : gameModels.entrySet()) {
            String name = entry.getKey();
            models.put(name, new GameModelReference(name, entry.getValue(), textures.get(name)));
        }
        return models;
    }

    @Override
    public StatueQubbleModel getModel(GameModelReference reference, String name) {
        return new StatueQubbleModel(reference.getModel());
    }

    @Override
    public Tuple<BufferedImage, TextureProvider<?, ?>> getTexture(GameModelReference model, String name) {
        try {
            ResourceLocation texture = model.getTexture();
            BufferedImage image = ImageIO.read(GameModelLoader.class.getResourceAsStream("/assets/" + texture.getResourceDomain() + "/" + texture.getResourcePath()));
            return new Tuple<>(image, ProviderHandler.GAME_TEXTURE_PROVIDER);
        } catch (Exception e) {
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
        return "Game";
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
}
