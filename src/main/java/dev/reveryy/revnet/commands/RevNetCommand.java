package dev.reveryy.revnet.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.ListArgumentBuilder;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.reveryy.revnet.enums.NetError;
import dev.reveryy.revnet.managers.DeviceManager;
import dev.reveryy.revnet.managers.NetworkManager;
import dev.reveryy.revnet.models.Device;
import dev.reveryy.revnet.models.Network;
import org.bukkit.entity.Player;

import java.util.List;

public class RevNetCommand {
    private final DeviceManager deviceManager;
    private final NetworkManager networkManager;

    public RevNetCommand(DeviceManager deviceManager, NetworkManager networkManager) {
        this.deviceManager = deviceManager;
        this.networkManager = networkManager;
    }

    public void register() {

        // Sezione device
        CommandAPICommand deviceCrea = new CommandAPICommand("crea")
                .withArguments(new StringArgument("nome"), new StringArgument("indirizzo"))
                .executes((s, args) -> {
                    NetError rs = deviceManager.crea((String) args.get("nome"), (String) args.get("indirizzo"));
                    if (rs.equals(NetError.DEVICE_CONFLICT)) {
                        s.sendMessage("Errore: il dispositivo " + (String) args.get("nome") + " e' già esistente.");
                        return;
                    }

                    s.sendMessage("Dispositivo " + (String) args.get("nome") + " creato con successo.");
                });

        CommandAPICommand deviceElimina = new CommandAPICommand("elimina")
                .withArguments(new StringArgument("nome")
                        .replaceSuggestions(ArgumentSuggestions.stringCollection(info ->
                                deviceManager.listaDevices().stream().map(Device::getNome).toList())))
                .executes((s, args) -> {
                   NetError rs = deviceManager.elimina((String) args.get("nome"));
                   if (rs.equals(NetError.DEVICE_NOT_FOUND)) {
                       s.sendMessage("Errore: dispositivo " + (String) args.get("nome") + " non esistente.");
                       return;
                   }

                   s.sendMessage("Dispositivo " + (String) args.get("nome") + " eliminato con successo.");
                });

        CommandAPICommand deviceLista = new CommandAPICommand("lista")
                .executes((s, args) -> {
                    List<Device> devices = deviceManager.listaDevices();
                    if (devices.isEmpty()) {
                        s.sendMessage("----------------------------------------");
                        s.sendMessage("\nNessun dispositivo trovato.\n");
                        s.sendMessage("----------------------------------------");
                        return;
                    }

                    s.sendMessage("----------------------------------------");
                    for (Device device : devices)
                        s.sendMessage("| " + device.getNome() +
                                ": " + device.getIndirizzo() +
                                " Net: " +
                                (device.getRete() == null ? "non collegata." : device.getRete()) + "\n");
                    s.sendMessage("----------------------------------------");
                });

        CommandAPICommand devicePing = new CommandAPICommand("ping")
                .withArguments(new ListArgumentBuilder<String>("devices")
                        .withList(
                                deviceManager.listaDevices().stream().map(Device::getNome).toList()
                        ).withStringMapper().buildGreedy())
                .executes((s, args) -> {
                    NetError rs = deviceManager.pingDevices((List<String>) args.get("devices"));

                    if (rs.equals(NetError.DEVICE_NOT_FOUND)) {
                        s.sendMessage("Errore: uno o più dispostivi non trovati.");
                        return;
                    }

                    if (rs.equals(NetError.DEVICE_PING_FAILURE)) {
                        s.sendMessage("Ping fallito! Dispositivi su reti diverse.");
                        return;
                    }

                    s.sendMessage("Ping completato! Tutti i dispositivi sulla stessa rete.");
                });
        CommandAPICommand deviceCommand = new CommandAPICommand("device")
                .withAliases("dispositivo")
                .withSubcommand(deviceCrea)
                .withSubcommand(deviceElimina)
                .withSubcommand(deviceLista);


        // Sezione Network

        CommandAPICommand networkCrea = new CommandAPICommand("crea")
                .withArguments(new StringArgument("nome"), new StringArgument("indirizzamento"))
                .executes((s, args) -> {
                    NetError rs = networkManager.crea((String) args.get("nome"), (String) args.get("indirizzamento"));
                    if (rs.equals(NetError.NET_CONFLICT)) {
                        s.sendMessage("Errore: rete " + (String) args.get("nome") + " già esistente.");
                        return;
                    }

                    s.sendMessage("Rete " + (String) args.get("nome") + "  IPs: " + (String) args.get("indirizzamento") + " creata con successo.");
                });

        CommandAPICommand networkElimina = new CommandAPICommand("elimina")
                .withArguments(new StringArgument("nome").replaceSuggestions(
                        ArgumentSuggestions.stringCollection(info ->
                                networkManager.listaNetwork().stream().map(Network::getNome).toList()
                        )
                ))
                .executes((s, args) -> {
                   NetError rs = networkManager.elimina((String) args.get("nome"));
                   if (rs.equals(NetError.NET_NOT_FOUND)) {
                       s.sendMessage("Errore: rete " + (String) args.get("nome") + " non esistente.");
                       return;
                   }

                   s.sendMessage("Rete " + (String) args.get("nome") + " eliminata con successo.");
                });

        CommandAPICommand networkLista = new CommandAPICommand("lista")
                .executes((s, args) -> {
                    List<Network> networks = networkManager.listaNetwork();
                    if (networks.isEmpty()) {
                        s.sendMessage("----------------------------------------");
                        s.sendMessage("\nNessuna rete trovata.\n\n");
                        s.sendMessage("----------------------------------------");
                        return;
                    }

                    s.sendMessage("----------------------------------------");
                    for (Network network : networks)
                        s.sendMessage("\n| " + network.getNome() +
                                ": " + network.getIndirizzamento() +
                                " N. Disp: " + deviceManager.listaDevice(network.getNome()).stream().count() + ".\n\n");
                    s.sendMessage("----------------------------------------");
                });


        CommandAPICommand networkCommand = new CommandAPICommand("network")
                .withAliases("rete")
                .withSubcommand(networkCrea)
                .withSubcommand(networkElimina)
                .withSubcommand(networkLista);


        // Comando Principale
        new CommandAPICommand("revnet")
                .withAliases("rv", "net")
                .withSubcommand(deviceCommand)
                .withSubcommand(networkCommand)
                .register();
    }
}
