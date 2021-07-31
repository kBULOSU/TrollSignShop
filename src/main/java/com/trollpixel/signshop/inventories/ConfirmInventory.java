package com.trollpixel.signshop.inventories;

import com.trollpixel.signshop.misc.utils.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@AllArgsConstructor(staticName = "of")
public class ConfirmInventory {

    private Consumer<InventoryClickEvent> onAccept;

    private Consumer<InventoryClickEvent> onDeny;

    private ItemStack icon;

    private long createTime;

    public ConfirmInventory(Consumer<InventoryClickEvent> onAccept, Consumer<InventoryClickEvent> onDeny, ItemStack icon) {
        this(onAccept, onDeny, icon, System.currentTimeMillis());
    }

    public ConfirmInventory createTime(long createTime) {
        this.createTime = createTime;
        return this;
    }

    public ConfirmInventory onAccept(Consumer<InventoryClickEvent> onAccept) {
        this.onAccept = onAccept;
        return this;
    }

    public ConfirmInventory onDeny(Consumer<InventoryClickEvent> onDeny) {
        this.onDeny = onDeny;
        return this;
    }

    public ConfirmInventory icon(ItemStack icon) {
        this.icon = icon;
        return this;
    }

    public CustomInventory make(String... acceptDescription) {
        return new Inventory(onAccept, onDeny, icon, createTime, acceptDescription);
    }

    public static ConfirmInventory of(Consumer<InventoryClickEvent> onAccept, Consumer<InventoryClickEvent> onDeny, ItemStack icon) {
        return new ConfirmInventory(onAccept, onDeny, icon);
    }

    public static class Inventory extends CustomInventory {

        private static final String TIMEOUT_MESSAGE = "&cVocê demorou muito para finalizar, tente novamente.";

        @Getter
        private long createTime = System.currentTimeMillis();

        private final Consumer<InventoryClickEvent> onAccept;

        private final Consumer<InventoryClickEvent> onDeny;

        @Getter
        private final ItemStack icon;

        @Getter
        private final String[] acceptDescription;

        public Inventory(Consumer<InventoryClickEvent> onAccept, Consumer<InventoryClickEvent> onDeny, ItemStack icon, String... acceptDescription) {
            super(icon == null ? 3 * 9 : 4 * 9, "Confirmação");

            this.onAccept = onAccept;
            this.onDeny = onDeny;
            this.icon = icon;
            this.acceptDescription = acceptDescription;
            this.buildInventory();
        }

        public Inventory(Consumer<InventoryClickEvent> onAccept, Consumer<InventoryClickEvent> onDeny, ItemStack icon, long createTime, String... acceptDescription) {
            this(onAccept, onDeny, icon, acceptDescription);
            this.createTime = createTime;
        }

        private void buildInventory() {
            ItemBuilder accept = new ItemBuilder(Material.WOOL)
                    .durability(5)
                    .name("&aAceitar (Leia abaixo)")
                    .lore(getAcceptDescription());

            ItemBuilder deny = new ItemBuilder(Material.WOOL)
                    .durability(14)
                    .name("&cNegar")
                    .lore("&7Cancelar esta operação.");

            this.getRawInventory().setItem(icon == null ? 11 : 20, accept.make(), (InventoryClickEvent event) -> {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();

                if (createTime != -1 && createTime + TimeUnit.SECONDS.toMillis(15) < System.currentTimeMillis()) {
                    player.sendMessage(MessageUtils.translateColorCodes(TIMEOUT_MESSAGE));
                    return;
                }

                onAccept(event);
            });

            this.getRawInventory().setItem(icon == null ? 15 : 24, deny.make(), (InventoryClickEvent event) -> {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                onDeny(event);
            });

            if (icon != null) {
                this.getRawInventory().setItem(13, icon.clone());
            }
        }

        public ICustomInventory getRawInventory() {
            return this;
        }

        public void onAccept(InventoryClickEvent event) {
            if (onAccept != null) {
                onAccept.accept(event);
            }
        }

        public void onDeny(InventoryClickEvent event) {
            if (onDeny != null) {
                onDeny.accept(event);
            }
        }
    }
}
