package me.kardoskevin07.telecominfo.addons;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.CellTower;
import com.dbteku.telecom.models.WorldLocation;
import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import de.bluecolored.bluemap.api.markers.ShapeMarker;
import de.bluecolored.bluemap.api.math.Shape;
import org.bukkit.Bukkit;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BlueMapAddon {
    public BlueMapAddon(BlueMapAPI api) {
        List<Carrier> carrierList = TelecomApi.get().getAllCarriers();
        for (Carrier carrier : carrierList) {
            // Create a MarkerSet for the carrier
            MarkerSet markerSet = MarkerSet.builder().label(carrier.getName()).build();

            Iterator<CellTower> cellTowerIterator = carrier.getTowers();

            // loop over all celltowers the carrier has
            while (cellTowerIterator.hasNext()) {
                CellTower cellTower = cellTowerIterator.next();
                WorldLocation worldLocation = cellTower.getLocation();

                // make a POIMarker for each CellTower
                POIMarker poiMarker = POIMarker.builder()
                        .label(carrier.getName() + " - " + cellTower.getType())
                        .position(worldLocation.getX()  + 0.5, worldLocation.getY()  + 0.5, worldLocation.getZ()  + 0.5)
                        .build();

                // Add the poiMarker to the markerSet, with its id as a random UUID
                markerSet.getMarkers().put(String.valueOf(UUID.randomUUID()), poiMarker);

                // variables for drawing circle
                int radius = 200;
                int numOfPoints = 16;

                ShapeMarker shapeMarker = ShapeMarker.builder()
                        .label(carrier.getName() + " - " + cellTower.getType())
                        .shape(Shape.createCircle(new Vector2d(cellTower.getLocation().getX() + 0.5, cellTower.getLocation().getZ() + 0.5), radius, numOfPoints), (float) (cellTower.getLocation().getY() + 0.5))
                        .centerPosition()
                        .maxDistance(Double.MAX_VALUE)
                        .depthTestEnabled(false)
                        .build();

                markerSet.getMarkers().put(String.valueOf(UUID.randomUUID()), shapeMarker);
            }
            // TODO: make it work on every world, separately
            api.getWorld(Bukkit.getWorld("world")).ifPresent(world -> {
                for (BlueMapMap map : world.getMaps()) {
                    map.getMarkerSets().put(carrier.getName(), markerSet);
                }
            });
        }
    }
}
