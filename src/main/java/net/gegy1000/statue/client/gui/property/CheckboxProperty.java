package net.gegy1000.statue.client.gui.property;

import net.ilexiconn.llibrary.server.property.IBooleanProperty;

import java.util.function.Consumer;

public class CheckboxProperty implements IBooleanProperty {
    private Consumer<Boolean> submit;
    private boolean state;

    public CheckboxProperty(Consumer<Boolean> submit) {
        this.submit = submit;
    }

    @Override
    public boolean getBoolean() {
        return this.state;
    }

    @Override
    public void setBoolean(boolean state) {
        this.state = state;
        this.submit.accept(state);
    }

    @Override
    public boolean isValidBoolean(boolean state) {
        return true;
    }
}
