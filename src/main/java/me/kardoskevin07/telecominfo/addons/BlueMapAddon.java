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
import java.util.Random;

public class BlueMapAddon {
    public BlueMapAddon(BlueMapAPI api) {
        List<Carrier> carrierList = TelecomApi.get().getAllCarriers();
        for (Carrier carrier : carrierList) {
            MarkerSet markerSet = MarkerSet.builder().label(carrier.getName()).build();

            Iterator<CellTower> cellTowerIterator = carrier.getTowers();

            for (; cellTowerIterator.hasNext(); ) {
                CellTower cellTower = cellTowerIterator.next();
                WorldLocation worldLocation = cellTower.getLocation();

                POIMarker marker = POIMarker.builder()
                        .label(carrier.getName() + " - " + cellTower.getType())
                        .position((float)worldLocation.getX(), (float)worldLocation.getY(), (float)worldLocation.getZ())
                        .build();

                markerSet.getMarkers().put("telecominfo marker" + new Random(33444343), marker);
            }
            api.getWorld(Bukkit.getWorld("world")).ifPresent(world -> {
                for (BlueMapMap map : world.getMaps()) {
                    map.getMarkerSets().put(carrier.getName(), markerSet);
                }
            });
        }
    }
}
