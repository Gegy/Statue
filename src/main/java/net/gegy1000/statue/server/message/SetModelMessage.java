package net.gegy1000.statue.server.message;

import io.netty.buffer.ByteBuf;
import net.gegy1000.statue.server.api.ModelProvider;
import net.gegy1000.statue.server.api.ProviderHandler;
import net.gegy1000.statue.server.api.StatueModel;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetModelMessage extends AbstractMessage<SetModelMessage> {
    private BlockPos pos;
    private StatueModel model;
    private ModelProvider provider;

    public SetModelMessage() {
    }

    public SetModelMessage(BlockPos pos, StatueModel model, ModelProvider provider) {
        this.pos = pos;
        this.model = model;
        this.provider = provider;
    }

    @Override
    public void onClientReceived(Minecraft client, SetModelMessage message, EntityPlayer player, MessageContext context) {
        this.handle(player.worldObj, player, message, context);
    }

    @Override
    public void onServerReceived(MinecraftServer server, SetModelMessage message, EntityPlayer player, MessageContext context) {
        this.handle(player.worldObj, player, message, context);
    }

    protected void handle(World world, EntityPlayer player, SetModelMessage message, MessageContext context) {
        if (message.model != null) {
            BlockPos pos = message.pos;
            TileEntity entity = world.getTileEntity(pos);
            if (entity instanceof StatueBlockEntity) {
                StatueBlockEntity statue = (StatueBlockEntity) entity;
                if (context.side.isServer() && !statue.canInteract(player)) {
                    return;
                }
                statue.load(message.provider, message.model);
                if (context.side.isServer()) {
                    statue.send(message);
                }
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.provider = ProviderHandler.get(buf.readByte());
        if (this.provider != null) {
            this.model = this.provider.deserialize(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        buf.writeByte(ProviderHandler.get(this.provider));
        this.provider.serialize(this.model, buf);
    }
}
