package me.kardoskevin07.telecominfo.commands;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import com.dbteku.telecom.models.CellTower;
import com.dbteku.telecom.models.WorldLocation;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class infoCommand implements TabExecutor {

    List<String> subCommands = new ArrayList<String>(){{
        add("subscribers");
        add("signal");
        add("general");
        add("price");
    }};

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage("§cInvalid input");
            return false;
        } else {
            if (strings.length > 2 || strings.length < 2 || strings[1] == null) {
                commandSender.sendMessage("§cInvalid input");
                return false;
            }
            Carrier carrier = TelecomApi.get().getCarrierByName(strings[1]);
            if (carrier.isNull()) {
                commandSender.sendMessage("§cInvalid carrier");
                return false;
            } else {
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


                switch (strings[0]) {


                    case "subscribers":
                        List<String> subscribers = carrier.getSubscribers();
                        String subscriberString = "";

                        for (int i = 0; i < subscribers.size(); i++) {
                            // check if not last element
                            if (i != subscribers.size() - 1) {
                                subscriberString += subscribers.get(i) + ", ";
                                continue;
                            }
                            subscriberString += subscribers.get(i);
                        }

                        commandSender.sendMessage("§7Subscribers of §c§l" + carrier.getName() + "§7:");
                        commandSender.sendMessage(subscriberString);
                        break;


                    case "signal":
                        if (commandSender instanceof Player) {
                            Location location = ((Player) commandSender).getLocation();
                            WorldLocation worldLocation = new WorldLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), ((Player) commandSender).getWorld().getName());
                            if (carrier.getBestTowerBySignalStrength(worldLocation).determineStrength(worldLocation) > 0 && carrier.getBestTowerByBand(worldLocation).determineStrength(worldLocation) > 0) {
                                commandSender.sendMessage("§7Signal information about §c§l" + carrier.getName() + "§7:");

                                commandSender.sendMessage("§r - §cBest by band: §r" + carrier.getBestTowerByBand(worldLocation).getType().getBand().getLabel() + ", " +
                                        carrier.getBestTowerByBand(worldLocation).determineStrength(worldLocation));

                                commandSender.sendMessage("§r - §cBest by signal strength: §r" + carrier.getBestTowerBySignalStrength(worldLocation).getType().getBand().getLabel() + ", " +
                                        carrier.getBestTowerBySignalStrength(worldLocation).determineStrength(worldLocation));
                            } else {
                                commandSender.sendMessage("§cThere are no towers in range for this carrier");
                            }
                        } else {
                            commandSender.sendMessage("§cSorry, this command is only for players");
                        }
                        break;


                    case "general":
                        commandSender.sendMessage("§7Information about §c§l" + carrier.getName() + "§7:");
                        commandSender.sendMessage("§r - §cOwner: §r" + carrier.getOwner());
                        commandSender.sendMessage("§r - §cPeers: §r" + peersString);
                        commandSender.sendMessage("§r - §cPrice per text: §r" + carrier.getPricePerText());
                        commandSender.sendMessage("§r - §cPrice per minute: §r" + carrier.getPricePerMinute());
                        commandSender.sendMessage("§r - §cSubscribers: §r" + carrier.getSubscribers().size());
                        break;

                    case "price":
                        commandSender.sendMessage("§7Price information about §c§l" + carrier.getName() + "§7:");
                        commandSender.sendMessage("§r - §cPrice per text§r: " + carrier.getPricePerText());
                        commandSender.sendMessage("§r - §cPrice per minute§r: " + carrier.getPricePerMinute());
                        if (peers.size() != 0) {
                            commandSender.sendMessage("§r - §cPeers§r: " + peersString);
                            for (int i = 0; i < peers.size(); i++) {
                                Carrier peer = TelecomApi.get().getCarrierById(peers.get(i));
                                commandSender.sendMessage("§r    - §c" + peer.getName()
                                                            + "§r: Text: §7" + (peer.getPricePerText() + carrier.getPricePerText())
                                                            + "§r, Minute: §7" + (peer.getPricePerMinute() + carrier.getPricePerMinute()));
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
