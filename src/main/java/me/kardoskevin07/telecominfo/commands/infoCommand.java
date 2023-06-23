package me.kardoskevin07.telecominfo.commands;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.WorldLocation;
import me.kardoskevin07.telecominfo.TelecomInfo;
import me.kardoskevin07.telecominfo.utils.PlaceholderParse;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class infoCommand implements TabExecutor {

    private final TelecomInfo mainClass = TelecomInfo.getInstance();
    private final FileConfiguration config = mainClass.getConfig();
    private final boolean debug = mainClass.getConfig().getBoolean("debug");
    private final Logger logger = mainClass.getLogger();
    private final PlaceholderParse parser = new PlaceholderParse();


    List<String> subCommands = new ArrayList<String>(){{
        add("subscribers");
        add("signal");
        add("general");
        add("price");
    }};

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (debug) logger.info("infoCommand executed by " + commandSender.getName());

        if (strings.length < 1) {
            if (debug) logger.info("No subcommand specified");
            commandSender.sendMessage("§cInvalid input");
            return false;
        } else {
            if (debug) logger.info("Subcommand specified, " + strings[0]);

            if (strings.length > 2 || strings.length < 2 || strings[1] == null) {
                if (debug) logger.info("Carrier not specified, or array is too long");
                commandSender.sendMessage("§cInvalid input");
                return false;
            }
            Carrier carrier = TelecomApi.get().getCarrierByName(strings[1]);
            if (carrier.isNull()) {
                if (debug) logger.info("Null carrier");
                commandSender.sendMessage("§cInvalid carrier");
                return false;
            } else {
                if (debug) logger.info("Carrier is valid");

                List<String> peers = new ArrayList<>();
                carrier.getPeers().forEachRemaining(peers::add);


                switch (strings[0]) {


                    case "subscribers":
                        if (debug) logger.info("Subscribers subcommand");
                        commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.subscribers.title"),carrier));
                        commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.subscribers.data"),carrier));
                        break;


                    case "signal":
                        if (debug) logger.info("Signal subcommand");

                        if (commandSender instanceof Player) {
                            Location location = ((Player) commandSender).getLocation();
                            WorldLocation worldLocation = new WorldLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), ((Player) commandSender).getWorld().getName());
                            if (carrier.getBestTowerBySignalStrength(worldLocation).determineStrength(worldLocation) > 0 && carrier.getBestTowerByBand(worldLocation).determineStrength(worldLocation) > 0) {
                                if (debug) logger.info("Towers found, sending message");
                                commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.signal.title"),carrier,worldLocation));
                                commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.signal.data"),carrier,worldLocation));
                            } else {
                                if (debug) logger.info("No towers found");
                                commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.signal.towerError"),carrier));
                            }
                        } else {
                            if (debug) logger.info("CommandSender is not a player");
                            commandSender.sendMessage("§cSorry, this command is only for players");
                        }
                        break;


                    case "general":
                        if (debug) logger.info("General subcommand");

                        commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.general.title"),carrier));
                        commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.general.owner"),carrier));
                        commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.general.peers"),carrier));
                        commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.general.textPrice"),carrier));
                        commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.general.callPrice"),carrier));
                        commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.general.subscribers"),carrier));
                        break;

                    case "price":
                        if (debug) logger.info("Price subcommand");

                        commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.price.title"),carrier));
                        commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.price.textPrice"),carrier));
                        commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.price.callPrice"),carrier));
                        if (peers.size() != 0) {
                            commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.price.peers.title"),carrier));
                            for (int i = 0; i < peers.size(); i++) {
                                Carrier peer = TelecomApi.get().getCarrierById(peers.get(i));
                                commandSender.sendMessage(parser.parse(config.getString("lang.infoCommand.price.peers.data"),carrier,peer));
                            }
                        }
                        break;


                    default:
                        commandSender.sendMessage("§cInvalid subcommand");
                        return false;
                }
            }
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        List<String> carriers = new ArrayList<>();
        for (int i = 0; i < TelecomApi.get().getAllCarriers().size(); i++) {
            carriers.add(TelecomApi.get().getAllCarriers().get(i).getName());
        }

        switch (strings[0]) {
            case "subscribers":
            case "signal":
            case "general":
            case "price":
                if (strings.length > 2) {
                    return null;
                } else {
                    return carriers;
                }
            default:
                if (strings.length > 1) {
                    return null;
                } else {
                    return subCommands;
            }
        }
    }
}
