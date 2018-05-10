package net.gegy1000.statue.server;

import net.gegy1000.statue.server.api.ProviderHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ServerProxy {
    public void onPreInit() {
        ProviderHandler.onPreInit();
    }

    public void onInit() {

    }

    public void onPostInit() {

    }

    public void openSelectModelGui(World world, BlockPos pos) {
    }
}
