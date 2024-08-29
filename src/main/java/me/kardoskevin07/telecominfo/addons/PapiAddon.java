package me.kardoskevin07.telecominfo.addons;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.WorldLocation;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.kardoskevin07.telecominfo.TelecomInfo;
import me.kardoskevin07.telecominfo.models.AreaScan;
import me.kardoskevin07.telecominfo.utils.ParseToMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class PapiAddon extends PlaceholderExpansion {

    private final TelecomInfo main = TelecomInfo.getInstance();
    private final FileConfiguration config = main.getConfig();

    @Override
    public @NotNull String getIdentifier() {
        return "telecominfo";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", main.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return main.getDescription().getVersion();
    }
    @Override
    public boolean persist() {return true;}
    @Override
    public boolean canRegister() {return true;}

    public String onPlaceholderRequest(Player player, @NotNull String s) {
        if (s.split("_").length >= 2) {
            String carrierString = s.substring(s.indexOf("_") + 1, s.lastIndexOf("_"));
            Carrier carrier = TelecomApi.get().getCarrierByName(carrierString);
            String dataName = s.substring(s.lastIndexOf("_") + 1);

            if (s.startsWith("carrier_")) {
                ParseToMap ptm = new ParseToMap();

                HashMap<String, String> dataMap = ptm.parseCarrier(carrier);
                if (dataMap.containsKey(dataName)) {
                    return dataMap.get(dataName);
                }
            }
            if (s.startsWith("location_")) {
                ParseToMap ptm = new ParseToMap();

                HashMap<String, String> dataMap = ptm.parseCarrierAtLocation(carrier, new WorldLocation(player.getLocation()));
                if (dataMap.containsKey(dataName)) {
                    return dataMap.get(dataName);
                }
            }
            if (s.startsWith("area_")) {
                ParseToMap ptm = new ParseToMap();

                int scanDensity = config.getInt("infoCommand.signal.scanDensity");
                int scanRadius = config.getInt("infoCommand.signal.scanRadius");
                AreaScan as = new AreaScan(new WorldLocation(player.getLocation()), carrier, scanRadius, scanDensity);

                HashMap<String, String> dataMap = ptm.parseScan(as);
                if (dataMap.containsKey(dataName)) {
                    return dataMap.get(dataName);
                }
            }
        }
        return null;
    }
}
