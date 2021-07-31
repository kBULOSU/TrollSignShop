package com.trollpixel.signshop.controller;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.trollpixel.signshop.dao.ChestShopDAO;
import com.trollpixel.signshop.location.SerializedLocation;
import com.trollpixel.signshop.location.unserializer.BukkitLocationParser;
import com.trollpixel.signshop.model.ChestShop;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ChestShopController {

    private final ChestShopDAO chestShopDAO;

    private final LoadingCache<SerializedLocation, ChestShop> shops = CacheBuilder.newBuilder()
            .expireAfterWrite(1L, TimeUnit.MINUTES)
            .build(new CacheLoader<SerializedLocation, ChestShop>() {
                @Override
                public ChestShop load(SerializedLocation key) {
                    return chestShopDAO.fetchByLocation(key);
                }
            });

    public ChestShop get(SerializedLocation location) {
        try {
            return shops.get(location);
        } catch (Exception e) {
            return null;
        }
    }

    public ChestShop get(Location location) {
        return get(BukkitLocationParser.serialize(location));
    }

    public void invalidate(SerializedLocation location) {
        shops.invalidate(location);
    }
}
