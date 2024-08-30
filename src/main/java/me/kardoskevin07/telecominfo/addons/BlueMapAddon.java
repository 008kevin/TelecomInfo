package me.kardoskevin07.telecominfo.addons;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.CellTower;
import com.dbteku.telecom.models.WorldLocation;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
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
                POIMarker marker = POIMarker.builder()
                        .label(carrier.getName() + " - " + cellTower.getType())
                        .position((float)worldLocation.getX(), (float)worldLocation.getY(), (float)worldLocation.getZ())
                        .build();

                // Add the marker to the markerSet, with its id as a random UUID
                markerSet.getMarkers().put(String.valueOf(UUID.randomUUID()), marker);
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
