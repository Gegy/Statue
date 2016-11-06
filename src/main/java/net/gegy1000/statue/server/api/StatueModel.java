package net.gegy1000.statue.server.api;

import net.minecraft.client.model.ModelBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface StatueModel {
    @SideOnly(Side.CLIENT)
    ModelBase create();

    String getName();
    String getAuthor();
}
