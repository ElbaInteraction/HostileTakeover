package elbainteraction.hostiletakeover;

import android.widget.ListView;

import com.google.android.gms.games.Game;
import com.google.android.gms.maps.GoogleMap;

import java.sql.Date;

/**
 * Created by Henrik on 2015-04-22.
 */
public class GameInstanceFactory {
    public final static int SMALL_MAP = 10;
    public final static int MEDIUM_MAP = 15;
    public final static int LARGE_MAP = 20;

    public GameInstanceFactory(){

    }


    public GameInstance createGameInsteance(String gameName, int numberOfTeams,double lat,double lng, String mapSize, int gameTime, GoogleMap map){
        //Upload the new game to the database?
        GameInstance gameInstance;
        int pasedMapSize;
        switch (mapSize){
            case "SMALL":
            case"small":
                pasedMapSize = SMALL_MAP;
                break;
            case "MEDIUM":
            case "medium":
                pasedMapSize = MEDIUM_MAP;
                break;
            case "LARGE":
            case "large":
                pasedMapSize = LARGE_MAP;
                break;
            default:
                pasedMapSize = SMALL_MAP;
                break;
        }

       gameInstance = new GameInstance(gameName,lat+( (0.0003*pasedMapSize)/2),lng -( (0.0005*pasedMapSize)/2),numberOfTeams,pasedMapSize);
       gameInstance.setGoogleMap(map);
        return gameInstance;
    }
}
