package com.trollpixel.signshop.database;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@RequiredArgsConstructor
public class MysqlDatabase {

    @NonNull
    private final InetSocketAddress address;

    @NonNull
    private final String user, password;

    @NonNull
    @Getter
    private final String database;

    private Connection connection;

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                openConnection();
            }

            return connection;
        } catch (SQLException throwables) {
            return null;
        }
    }

    public void openConnection() {
        String url = String.format(
                "jdbc:mysql://%s:%s/%s",
                address.getHostName(),
                address.getPort(),
                database
        );

        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            System.out.println("Erro na hora de abrir conexão.");
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}