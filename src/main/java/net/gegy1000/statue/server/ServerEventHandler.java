package net.gegy1000.statue.server;

import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class ServerEventHandler {
    @SubscribeEvent
    public void onChunkWatch(ChunkWatchEvent event) {
        ChunkPos chunkPos = event.getChunk();
        EntityPlayerMP player = event.getPlayer();
        Chunk chunk = player.worldObj.getChunkFromChunkCoords(chunkPos.chunkXPos, chunkPos.chunkZPos);
        for (Map.Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet()) {
            if (entry.getValue() instanceof StatueBlockEntity) {
                StatueBlockEntity entity = (StatueBlockEntity) entry.getValue();
                entity.watchChunk(player);
            }
        }
    }
}
