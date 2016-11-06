package net.gegy1000.statue.client.render;

import net.gegy1000.statue.Statue;
import net.gegy1000.statue.client.render.block.StatueRenderer;
import net.gegy1000.statue.server.api.DefaultRenderedItem;
import net.gegy1000.statue.server.block.BlockRegistry;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class RenderRegistry {
    public static void onPreInit() {
        for (Block block : BlockRegistry.BLOCKS) {
            if (block instanceof DefaultRenderedItem) {
                RenderRegistry.registerRenderer(block, ((DefaultRenderedItem) block).getResource(block.getUnlocalizedName().substring("tile.".length())));
            }
        }

        ClientRegistry.bindTileEntitySpecialRenderer(StatueBlockEntity.class, new StatueRenderer());
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.STATUE), 0, StatueBlockEntity.class);
    }

    private static void registerRenderer(Item item, String name) {
        ModelResourceLocation resource = new ModelResourceLocation(Statue.MODID + ":" + name, "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, resource);
    }

    private static void registerRenderer(Block block, String name) {
        registerRenderer(Item.getItemFromBlock(block), name);
    }
}
