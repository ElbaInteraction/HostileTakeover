package elbainteraction.hostiletakeover;

/**
 * Created by Filip on 2015-04-15.
 */

import java.sql.Connection;
/**
 * Created by Filip on 2015-04-15.
 */

import android.util.Log;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {

    PreparedStatement statement;

    private Connection connection;
    private String userName;
    private String password;


    public DatabaseConnection() {
        statement = null;
        connection = null;
    }

    public boolean openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(  //bör ändras till rätt url
                    "jdbc:mysql://puccini.cs.lth.se/" + userName, userName,
                    password);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            Log.d("Tag", e.toString());
        }
        connection = null;
    }

    public boolean isConnected() {
        return connection != null;
    }

    void createStatement(String sql) {
        try {
            statement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void closeStatement() {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getTeams() {

        ArrayList<String> teams = new ArrayList<String>();

        try {
            String sql = "select teamName from Teams";
            createStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                teams.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement();
        }
        return teams;
    }

    public GameTile[][] getGameTiles() {

        try {
            String sql = "select * from Tiles natural join Teams";
            createStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            int rows = (int) Math.sqrt(resultSet.getFetchSize());
            GameTile[][] tiles = new GameTile[rows][rows];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < rows; j++) {
                    resultSet.next();

                    double lat = resultSet.getDouble(1);
                    double lng = resultSet.getDouble(2);
                    double height = resultSet.getDouble(3);
                    double width = resultSet.getDouble(4);
                    String teamName = resultSet.getString(5);
                    int color = resultSet.getInt(6);

                    Team team = new Team(color, teamName);
                    GameTile tile = new GameTile(lat, lng, height, width, team);
                    tiles[i][j] = tile;
                }
            }
            return tiles;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Team getTileTeam(double lat, double lng) {

        try {
            String sql = "select teamName, teamColor from Tiles natural join Teams where latitude = ? and longitude = ?";
            createStatement(sql);
            statement.setDouble(1, lat);
            statement.setDouble(2, lng);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return new Team(resultSet.getInt(2), resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setTileTeam(GameTile tile) {

        double lat = tile.getLat();
        double lng = tile.getLng();
        String teamName = tile.getTeam().getTeamName();
        try {
            connection.setAutoCommit(false);
            String sql = "select * from Tiles where latitude = ? and longitude = ? for update;";
            createStatement(sql);
            statement.setDouble(1, lat);
            statement.setDouble(2, lng);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {

                String currentTeamName = resultSet.getString(5);
                //if the correct team is already holding that tile
                if (currentTeamName.equals(teamName)) {
                    connection.rollback();
                    return false;
                }

                sql = "update Tiles set teamName = ? where latitude = ? and longitude = ?";
                createStatement(sql);
                statement.setString(1, teamName);
                statement.setDouble(2, lat);
                statement.setDouble(3, lng);

                int count = statement.executeUpdate();
                closeStatement();

                if (count != 1) {
                    connection.rollback();
                    return false;
                }
                connection.commit();
                return true;

            } else {
                connection.rollback();
                return false;
            }

        } catch (SQLException e) {
            closeStatement();
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
            e.printStackTrace();

        } finally {
            closeStatement();
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
        return false;
    }
}





