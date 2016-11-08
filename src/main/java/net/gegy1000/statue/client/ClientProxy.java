package net.gegy1000.statue.client;

import net.gegy1000.statue.client.gui.StatueModelGUI;
import net.gegy1000.statue.client.model.game.GameModelLoader;
import net.gegy1000.statue.client.render.RenderRegistry;
import net.gegy1000.statue.server.ServerProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ServerProxy {
    public static final Minecraft MINECRAFT = Minecraft.getMinecraft();

    @Override
    public void onPreInit() {
        super.onPreInit();
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        RenderRegistry.onPreInit();
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void onPostInit() {
        super.onPostInit();
        GameModelLoader.INSTANCE.load();
    }

    @Override
    public void selectModel(World world, BlockPos pos) {
        ClientProxy.MINECRAFT.displayGuiScreen(new StatueModelGUI(world, pos));
    }
}
