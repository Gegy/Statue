package net.gegy1000.statue.client.render;

import net.gegy1000.statue.Statue;
import net.gegy1000.statue.server.block.BlockRegistry;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Statue.MODID, value = Side.CLIENT)
public class RenderRegistry {
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        RenderRegistry.registerRenderer(BlockRegistry.STATUE, "statue");

        ClientRegistry.bindTileEntitySpecialRenderer(StatueBlockEntity.class, new StatueRenderer());

        Item.getItemFromBlock(BlockRegistry.STATUE).setTileEntityItemStackRenderer(new TileEntityItemStackRenderer() {
            @Override
            public void renderByItem(ItemStack stack, float partialTicks) {
                TileEntitySpecialRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(StatueBlockEntity.class);
                renderer.render(null, 0.0, 0.0, 0.0, partialTicks, 0, 1.0F);
            }
        });
    }

    private static void registerRenderer(Item item, String name) {
        ModelResourceLocation resource = new ModelResourceLocation(Statue.MODID + ":" + name, "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, resource);
    }

    private static void registerRenderer(Block block, String name) {
        registerRenderer(Item.getItemFromBlock(block), name);
    }
}
