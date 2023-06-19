package me.kardoskevin07.telecominfo.commands;

import com.dbteku.telecom.api.TelecomApi;
import com.dbteku.telecom.models.Carrier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.CharArrayReader;
import java.util.List;

public class listCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        List<Carrier> carrierList = TelecomApi.get().getAllCarriers();

        commandSender.sendMessage("Carrier name | Owner | p/t | p/c");
        for (int i = 0; i < carrierList.size(); i++) {
            Carrier carrier = carrierList.get(i);

            String div = " | ";
            String row =    carrier.getName() + div +
                            carrier.getOwner() + div +
                            carrier.getPricePerText() + div +
                            carrier.getPricePerMinute();

            commandSender.sendMessage(row);
        }

        return true;
    }
}
