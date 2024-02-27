package me.kardoskevin07.telecominfo.models;

import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.CellTower;
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
    int scanDensity;

    int coveredAmount = 0;
    ArrayList<TowerSignal> signalArrayList;

    public AreaScan(WorldLocation location, Carrier carrier, int scanRadius, int scanDensity) {
        this.location = location;
        this.carrier = carrier;
        this.scanRadius = scanRadius;
        this.scanDensity = scanDensity;

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
        signalArrayList = new ArrayList<>();
        coveredAmount = 0;

        int circleCount = scanRadius / (scanRadius / scanDensity);

        if (debug) logger.info("Started signal scan at " + location.toString());

        CellTower currentLocationTower = carrier.getBestTowerByBand(location);
        if (currentLocationTower.determineStrength(location) > 0) {
            signalArrayList.add(new TowerSignal(currentLocationTower, location));
            coveredAmount++;
        }

        for (int circleNum = 0; circleNum < circleCount; circleNum++) {
            int r = scanRadius - circleNum * (scanRadius / circleCount);
            if (debug) logger.info("Circle with radius of " + r);

            int scanPointCount = 360 / (360 / scanDensity) * (Math.abs(circleNum - circleCount));

            for (int pointNum = 0; pointNum < scanPointCount; pointNum++) {
                double angleRad = pointNum * 0.5 * Math.PI;
                if (debug) logger.info("Angle in radians: " + angleRad);

                WorldLocation scanLocation = new WorldLocation(
                        (int)(r * Math.sin(angleRad)) + location.getX(),
                        location.getY(),
                        (int)(r * Math.cos(angleRad)) + location.getZ(),
                        location.getWorldName()
                );
                if (debug) logger.info("Point at " + scanLocation);

                CellTower bestTower = carrier.getBestTowerByBand(scanLocation);
                if (bestTower.determineStrength(scanLocation) > 0) {
                    signalArrayList.add(new TowerSignal(bestTower, scanLocation));
                    coveredAmount++;
                }

            }
        }
        if (debug) logger.info("Finished scan");
    }

}
