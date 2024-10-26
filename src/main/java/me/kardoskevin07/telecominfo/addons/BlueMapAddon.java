package me.kardoskevin07.telecominfo.addons;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.CellTower;
import com.dbteku.telecom.models.TowerType;
import com.dbteku.telecom.models.WorldLocation;
import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import de.bluecolored.bluemap.api.markers.ShapeMarker;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import me.kardoskevin07.telecominfo.TelecomInfo;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.geom.GeneralPath;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class BlueMapAddon {

    private final TelecomInfo mainClass = TelecomInfo.getInstance();
    private final FileConfiguration config = mainClass.getConfig();
    private final boolean debug = mainClass.getConfig().getBoolean("debug");
    private final Logger logger = mainClass.getLogger();
    
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

                    // Skip iteration if other world
                    if (!Objects.equals(cellTower.getLocation().getWorldName(), world.getName())) {continue;}
                    
                    // check for the mode
                    if (Objects.equals(config.getString("bluemap.mode"), "poi") || Objects.equals(config.getString("bluemap.mode"), "both")) {
                        // Add poiMarker to the markerSet, with its id as a random UUID
                        markerSet.getMarkers().put(String.valueOf(UUID.randomUUID()), getPoiMarker(carrier, cellTower));
                    } 
                    else if (Objects.equals(config.getString("bluemap.mode"), "radius") || Objects.equals(config.getString("bluemap.mode"), "both")) {
                        // Add shapeMarker to the markerSet, with its id as a random UUID
                        markerSet.getMarkers().put(String.valueOf(UUID.randomUUID()), getShapeMarker(carrier, cellTower));
                    } 
                    else {
                        logger.severe("Unknown bluemap mode \"" + config.getString("bluemap.mode") + "\" set in the config. Unable to add any map markers");
                    }
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

    private static POIMarker getPoiMarker(Carrier carrier, CellTower cellTower) {
        return POIMarker.builder()
                .label(carrier.getName() + " - " + cellTower.getType())
                .position(cellTower.getLocation().getX()  + 0.5, cellTower.getLocation().getY()  + 0.5, cellTower.getLocation().getZ()  + 0.5)
                .build();
    }

    private ShapeMarker getShapeMarker(Carrier carrier, CellTower cellTower) {
        int radius = config.getInt("general." + cellTower.getType() + ".range");
        int numOfPoints = (int) (Math.ceil((double) radius / 100) * 16);
        int color = Integer.decode("0x" + config.getString("general." + cellTower.getType() + ".color"));

        // build circle shapeMarker
        return ShapeMarker.builder()
                .label(carrier.getName() + " - " + cellTower.getType())
                .shape(Shape.createCircle(new Vector2d(cellTower.getLocation().getX() + 0.5, cellTower.getLocation().getZ() + 0.5), radius, numOfPoints), (float) (cellTower.getLocation().getY() + 0.5))
                .centerPosition()
                .maxDistance(Double.MAX_VALUE)
                .depthTestEnabled(false)
                .lineColor(new Color(color, 0.9f))
                .fillColor(new Color(color, 0.3f))
                .build();
    }
}
