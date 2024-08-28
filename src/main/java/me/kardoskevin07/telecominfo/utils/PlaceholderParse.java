package me.kardoskevin07.telecominfo.utils;

import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.WorldLocation;
import me.kardoskevin07.telecominfo.TelecomInfo;
import me.kardoskevin07.telecominfo.models.AreaScan;
import org.apache.commons.text.StringSubstitutor;
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

    int scanRadius;
    int scansPerRadius;

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
        this.scanRadius = radius;
        this.scansPerRadius = scansPerRadius;
        return this;
    }
    public PlaceholderParse setPeer(Carrier peer) {
        this.peer = peer;
        return this;
    }


    public String parse(String input) {
        if (debug) logger.info("Parsing placeholders for carrier " + carrier.getName() + " with worldLocation in string " + input);

        HashMap<String, String> valuesMap = new HashMap<>();

        ParseToMap ptm = new ParseToMap();

        if (carrier != null) {
            valuesMap.putAll(ptm.parseCarrier(carrier));
        }
        if (peer != null) {
            valuesMap.putAll(ptm.parsePeer(peer));
        }
        if (carrier != null && location != null) {
            valuesMap.putAll(ptm.parseCarrierAtLocation(carrier, location));
        }
        if (scan != null) {
            valuesMap.putAll(ptm.parseScan(scan));
        }


        return new StringSubstitutor(valuesMap).replace(input);
    }
}