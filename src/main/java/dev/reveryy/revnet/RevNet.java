package dev.reveryy.revnet;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.reveryy.revdata.RepositoryFactory;
import dev.reveryy.revdata.RevDataConnection;
import dev.reveryy.revnet.commands.RevNetCommand;
import dev.reveryy.revnet.managers.DeviceManager;
import dev.reveryy.revnet.managers.NetworkManager;
import dev.reveryy.revnet.models.Device;
import dev.reveryy.revnet.models.Network;
import dev.reveryy.revnet.repository.DeviceRepository;
import dev.reveryy.revnet.repository.NetworkRepository;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public final class RevNet extends JavaPlugin {

    private NetworkManager networkManager;
    private DeviceManager deviceManager;
    private Connection connection;


    @Override
    public void onEnable() {

        // SQL e Repository
        connection = RevDataConnection.open(this, "revnet.db");
        DeviceRepository deviceRepository = RepositoryFactory.create(connection, DeviceRepository.class, Device.class);
        NetworkRepository networkRepository = RepositoryFactory.create(connection, NetworkRepository.class, Network.class);

        // Manager
        this.networkManager = new NetworkManager(deviceRepository, networkRepository);
        this.deviceManager = new DeviceManager(networkManager, deviceRepository);

        // Register Comandi
        RevNetCommand revNetCommand = new RevNetCommand(deviceManager, networkManager);
        revNetCommand.register();
    }
    @Override
    public void onDisable() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                getLogger().warning("Errore chiudendo la connessione: " + e.getMessage());
            }
        }
    }
}
