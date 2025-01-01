package plugin.ap.upgradedTNT;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Command implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length > 0) {
            if (sender instanceof Player p) {
                if (args[0].equals("list")) {
                    UpgradedTNT.getTNTNames().forEach(p::sendMessage);
                } else if (args[0].equals("give")) {
                    String name = args[1];
                    int amount = Integer.parseInt(args[3]);
                    String playerName = args[2];

                    if (UpgradedTNT.getTNTNames().contains(name)) {
                        ItemStack item = new ItemStack(Material.TNT, amount);
                        ItemMeta meta = item.getItemMeta();
                        List<String> lore = new ArrayList<>();

                        for (String s : UpgradedTNT.inst().getConfig().getStringList("tnts." + name + ".item-lore")) {
                            lore.add(HexUtil.translate(s
                                    .replace("{radius}", "" + UpgradedTNT.inst().getConfig().getInt("tnts." + name + ".radius"))
                                    .replace("{fuse}", "" + UpgradedTNT.inst().getConfig().getInt("tnts." + name + ".fuse-ticks"))));
                        }

                        meta.setDisplayName(HexUtil.translate(UpgradedTNT.inst().getConfig().getString("tnts." + name + ".item-name", HexUtil.translate("&cНе удалось загрузить(не было найдено)"))
                                .replace("{radius}", "" + UpgradedTNT.inst().getConfig().getInt("tnts." + name + ".radius"))
                                .replace("{fuse}", "" + UpgradedTNT.inst().getConfig().getInt("tnts." + name + ".fuse-ticks"))));
                        meta.setLore(lore);
                        meta.getPersistentDataContainer().set(new NamespacedKey(UpgradedTNT.inst(), "istnt"), PersistentDataType.BOOLEAN, true);
                        meta.getPersistentDataContainer().set(new NamespacedKey(UpgradedTNT.inst(), "tntname"), PersistentDataType.STRING, name);
                        item.setItemMeta(meta);

                        if (Bukkit.getPlayer(playerName) != null) {
                            Bukkit.getPlayer(playerName).getInventory().addItem(item);
                        }
                    } else {
                        p.sendMessage(HexUtil.translate("&c\"" + name + "\"" + " - не существует!"));
                    }
                } else if (args[0].equals("spawn")) {
                    Location loc = p.getLocation();

                    ItemDisplay item = loc.getWorld().spawn(loc, ItemDisplay.class);

                    item.setItemStack(new ItemStack(Material.TNT));
                    item.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args.length == 2) {
                return UpgradedTNT.getTNTNames();
            } else if (args.length == 3) {
                List<String> names = new ArrayList<>();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    names.add(p.getName());
                }

                return names;
            } else if (args.length == 4 ) {
                return List.of("<введите_целое_число>");
            }
        }

        return List.of("list", "give");
    }
}
