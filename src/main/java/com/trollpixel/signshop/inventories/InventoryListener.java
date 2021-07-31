package com.trollpixel.signshop.inventories;

import net.minecraft.server.v1_8_R3.IInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {

    @EventHandler
    public void on(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        CraftInventory craftInventory = (CraftInventory) inventory;
        IInventory iInventory = craftInventory.getInventory();

        if (iInventory instanceof CustomInventory.MinecraftInventory) {
            ((CustomInventory.MinecraftInventory) iInventory).getParent().onDrag(event);
        }
    }

    @EventHandler
    public void on(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (event.getClick() == ClickType.DOUBLE_CLICK || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        CraftInventory craftInventory = (CraftInventory) inventory;
        IInventory iInventory = craftInventory.getInventory();

        if (iInventory instanceof CustomInventory.MinecraftInventory) {
            ((CustomInventory.MinecraftInventory) iInventory).getParent().onClick(event);
        }
    }

    @EventHandler
    public void on(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        CraftInventory craftInventory = (CraftInventory) inventory;

        IInventory inventory_ = craftInventory.getInventory();
        if (inventory_ instanceof CustomInventory.MinecraftInventory) {
            ((CustomInventory.MinecraftInventory) inventory_).getParent().onOpen(event);
        }
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        CraftInventory craftInventory = (CraftInventory) inventory;
        IInventory iInventory = craftInventory.getInventory();

        if (iInventory instanceof CustomInventory.MinecraftInventory) {
            ((CustomInventory.MinecraftInventory) iInventory).getParent().onClose(event);
        }
    }
}
