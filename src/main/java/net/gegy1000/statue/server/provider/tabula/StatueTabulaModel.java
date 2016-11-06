package net.gegy1000.statue.server.provider.tabula;

import net.gegy1000.statue.client.model.OutlinedTabulaModel;
import net.gegy1000.statue.server.api.StatueModel;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StatueTabulaModel implements StatueModel {
    private TabulaModelContainer model;

    public StatueTabulaModel(TabulaModelContainer model) {
        this.model = model;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBase create() {
        return new OutlinedTabulaModel(this.model);
    }

    @Override
    public String getName() {
        return this.model.getName();
    }

    @Override
    public String getAuthor() {
        return this.model.getAuthor();
    }

    public TabulaModelContainer get() {
        return this.model;
    }
}
