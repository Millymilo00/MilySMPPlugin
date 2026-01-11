package io.github.millymilo000.milySMPPlugin.commands;

import io.github.millymilo000.milySMPPlugin.BorderManager;
import io.github.millymilo000.milySMPPlugin.MilySMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

public class PayBorderCommand implements CommandExecutor {

    private final MilySMPPlugin plugin;
    private final BorderManager borderManager;

    public PayBorderCommand(MilySMPPlugin plugin, BorderManager borderManager) {
        this.plugin = plugin;
        this.borderManager = borderManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String  [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use the command");
            return false;
        }

        int hours = 1;

        if (args.length > 0) {
            try {
                hours = Integer.parseInt(args[0]);
            } catch (NumberFormatException error) {
                player.sendMessage(ChatColor.DARK_RED + "You have to either give a number or just leave it as nothing for 1 hour!");
                return false;
            }
        }

        int price = borderManager.getPrice(player.getUniqueId());
        int increaseAmt = plugin.getConfig().getInt("border.cross-price-increase");
        int finalPrice = 0;
        for (int i = 0; i<hours; i++) {
            finalPrice += price;
            price += increaseAmt;
        }
        if (finalPrice > plugin.getConfig().getInt("border.max-price")) {
            finalPrice = plugin.getConfig().getInt("border.max-price");
        }

        Inventory paymentInv = Bukkit.createInventory(player, 54, String.format(ChatColor.BOLD + "Insert %d wheat worth of items", finalPrice));

        player.openInventory(paymentInv);
        player.setMetadata("OpenedMenu", new FixedMetadataValue(plugin, "payGui"));
        player.setMetadata("borderPay", new FixedMetadataValue(plugin, new int[] {finalPrice, hours}));

        return true;
    }
}
