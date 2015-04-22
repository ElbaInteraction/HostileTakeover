package elbainteraction.hostiletakeover;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

/**
 * Created by Henrik on 2015-04-09.
 */
public class GameInstance implements Runnable {
    public static final double LUND_MAP_X_START_POINT = 55.719763;
    public static final double LUND_MAP_Y_START_POINT = 13.184195;
    public static final Team NO_TEAM = new Team(Color.TRANSPARENT,"");
    private double overlayStartLat;
    private double overlayStartLng;
    private double overlayWidth;
    private double overlayHeight;
    private int numberOfRows;
    protected GameTile[][] gameTiles;
    private int numberOfTeams;
    private GoogleMap mMap;
    private int userTeamColor;
    private DatabaseConnection db;


    public GameInstance(int numberOfTeams, GoogleMap mMap, double overLayStartLat, double overlayStartLng,
                        double overlayWidth, double overlayHeight, int numberOfRows) {
        this.mMap = mMap;
        this.numberOfTeams = numberOfTeams;
        this.overlayStartLat = overLayStartLat;
        this.overlayStartLng = overlayStartLng;
        this.overlayWidth = overlayWidth;
        this.overlayHeight = overlayHeight;
        this.numberOfRows = numberOfRows;
        gameTiles = new GameTile[numberOfRows][numberOfRows];
        db.openConnection();



    }

    /**
     * Initiates the runnable thread to start the game  *
     */
    public void initiateGame() {
        initiateOverlay();
        Thread thread = new Thread(this);
        thread.start();

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
                mMap.addPolygon(rectangle);
            }

        }


    }

    public void changeTileTeam(LatLng userLocation) {
        GameTile gameTile = findTile(userLocation);
        if(db.setTileTeam(gameTile)){
            changeTileColor(gameTile, userTeamColor);
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
        mMap.addPolygon(rectangle);

    }


    @Override
    public void run() {

    }

    public void setTeamColor(String teamColor) {
        switch (teamColor){
            case "RED":
                this.userTeamColor = Color.argb(70, 254, 0, 0);
                break;
            case "BLUE":
                this.userTeamColor = Color.argb(70, 0, 0, 254);
                break;
        }

    }
}
