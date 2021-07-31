package com.trollpixel.signshop.location;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SerializedBlock implements Cloneable {

    @NonNull
    private String worldName = "world";

    private int x;

    private int y;

    private int z;

    public SerializedBlock(int x, int y, int z) {
        this("world", x, y, z);
    }

    public Block asBukkitBlock() {
        return Bukkit.getWorld(this.worldName).getBlockAt(x, y, z);
    }

    @Override
    public SerializedBlock clone() {
        try {
            SerializedBlock clone = (SerializedBlock) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
