package io.github.millymilo000.milySMPPlugin.listeners;

import io.github.millymilo000.milySMPPlugin.BorderManager;
import io.github.millymilo000.milySMPPlugin.MilySMPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class PayGuiListener implements Listener {

    private final MilySMPPlugin plugin;
    private final BorderManager borderManager;

    public PayGuiListener(MilySMPPlugin plugin, BorderManager borderManager) {
        this.plugin = plugin;
        this.borderManager = borderManager;
    }

    @EventHandler
    public void onCloseInv(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (player.hasMetadata("OpenedMenu") && "payGui".equalsIgnoreCase(player.getMetadata("OpenedMenu").getFirst().asString())) {
            player.removeMetadata("OpenedMenu", plugin);
            int[] borderPay = (int[]) player.getMetadata("borderPay").getFirst().value();
            player.removeMetadata("borderPay", plugin);
            int price = borderPay[0];
            int hours = borderPay[1];

            ItemStack[] contents = event.getInventory().getContents();
            if (!event.getInventory().contains(Material.WHEAT) && !event.getInventory().contains(Material.HAY_BLOCK)) {
                player.sendMessage(ChatColor.DARK_AQUA + "Transaction canceled");
                return;
            }

            // Get the wheat in the contents
            int wheatAmt = Arrays.stream(contents).filter(Objects::nonNull).filter(i -> i.getType().equals(Material.WHEAT)).reduce(0, (subtotal, item) -> subtotal + item.getAmount(), Integer::sum);
            // Get the hay bales, convert to wheat, and add
            wheatAmt += 9 * Arrays.stream(contents).filter(Objects::nonNull).filter(i -> i.getType().equals(Material.HAY_BLOCK)).reduce(0, (subtotal, item) -> subtotal + item.getAmount(), Integer::sum);


            if (wheatAmt < price) {
                player.sendMessage(String.format(ChatColor.DARK_RED +  "You didn't give enough wheat. You paid %d, you needed to pay %d", wheatAmt, price));
                // If I don't do a runTaskLater, it wont give the items until the player run the /pay command again to get their items
                plugin.getServer().getScheduler().runTaskLater(plugin, task -> {
                    player.give(Arrays.stream(contents).filter(Objects::nonNull).collect(Collectors.toList()));
                }, 5);
                return;
            }

            player.sendMessage(ChatColor.GREEN + "Access granted, you can be out for " + hours + " hour(s)");
            borderManager.addUser(player.getUniqueId(), hours);
            if (wheatAmt > price) {
                int refundAmt = wheatAmt - price;
                player.sendMessage(ChatColor.DARK_AQUA + "You were refunded " + refundAmt +" wheat for the extra.");
                int refHayAmt = refundAmt / 9;
                int refWheatAmt = refundAmt - refHayAmt * 9;

                ItemStack wheat = new ItemStack(Material.WHEAT);
                ItemStack hay = new ItemStack(Material.HAY_BLOCK);
                // you cant set items amount to be 0, so heres this awful solution so we dont do that.
                if (refHayAmt == 0) {
                    wheat.setAmount(refWheatAmt);
                    // If I don't do a runTaskLater, it wont give the items until the player run the /pay command again to get their items
                    plugin.getServer().getScheduler().runTaskLater(plugin, task -> {
                        player.give(wheat);
                    }, 5);
                } else if (refWheatAmt == 0) {
                    hay.setAmount(refHayAmt);
                    plugin.getServer().getScheduler().runTaskLater(plugin, task -> {
                        player.give(hay);
                    }, 5);
                } else {
                    wheat.setAmount(refWheatAmt);
                    hay.setAmount(refHayAmt);
                    plugin.getServer().getScheduler().runTaskLater(plugin, task -> {
                        player.give(wheat, hay);
                    }, 5);
                }
            }
        }
    }
}
