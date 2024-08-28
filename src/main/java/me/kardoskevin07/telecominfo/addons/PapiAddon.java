package me.kardoskevin07.telecominfo.addons;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.kardoskevin07.telecominfo.TelecomInfo;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PapiAddon extends PlaceholderExpansion {

    private final TelecomInfo plugin;

    public PapiAddon(TelecomInfo plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "telecominfo";
    }

    @Override
    public @NotNull String getAuthor() {
        return "008kevin";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
    @Override
    public boolean persist() {return true;}

    public String onRequest(OfflinePlayer player, @NotNull String s) {
        /*if (s.startsWith("carrier_")) {
            ParseToMap ptm = new ParseToMap();

            String carrierString = s.substring(0, s.lastIndexOf("_") - 1);
            System.out.println(carrierString);
            Carrier carrier = TelecomApi.get().getCarrierByName(carrierString);
            String dataName = s.substring(s.lastIndexOf("_") + 1);
            System.out.println(dataName);

            HashMap<String, String> dataMap = ptm.parseCarrier(carrier);
            if (dataMap.containsKey(dataName)) {
                return dataMap.get(dataName);
            }
        }*/
        if (s.equalsIgnoreCase("ping")) {
            return "pong";
        }

        return null;
    }
}
