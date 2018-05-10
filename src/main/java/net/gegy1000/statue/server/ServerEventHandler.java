package net.gegy1000.statue.server;

import net.gegy1000.statue.Statue;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Statue.MODID)
public class ServerEventHandler {
    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent event) {
        EntityPlayerMP player = event.getPlayer();
        Chunk chunk = event.getChunkInstance();
        if (chunk != null) {
            for (Map.Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet()) {
                if (entry.getValue() instanceof StatueBlockEntity) {
                    StatueBlockEntity entity = (StatueBlockEntity) entry.getValue();
                    entity.watchChunk(player);
                }
            }
        }
    }
}
