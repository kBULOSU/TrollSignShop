package com.trollpixel.signshop.listeners.player;

import com.trollpixel.signshop.controller.ChestShopController;
import com.trollpixel.signshop.dao.ChestShopDAO;
import com.trollpixel.signshop.inventories.impl.EditShopInventory;
import com.trollpixel.signshop.inventories.impl.PreviewItemInventory;
import com.trollpixel.signshop.location.SerializedLocation;
import com.trollpixel.signshop.location.unserializer.BukkitLocationParser;
import com.trollpixel.signshop.misc.utils.SignShopUtils;
import com.trollpixel.signshop.model.ChestShop;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class PlayerInteractListener implements Listener {

    private final ChestShopDAO chestShopDAO;
    private final ChestShopController chestShopController;

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void on(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) {
            return;
        }

        SerializedLocation serializedLocation = BukkitLocationParser.serialize(block.getLocation());

        ChestShop chestShop = chestShopController.get(serializedLocation);
        if (chestShop == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (player.isSneaking()) {

                if (player.isOp()) {
                    player.openInventory(new EditShopInventory(chestShop, chestShopDAO, chestShopController));
                } else {
                    player.openInventory(new PreviewItemInventory(chestShop));
                }

                return;
            }

            SignShopUtils.buy(player, chestShop);

        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

            SignShopUtils.sell(player, chestShop, player.isSneaking());

        }
    }
}
