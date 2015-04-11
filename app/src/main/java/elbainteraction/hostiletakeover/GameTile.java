package elbainteraction.hostiletakeover;

import com.google.android.gms.maps.model.LatLng;

/**
 * Object for storing data about a specific tile on the map. Specifies parameters like tile location, size and current owning team.
 */
public class GameTile {
    private double lat, lng, height, width;
    private Team owningTeam;

    public GameTile(double lat, double lng, double height, double width, Team owningTeam) {
        this.lat = lat;
        this.lng = lng;
        this.height = height;
        this.width = width;
        this.owningTeam = owningTeam;
    }

    /*Sets a new owning team for the tile. Returns the old owner of the tile.*/
    public Team setOwningTeam(Team newOwningTeam) {
        Team oldOwningTeam = owningTeam;
        owningTeam = newOwningTeam;
        return oldOwningTeam;
    }

    public boolean locationInTile(LatLng location) {
        if (location.latitude <= lat && location.latitude > lat - height) {
            if (location.longitude >= lng && location.longitude < lng + width)

                return true;
        }
        return false;
    }

    public double getLat() {

        return lat;
    }

    public double getLng() {

        return lng;
    }

    public double getHeight() {

        return height;
    }

    public double getWidth() {

        return width;
    }
}
