package com.zpedroo.bosses.mysql;

import com.zpedroo.bosses.objects.general.PlayerData;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.sql.*;
import java.util.*;

public class DBManager {

    public void savePlayerData(PlayerData data) {
        executeUpdate("REPLACE INTO `" + DBConnection.TABLE + "` (`uuid`, `killed_bosses_amount`) VALUES " +
                "('" + data.getUniqueId().toString() + "', " +
                "'" + data.getKilledBossesAmount() + "');");
    }

    public PlayerData getPlayerDataFromDatabase(Player player) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        String query = "SELECT * FROM `" + DBConnection.TABLE + "` WHERE `uuid`='" + player.getUniqueId().toString() + "';";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();

            if (result.next()) {
                UUID uuid = UUID.fromString(result.getString(1));
                int killedBossesAmount = result.getInt(2);

                return new PlayerData(uuid, killedBossesAmount);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeConnection(connection, result, preparedStatement, null);
        }

        return new PlayerData(player.getUniqueId(), 0);
    }

    public List<PlayerData> getTopBosses() {
        List<PlayerData> top = new ArrayList<>(10);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        String query = "SELECT * FROM `" + DBConnection.TABLE + "` ORDER BY `killed_bosses_amount` DESC LIMIT 10;";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();

            while (result.next()) {
                UUID uuid = UUID.fromString(result.getString(1));
                int killedBossesAmount = result.getInt(2);

                top.add(new PlayerData(uuid, killedBossesAmount));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection(connection, result, preparedStatement, null);
        }

        return top;
    }

    private void executeUpdate(String query) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection(connection, null, null, statement);
        }
    }

    private void closeConnection(Connection connection, ResultSet resultSet, PreparedStatement preparedStatement, Statement statement) {
        try {
            if (connection != null) connection.close();
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (statement != null) statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    protected void createTable() {
        executeUpdate("CREATE TABLE IF NOT EXISTS `" + DBConnection.TABLE + "` (`uuid` VARCHAR(255), `killed_bosses_amount` INTEGER, PRIMARY KEY(`uuid`));");
    }

    private Connection getConnection() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }
}