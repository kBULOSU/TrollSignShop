package com.trollpixel.signshop.location;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SerializedLocation implements Cloneable {

    @NonNull
    private String worldName = "world";

    private double x;

    private double y;

    private double z;

    private float yaw;

    private float pitch;

    public SerializedLocation(double x, double y, double z) {
        this("world", x, y, z, 0, 0);
    }

    public SerializedLocation(double x, double y, double z, float yaw, float pitch) {
        this("world", x, y, z, yaw, pitch);
    }

    public SerializedLocation(String worldName, double x, double y, double z) {
        this(worldName, x, y, z, 0, 0);
    }

    public <U extends LocationParser<T>, T> T parser(U parser) {
        return parser.apply(this);
    }

    @Override
    public SerializedLocation clone() {
        try {
            SerializedLocation clone = (SerializedLocation) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
