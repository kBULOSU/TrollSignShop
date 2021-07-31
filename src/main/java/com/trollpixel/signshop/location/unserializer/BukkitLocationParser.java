package com.trollpixel.signshop.location.unserializer;

import com.trollpixel.signshop.location.LocationParser;
import com.trollpixel.signshop.location.SerializedLocation;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@NoArgsConstructor
public class BukkitLocationParser implements LocationParser<Location> {

    public static SerializedLocation serialize(Location location) {
        return new SerializedLocation(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    @Override
    public Location apply(SerializedLocation serialized) {
        return new Location(
                Bukkit.getWorld(serialized.getWorldName()),
                serialized.getX(),
                serialized.getY(),
                serialized.getZ(),
                serialized.getYaw(),
                serialized.getPitch()
        );
    }
}
