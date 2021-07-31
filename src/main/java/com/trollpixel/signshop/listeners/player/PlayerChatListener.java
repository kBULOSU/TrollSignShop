package com.trollpixel.signshop.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PlayerChatListener implements Listener {

    private static final Map<String, Consumer<AsyncPlayerChatEvent>> CONSUMERS = new ConcurrentHashMap<>();

    public static void on(Player player, Consumer<AsyncPlayerChatEvent> consumer) {
        CONSUMERS.put(player.getName(), consumer);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Consumer<AsyncPlayerChatEvent> consumer = CONSUMERS.remove(event.getPlayer().getName());
        if (consumer != null) {
            event.setCancelled(true);

            consumer.accept(event);
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        CONSUMERS.remove(event.getPlayer().getName());
    }
}
