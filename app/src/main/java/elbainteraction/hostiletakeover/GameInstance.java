package elbainteraction.hostiletakeover;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Henrik on 2015-04-09.
 */
public class GameInstance {
    public static final double LUND_MAP_X_START_POINT = 55.719763;
    public static final double LUND_MAP_Y_START_POINT = 13.184195;
    public static final Team NO_TEAM = new Team(Color.TRANSPARENT, "");

    public static final double overlayWidth = 0.005;
    public final static double overlayHeight = 0.003;

    private int numberOfRows;

    private double overlayStartLat;
    private double overlayStartLng;

    private String gameName;
    private Date endTime;

    protected GameTile[][] gameTiles;
    private int numberOfTeams;
    private GoogleMap map;
    private int userTeamColor = Color.RED;
    private DatabaseConnection db;
    private Team teams[];


    public GameInstance(String gameName, double overLayStartLat, double overlayStartLng, //Date endTime,
                        int numberOfTeams, int numberOfRows) {
        this.gameName = gameName;
        this.overlayStartLat = overLayStartLat;
        this.overlayStartLng = overlayStartLng;
        this.endTime = endTime;
        this.numberOfTeams = numberOfTeams;
        teams = new Team[4];
        this.numberOfRows = numberOfRows;
        this.gameTiles = new GameTile[numberOfRows][numberOfRows];
        db = DatabaseConnection.getInstance(); //hämtar med singleton.



    }

    public void setGoogleMap(GoogleMap map) {
        this.map = map;
    }

    /**
     * Initiates the runnable thread to start the game  *
     */
    public void initiateGame(Context c) {
        initiateOverlay();
        createTeams(c);


    }
    private void createTeams(Context c){
        if(PreferenceManager.getDefaultSharedPreferences(c).getBoolean(c.getString(R.string.pref_key_colorblind),false)){
        switch(numberOfTeams){
            case 4:
                teams[3]=new Team(c.getResources().getColor(R.color.main_green),"Team4");
            case 3:
                teams[2]=new Team(c.getResources().getColor(R.color.main_yellow),"Team3");

            case 2:
                teams[1]=new Team(c.getResources().getColor(R.color.main_blue),"Team2");
            case 1:
                teams[0]=new Team(c.getResources().getColor(R.color.main_red),"Team1");
        }
            return;}
        switch(numberOfTeams){
            case 4:
                teams[3]=new Team(c.getResources().getColor(R.color.green_alternative),"Team4");
            case 3:
                teams[2]=new Team(c.getResources().getColor(R.color.main_yellow),"Team3");

            case 2:
                teams[1]=new Team(c.getResources().getColor(R.color.main_blue),"Team2");
            case 1:
                teams[0]=new Team(c.getResources().getColor(R.color.red_alternative),"Team1");
        }
    }

    public void initiateGame(Context c, String gameName) {
        initiateOverlay();
        gameTiles = db.getGameTiles();
        createTeams(c);//mähä
    }

    /**
     * Method for initiating the square tiles dividing the map in different zones.  *
     */
    private void initiateOverlay() {

        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfRows; j++) {

                gameTiles[i][j] = new GameTile(overlayStartLat - overlayHeight * j, overlayStartLng + overlayWidth * i, overlayHeight, overlayWidth, NO_TEAM);

                PolygonOptions rectangle = new PolygonOptions().add(
                        new LatLng(overlayStartLat - overlayHeight * i, overlayStartLng + overlayWidth * j),
                        new LatLng(overlayStartLat - overlayHeight * (i + 1), overlayStartLng + overlayWidth * j),
                        new LatLng(overlayStartLat - overlayHeight * (i + 1), overlayStartLng + overlayWidth * (j + 1)),
                        new LatLng(overlayStartLat - overlayHeight * i, overlayStartLng + overlayWidth * (j + 1)))
                        .fillColor(Color.TRANSPARENT)
                        .strokeWidth(1);
                map.addPolygon(rectangle);
            }

        }

    }

    public void changeTileTeam(LatLng userLocation) {
        GameTile gameTile = findTile(userLocation);
        if (!db.setTileTeam(gameTile)) {//FOR TESTING PURPOSES SET NO FALSE IN ORDER TO CHANGE TILE COLOR
            gameTile.setOwningTeam(teams[0]); // ALWAYS SETS THE OWNER TO TEAM 1.
            changeTileColor(gameTile, teams[0].teamColor);
        }

    }

    private GameTile findTile(LatLng userLocation) {
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfRows; j++) {
                if (gameTiles[i][j].locationInTile(userLocation)) {
                    return gameTiles[i][j];
                }
            }
        }
        return null;
    }

    private void changeTileColor(GameTile gameTile, int teamColor) {

        PolygonOptions rectangle = new PolygonOptions().add(
                new LatLng(gameTile.getLat(), gameTile.getLng()),
                new LatLng(gameTile.getLat() - gameTile.getHeight(), gameTile.getLng()),
                new LatLng(gameTile.getLat() - gameTile.getHeight(), gameTile.getLng() + gameTile.getWidth()),
                new LatLng(gameTile.getLat(), gameTile.getLng() + gameTile.getWidth()))
                .fillColor(teamColor)
                .strokeWidth(1);
        map.addPolygon(rectangle);

    }
}
