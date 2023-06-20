package me.kardoskevin07.telecominfo.commands;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import me.kardoskevin07.telecominfo.utils.TableGenerator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class listCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        List<Carrier> carrierList = TelecomApi.get().getAllCarriers();

        int carriersPerPage = 5;
        int totalPages = (carrierList.size() + 1) / carriersPerPage;
        int currentPage;

        // Check if input is valid
        if (strings.length > 1) {
            commandSender.sendMessage("§cInvalid input");
            return false;
        }

        // If no page is specified, default to page 1
        if (strings.length == 0) {
            currentPage = 1;
        } else {
            currentPage = Integer.parseInt(strings[0]);
        }


        // Check if page is valid and is a number
        if (currentPage > totalPages || currentPage < 1) {
            commandSender.sendMessage("§cInvalid page number");
        } else {

            // Create a new table generator
            TableGenerator tg = new TableGenerator(TableGenerator.Alignment.LEFT,
                    TableGenerator.Alignment.LEFT,
                    TableGenerator.Alignment.RIGHT,
                    TableGenerator.Alignment.RIGHT);
            tg.addRow("§c§nCarrier name§r", "§c§nOwner§r", "§c§np/t§r", "§c§np/m§r");
            // Add rows to table generator
            for (int i = (currentPage - 1) * carriersPerPage; i < currentPage * carriersPerPage; i++) {
                if (i >= carrierList.size()) {
                    break;
                }
                Carrier carrier = carrierList.get(i);

                tg.addRow("§7" + carrier.getName(), "§7" + carrier.getOwner(), "§7" + carrier.getPricePerText(), "§7" + carrier.getPricePerMinute());
            }

            commandSender.sendMessage("§7§l" + carrierList.size() + " carriers, page " + currentPage + " of " + totalPages + "§r");
            // Check if sender is console
            if (commandSender.getName().equals("CONSOLE")) {
                for (String row : tg.generate(TableGenerator.Receiver.CONSOLE, true, true)) {
                    commandSender.sendMessage(row);
                }
            } else {
                for (String row : tg.generate(TableGenerator.Receiver.CLIENT, true, true)) {
                    commandSender.sendMessage(row);
                }
            }

        }
        return true;
    }
}
