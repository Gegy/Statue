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

public class SetLockedMessage extends AbstractMessage<SetLockedMessage> {
    private BlockPos pos;
    private boolean locked;

    public SetLockedMessage() {
    }

    public SetLockedMessage(BlockPos pos, boolean locked) {
        this.pos = pos;
        this.locked = locked;
    }

    @Override
    public void onClientReceived(Minecraft client, SetLockedMessage message, EntityPlayer player, MessageContext context) {
        this.handle(player.world, player, message, context);
    }

    @Override
    public void onServerReceived(MinecraftServer server, SetLockedMessage message, EntityPlayer player, MessageContext context) {
        this.handle(player.world, player, message, context);
    }

    protected void handle(World world, EntityPlayer player, SetLockedMessage message, MessageContext context) {
        BlockPos pos = message.pos;
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof StatueBlockEntity) {
            StatueBlockEntity statue = (StatueBlockEntity) entity;
            if (context.side.isServer() && !statue.canInteract(player, true)) {
                if (context.side.isServer()) {
                    statue.send(new SetLockedMessage(message.pos, statue.isLocked()));
                }
                return;
            }
            statue.setLocked(message.locked, false);
            if (context.side.isServer()) {
                statue.send(message);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.locked = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        buf.writeBoolean(this.locked);
    }
}
