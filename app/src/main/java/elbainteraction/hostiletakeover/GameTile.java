package elbainteraction.hostiletakeover;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

/**
 * Object for storing data about a specific tile on the map. Specifies parameters like tile location, size and current owning team.
 */
public class GameTile {
    private double lat, lng, height, width;
    private Team owningTeam;
    private Polygon polygon;

    public GameTile(double lat, double lng, double height, double width, Team owningTeam) {
        this.lat = lat;
        this.lng = lng;
        this.height = height;
        this.width = width;
        this.owningTeam = owningTeam;
        this.polygon = null;
    }

    /*Sets a new owning team for the tile. Returns the old owner of the tile.*/
    public Team setOwningTeam(Team newOwningTeam) {
        Team oldOwningTeam = owningTeam;
        owningTeam = newOwningTeam;
        return oldOwningTeam;
    }

    /*Checks if the location parameter is within the boundaries of the tile.*/
    public boolean locationInTile(LatLng location) {
        if (location.latitude <= lat && location.latitude > lat - height) {
            if (location.longitude >= lng && location.longitude < lng + width)
                return true;
        }
        return false;
    }

    public double getLat() { return lat;}

    public double getLng() { return lng;}

    public double getHeight() { return height;}

    public double getWidth() {return width;}

    //If the tile has a polygon painted, remove it and then add the new polygon to the object.
    public Polygon setPolygon(Polygon polygon){
        if(this.polygon != null)this.polygon.remove();

        this.polygon = polygon;
        return polygon;
    }
}
