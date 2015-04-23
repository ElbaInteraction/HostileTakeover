package elbainteraction.hostiletakeover;

import com.google.android.gms.games.Game;
import com.google.android.gms.maps.GoogleMap;

import java.sql.Date;

/**
 * Created by Henrik on 2015-04-22.
 */
public class GameInstanceFactory {
    private DatabaseConnection db;
    public GameInstanceFactory(){
        db = DatabaseConnection.getInstance();
    }

    public GameInstance createGameInsteance(String gameName,GoogleMap map){
        GameInstance gameInstance = db.getGame(gameName);
        gameInstance.setGoogleMap(map);
        return gameInstance;

    }
    public GameInstance createGameInsteance(String gameName, int numberOfTeams, int mapSize, int gameTime, GoogleMap map){
        //Upload the new game to the database?
        GameInstance gameInstance = new GameInstance(gameName,GameInstance.LUND_MAP_X_START_POINT,GameInstance.LUND_MAP_Y_START_POINT,numberOfTeams,mapSize);
        gameInstance.setGoogleMap(map);
        return gameInstance;
    }
}
