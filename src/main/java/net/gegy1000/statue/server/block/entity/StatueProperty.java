package net.gegy1000.statue.server.block.entity;

public enum StatueProperty {
    ROTATION_X(0.0F),
    ROTATION_Y(0.0F),
    ROTATION_Z(0.0F),
    SCALE_X(1.0F),
    SCALE_Y(1.0F),
    SCALE_Z(1.0F),
    OFFSET_X(0.0F),
    OFFSET_Y(0.0F),
    OFFSET_Z(0.0F);

    private float defaultValue;

    StatueProperty(float defaultValue) {
        this.defaultValue = defaultValue;
    }

    public float getDefaultValue() {
        return this.defaultValue;
    }
}
