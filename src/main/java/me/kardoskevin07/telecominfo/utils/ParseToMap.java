package me.kardoskevin07.telecominfo.utils;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.CellTower;
import com.dbteku.telecom.models.WorldLocation;
import me.kardoskevin07.telecominfo.TelecomInfo;
import me.kardoskevin07.telecominfo.models.AreaScan;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;


public class ParseToMap {

    private final TelecomInfo mainClass = TelecomInfo.getInstance();
    private final FileConfiguration config = mainClass.getConfig();

    public HashMap<String, String> parseCarrier(Carrier carrier) {
        HashMap<String, String> parseMap = new HashMap<>();

        List<String> subscribers = carrier.getSubscribers();
        String subscriberString = String.join(", ", subscribers);

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

    public HashMap<String, String> parsePlayer(Player player) {
        HashMap<String, String> parseMap = new HashMap<>();

        Carrier carrier = null;
        Iterator<Carrier> carrierIterator = TelecomApi.get().getAllCarriers().iterator();
        while (carrierIterator.hasNext() && carrier == null) {
            Carrier c = carrierIterator.next();
            if (c.isASubscriber(player.getName())) {
                carrier = c;
            }
        }

        if (carrier != null) {
            WorldLocation l = new WorldLocation(player.getLocation());
            CellTower bestTowerByBand = carrier.getBestTowerByBand(l);
            CellTower bestTowerByStrength = carrier.getBestTowerBySignalStrength(l);

            // search for a tower in the carriers peers if own carrier has no signal
            if (bestTowerByStrength.determineStrength(l) <= 0) {
                Iterator<String> it = carrier.getPeers();
                while (it.hasNext()) {
                    Carrier peer = TelecomApi.get().getCarrierById(it.next());
                    // best band
                    CellTower peerBestBand = peer.getBestTowerByBand(l);
                    if (peerBestBand.determineStrength(l) > bestTowerByBand.determineStrength(l)) {
                        bestTowerByBand = peerBestBand;
                    }

                    // best signal
                    CellTower peerBestStrength = peer.getBestTowerBySignalStrength(l);
                    if (peerBestStrength.determineStrength(l) > bestTowerByStrength.determineStrength(l)) {
                        bestTowerByStrength = peerBestStrength;
                    }
                }
            }

            parseMap.put(
                    "bestBandTower",
                    bestTowerByStrength.getType() != null ? bestTowerByBand.getType() : config.getString("lang.placeholder.noSignal")
            );
            parseMap.put(
                    "bestBandTowerStrength",
                    formatSignalStrength(bestTowerByBand.determineStrength(l))
            );

            parseMap.put(
                    "bestSignalTower",
                    bestTowerByStrength.getType() != null ? bestTowerByStrength.getType() : config.getString("lang.placeholder.noSignal")
            );
            parseMap.put(
                    "bestSignalTowerStrength",
                    formatSignalStrength(bestTowerByStrength.determineStrength(l))
            );

            parseMap.put("carrier", carrier.getName());
            parseMap.put("isSubscribed", "yes");
            parseMap.put("isRoaming", bestTowerByBand.getCarrier().equals(carrier.getName()) ? "no" : "yes");
            parseMap.put("roamingCarrier", bestTowerByBand.getCarrier().equals(carrier.getName()) ? "" : bestTowerByBand.getCarrier());
        } else {
            parseMap.put("bestBandTower", config.getString("lang.placeholder.noCarrier"));
            parseMap.put("bestBandTowerStrength", config.getString("lang.placeholder.noCarrier"));
            parseMap.put("bestSignalTower", config.getString("lang.placeholder.noCarrier"));
            parseMap.put("bestSignalTowerStrength", config.getString("lang.placeholder.noCarrier"));
            parseMap.put("carrier", config.getString("lang.placeholder.noCarrier"));
            parseMap.put("isSubscribed", "no");
            parseMap.put("isRoaming", "no");
            parseMap.put("roamingCarrier", "");
        }

        return parseMap;
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
        if (signalStrength <= 0) return ChatColor.RED + config.getString("lang.placeholder.noSignal");
        else if (signalStrength < 0.20) return ChatColor.RED + "⏺○○○○";
        else if (signalStrength < 0.40) return ChatColor.YELLOW + "⏺⏺○○○";
        else if (signalStrength < 0.60) return ChatColor.GREEN + "⏺⏺⏺○○";
        else if (signalStrength < 0.80) return ChatColor.GREEN + "⏺⏺⏺⏺○";
        else
            return ChatColor.GREEN + "⏺⏺⏺⏺⏺";
    }

}
