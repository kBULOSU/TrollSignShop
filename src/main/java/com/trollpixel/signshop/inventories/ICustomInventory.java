package com.trollpixel.signshop.inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface ICustomInventory {

    ItemStack BACK_ARROW = new ItemBuilder(Material.ARROW).name("&aVoltar")
            .lore("", "&eClique para voltar.")
            .make();

    int getSize();

    <T extends ClickListener> T getListener(int slot);

    void setItem(int slot, ItemStack item);

    void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> callback);

    void addItem(ItemStack item, Consumer<InventoryClickEvent> consumer);

    default void backItem(Consumer<InventoryClickEvent> consumer) {
        backItem(getSize() - 5, consumer);
    }

    default void backItem(int slot, Consumer<InventoryClickEvent> consumer) {
        setItem(slot, BACK_ARROW, consumer);
    }

    default void backItem(Inventory previous) {
        backItem(getSize() - 5, previous);
    }

    default void backItem(int slot, Inventory previous) {
        backItem(slot, (InventoryClickEvent event) -> event.getWhoClicked().openInventory(previous));
    }

    default void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() != null && !event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            ClickListener listener = getListener(event.getSlot());

            if (listener != null) {
                if (listener instanceof ConsumerClickListener) {
                    ((ConsumerClickListener) listener).accept(event);
                } else if (listener instanceof RunnableClickListener) {
                    ((RunnableClickListener) listener).run();
                }
            }
        }
    }

    default void onDrag(InventoryDragEvent event) {
    }

    default void onOpen(InventoryOpenEvent event) {
    }

    default void onClose(InventoryCloseEvent event) {
    }

    interface ClickListener {

    }

    interface ConsumerClickListener extends ClickListener, Consumer<InventoryClickEvent> {

    }

    interface RunnableClickListener extends ClickListener, Runnable {

    }
}
