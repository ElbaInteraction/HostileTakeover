package elbainteraction.hostiletakeover;

import com.google.android.gms.games.Game;
import com.google.android.gms.maps.GoogleMap;

import java.sql.Date;

/**
 * Created by Henrik on 2015-04-22.
 */
public class GameInstanceFactory {
    private DatabaseConnection db;
    public final static int SMALL_MAP = 6;
    public final static int MEDIUM_MAP = 10;
    public final static int LARGE_MAP = 20;

    public GameInstanceFactory(){
        db = DatabaseConnection.getInstance();
    }

    public GameInstance createGameInsteance(String gameName,GoogleMap map){
        GameInstance gameInstance = db.getGame(gameName);
        gameInstance.setGoogleMap(map);
        return gameInstance;

    }
    public GameInstance createGameInsteance(String gameName, int numberOfTeams, String mapSize, int gameTime, GoogleMap map){
        //Upload the new game to the database?
        GameInstance gameInstance;
        int pasedMapSize;
        switch (mapSize){
            case "small":
                pasedMapSize = SMALL_MAP;
                break;
            case "medium":
                pasedMapSize = MEDIUM_MAP;
                break;
            case "large":
                pasedMapSize = LARGE_MAP;
                break;
            default:
                pasedMapSize = SMALL_MAP;
                break;
        }

       gameInstance = new GameInstance(gameName,GameInstance.LUND_MAP_X_START_POINT,GameInstance.LUND_MAP_Y_START_POINT,numberOfTeams,pasedMapSize);
       gameInstance.setGoogleMap(map);
        return gameInstance;
    }
}
