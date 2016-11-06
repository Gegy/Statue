package net.gegy1000.statue.server.provider.qubble;

import net.gegy1000.statue.client.model.QubbleModelBase;
import net.gegy1000.statue.server.api.StatueModel;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StatueQubbleModel implements StatueModel {
    private QubbleModel model;

    public StatueQubbleModel(QubbleModel model) {
        this.model = model;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBase create() {
        return new QubbleModelBase(this.model);
    }

    @Override
    public String getName() {
        return this.model.getName();
    }

    @Override
    public String getAuthor() {
        return this.model.getAuthor();
    }

    public QubbleModel get() {
        return this.model;
    }
}
