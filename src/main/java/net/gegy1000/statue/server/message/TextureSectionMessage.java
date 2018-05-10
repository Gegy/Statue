package net.gegy1000.statue.server.message;

import io.netty.buffer.ByteBuf;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.gegy1000.statue.server.block.entity.TextureColourPalette;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TextureSectionMessage extends AbstractMessage<TextureSectionMessage> {
    private BlockPos pos;
    private int[] texture;
    private int x;
    private int y;
    private int width;
    private int id;
    private TextureColourPalette palette;

    public TextureSectionMessage() {
    }

    public TextureSectionMessage(BlockPos pos, int id, int x, int y, int width, int[] texture) {
        this.pos = pos;
        this.id = id;
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.palette = TextureColourPalette.create(texture);
    }

    @Override
    public void onClientReceived(Minecraft client, TextureSectionMessage message, EntityPlayer player, MessageContext context) {
        this.handle(player.world, player, message, context);
    }

    @Override
    public void onServerReceived(MinecraftServer server, TextureSectionMessage message, EntityPlayer player, MessageContext context) {
        this.handle(player.world, player, message, context);
    }

    protected void handle(World world, EntityPlayer player, TextureSectionMessage message, MessageContext context) {
        BlockPos pos = message.pos;
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof StatueBlockEntity) {
            StatueBlockEntity statue = (StatueBlockEntity) entity;
            statue.buildSection(message.x, message.y, message.width, message.texture, message.id);
            if (context.side.isServer()) {
                if (!statue.canInteract(player)) {
                    return;
                }
                statue.send(message);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.id = buf.readInt();
        this.x = buf.readShort();
        this.y = buf.readShort();
        this.width = buf.readShort();
        this.palette = TextureColourPalette.deserialize(buf);
        int size = buf.readShort();
        if (size < 0) {
            this.texture = new int[-size];
        } else {
            this.texture = new int[size];
            if (this.palette == null) {
                for (int i = 0; i < size; i++) {
                    this.texture[i] = buf.readInt();
                }
            } else {
                for (int i = 0; i < size; i++) {
                    this.texture[i] = this.palette.getColour(buf.readByte() & 0xFF);
                }
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        buf.writeInt(this.id);
        buf.writeShort(this.x);
        buf.writeShort(this.y);
        buf.writeShort(this.width);
        if (this.palette == null) {
            buf.writeByte(0);
        } else {
            this.palette.serialize(buf);
        }
        boolean empty = true;
        for (int pixel : this.texture) {
            if (pixel != 0) {
                empty = false;
                break;
            }
        }
        if (!empty) {
            buf.writeShort(this.texture.length);
            if (this.palette == null) {
                for (int pixel : this.texture) {
                    buf.writeInt(pixel);
                }
            } else {
                for (int pixel : this.texture) {
                    buf.writeByte(this.palette.getIndex(pixel) & 0xFF);
                }
            }
        } else {
            buf.writeShort(-this.texture.length);
        }
    }
}
