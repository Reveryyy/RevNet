package dev.reveryy.revnet.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.reveryy.revnet.managers.DeviceManager;
import dev.reveryy.revnet.managers.NetworkManager;
import dev.reveryy.revnet.managers.RouterManager;


public class RevNetCommand {
    private final DeviceManager deviceManager;
    private final NetworkManager networkManager;
    private final RouterManager routerManager;

    public RevNetCommand(DeviceManager deviceManager,
                         NetworkManager networkManager,
                         RouterManager routerManager) {
        this.deviceManager = deviceManager;
        this.networkManager = networkManager;
        this.routerManager = routerManager;
    }

    public void register() {

        // Sezione device
        CommandAPICommand deviceCommand = DeviceCommand.getDeviceCommand(deviceManager);


        // Sezione Network
        CommandAPICommand networkCommand = NetworkCommand.getNetworkCommand(networkManager, deviceManager);

        // Sezione Router
        CommandAPICommand routerCommand = RouterCommand.getRouterCommand(routerManager);


        // Comando Principale
        new CommandAPICommand("revnet")
                .withAliases("rv", "net")
                .withSubcommand(deviceCommand)
                .withSubcommand(networkCommand)
                .register();
    }



}
