package me.kardoskevin07.telecominfo.utils;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.WorldLocation;
import me.kardoskevin07.telecominfo.TelecomInfo;
import org.apache.commons.text.StringSubstitutor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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


    public String parse(String input, Carrier carrier, WorldLocation worldLocation) {
        if (debug) logger.info("Parsing placeholders for carrier " + carrier.getName() + " with worldLocation in string " + input);

        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("carrier", carrier.getName());
        valuesMap.put("owner", carrier.getOwner());
        valuesMap.put("textPrice", "" + carrier.getPricePerText());
        valuesMap.put("callPrice", "" + carrier.getPricePerMinute());
        // valuesMap.put("bestBandTower", carrier.getBestTowerByBand(worldLocation).getType().getBand().getLabel());
        valuesMap.put("bestBandTower", carrier.getBestTowerByBand(worldLocation).getType());
        valuesMap.put("bestBandTowerStrength", "" + carrier.getBestTowerByBand(worldLocation).determineStrength(worldLocation));
        // valuesMap.put("bestSignalTower", carrier.getBestTowerBySignalStrength(worldLocation).getType().getBand().getLabel());
        valuesMap.put("bestSignalTower", carrier.getBestTowerBySignalStrength(worldLocation).getType());
        valuesMap.put("bestSignalTowerStrength", "" + carrier.getBestTowerBySignalStrength(worldLocation).determineStrength(worldLocation));

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
