package com.trollpixel.signshop.misc.utils;

import com.trollpixel.signshop.APIConstants;
import com.trollpixel.signshop.SignShopPlugin;
import com.trollpixel.signshop.model.ChestShop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SignShopUtils {

    /*
    public static Chest getAttachedChest(Block block) {
        if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) {
            return null;
        }

        Sign sign = (Sign) block.getState().getData();

        Block target = block.getRelative(sign.getAttachedFace());
        if (target.getState() != null && target.getState() instanceof Chest) {
            return (Chest) target.getState();
        }

        return null;
    }
     */

    public static void updateSignText(ChestShop chestShop) {
        if (chestShop == null) {
            return;
        }

        Block block = chestShop.getSerializedLocation().parser(APIConstants.LOCATION_PARSER).getBlock();
        if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) {
            return;
        }

        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();

        String sellString = chestShop.getSellPrice() == 0.0 ? "" : "&4V &0" + PriceUtils.toUserEnd(chestShop.getSellPrice());
        String buyString = chestShop.getBuyPrice() == 0.0 ? "" : "&2C &0" + PriceUtils.toUserEnd(chestShop.getBuyPrice());
        if (chestShop.getSellPrice() == 0.0 && chestShop.getBuyPrice() <= 0) {
            sellString = "";
            buyString = "&lGrátis";
        }

        String secondLine = ChatColor.translateAlternateColorCodes('&', sellString + " " + buyString);
        String thirdLine = ChatColor.translateAlternateColorCodes('&', String.valueOf(chestShop.getAmount()));

        String itemName;
        if (chestShop.getItemName() != null) {
            itemName = chestShop.getItemName();
        } else if (chestShop.getItemStack() != null) {
            itemName = chestShop.getItemStack().getType().name();
        } else {
            itemName = "A configurar.";
        }

        sign.setLine(0, "Loja");
        sign.setLine(1, secondLine);
        sign.setLine(2, thirdLine);
        sign.setLine(3, itemName);

        sign.update();
    }

    public static boolean buy(Player player, ChestShop shop) {
        if (player == null || !player.isOnline()) {
            return false;
        }

        /*
        boolean adminShop = shop.getUserName().equals("Loja");

        Chest attachedChest = getAttachedChest(block);
        if (attachedChest == null && !adminShop) {
            player.sendMessage("§cEsta placa não está colocada em nenhum baú.");
            return false;
        }

         */

        if (shop.getItemStack() == null) {
            player.sendMessage("§cEsta placa não está pronta ainda.");
            return false;
        }

        if (shop.getBuyPrice() == 0.0) {
            player.sendMessage("§cEsta loja não vende itens.");
            return false;
        }

        ItemStack clone = shop.getItemStack().clone();
        clone.setAmount(shop.getAmount());

       /*
        if (!adminShop && InventoryUtils.countItems(attachedChest.getBlockInventory(), clone) < shop.getAmount()) {
            player.sendMessage("§cEsta loja não possui mais itens para vender.");
            return false;
        }
        */

        if (!InventoryUtil.fits(player.getInventory(), clone)) {
            player.sendMessage("§cVocê não tem espaço em seu inventário para comprar este item.");
            return false;
        }

        double doubleValue = shop.getBuyPrice();
        if (SignShopPlugin.ECONOMY.getBalance(player.getName()) < doubleValue) {
            player.sendMessage("§cVocê não possui coins suficiente para comprar este item.");
            return false;
        }

        SignShopPlugin.ECONOMY.withdrawPlayer(player.getName(), doubleValue);

       /*
       if (!adminShop) {
            InventoryUtils.removeItems(attachedChest.getBlockInventory(), shop.getItemStack(), shop.getAmount());
        }
        */

        InventoryUtil.addAllItems(player.getInventory(), false, clone);

        player.updateInventory();

        player.sendMessage(String.format(
                "§aVocê comprou §7%s §aitens por §7%s §acoins.",
                shop.getAmount(),
                PriceUtils.toUserEnd(doubleValue)
        ));

        return true;
    }

    public static boolean sell(Player player, ChestShop shop, boolean all) {
        if (player == null || !player.isOnline()) {
            return false;
        }

        /*
        boolean adminShop = shop.getUserName().equals("Loja");

        Chest attachedChest = getAttachedChest(block);
        if (attachedChest == null && !adminShop) {
            player.sendMessage("§cEsta placa não está colocada em nenhum baú.");
            return false;
        }
         */

        if (shop.getItemStack() == null) {
            player.sendMessage("§cEsta placa não está pronta ainda.");
            return false;
        }

        PlayerInventory inventory = player.getInventory();

        int itemCount = InventoryUtil.countItems(inventory, shop.getItemStack());
        if (itemCount < shop.getAmount()) {
            player.sendMessage("§cVocê não tem itens para vender nesta loja.");
            return false;
        }

        if (!all) {
            itemCount = shop.getAmount();
        }

        ItemStack clone = shop.getItemStack().clone();
        clone.setAmount(itemCount);

        /*
        if (attachedChest != null && !InventoryUtils.fits(attachedChest.getBlockInventory(), clone)) {
            player.sendMessage("§cEste baú já está lotado.");
            return false;
        }
         */

        double sellValue = shop.getSellPrice() / shop.getAmount() * itemCount;

        /*
        if (!adminShop && ChestShopPlugin.ECONOMY.getBalance(shop.getUserName()) < sellValue) {
            player.sendMessage("§cO jogador não tem dinheiro suficiente para comprar seu item.");
            return false;
        }
         */

        SignShopPlugin.ECONOMY.depositPlayer(player.getName(), sellValue);

       /*
       if (!adminShop) {
            InventoryUtils.addAllItems(attachedChest.getBlockInventory(), false, clone);
        }
        */

        InventoryUtil.removeItems(inventory, shop.getItemStack(), itemCount);

        /*
        if (!adminShop) {
            ChestShopPlugin.ECONOMY.withdrawPlayer(shop.getUserName(), sellValue);
        }
         */

        player.sendMessage(String.format(
                "§aVocê vendeu §7%s §aitens por §7%s coins§a.",
                itemCount,
                PriceUtils.toUserEnd(sellValue)
        ));

        return true;
    }
}
