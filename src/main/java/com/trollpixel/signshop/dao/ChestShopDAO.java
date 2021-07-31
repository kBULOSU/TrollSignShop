package com.trollpixel.signshop.dao;

import com.trollpixel.signshop.database.MysqlDatabase;
import com.trollpixel.signshop.location.SerializedLocation;
import com.trollpixel.signshop.misc.utils.InventoryUtil;
import com.trollpixel.signshop.model.ChestShop;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.sql.*;

@RequiredArgsConstructor
public class ChestShopDAO {

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS `chestShops` " +
            "(`location_world` VARCHAR(255) NOT NULL, `location_x` DOUBLE NOT NULL," +
            " `location_y` DOUBLE NOT NULL, `location_z` DOUBLE NOT NULL, `sell_price` DOUBLE NOT NULL," +
            " `buy_price` DOUBLE NOT NULL, `amount` INT NOT NULL, " +
            "`item` LONGTEXT DEFAULT NULL, `item_name` VARCHAR(16) DEFAULT NULL, PRIMARY KEY " +
            "(`location_world`, `location_x`, `location_y`, `location_z`));";

    private static final String INSERT_QUERY = "INSERT INTO `chestShops` (`location_world`, `location_x`, `location_y`, `location_z`, " +
            "`sell_price`, `buy_price`, `amount`) VALUES (?, ?, ?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE `amount`=VALUES(`amount`), `sell_price`=VALUES(`sell_price`), " +
            "`buy_price`=VALUES(`buy_price`);";

    private static final String SELECT_BY_LOCATION_QUERY = "SELECT * FROM `chestShops` WHERE " +
            "`location_world`=? AND `location_x`=? AND `location_y`=? AND `location_z`=? LIMIT 1;";

    private static final String UPDATE_QUERY = "UPDATE `chestShops` SET `sell_price` = ?, `buy_price` = ?," +
            " `amount` = ?, `item` = ?, `item_name` = ? WHERE `location_world` = ? AND `location_x` = ? " +
            "AND `location_y` = ? AND `location_z` = ? LIMIT 1;";

    private static final String DELETE_QUERY = "DELETE FROM `chestShops` WHERE `location_world` = ? " +
            "AND `location_x` = ? AND `location_y` = ? AND `location_z` = ? LIMIT 1;";

    @NonNull
    private final MysqlDatabase database;

    public ChestShop insert(SerializedLocation location, int amount, Double sellPrice, Double buyPrice) {
        Connection connection = database.getConnection();
        if (connection == null) {
            System.out.println("Conexão nula ao tentar criar a database");
            return null;
        }

        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {

            statement.setString(1, location.getWorldName());
            statement.setDouble(2, location.getX());
            statement.setDouble(3, location.getY());
            statement.setDouble(4, location.getZ());
            statement.setDouble(5, sellPrice);
            statement.setDouble(6, buyPrice);
            statement.setInt(7, amount);

            statement.executeUpdate();

            return new ChestShop(
                    location,
                    null,
                    null,
                    amount,
                    sellPrice,
                    buyPrice
            );

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public void update(ChestShop chestShop) {
        Connection connection = database.getConnection();
        if (connection == null) {
            System.out.println("Conexão nula");
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {

            statement.setDouble(1, chestShop.getSellPrice());
            statement.setDouble(2, chestShop.getBuyPrice());
            statement.setInt(3, chestShop.getAmount());

            if (chestShop.getItemStack() != null) {
                statement.setString(4, InventoryUtil.serializeContents(new ItemStack[]{chestShop.getItemStack()}));
            } else {
                statement.setNull(4, Types.VARCHAR);
            }

            if (chestShop.getItemName() != null) {
                statement.setString(5, chestShop.getItemName());
            } else {
                statement.setNull(5, Types.VARCHAR);
            }

            SerializedLocation serializedLocation = chestShop.getSerializedLocation();

            statement.setString(6, serializedLocation.getWorldName());
            statement.setDouble(7, serializedLocation.getX());
            statement.setDouble(8, serializedLocation.getY());
            statement.setDouble(9, serializedLocation.getZ());

            statement.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public ChestShop fetchByLocation(SerializedLocation location) {
        Connection connection = database.getConnection();
        if (connection == null) {
            System.out.println("Conexão nula");
            return null;
        }

        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_LOCATION_QUERY)) {

            statement.setString(1, location.getWorldName());
            statement.setDouble(2, location.getX());
            statement.setDouble(3, location.getY());
            statement.setDouble(4, location.getZ());

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            return new ChestShop(
                    location,
                    resultSet.getString("item") != null ? InventoryUtil.deserializeContents(resultSet.getString("item"))[0] : null,
                    resultSet.getString("item_name") != null ? resultSet.getString("item_name") : null,
                    resultSet.getInt("amount"),
                    resultSet.getDouble("sell_price"),
                    resultSet.getDouble("buy_price")
            );

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public void delete(ChestShop chestShop) {
        Connection connection = database.getConnection();
        if (connection == null) {
            System.out.println("Conexão nula ao tentar criar a database");
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {

            SerializedLocation serializedLocation = chestShop.getSerializedLocation();

            statement.setString(1, serializedLocation.getWorldName());
            statement.setDouble(2, serializedLocation.getX());
            statement.setDouble(3, serializedLocation.getY());
            statement.setDouble(4, serializedLocation.getZ());

            statement.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void createTable() {
        Connection connection = database.getConnection();
        if (connection == null) {
            System.out.println("Conexão nula ao tentar criar a database");
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY)) {
            statement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
