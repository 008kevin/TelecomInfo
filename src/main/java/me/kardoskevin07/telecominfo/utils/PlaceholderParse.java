package me.kardoskevin07.telecominfo.utils;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.WorldLocation;
import me.kardoskevin07.telecominfo.TelecomInfo;
import me.kardoskevin07.telecominfo.models.AreaScan;
import org.apache.commons.text.StringSubstitutor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.logging.Logger;

public class PlaceholderParse {

    private final TelecomInfo mainClass = TelecomInfo.getInstance();
    private final FileConfiguration config = mainClass.getConfig();
    private final boolean debug = mainClass.getConfig().getBoolean("debug");
    private final Logger logger = mainClass.getLogger();

    Carrier carrier;
    WorldLocation location;
    AreaScan scan;
    Carrier peer;

    public PlaceholderParse setCarrier(Carrier carrier) {
        this.carrier = carrier;
        return this;
    }
    public PlaceholderParse setLocation(WorldLocation location) {
        this.location = location;
        return this;
    }
    public PlaceholderParse areaScan(int radius, int scansPerRadius) {
        this.scan = new AreaScan(this.location, this.carrier, radius, scansPerRadius);
        return this;
    }
    public PlaceholderParse setPeer(Carrier peer) {
        this.peer = peer;
        return this;
    }


    public String parse(String input, Carrier carrier) {
        if (debug) logger.info("Parsing placeholders for carrier " + carrier.getName() + " in string " + input);

        List<String> subscribers = carrier.getSubscribers();
        if (debug) logger.info("Creating subscribers string");
        String subscriberString = stringifyList(subscribers);

        List<String> peers = new ArrayList<>();
        carrier.getPeers().forEachRemaining(peers::add);
        if (debug) logger.info("Creating peer string");
        String peersString = stringifyCarrierIdList(peers);


        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("carrier", carrier.getName());
        valuesMap.put("owner", carrier.getOwner());
        valuesMap.put("textPrice", "" + carrier.getPricePerText());
        valuesMap.put("callPrice", "" + carrier.getPricePerMinute());
        valuesMap.put("subscribers", subscriberString);
        valuesMap.put("subscriberCount", "" + subscribers.size());
        valuesMap.put("peers", peersString);

        StringSubstitutor sub = new StringSubstitutor(valuesMap);

        return sub.replace(input);
    }


    public String parse(String input, Carrier carrier, WorldLocation Location, boolean doAreaScan) {
        if (debug)
            logger.info("Parsing placeholders for carrier " + carrier.getName() + " with worldLocation in string " + input);

        int scansPerRadius = config.getInt("infoCommand.signal.scansPerRadius");
        int scanRadius = config.getInt("infoCommand.signal.scanRadius");
        int blocksPerScans = scanRadius / scansPerRadius;

        int scanAmount = 0;
        int coveredAmount = 0;
        double averageSignalStrength = 0;
        String averageCellType = "";
        if (doAreaScan) {
            WorldLocation scanLocation = new WorldLocation(Location.getX() - scanRadius - blocksPerScans,
                                                            Location.getY(),
                                                            Location.getZ() - scanRadius - blocksPerScans,
                                                            Location.getWorldName());
            scanAmount = (scansPerRadius * 2 + 1) * (scansPerRadius * 2 + 1);

            AreaScan scan = new AreaScan(scanLocation, carrier, scanRadius, scansPerRadius);

            averageSignalStrength = scan.getAverageSignalStrength();
            averageCellType = scan.getMostCommonCellType();
            coveredAmount = scan.getCoveredAmount();
        }

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("carrier", carrier.getName());
        valuesMap.put("owner", carrier.getOwner());
        valuesMap.put("textPrice", "" + carrier.getPricePerText());
        valuesMap.put("callPrice", "" + carrier.getPricePerMinute());
        // (only before 0.31) valuesMap.put("bestBandTower", carrier.getBestTowerByBand(worldLocation).getType().getBand().getLabel());
        valuesMap.put("bestBandTower", carrier.getBestTowerByBand(Location).getType());
        valuesMap.put("bestBandTowerStrength", formatSignalStrength(carrier.getBestTowerByBand(Location).determineStrength(Location)));
        // (only before 0.31) valuesMap.put("bestSignalTower", carrier.getBestTowerBySignalStrength(worldLocation).getType().getBand().getLabel());
        valuesMap.put("bestSignalTower", carrier.getBestTowerBySignalStrength(Location).getType());
        valuesMap.put("bestSignalTowerStrength", formatSignalStrength(carrier.getBestTowerBySignalStrength(Location).determineStrength(Location)));
        if (doAreaScan) {
            valuesMap.put("averageSignalArea", scanRadius * 2 + "x" + scanRadius * 2);
            if (coveredAmount > 0) {
                valuesMap.put("averageSignalStrength", formatSignalStrength(averageSignalStrength));
                valuesMap.put("averageCellType", averageCellType);
                valuesMap.put("coverage", (float) Math.round(((float) coveredAmount / (float) scanAmount) * 10000) / 100.0 + "%");
            } else {
                valuesMap.put("averageSignalStrength", config.getString("lang.infoCommand.signal.areaError"));
                valuesMap.put("averageCellType", config.getString("lang.infoCommand.signal.areaError"));
                valuesMap.put("coverage", config.getString("lang.infoCommand.signal.areaError"));
            }
        }

        StringSubstitutor sub = new StringSubstitutor(valuesMap);

        return sub.replace(input);
    }

    public String parse(String input, Carrier carrier, Carrier peer) {
        if (debug)
            logger.info("Parsing placeholders for carrier " + carrier.getName() + " with peer in string " + input);

        Map<String, String> valuesMap = new HashMap<>();
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

        return sub.replace(input);
    }

    private String formatSignalStrength(double signalStrength) {
        if (signalStrength < 0.20) return ChatColor.RED + "⏺○○○○";
        else if (signalStrength < 0.40) return ChatColor.YELLOW + "⏺⏺○○○";
        else if (signalStrength < 0.60) return ChatColor.GREEN + "⏺⏺⏺○○";
        else if (signalStrength < 0.80) return ChatColor.GREEN + "⏺⏺⏺⏺○";
        else
            return ChatColor.GREEN + "⏺⏺⏺⏺⏺";
    }

    private String stringifyList(List<String> list) {
        StringBuilder listAsString = new StringBuilder();

        for (int i = 0; i < list.size() - 1; i++) {
            listAsString.append(list.get(i)).append(", ");
        }
        if (!list.isEmpty()) {
            listAsString.append(list.get(list.size() - 1));
        }

        return listAsString.toString();
    }

    private String stringifyCarrierIdList(List<String> list) {
        StringBuilder listAsString = new StringBuilder();

        for (int i = 0; i < list.size() - 1; i++) {
            listAsString.append(getCarrierById(list.get(i)).getName()).append(", ");
        }
        if (!list.isEmpty()) {
            listAsString.append(getCarrierById(list.get(list.size() - 1)).getName());
        }

        return listAsString.toString();
    }

    private Carrier getCarrierById(String id) {
        return TelecomApi.get().getCarrierById(id);
    }
}