package dev.reveryy.revnet;

import dev.reveryy.revconfig.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RevNet extends JavaPlugin {

    private TestConfig config;

    @Override
    public void onEnable() {
        config = ConfigManager.load(this, TestConfig.class);

        ConfigManager.enableAutoReload(this, TestConfig.class, config);
    }

    public void reloadConfigManually() {
        ConfigManager.reload(this, TestConfig.class, config);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
