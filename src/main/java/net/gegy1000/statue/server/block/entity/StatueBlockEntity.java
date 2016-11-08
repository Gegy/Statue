package net.gegy1000.statue.server.block.entity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import net.gegy1000.statue.Statue;
import net.gegy1000.statue.server.api.ImportFile;
import net.gegy1000.statue.server.api.ModelProvider;
import net.gegy1000.statue.server.api.ProviderHandler;
import net.gegy1000.statue.server.api.StatueModel;
import net.gegy1000.statue.server.api.StatueTexture;
import net.gegy1000.statue.server.api.TextureProvider;
import net.gegy1000.statue.server.message.CreateTextureMessage;
import net.gegy1000.statue.server.message.RemoveTextureMessage;
import net.gegy1000.statue.server.message.SetLockedMessage;
import net.gegy1000.statue.server.message.SetModelMessage;
import net.gegy1000.statue.server.message.SetPropertiesMessage;
import net.gegy1000.statue.server.message.TextureSectionMessage;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class StatueBlockEntity extends TileEntity implements ITickable {
    private static final int INTERP_TICKS = 10;

    private StatueModel statueModel;
    private ModelProvider provider;

    private ModelBase model;

    private BuildingTexture buildingTexture;

    private TextureProvider textureProvider;
    private StatueTexture statueTexture;
    private ResourceLocation texture;

    private ArrayDeque<IMessage> queuedMessages = new ArrayDeque<>();
    private ArrayDeque<Tuple<IMessage, EntityPlayerMP>> queuedPlayerMessages = new ArrayDeque<>();

    private Map<StatueProperty, Float> prevProperties = new HashMap<>();
    private Map<StatueProperty, Float> properties = new HashMap<>();
    private Map<StatueProperty, Float> interpAmounts = new HashMap<>();

    private UUID owner;

    private int interpTick;

    private boolean propertiesDirty;

    private int tick;

    private boolean locked = true;

    @Override
    public void update() {
        if (this.worldObj.isRemote) {
            this.prevProperties = new HashMap<>(this.properties);
        }
        this.tick++;
        for (int i = 0; i < 32; i++) {
            if (!this.queuedMessages.isEmpty()) {
                this.send(this.queuedMessages.poll());
            } else {
                break;
            }
        }
        if (!this.worldObj.isRemote) {
            for (int i = 0; i < 32; i++) {
                if (!this.queuedPlayerMessages.isEmpty()) {
                    Tuple<IMessage, EntityPlayerMP> message = this.queuedPlayerMessages.poll();
                    Statue.WRAPPER.sendTo(message.getFirst(), message.getSecond());
                } else {
                    break;
                }
            }
        }
        if (this.buildingTexture != null && this.buildingTexture.isComplete()) {
            this.buildingTexture.complete(this);
            this.buildingTexture = null;
        }
        if (this.tick % INTERP_TICKS == 0) {
            if (this.propertiesDirty && this.worldObj.isRemote) {
                this.propertiesDirty = false;
                this.send(new SetPropertiesMessage(this.pos, this.properties));
            }
        }
        if (this.worldObj.isRemote) {
            if (this.interpTick > 0) {
                this.interpTick--;
                boolean dirty = this.propertiesDirty;
                for (StatueProperty property : StatueProperty.values()) {
                    this.setProperty(property, this.getProperty(property) + this.interpAmounts.get(property));
                }
                this.propertiesDirty = dirty;
            }
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        this.delete();
    }

    public void delete() {
        if (this.worldObj.isRemote && this.statueTexture != null) {
            this.statueTexture.delete(this.worldObj);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = this.writeSyncData(super.writeToNBT(compound));
        if (this.textureProvider != null && this.statueTexture != null) {
            compound.setString("TextureName", this.statueTexture.getName());
            compound.setByte("TextureProvider", (byte) ProviderHandler.get(this.provider));
            int width = this.statueTexture.getWidth();
            int height = this.statueTexture.getHeight();
            int[] pixels = new int[width * height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    pixels[x + (y * width)] = this.statueTexture.get(x, y);
                }
            }
            TextureColourPalette palette = TextureColourPalette.create(pixels);
            if (palette == null) {
                compound.setIntArray("TextureData", pixels);
            } else {
                palette.serialize(compound);
                byte[] data = new byte[width * height];
                for (int i = 0; i < data.length; i++) {
                    data[i] = (byte) (palette.getIndex(pixels[i]) & 0xFF);
                }
                compound.setByteArray("TextureData", data);
            }
            compound.setShort("Width", (short) width);
            compound.setShort("Height", (short) height);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.readSyncData(compound);
        if (compound.hasKey("TextureProvider") && compound.hasKey("TextureData")) {
            this.textureProvider = ProviderHandler.getTexture(compound.getByte("TextureProvider"));
            if (this.textureProvider != null) {
                String name = compound.getString("TextureName");
                int width = compound.getShort("Width");
                int height = compound.getShort("Height");
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                if (compound.hasKey("TexturePalette")) {
                    TextureColourPalette palette = TextureColourPalette.deserialize(compound);
                    byte[] data = compound.getByteArray("TextureData");
                    for (int i = 0; i < data.length; i++) {
                        image.setRGB(i % width, i / width, palette.getColour(data[i]));
                    }
                } else {
                    image.setRGB(0, 0, width, height, compound.getIntArray("TextureData"), 0, width);
                }
                this.statueTexture = this.textureProvider.create(image, name);
            }
        }
    }

    private NBTTagCompound writeSyncData(NBTTagCompound compound) {
        if (this.provider != null && this.statueModel != null) {
            compound.setByte("ModelProvider", (byte) ProviderHandler.get(this.provider));
            ByteBuf modelData = new UnpooledByteBufAllocator(false).buffer();
            this.provider.serialize(this.statueModel, modelData);
            compound.setByteArray("ModelData", modelData.array());
        }
        NBTTagCompound propertiesTag = new NBTTagCompound();
        for (Map.Entry<StatueProperty, Float> entry : this.properties.entrySet()) {
            String name = entry.getKey().name().toLowerCase(Locale.ENGLISH);
            propertiesTag.setFloat(name, entry.getValue());
        }
        compound.setTag("StatueProperties", propertiesTag);
        if (this.owner != null) {
            compound.setUniqueId("Owner", this.owner);
        }
        compound.setBoolean("Locked", this.locked);
        return compound;
    }

    private void readSyncData(NBTTagCompound compound) {
        if (compound.hasKey("ModelProvider") && compound.hasKey("ModelData")) {
            ModelProvider<?, ? extends ImportFile> provider = ProviderHandler.get(compound.getByte("ModelProvider"));
            byte[] data = compound.getByteArray("ModelData");
            ByteBuf modelData = new UnpooledByteBufAllocator(false).buffer(data.length).writeBytes(data);
            StatueModel model = provider.deserialize(modelData);
            if (model != null) {
                this.load(provider, model);
            }
        }
        if (compound.hasKey("StatueProperties")) {
            NBTTagCompound propertiesTag = compound.getCompoundTag("StatueProperties");
            for (StatueProperty property : StatueProperty.values()) {
                String name = property.name().toLowerCase(Locale.ENGLISH);
                if (propertiesTag.hasKey(name)) {
                    this.setProperty(property, propertiesTag.getFloat(name));
                }
            }
        }
        this.owner = compound.getUniqueId("Owner");
        this.locked = compound.getBoolean("Locked");
    }

    public ModelBase getModel() {
        return this.model;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        if (!this.worldObj.isRemote) {
            this.markDirty();
        }
    }

    public void load(ModelProvider provider, StatueModel model) {
        this.provider = provider;
        this.statueModel = model;
        if (model != null && this.worldObj != null && this.worldObj.isRemote) {
            this.create(model);
        }
        this.properties.clear();
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.markDirty();
        }
    }

    private void create(StatueModel model) {
        this.model = model.create();
    }

    public void loadTexture(TextureProvider provider, StatueTexture texture) {
        if (this.statueTexture != null && this.statueTexture != texture) {
            this.statueTexture.delete(this.worldObj);
        }
        this.textureProvider = provider;
        this.statueTexture = texture;
        if (texture != null) {
            this.texture = texture.get(this.worldObj);
        }
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.markDirty();
        }
    }

    public void set(ModelProvider provider, StatueModel model) {
        this.load(provider, model);
        if (this.worldObj.isRemote) {
            this.queuedMessages.add(new SetModelMessage(this.pos, this.statueModel, this.provider));
        }
    }

    public void setTexture(TextureProvider<?, ?> provider, StatueTexture texture) {
        if (this.worldObj.isRemote) {
            if (texture == null || provider == null) {
                this.queuedMessages.add(new RemoveTextureMessage(this.pos));
            } else {
                this.queuedMessages.addAll(this.createTextureMessages(provider, texture));
            }
        }
        this.loadTexture(provider, texture);
    }

    private List<IMessage> createTextureMessages(TextureProvider<?, ?> provider, StatueTexture texture) {
        List<IMessage> messages = new ArrayList<>();
        int width = texture.getWidth();
        int height = texture.getHeight();
        int sectionSize = 64;
        int sectionCountX = (int) Math.ceil((float) width / sectionSize);
        int sectionCountY = (int) Math.ceil((float) height / sectionSize);
        int id = (int) (BuildingTexture.currentID++ + (System.currentTimeMillis() % 0xFFFFFF));
        CreateTextureMessage creationMessage = new CreateTextureMessage(this.pos, id, texture.getName(), width, height, provider, sectionCountX * sectionCountY);
        messages.add(creationMessage);
        for (int sectionX = 0; sectionX < sectionCountX; sectionX++) {
            for (int sectionY = 0; sectionY < sectionCountY; sectionY++) {
                int minX = sectionX * sectionSize;
                int minY = sectionY * sectionSize;
                int maxX = Math.min(minX + sectionSize, width);
                int maxY = Math.min(minY + sectionSize, height);
                int sectionWidth = maxX - minX;
                int sectionHeight = maxY - minY;
                int[] data = new int[sectionWidth * sectionHeight];
                for (int x = 0; x < sectionWidth; x++) {
                    for (int y = 0; y < sectionHeight; y++) {
                        data[x + (y * sectionWidth)] = texture.get(minX + x, minY + y);
                    }
                }
                messages.add(new TextureSectionMessage(this.pos, id, minX, minY, sectionWidth, data));
            }
        }
        return messages;
    }

    public void startBuilding(int id, String name, TextureProvider<?, ?> provider, int width, int height, int count) {
        this.buildingTexture = new BuildingTexture(id, name, provider, width, height, count);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, SPacketUpdateTileEntity packet) {
        super.onDataPacket(networkManager, packet);
        this.readSyncData(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeSyncData(super.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void handleUpdateTag(NBTTagCompound compound) {
        this.readSyncData(compound);
    }

    public StatueModel getStatueModel() {
        return this.statueModel;
    }

    public StatueTexture getStatueTexture() {
        return this.statueTexture;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public void buildSection(int x, int y, int width, int[] data, int id) {
        if (this.buildingTexture != null && this.buildingTexture.getID() == id) {
            this.buildingTexture.load(x, y, width, data);
        }
    }

    public void send(IMessage message) {
        if (this.worldObj.isRemote) {
            Statue.WRAPPER.sendToServer(message);
        } else {
            NetworkRegistry.TargetPoint target = new NetworkRegistry.TargetPoint(this.worldObj.provider.getDimension(), this.pos.getX() + 0.5, this.pos.getY(), this.pos.getZ() + 0.5, 1024);
            Statue.WRAPPER.sendToAllAround(message, target);
        }
    }

    public void watchChunk(EntityPlayerMP player) {
        if (this.textureProvider != null && this.statueTexture != null) {
            List<IMessage> messages = this.createTextureMessages(this.textureProvider, this.statueTexture);
            for (IMessage message : messages) {
                this.queuedPlayerMessages.add(new Tuple<>(message, player));
            }
        }
    }

    public float getProperty(StatueProperty property) {
        if (this.properties.containsKey(property)) {
            return this.properties.get(property);
        }
        return property.getDefaultValue();
    }

    public float getPreviousProperty(StatueProperty property) {
        if (this.prevProperties.containsKey(property)) {
            return this.prevProperties.get(property);
        }
        return property.getDefaultValue();
    }

    public float getInterpolatedProperty(StatueProperty property, float partialTicks) {
        float previous = this.getPreviousProperty(property);
        float value = this.getProperty(property);
        return previous + (value - previous) * partialTicks;
    }

    public void setProperty(StatueProperty property, float value) {
        this.properties.put(property, value);
        this.propertiesDirty = true;
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.markDirty();
        }
    }

    public void setPropertiesNetwork(Map<StatueProperty, Float> properties) {
        if (this.worldObj.isRemote) {
            this.interpAmounts = new HashMap<>();
            for (Map.Entry<StatueProperty, Float> entry : properties.entrySet()) {
                StatueProperty property = entry.getKey();
                this.interpAmounts.put(property, (properties.get(property) - this.getProperty(property)) / INTERP_TICKS);
            }
            this.interpTick = INTERP_TICKS;
        } else {
            this.properties = properties;
            this.markDirty();
        }
    }

    public void setLocked(boolean locked, boolean send) {
        this.locked = locked;
        if (!this.worldObj.isRemote) {
            this.markDirty();
        } else {
            if (send) {
                this.send(new SetLockedMessage(this.pos, locked));
            }
        }
    }

    public boolean canInteract(EntityPlayer player) {
        return this.canInteract(player, false);
    }

    public boolean canInteract(EntityPlayer player, boolean ignoreLock) {
        return player.getDistanceSqToCenter(this.pos) <= 64 && (this.owner == null || (!ignoreLock && !this.locked) || player.getUniqueID().equals(this.owner));
    }

    public boolean isLocked() {
        return this.locked;
    }

    public Map<StatueProperty, Float> getProperties() {
        return this.properties;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 16384.0;
    }
}
