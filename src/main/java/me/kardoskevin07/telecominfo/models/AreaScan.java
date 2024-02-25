package me.kardoskevin07.telecominfo.models;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.WorldLocation;
import me.kardoskevin07.telecominfo.TelecomInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AreaScan {

    private final TelecomInfo mainClass = TelecomInfo.getInstance();
    private final boolean debug = mainClass.getConfig().getBoolean("debug");
    private final Logger logger = mainClass.getLogger();

    WorldLocation location;
    Carrier carrier;
    int scanRadius;
    int scansPerRadius;

    int coveredAmount = 0;
    ArrayList<TowerSignal> signalArrayList = new ArrayList<>();

    public AreaScan(WorldLocation location, Carrier carrier, int scanRadius, int scansPerRadius) {
        this.location = location;
        this.carrier = carrier;
        this.scanRadius = scanRadius;
        this.scansPerRadius = scansPerRadius;

        scan();
    }

    public double getAverageSignalStrength() {
        double avgSignal = 0;

        for (TowerSignal towerSignal : signalArrayList) {
            avgSignal += towerSignal.strength;
            if (debug) logger.info(String.valueOf(avgSignal));
        }
        avgSignal /= signalArrayList.size();

        return avgSignal;
    }
    public String getMostCommonCellType() {
        HashMap<String, Integer> signalTypeMap = new HashMap<>();
        Map.Entry<String, Integer> mostCommonTypeEntry = null;

        // Create map of tower type
        for (TowerSignal towerSignal : signalArrayList) {
            String cellType = towerSignal.cellTower.getType();
            if (debug) logger.info("Adding cell type of " + cellType);
            if (!signalTypeMap.containsKey(cellType)) {
                signalTypeMap.put(cellType, 1);
            } else {
                signalTypeMap.put(cellType, signalTypeMap.get(cellType) + 1);
            }
        }

        // Get the most common item from the map
        if (!signalTypeMap.isEmpty()) {
            for (Map.Entry<String, Integer> entry : signalTypeMap.entrySet()) {
                if (mostCommonTypeEntry == null || entry.getValue() > mostCommonTypeEntry.getValue()) {
                    mostCommonTypeEntry = entry;
                }
            }
            return mostCommonTypeEntry.getKey();
        }
        return null;
    }
    public int getCoveredAmount() {
        return this.coveredAmount;
    }

    private void scan() {
        WorldLocation scanLocation = this.location;

        int blocksPerScans = scanRadius / scansPerRadius;

        for (int i = 0; i <= scansPerRadius * 2; i++) {
            scanLocation = new WorldLocation(scanLocation.getX(),
                    scanLocation.getY(),
                    scanLocation.getZ() + blocksPerScans,
                    scanLocation.getWorldName());
            for (int j = 0; j <= scansPerRadius * 2; j++) {
                if (debug) logger.info("Scan Z" + i + "X" + j);
                scanLocation = new WorldLocation(scanLocation.getX() + blocksPerScans,
                        scanLocation.getY(),
                        scanLocation.getZ(),
                        scanLocation.getWorldName());
                if (debug) logger.info(scanLocation.getX() + " " + scanLocation.getZ());
                if (carrier.getBestTowerByBand(scanLocation).determineStrength(scanLocation) > 0) {
                    signalArrayList.add(new TowerSignal(TelecomApi.get().getCarrierByName(carrier.getName()).getBestTowerByBand(scanLocation), scanLocation));
                    coveredAmount++;
                    if (debug) logger.info(String.valueOf(coveredAmount));
                }
            }
            scanLocation = new WorldLocation(scanLocation.getX() - scanRadius * 2 - blocksPerScans,
                    scanLocation.getY(),
                    scanLocation.getZ(),
                    scanLocation.getWorldName());
        }
    }

}
