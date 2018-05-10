package net.gegy1000.statue.server.message;

import io.netty.buffer.ByteBuf;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.gegy1000.statue.server.block.entity.StatueProperty;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;

public class SetPropertiesMessage extends AbstractMessage<SetPropertiesMessage> {
    private BlockPos pos;
    private Map<StatueProperty, Float> properties = new HashMap<>();

    public SetPropertiesMessage() {
    }

    public SetPropertiesMessage(BlockPos pos, Map<StatueProperty, Float> properties) {
        this.pos = pos;
        this.properties = properties;
    }

    @Override
    public void onClientReceived(Minecraft client, SetPropertiesMessage message, EntityPlayer player, MessageContext context) {
        this.handle(player.world, player, message, context);
    }

    @Override
    public void onServerReceived(MinecraftServer server, SetPropertiesMessage message, EntityPlayer player, MessageContext context) {
        this.handle(player.world, player, message, context);
    }

    protected void handle(World world, EntityPlayer player, SetPropertiesMessage message, MessageContext context) {
        BlockPos pos = message.pos;
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof StatueBlockEntity) {
            StatueBlockEntity statue = (StatueBlockEntity) entity;
            if (context.side.isServer() && !statue.canInteract(player)) {
                statue.send(new SetPropertiesMessage(message.pos, statue.getProperties()));
                return;
            }
            statue.setPropertiesNetwork(message.properties);
            if (context.side.isServer()) {
                statue.send(message);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        for (StatueProperty property : StatueProperty.values()) {
            this.properties.put(property, buf.readFloat());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        for (StatueProperty property : StatueProperty.values()) {
            Float value = this.properties.get(property);
            buf.writeFloat(value == null ? property.getDefaultValue() : value);
        }
    }
}
