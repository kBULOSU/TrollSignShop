package com.trollpixel.signshop.listeners;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.trollpixel.signshop.SignShopPlugin;
import com.trollpixel.signshop.controller.ChestShopController;
import com.trollpixel.signshop.dao.ChestShopDAO;
import com.trollpixel.signshop.location.SerializedLocation;
import com.trollpixel.signshop.location.unserializer.BukkitLocationParser;
import com.trollpixel.signshop.misc.utils.SignShopUtils;
import com.trollpixel.signshop.model.ChestShop;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

@RequiredArgsConstructor
public class BlockListeners implements Listener {

    private final ChestShopDAO chestShopDAO;
    private final ChestShopController chestShopController;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
            SerializedLocation serializedLocation = BukkitLocationParser.serialize(block.getLocation());

            ChestShop chestShop = chestShopController.get(serializedLocation);
            if (chestShop == null) {
                return;
            }

            event.setCancelled(true);

            Player player = event.getPlayer();

            if (!player.isOp()) {
                player.sendMessage("§cApenas o criador da loja pode destruir a placa.");
                return;
            }

            block.breakNaturally();

            chestShopDAO.delete(chestShop);

            chestShopController.invalidate(serializedLocation);

            player.sendMessage("§aLoja removida com sucesso.");
        }
    }

    @EventHandler
    public void on(SignChangeEvent event) {
        if (!event.getLine(0).equalsIgnoreCase("Loja")) {
            return;
        }

        Block block = event.getBlock();

        Player player = event.getPlayer();

        String priceLine = event.getLine(1);
        priceLine = priceLine.replace(" ", "");
        String[] priceSplit = priceLine.split(":");

        Double buy = Doubles.tryParse(priceSplit[0]);
        if (buy == null) {
            block.breakNaturally();

            player.sendMessage("§cPreço de compra inválido. Exemplo: 30B.");
            return;
        }

        Double sell = 0.0;

        if (priceSplit.length > 1) {
            sell = Doubles.tryParse(priceSplit[1]);
        }

        if (sell == null) {
            block.breakNaturally();

            player.sendMessage("§cPreço de venda inválido. Exemplo: 30B.");
            return;
        }

        Integer amount = Ints.tryParse(event.getLine(2));
        if (amount == null || amount <= 0) {
            block.breakNaturally();

            player.sendMessage("§cQuantia de item inválida.");
            return;
        }

        /*
        Chest attachedChest = ChestShopUtils.getAttachedChest(block);
        if (attachedChest == null) {
            if (!op) {
                block.breakNaturally();

                player.sendMessage("§cVocê precisa colocar a placa junto de um baú.");
                return;
            }

            sellerName = "Loja";
        }
         */

        ChestShop chestShop = chestShopDAO.insert(BukkitLocationParser.serialize(block.getLocation()), amount, sell, buy);
        if (chestShop == null) {
            block.breakNaturally();

            player.sendMessage("§cHouve um erro, tente novamente mais tarde.");
            return;
        }

        event.setLine(0, "");
        event.setLine(1, ChatColor.DARK_GRAY + "Carregando...");
        event.setLine(2, "");
        event.setLine(3, "");

        Bukkit.getScheduler().runTask(SignShopPlugin.INSTANCE, () -> SignShopUtils.updateSignText(chestShop));

        player.sendMessage("§aPlaca criada com sucesso! Agora clique com o shift + botão esquerdo na placa para configurar!");
    }
}
