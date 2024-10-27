package me.kardoskevin07.telecominfo.addons;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.CellTower;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Logger;

public class BlueMapAddon {

    private final TelecomInfo mainClass = TelecomInfo.getInstance();
    private final FileConfiguration config = mainClass.getConfig();
    private final boolean debug = mainClass.getConfig().getBoolean("debug");
    private final Logger logger = mainClass.getLogger();
    
    public BlueMapAddon(BlueMapAPI api) {
        new BukkitRunnable() 
        {
            @Override
            public void run()
            {
                if (debug) logger.info("Updating BlueMap markers");
                List<Carrier> carrierList = TelecomApi.get().getAllCarriers();
                List<World> worlds = Bukkit.getWorlds();
                if (Objects.equals(config.getString("bluemap.groupBy"), "carrier")) {
                    if (debug) logger.info("Group by: carrier");
                    for (Carrier carrier : carrierList) {
                        for (World world : worlds) {
                            MarkerSet markerSet = MarkerSet.builder()
                                    .label(carrier.getName())
                                    .defaultHidden(config.getBoolean("bluemap.hiddenByDefault"))
                                    .build();

                            Iterator<CellTower> cellTowerIterator = carrier.getTowers();
                            // loop over all cell towers the carrier has
                            while (cellTowerIterator.hasNext()) {
                                CellTower cellTower = cellTowerIterator.next();

                                // Skip iteration if other world
                                if (!Objects.equals(cellTower.getLocation().getWorldName(), world.getName())) {
                                    continue;
                                }

                                addMarkersToSet(carrier, markerSet, cellTower);
                            }
                            // add MarkerSet to world
                            api.getWorld(Bukkit.getWorld(world.getName())).ifPresent(currentWorld -> {
                                for (BlueMapMap map : currentWorld.getMaps()) {
                                    map.getMarkerSets().put(carrier.getName(), markerSet);
                                }
                            });
                        }
                    }
                } else if (Objects.equals(config.getString("bluemap.groupBy"), "type")) {
                    if (debug) logger.info("Group by: type");
                    Set<String> rangeKeys = config.getConfigurationSection("general").getKeys(false);
                    for (String towerType : rangeKeys) {
                        for (Carrier carrier : carrierList) {
                            for (World world : worlds) {
                                MarkerSet markerSet = MarkerSet.builder()
                                        .label(carrier.getName() + " - " + towerType)
                                        .defaultHidden(config.getBoolean("bluemap.hiddenByDefault"))
                                        .build();

                                Iterator<CellTower> cellTowerIterator = carrier.getTowers();
                                // loop over all cell towers the carrier has
                                while (cellTowerIterator.hasNext()) {
                                    CellTower cellTower = cellTowerIterator.next();

                                    // Skip iteration if other world or other type
                                    if (!Objects.equals(cellTower.getLocation().getWorldName(), world.getName()) || !Objects.equals(cellTower.getType(), towerType)) {
                                        continue;
                                    }

                                    addMarkersToSet(carrier, markerSet, cellTower);
                                }
                                // add MarkerSet to world if its is not empty
                                if (!markerSet.getMarkers().isEmpty()) {
                                    api.getWorld(Bukkit.getWorld(world.getName())).ifPresent(currentWorld -> {
                                        for (BlueMapMap map : currentWorld.getMaps()) {
                                            map.getMarkerSets().put(carrier.getName() + "_" + towerType, markerSet);
                                        }
                                    });
                                }
                            }
                        }
                    }
                } else {
                    logger.severe("Unknown bluemap sort \"" + config.getString("bluemap.groupBy") + "\" set in the config. Unable to add any map markers");
                }
            }
        }.runTaskTimer(mainClass, 0L, 20L * config.getInt("bluemap.updateInterval"));
    }

    private void addMarkersToSet(Carrier carrier, MarkerSet markerSet, CellTower cellTower) {
        // check for the mode
        if (Objects.equals(config.getString("bluemap.mode"), "poi") || Objects.equals(config.getString("bluemap.mode"), "both")) {
            // Add poiMarker to the markerSet, with its id as a random UUID
            if (debug) logger.info("Adding POI marker: " + carrier.getName() + " " + cellTower.getType());
            markerSet.getMarkers().put(String.valueOf(UUID.randomUUID()), getPoiMarker(carrier, cellTower));
        } 
        if (Objects.equals(config.getString("bluemap.mode"), "radius") || Objects.equals(config.getString("bluemap.mode"), "both")) {
            // Add shapeMarker to the markerSet, with its id as a random UUID
            if (debug) logger.info("Adding Shape marker: " + carrier.getName() + " " + cellTower.getType());
            markerSet.getMarkers().put(String.valueOf(UUID.randomUUID()), getShapeMarker(carrier, cellTower));
        } 
        
        if (!(Objects.equals(config.getString("bluemap.mode"), "radius") || Objects.equals(config.getString("bluemap.mode"), "poi") || Objects.equals(config.getString("bluemap.mode"), "both"))) {
             logger.severe("Unknown bluemap mode \"" + config.getString("bluemap.mode") + "\" set in the config. Unable to add any map markers");
        }
    }

    private POIMarker getPoiMarker(Carrier carrier, CellTower cellTower) {
        return POIMarker.builder()
                .label("Tower: " + carrier.getName() + " - " + cellTower.getType())
                .position(cellTower.getLocation().getX()  + 0.5, cellTower.getLocation().getY()  + 0.5, cellTower.getLocation().getZ()  + 0.5)
                .build();
    }

    private ShapeMarker getShapeMarker(Carrier carrier, CellTower cellTower) {
        ShapeMarker.Builder shapeMarkerBuilder = ShapeMarker.builder()
                .label("Radius: " + carrier.getName() + " - " + cellTower.getType())
                .maxDistance(Double.MAX_VALUE)
                .depthTestEnabled(false);
        
        int radius = config.getInt("general." + cellTower.getType() + ".range");
        
        String colorString = config.getString("general." + cellTower.getType() + ".color");


        int numOfPoints;
        if (radius != 0) {
            numOfPoints = (int) (Math.ceil((double) radius / 100) * 16);
        } else {
            numOfPoints = 3;
            logger.severe("Radius is not set for " + cellTower.getType());
        }
        
        if (debug) logger.info("\tNumber of points: " + numOfPoints + " (radius: " + radius + ")");

        int color;
        try {
            color = Integer.decode("0x" + colorString);
        } catch (NumberFormatException e) {
            logger.severe("Incorrect format for color of " + cellTower.getType());
            color = 0;
        }
        
        // build circle shapeMarker
        return shapeMarkerBuilder
                .shape(Shape.createCircle(new Vector2d(cellTower.getLocation().getX() + 0.5, cellTower.getLocation().getZ() + 0.5), radius, numOfPoints), (float) (cellTower.getLocation().getY() + 0.5))
                .centerPosition()
                .lineColor(new Color(color, 0.9f))
                .fillColor(new Color(color, 0.2f))
                .build();
    }
}
