package me.kardoskevin07.telecominfo.utils;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.WorldLocation;
import me.kardoskevin07.telecominfo.TelecomInfo;
import me.kardoskevin07.telecominfo.models.AreaScan;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ParseToMap {

    private final TelecomInfo mainClass = TelecomInfo.getInstance();
    private final FileConfiguration config = mainClass.getConfig();

    public HashMap<String, String> parseCarrier(Carrier carrier) {
        HashMap<String, String> parseMap = new HashMap<>();

        List<String> subscribers = carrier.getSubscribers();
        String subscriberString = stringifyList(subscribers);

        List<String> peers = new ArrayList<>();
        carrier.getPeers().forEachRemaining(peers::add);
        String peersString = stringifyCarrierIdList(peers);

        parseMap.put("carrier", carrier.getName());
        parseMap.put("owner", carrier.getOwner());
        parseMap.put("textPrice", "" + carrier.getPricePerText());
        parseMap.put("callPrice", "" + carrier.getPricePerMinute());
        parseMap.put("subscribers", subscriberString);
        parseMap.put("subscriberCount", "" + subscribers.size());
        parseMap.put("peers", peersString);

        return parseMap;
    }

    public HashMap<String, String> parsePeer(Carrier peer) {
        HashMap<String, String> parseMap = new HashMap<>();

        parseMap.put("peer", peer.getName());
        parseMap.put("peerOwner", peer.getOwner());
        parseMap.put("peerTextPrice", "" + peer.getPricePerText());
        parseMap.put("peerCallPrice", "" + peer.getPricePerMinute());
        parseMap.put("peerSubscribers", "" + peer.getSubscribers().size());

        return parseMap;
    }

    public HashMap<String, String> parseCarrierAtLocation(Carrier carrier, WorldLocation location) {
        HashMap<String, String> parseMap = new HashMap<>();

        parseMap.put(
                "bestBandTower",
                carrier.getBestTowerByBand(location).getType()
        );
        parseMap.put(
                "bestBandTowerStrength",
                formatSignalStrength(carrier.getBestTowerByBand(location).determineStrength(location))
        );
        parseMap.put(
                "bestSignalTower",
                carrier.getBestTowerBySignalStrength(location).getType()
        );
        parseMap.put(
                "bestSignalTowerStrength",
                formatSignalStrength(carrier.getBestTowerBySignalStrength(location).determineStrength(location))
        );

        return parseMap;
    }

    public HashMap<String, String> parseScan(AreaScan scan) {
        HashMap<String, String> parseMap = new HashMap<>();

        int coveredAmount = scan.getCoveredAmount();
        double averageSignalStrength = scan.getAverageSignalStrength();
        String averageCellType = scan.getMostCommonCellType();
        int scanAmount = scan.getScanAmount();
        int scanRadius = scan.getScanRadius();

        parseMap.put("averageSignalRadius", String.valueOf(scanRadius));
        if (coveredAmount > 0) {
            parseMap.put("averageSignalStrength", formatSignalStrength(averageSignalStrength));
            parseMap.put("averageCellType", averageCellType);
            parseMap.put("coverage", (float) Math.round(((float) coveredAmount / (float) scanAmount) * 10000) / 100.0 + "%");
        } else {
            parseMap.put("averageSignalStrength", config.getString("lang.infoCommand.signal.areaError"));
            parseMap.put("averageCellType", config.getString("lang.infoCommand.signal.areaError"));
            parseMap.put("coverage", config.getString("lang.infoCommand.signal.areaError"));
        }

        return parseMap;
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

    private String formatSignalStrength(double signalStrength) {
        if (signalStrength < 0.20) return ChatColor.RED + "⏺○○○○";
        else if (signalStrength < 0.40) return ChatColor.YELLOW + "⏺⏺○○○";
        else if (signalStrength < 0.60) return ChatColor.GREEN + "⏺⏺⏺○○";
        else if (signalStrength < 0.80) return ChatColor.GREEN + "⏺⏺⏺⏺○";
        else
            return ChatColor.GREEN + "⏺⏺⏺⏺⏺";
    }

}
