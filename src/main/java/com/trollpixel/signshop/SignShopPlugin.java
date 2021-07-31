package com.trollpixel.signshop;

import com.trollpixel.signshop.controller.ChestShopController;
import com.trollpixel.signshop.dao.ChestShopDAO;
import com.trollpixel.signshop.inventories.InventoryListener;
import com.trollpixel.signshop.listeners.BlockListeners;
import com.trollpixel.signshop.listeners.player.PlayerChatListener;
import com.trollpixel.signshop.listeners.player.PlayerInteractListener;
import com.trollpixel.signshop.database.MysqlDatabase;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetSocketAddress;

public class SignShopPlugin extends JavaPlugin {

    public static SignShopPlugin INSTANCE;

    public static Economy ECONOMY;

    @Getter
    private MysqlDatabase mysqlDatabase;

    @Getter
    private ChestShopDAO chestShopDAO;

    @Getter
    private ChestShopController chestShopController;

    @Override
    public void onEnable() {
        super.onEnable();

        INSTANCE = this;

        saveDefaultConfig();

        ECONOMY = getServer().getServicesManager().getRegistration(Economy.class).getProvider();

        createDatabase();

        chestShopDAO = new ChestShopDAO(mysqlDatabase);
        chestShopDAO.createTable();

        chestShopController = new ChestShopController(chestShopDAO);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BlockListeners(chestShopDAO, chestShopController), this);
        pluginManager.registerEvents(new PlayerChatListener(), this);
        pluginManager.registerEvents(new PlayerInteractListener(chestShopDAO, chestShopController), this);
        pluginManager.registerEvents(new InventoryListener(), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        INSTANCE = null;

        if (mysqlDatabase != null) {
            mysqlDatabase.closeConnection();
        }
    }

    private void createDatabase() {
        ConfigurationSection section = getConfig().getConfigurationSection("database");

        InetSocketAddress inetSocketAddress = new InetSocketAddress(
                section.getString("address"),
                section.getInt("port")
        );

        String databaseName = section.getString("name");
        String username = section.getString("username");
        String password = section.getString("password");

        mysqlDatabase = new MysqlDatabase(inetSocketAddress, username, password, databaseName);
        mysqlDatabase.openConnection();

        System.out.println("Conexão MySQL aberta!");
    }
}
