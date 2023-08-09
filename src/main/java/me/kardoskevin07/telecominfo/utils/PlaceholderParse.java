package me.kardoskevin07.telecominfo.utils;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.WorldLocation;
import me.kardoskevin07.telecominfo.TelecomInfo;
import me.kardoskevin07.telecominfo.models.TowerSignal;
import org.apache.commons.text.StringSubstitutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.Math.round;

public class PlaceholderParse {

    private TelecomInfo mainClass = TelecomInfo.getInstance();
    private FileConfiguration config = mainClass.getConfig();
    private boolean debug = mainClass.getConfig().getBoolean("debug");
    private Logger logger = mainClass.getLogger();

    public String parse(String input, Carrier carrier) {
        if (debug) logger.info("Parsing placeholders for carrier " + carrier.getName() + " in string " + input);

        List<String> subscribers = carrier.getSubscribers();
        String subscriberString = "";

        if (debug) logger.info("Creating subscribers string");
        for (int i = 0; i < subscribers.size(); i++) {
            // check if not last element
            if (i != subscribers.size() - 1) {
                subscriberString += subscribers.get(i) + ", ";
                continue;
            }
            subscriberString += subscribers.get(i);
        }

        List<String> peers = new ArrayList<>();
        carrier.getPeers().forEachRemaining(peers::add);
        String peersString = "";

        for (int i = 0; i < peers.size(); i++) {

            // check if not last element
            if (i != peers.size() - 1) {
                peersString += TelecomApi.get().getCarrierById(peers.get(i)).getName() + ", ";
                continue;
            }
            peersString += TelecomApi.get().getCarrierById(peers.get(i)).getName();
        }

        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("carrier", carrier.getName());
        valuesMap.put("owner", carrier.getOwner());
        valuesMap.put("textPrice", "" + carrier.getPricePerText());
        valuesMap.put("callPrice", "" + carrier.getPricePerMinute());
        valuesMap.put("subscribers", subscriberString);
        valuesMap.put("subscriberCount", "" + subscribers.size());
        valuesMap.put("peers", peersString);

        StringSubstitutor sub = new StringSubstitutor(valuesMap);

        String output = sub.replace(input);

        return output;
    }


    public String parse(String input, Carrier carrier, WorldLocation Location, boolean doAreaScan) {
        if (debug) logger.info("Parsing placeholders for carrier " + carrier.getName() + " with worldLocation in string " + input);

        int scansPerRadius = config.getInt("infoCommand.signal.scansPerRadius");
        int scanRadius = config.getInt("infoCommand.signal.scanRadius");
        int blocksPerScans = scanRadius / scansPerRadius;
        ArrayList<TowerSignal> signalArrayList = new ArrayList<>();
        double averageSignalStrength = 0;
        HashMap<String, Integer> signalTypes = new HashMap<String, Integer>();
        WorldLocation scanLocation = new WorldLocation(Location.getX() -  scanRadius - blocksPerScans, Location.getY(), Location.getZ() -  scanRadius  - blocksPerScans, Location.getWorldName());
        int scanAmount = (scansPerRadius * 2 + 1) * (scansPerRadius * 2 + 1);
        int coveredAmount = 0;
        String averageCellType = null;

        if (doAreaScan) {
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
                    if (debug) logger.info("" + scanLocation.getX() + " " + scanLocation.getZ());
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

            if (debug) logger.info("Calculating average signal");
            for (int i = 0; i < signalArrayList.size(); i++) {
                averageSignalStrength += signalArrayList.get(i).strength;
                if (debug) logger.info(String.valueOf(averageSignalStrength));
            }
            averageSignalStrength /= signalArrayList.size();
            if (debug) logger.info("Creating cell type hashmap");
            for (int i = 0; i < signalArrayList.size(); i++) {
                String cellType = signalArrayList.get(i).cellTower.getType();
                if (debug) logger.info("Adding cell type of " + cellType);
                if (!signalTypes.containsKey(cellType)) {
                    signalTypes.put(cellType, 1);
                } else {
                    signalTypes.put(cellType, signalTypes.get(cellType) + 1);
                }
            }
            if (debug) logger.info("Getting largest value from hashmap");
            Map.Entry<String, Integer> averageCellTypeEntry = null;
            if (signalTypes.size() != 0) {
                for (Map.Entry<String, Integer> entry : signalTypes.entrySet()) {
                    if (averageCellTypeEntry == null || entry.getValue() > averageCellTypeEntry.getValue()) {
                        averageCellTypeEntry = entry;
                    }
                }
                averageCellType = averageCellTypeEntry.getKey();
            }
        }

        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("carrier", carrier.getName());
        valuesMap.put("owner", carrier.getOwner());
        valuesMap.put("textPrice", "" + carrier.getPricePerText());
        valuesMap.put("callPrice", "" + carrier.getPricePerMinute());
        // (only before 0.31) valuesMap.put("bestBandTower", carrier.getBestTowerByBand(worldLocation).getType().getBand().getLabel());
        valuesMap.put("bestBandTower", carrier.getBestTowerByBand(Location).getType());
        valuesMap.put("bestBandTowerStrength", "" + carrier.getBestTowerByBand(Location).determineStrength(Location));
        // (only before 0.31) valuesMap.put("bestSignalTower", carrier.getBestTowerBySignalStrength(worldLocation).getType().getBand().getLabel());
        valuesMap.put("bestSignalTower", carrier.getBestTowerBySignalStrength(Location).getType());
        valuesMap.put("bestSignalTowerStrength", "" + carrier.getBestTowerBySignalStrength(Location).determineStrength(Location));
        if (doAreaScan) {
            valuesMap.put("averageSignalArea", scanRadius * 2 + "x" + scanRadius * 2);
            if (coveredAmount > 0) {
                valuesMap.put("averageSignalStrength", "" + averageSignalStrength);
                valuesMap.put("averageCellType", averageCellType);
                valuesMap.put("coverage", (float) Math.round(((float) coveredAmount / (float) scanAmount) * 10000) / 100.0 + "%");
            } else {
                valuesMap.put("averageSignalStrength", config.getString("lang.infoCommand.signal.areaError"));
                valuesMap.put("averageCellType", config.getString("lang.infoCommand.signal.areaError"));
                valuesMap.put("coverage", config.getString("lang.infoCommand.signal.areaError"));
            }
        }

        StringSubstitutor sub = new StringSubstitutor(valuesMap);

        String output = sub.replace(input);
        return output;
    }

    public String parse(String input, Carrier carrier, Carrier peer) {
        if (debug) logger.info("Parsing placeholders for carrier " + carrier.getName() + " with peer in string " + input);

        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("carrier", carrier.getName());
        valuesMap.put("owner", carrier.getOwner());
        valuesMap.put("textPrice", "" + carrier.getPricePerText());
        valuesMap.put("callPrice", "" + carrier.getPricePerMinute());
        valuesMap.put("peer", peer.getName());
        valuesMap.put("peerOwner", peer.getOwner());
        valuesMap.put("peerTextPrice", "" + peer.getPricePerText());
        valuesMap.put("peerCallPrice", "" + peer.getPricePerMinute());
        valuesMap.put("peerSubscribers", "" + peer.getSubscribers().size());

        StringSubstitutor sub = new StringSubstitutor(valuesMap);

        String output = sub.replace(input);

        return output;
    }

}
