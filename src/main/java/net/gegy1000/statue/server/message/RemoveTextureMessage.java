package net.gegy1000.statue.server.message;

import io.netty.buffer.ByteBuf;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RemoveTextureMessage extends AbstractMessage<RemoveTextureMessage> {
    private BlockPos pos;

    public RemoveTextureMessage() {
    }

    public RemoveTextureMessage(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void onClientReceived(Minecraft client, RemoveTextureMessage message, EntityPlayer player, MessageContext context) {
        this.handle(player.worldObj, player, message, context);
    }

    @Override
    public void onServerReceived(MinecraftServer server, RemoveTextureMessage message, EntityPlayer player, MessageContext context) {
        this.handle(player.worldObj, player, message, context);
    }

    protected void handle(World world, EntityPlayer player, RemoveTextureMessage message, MessageContext context) {
        BlockPos pos = message.pos;
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof StatueBlockEntity) {
            StatueBlockEntity statue = (StatueBlockEntity) entity;
            if (context.side.isServer() && !statue.canInteract(player)) {
                return;
            }
            statue.loadTexture(null, null);
            if (context.side.isServer()) {
                statue.send(message);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
    }
}
