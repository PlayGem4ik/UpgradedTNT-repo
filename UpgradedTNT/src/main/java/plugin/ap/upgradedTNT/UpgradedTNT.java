package plugin.ap.upgradedTNT;

import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class UpgradedTNT extends JavaPlugin {
    private static UpgradedTNT inst;

    @Override
    public void onLoad() {
        inst = this;
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        saveDefaultConfig();
        saveConfig();

        getCommand("upgradedtnt").setTabCompleter(new Command());
        getCommand("upgradedtnt").setExecutor(new Command());
        getServer().getPluginManager().registerEvents(new EventListener(), inst);
    }

    public static UpgradedTNT inst() {
        return inst;
    }

    public static List<String> getTNTNames() {
        Map<String, Object> values = inst.getConfig().getValues(true);
        List<String> names = new ArrayList<>();

        for (String key : values.keySet()) {
            if (key.contains(".") && key.split("\\.").length == 2) {
                names.add(key.split("\\.")[1]);
            }
        }

        return names;
    }
}
