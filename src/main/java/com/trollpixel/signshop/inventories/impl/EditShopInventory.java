package com.trollpixel.signshop.inventories.impl;

import com.google.common.primitives.Ints;
import com.trollpixel.signshop.APIConstants;
import com.trollpixel.signshop.controller.ChestShopController;
import com.trollpixel.signshop.dao.ChestShopDAO;
import com.trollpixel.signshop.inventories.ConfirmInventory;
import com.trollpixel.signshop.inventories.CustomInventory;
import com.trollpixel.signshop.inventories.ItemBuilder;
import com.trollpixel.signshop.listeners.player.PlayerChatListener;
import com.trollpixel.signshop.misc.utils.SignShopUtils;
import com.trollpixel.signshop.model.ChestShop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class EditShopInventory extends CustomInventory {

    private final ChestShop chestShop;
    private final ChestShopDAO chestShopDAO;
    private final ChestShopController chestShopController;

    public EditShopInventory(ChestShop chestShop, ChestShopDAO chestShopDAO, ChestShopController chestShopController) {
        super(4 * 9, "Editando Loja");

        this.chestShop = chestShop;
        this.chestShopDAO = chestShopDAO;
        this.chestShopController = chestShopController;

        ItemStack itemStack;
        if (chestShop.getItemStack() != null) {
            itemStack = chestShop.getItemStack();
        } else {
            itemStack = new ItemStack(Material.BARRIER);
        }

        this.setItem(
                13,
                itemStack
        );

        this.setItem(
                27,
                new ItemBuilder(Material.STORAGE_MINECART)
                        .name("§eAlterar quantia")
                        .flags(
                                ItemFlag.HIDE_ATTRIBUTES
                        )
                        .make(),
                (event) -> {
                    Player player = (Player) event.getWhoClicked();

                    player.closeInventory();

                    player.sendMessage("§aInsira a nova quantia desejada abaixo.");
                    player.sendMessage("§7Caso queira cancelar digite 'cancelar'.");

                    PlayerChatListener.on(
                            player,
                            (onChat) -> {
                                onChat.setCancelled(true);

                                String message = onChat.getMessage();

                                if (message.equalsIgnoreCase("cancelar")) {
                                    player.sendMessage("§cVocê cancelou a alteração com sucesso!");
                                    return;
                                }

                                Integer quantity = Ints.tryParse(message);

                                if (quantity == null || quantity < 1) {
                                    player.sendMessage("§cVocê inseriu uma quantia inválida!");
                                    return;
                                }

                                chestShop.setAmount(quantity);
                                SignShopUtils.updateSignText(chestShop);

                                player.sendMessage("§aLoja atualizada com sucesso!");

                                chestShopDAO.update(chestShop);
                                chestShopController.invalidate(chestShop.getSerializedLocation());
                            }
                    );
                }
        );

        this.setItem(
                30,
                new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name("§eAlterar nome")
                        .flags(
                                ItemFlag.HIDE_ATTRIBUTES
                        )
                        .make(),
                (event) -> {
                    Player player = (Player) event.getWhoClicked();

                    player.closeInventory();

                    player.sendMessage("§aInsira o novo nome desejado abaixo.");
                    player.sendMessage("§7Caso queira cancelar digite 'cancelar'.");

                    PlayerChatListener.on(
                            player,
                            (onChat) -> {
                                onChat.setCancelled(true);

                                String message = onChat.getMessage();

                                if (message.equalsIgnoreCase("cancelar")) {
                                    player.sendMessage("§cVocê cancelou a alteração com sucesso!");
                                    return;
                                }

                                chestShop.setItemName(ChatColor.translateAlternateColorCodes('&', message));
                                SignShopUtils.updateSignText(chestShop);

                                player.sendMessage("§aLoja atualizada com sucesso!");

                                chestShopDAO.update(chestShop);
                                chestShopController.invalidate(chestShop.getSerializedLocation());
                            }
                    );
                }
        );

        this.setItem(
                35,
                new ItemBuilder(Material.LAVA_BUCKET)
                        .name("§cDestruir")
                        .flags(
                                ItemFlag.HIDE_ATTRIBUTES
                        )
                        .make(),
                (event) -> {
                    Player player = (Player) event.getWhoClicked();

                    ConfirmInventory confirmInventory = new ConfirmInventory(
                            target -> {
                                Block block = chestShop.getSerializedLocation().parser(APIConstants.LOCATION_PARSER).getBlock();

                                block.breakNaturally();

                                chestShopDAO.delete(chestShop);
                                chestShopController.invalidate(chestShop.getSerializedLocation());
                            },
                            target -> player.openInventory(this),
                            new ItemBuilder(Material.PAPER)
                                    .name("§eDeletar")
                                    .make()
                    );

                    player.openInventory(confirmInventory.make(
                            "§7Ao clicar aqui, esta",
                            "§7loja será deletada."
                    ));
                }
        );
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);

        if (event.getClickedInventory() != null && event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            Player whoClicked = (Player) event.getWhoClicked();

            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null || currentItem.getType() == Material.AIR) {
                whoClicked.sendMessage("§cItem inválido.");
                return;
            }

            this.chestShop.setItemStack(currentItem);
            this.chestShop.setAmount(currentItem.getAmount());

            SignShopUtils.updateSignText(chestShop);

            whoClicked.sendMessage("§aVocê alterou o item da loja com sucesso.");

            chestShopDAO.update(chestShop);
            chestShopController.invalidate(chestShop.getSerializedLocation());

            whoClicked.openInventory(new EditShopInventory(chestShop, chestShopDAO, chestShopController));
        }
    }
}
