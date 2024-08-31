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
import org.bukkit.World;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BlueMapAddon {
    public BlueMapAddon(BlueMapAPI api) {
        List<Carrier> carrierList = TelecomApi.get().getAllCarriers();
        for (Carrier carrier : carrierList) {
            // Loop through all worlds
            List<World> worlds = Bukkit.getWorlds();
            for (World world : worlds) {
                // Create a MarkerSet for the carrier in world
                MarkerSet markerSet = MarkerSet.builder().label(carrier.getName()).build();

                Iterator<CellTower> cellTowerIterator = carrier.getTowers();
                // loop over all cell towers the carrier has
                while (cellTowerIterator.hasNext()) {
                    CellTower cellTower = cellTowerIterator.next();
                    WorldLocation worldLocation = cellTower.getLocation();

                    // Skip iteration if other world
                    if (!Objects.equals(worldLocation.getWorldName(), world.getName())) {continue;}

                    // make a POIMarker for the given cell tower
                    POIMarker poiMarker = POIMarker.builder()
                            .label(carrier.getName() + " - " + cellTower.getType())
                            .position(worldLocation.getX()  + 0.5, worldLocation.getY()  + 0.5, worldLocation.getZ()  + 0.5)
                            .build();

                    // Add poiMarker to the markerSet, with its id as a random UUID
                    markerSet.getMarkers().put(String.valueOf(UUID.randomUUID()), poiMarker);

                    int radius = 200;
                    int numOfPoints = (int) (Math.ceil((double) radius / 100) * 16);

                    // build circle shapeMarker for the given cell tower
                    ShapeMarker shapeMarker = ShapeMarker.builder()
                            .label(carrier.getName() + " - " + cellTower.getType())
                            .shape(Shape.createCircle(new Vector2d(cellTower.getLocation().getX() + 0.5, cellTower.getLocation().getZ() + 0.5), radius, numOfPoints), (float) (cellTower.getLocation().getY() + 0.5))
                            .centerPosition()
                            .maxDistance(Double.MAX_VALUE)
                            .depthTestEnabled(false)
                            .build();

                    // Add shapeMarker to the markerSet, with its id as a random UUID
                    markerSet.getMarkers().put(String.valueOf(UUID.randomUUID()), shapeMarker);
                }

                // Add markers to the given world
                api.getWorld(Bukkit.getWorld(world.getName())).ifPresent(currentWorld -> {
                    for (BlueMapMap map : currentWorld.getMaps()) {
                        map.getMarkerSets().put(carrier.getName(), markerSet);
                    }
                });
            }
        }
    }
}
