package elbainteraction.hostiletakeover;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Henrik on 2015-04-09.
 */
public class GameTile {
    private double x, y, widthX, widthY;
    private Team owningTeam;

    public GameTile(double x, double y, double widthX, double widthY, Team owningTeam){
        this.x = x;
        this.y = y;
        this.widthX = widthX;
        this.widthY = widthY;
        this.owningTeam = owningTeam;
    }

    /*Sets a new owning team for the tile. Returns the old owner of the tile.*/
    public Team setOwningTeam(Team newOwningTeam) {
        Team oldOwningTeam = owningTeam;
        owningTeam = newOwningTeam;
        return oldOwningTeam;
    }
    public boolean locationInTile(LatLng location) {
        if(location.latitude>=x && location.latitude<x+widthX){
            if(location.longitude<=y &&location.latitude>y+widthY )
                return true;

        }
        return false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidthX() {
        return widthX;
    }

    public double getWidthY() {
        return widthY;
    }
}
