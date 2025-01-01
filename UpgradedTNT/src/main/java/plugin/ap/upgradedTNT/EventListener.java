package plugin.ap.upgradedTNT;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.concurrent.Callable;

public class EventListener implements Listener {
    @EventHandler
    public void onPrime(TNTPrimeEvent e) {
        if (e.getBlock().getState().hasMetadata("tntname")) {
            MetadataValue name = e.getBlock().getState().getMetadata("tntname").get(0);

            if (UpgradedTNT.getTNTNames().contains(name.asString())) {
                e.getBlock().setType(Material.AIR);
                if (UpgradedTNT.getTNTNames().contains(name.asString())) {
                    TNTPrimed tnt = e.getBlock().getWorld().spawn(e.getBlock().getLocation(), TNTPrimed.class);

                    if (UpgradedTNT.inst().getConfig().getBoolean("tnts."+name+".enable-custom-name")) {
                        tnt.setCustomName(HexUtil.translate(UpgradedTNT.inst().getConfig().getString("tnts."+name+".custom-name")));
                        tnt.setCustomNameVisible(true);
                    }

                    tnt.setYield((float) UpgradedTNT.inst().getConfig().getDouble("tnts."+name+".radius"));
                    tnt.setFuseTicks(UpgradedTNT.inst().getConfig().getInt("tnts."+name+".fuse-ticks"));
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().getItemMeta() != null && e.getItemInHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(UpgradedTNT.inst(), "istnt"), PersistentDataType.BOOLEAN)) {
            e.getBlock().getState().setMetadata("tntname", new LazyMetadataValue(UpgradedTNT.inst(), new Callable<Object>() {
                @Override
                public Object call() {
                    return e.getItemInHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(UpgradedTNT.inst(), "tntname"), PersistentDataType.STRING);
                }
            }));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.TNT) {
            BlockState state = e.getBlock().getState();

            if (state.hasMetadata("tntname")) {
                String name = state.getMetadata("tntname").get(0).asString();

                if (UpgradedTNT.getTNTNames().contains(name)) {
                    e.setDropItems(false);
                    ItemStack item = new ItemStack(Material.TNT);
                    ItemMeta meta = item.getItemMeta();

                    meta.setDisplayName(HexUtil.translate(UpgradedTNT.inst().getConfig().getString("tnts."+name+".item-name")));
                    List<String> lore = UpgradedTNT.inst().getConfig().getStringList("tnts."+name+".item-lore");

                    for (int i = 0; i < lore.size(); i++) {
                        lore.set(i, HexUtil.translate(lore.get(i)
                                .replace("{radius}", ""+UpgradedTNT.inst().getConfig().getInt("tnts."+name+".radius"))
                                .replace("{fuse}", ""+UpgradedTNT.inst().getConfig().getInt("tnts."+name+".fuse-ticks"))));
                    }

                    meta.setLore(lore);
                    meta.getPersistentDataContainer().set(new NamespacedKey(UpgradedTNT.inst(), "istnt"), PersistentDataType.BOOLEAN, true);
                    meta.getPersistentDataContainer().set(new NamespacedKey(UpgradedTNT.inst(), "tntname"), PersistentDataType.STRING, name);
                    item.setItemMeta(meta);

                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(),item);
                }
            }
        }
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent e) {
        if (e.getItem().getItemMeta() != null) {
            if (e.getItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(UpgradedTNT.inst(), "istnt"), PersistentDataType.BOOLEAN)) {
                String value = e.getItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(UpgradedTNT.inst(), "tntname"), PersistentDataType.STRING);

                spawnTNT(value, e.getVelocity().toLocation(e.getBlock().getWorld()));
                e.setCancelled(true);
            }
        }
    }

    private void spawnTNT(String name, Location loc) {
        if (UpgradedTNT.getTNTNames().contains(name)) {
            TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);

            if (UpgradedTNT.inst().getConfig().getBoolean("tnts."+name+".enable-custom-name")) {
                tnt.setCustomName(HexUtil.translate(UpgradedTNT.inst().getConfig().getString("tnts."+name+".custom-name")));
                tnt.setCustomNameVisible(true);
            }
            tnt.setYield((float) UpgradedTNT.inst().getConfig().getDouble("tnts."+name+".radius"));
            tnt.setFuseTicks(UpgradedTNT.inst().getConfig().getInt("tnts."+name+".fuse-ticks"));
        }
    }
}
