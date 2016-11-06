package net.gegy1000.statue.server.message;

import io.netty.buffer.ByteBuf;
import net.gegy1000.statue.server.api.ProviderHandler;
import net.gegy1000.statue.server.api.TextureProvider;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CreateTextureMessage extends AbstractMessage<CreateTextureMessage> {
    private BlockPos pos;
    private TextureProvider provider;
    private String name;
    private int width;
    private int height;
    private int count;
    private int id;

    public CreateTextureMessage() {
    }

    public CreateTextureMessage(BlockPos pos, int id, String name, int width, int height, TextureProvider provider, int count) {
        this.pos = pos;
        this.id = id;
        this.provider = provider;
        this.name = name;
        this.width = width;
        this.height = height;
        this.count = count;
    }

    @Override
    public void onClientReceived(Minecraft client, CreateTextureMessage message, EntityPlayer player, MessageContext context) {
        this.handle(player.worldObj, player, message, context);
    }

    @Override
    public void onServerReceived(MinecraftServer server, CreateTextureMessage message, EntityPlayer player, MessageContext context) {
        this.handle(player.worldObj, player, message, context);
    }

    protected void handle(World world, EntityPlayer player, CreateTextureMessage message, MessageContext context) {
        if (message.provider != null) {
            BlockPos pos = message.pos;
            TileEntity entity = world.getTileEntity(pos);
            if (entity instanceof StatueBlockEntity) {
                StatueBlockEntity statue = (StatueBlockEntity) entity;
                if (context.side.isServer() && !statue.canInteract(player)) {
                    return;
                }
                statue.startBuilding(message.id, message.name, message.provider, message.width, message.height, message.count);
                if (context.side.isServer()) {
                    statue.send(message);
                }
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.id = buf.readInt();
        this.name = ByteBufUtils.readUTF8String(buf);
        this.provider = ProviderHandler.getTexture(buf.readByte());
        this.width = buf.readShort();
        this.height = buf.readShort();
        this.count = buf.readShort();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        buf.writeInt(this.id);
        ByteBufUtils.writeUTF8String(buf, this.name);
        buf.writeByte(ProviderHandler.get(this.provider));
        buf.writeShort(this.width);
        buf.writeShort(this.height);
        buf.writeShort(this.count);
    }
}
