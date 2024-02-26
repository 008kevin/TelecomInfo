package me.kardoskevin07.telecominfo.commands;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import me.kardoskevin07.telecominfo.TelecomInfo;
import me.kardoskevin07.telecominfo.utils.PlaceholderParse;
import me.kardoskevin07.telecominfo.utils.TableGenerator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class listCommand implements TabExecutor {


    private final TelecomInfo mainClass = TelecomInfo.getInstance();
    private final FileConfiguration config = mainClass.getConfig();
    private final boolean debug = mainClass.getConfig().getBoolean("debug");
    private final Logger logger = mainClass.getLogger();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (debug) logger.info("listCommand executed by " + commandSender.getName());

        List<Carrier> carrierList = TelecomApi.get().getAllCarriers();

        int carriersPerPage = config.getInt("listCommand.itemsPerPage");
        int totalPages = (int)Math.ceil(((double)carrierList.size() + 1.0) / (double)carriersPerPage);
        int currentPage;
        if(debug) logger.info(carriersPerPage + " " + totalPages);

        // Check if input is valid
        if (strings.length > 1) {
            if (debug) logger.info("Invalid input");
            commandSender.sendMessage("§cInvalid input");
            return false;
        }

        // If no page is specified, default to page 1
        if (strings.length == 0) {
            if (debug) logger.info("No page specified");
            currentPage = 1;
        } else {
            currentPage = Integer.parseInt(strings[0]);
            if (debug) logger.info("Page specified, " + currentPage);
        }


        // Check if page is valid and is a number
        if (currentPage > totalPages || currentPage < 1) {
            if (debug) logger.info("Invalid page number");
            commandSender.sendMessage("§cInvalid page number");
        } else {
            if (debug) logger.info("Page is valid");
            // Create a new table generator
            TableGenerator tg = new TableGenerator(TableGenerator.Alignment.LEFT,
                    TableGenerator.Alignment.LEFT,
                    TableGenerator.Alignment.RIGHT,
                    TableGenerator.Alignment.RIGHT);
            if (debug) logger.info("Table generator created");
            tg.addRow(mainClass.getConfig().getString("lang.listCommand.table.header.carrier"),
                            mainClass.getConfig().getString("lang.listCommand.table.header.owner"),
                            mainClass.getConfig().getString("lang.listCommand.table.header.textPrice"),
                            mainClass.getConfig().getString("lang.listCommand.table.header.callPrice"));
            if (debug) logger.info("Table headers added");
            // Add rows to table generator
            for (int i = (currentPage - 1) * carriersPerPage; i < currentPage * carriersPerPage; i++) {
                if (debug) logger.info("Adding row " + i);
                if (i >= carrierList.size()) {
                    break;
                }
                Carrier carrier = carrierList.get(i);

                PlaceholderParse parser = new PlaceholderParse().setCarrier(carrier);

                tg.addRow(parser.parse(mainClass.getConfig().getString("lang.listCommand.table.data.carrier")),
                        parser.parse(mainClass.getConfig().getString("lang.listCommand.table.data.owner")),
                        parser.parse(mainClass.getConfig().getString("lang.listCommand.table.data.textPrice")),
                        parser.parse(mainClass.getConfig().getString("lang.listCommand.table.data.callPrice")));
                if (debug) logger.info(carrier.getName() + " added");
            }

            commandSender.sendMessage("§7§l" + carrierList.size() + " carriers, page " + currentPage + " of " + totalPages + "§r");
            // Check if sender is console
            if (commandSender.getName().equals("CONSOLE")) {
                if (debug) logger.info("Sender is console");
                for (String row : tg.generate(TableGenerator.Receiver.CONSOLE, true, true)) {
                    commandSender.sendMessage(row);
                    if (debug) logger.info("Sending" + row);
                }
            } else {
                if (debug) logger.info("Sender is player");
                for (String row : tg.generate(TableGenerator.Receiver.CLIENT, true, true)) {
                    commandSender.sendMessage(row);
                    if (debug) logger.info("Sending" + row);
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (debug) logger.info("listCommand tab complete by " + commandSender.getName());
        List<String> completion = new ArrayList<>();

        switch (strings.length) {
            case 1:
                for (int i = 1; i <= (TelecomApi.get().getAllCarriers().size() + 1) / 5; i++) {
                    completion.add(String.valueOf(i));
                    if (debug) logger.info("Adding " + i + " to tab completion");
                }
                if (debug) logger.info("Returning tab completion");
                return completion;
        }

        if (debug) logger.info("No tab completion, returning null");
        return null;
    }
}
