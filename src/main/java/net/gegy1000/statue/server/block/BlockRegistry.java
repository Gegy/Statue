package net.gegy1000.statue.server.block;

import net.gegy1000.statue.Statue;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = Statue.MODID)
public class BlockRegistry {
    @GameRegistry.ObjectHolder("statue:statue")
    public static final Block STATUE = Blocks.AIR;

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new StatueBlock().setRegistryName(new ResourceLocation(Statue.MODID, "statue")));
        GameRegistry.registerTileEntity(StatueBlockEntity.class, "statue:statue");
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(STATUE).setRegistryName(new ResourceLocation(Statue.MODID, "statue")));
    }
}
