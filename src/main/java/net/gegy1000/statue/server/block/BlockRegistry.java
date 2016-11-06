package net.gegy1000.statue.server.block;

import net.gegy1000.statue.Statue;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class BlockRegistry {
    public static final StatueBlock STATUE = new StatueBlock();

    public static final List<Block> BLOCKS = new ArrayList<>();

    public static void onPreInit() {
        BlockRegistry.register(STATUE, new ResourceLocation(Statue.MODID, "statue"), StatueBlockEntity.class);

        GameRegistry.addRecipe(new ItemStack(STATUE), "qiq", "igi", "qiq", 'q', Blocks.QUARTZ_BLOCK, 'g', Items.GLOWSTONE_DUST, 'i', Items.IRON_INGOT);
    }

    private static void register(Block block, ResourceLocation identifier) {
        block.setUnlocalizedName(identifier.getResourcePath());
        GameRegistry.register(block, identifier);
        GameRegistry.register(new ItemBlock(block), identifier);
        BLOCKS.add(block);
    }

    private static void register(Block block, ResourceLocation identifier, String oreDict) {
        BlockRegistry.register(block, identifier);
        OreDictionary.registerOre(oreDict, block);
    }

    private static void register(Block block, ResourceLocation identifier, Class<? extends TileEntity> entity) {
        BlockRegistry.register(block, identifier);
        GameRegistry.registerTileEntity(entity, identifier.getResourceDomain() + "." + identifier.getResourcePath());
    }
}
