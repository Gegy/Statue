package net.gegy1000.statue.server;

import net.gegy1000.statue.server.api.ProviderHandler;
import net.gegy1000.statue.server.block.BlockRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ServerProxy {
    public void onPreInit() {
        ProviderHandler.onPreInit();
        BlockRegistry.onPreInit();

        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }

    public void onInit() {

    }

    public void onPostInit() {

    }

    public void selectModel(World world, BlockPos pos) {
    }
}
