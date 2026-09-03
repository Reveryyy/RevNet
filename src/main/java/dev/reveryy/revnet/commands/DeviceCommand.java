package dev.reveryy.revnet.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.reveryy.revnet.enums.NetError;
import dev.reveryy.revnet.managers.DeviceManager;
import dev.reveryy.revnet.models.Device;

import java.util.List;

public class DeviceCommand {
    public static CommandAPICommand getDeviceCommand(DeviceManager deviceManager) {
        CommandAPICommand deviceCrea = new CommandAPICommand("crea")
                .withArguments(new StringArgument("nome"), new StringArgument("indirizzo"), new IntegerArgument("cidr", 1, 31))
                .executes((s, args) -> {
                    NetError rs = deviceManager.crea((String) args.get("nome"), (String) args.get("indirizzo"), (Integer) args.get("cidr"));
                    if (rs.equals(NetError.INVALID_IP)){
                        s.sendMessage("Errore: indirizzo non valido. (0-255.0-255.0-255.0-255)");
                        return;
                    }
                    if (rs.equals(NetError.INVALID_CIDR)) {
                        s.sendMessage("Errore: cidr non valido. (1-31)");
                        return;
                    }

                    if (rs.equals(NetError.IP_CONFLICT)) {
                        s.sendMessage("Errore: L'indirizzo IP " + (String) args.get("indirizzo") + "/" + (Integer) args.get("cidr")+ " già occupato.");
                        return;
                    }

                    if (rs.equals(NetError.NET_NOT_FOUND)) {
                        s.sendMessage("Errore: rete non esistente.");
                        return;
                    }

                    s.sendMessage("Dispositivo " + (String) args.get("nome") +
                            "IP: " + (String) args.get("indirizzo") + "/" + (Integer) args.get("cidr") +
                            " creato con successo.");
                });

        CommandAPICommand deviceElimina = new CommandAPICommand("elimina")
                .withArguments(new StringArgument("indirizzo")
                        .replaceSuggestions(ArgumentSuggestions.stringCollection(info ->
                                deviceManager.listaDevices().stream().map(d -> d.getAddress().withoutPrefixLength().toString()).toList())))
                .executes((s, args) -> {
                    NetError rs = deviceManager.elimina((String) args.get("indirizzo"));
                    if (rs.equals(NetError.INVALID_IP)){
                        s.sendMessage("Errore: indirizzo non valido. (0-255.0-255.0-255.0-255)");
                        return;
                    }

                    if (rs.equals(NetError.DEVICE_NOT_FOUND)) {
                        s.sendMessage("Errore: dispositivo " + (String) args.get("indirizzo") + " non trovato.");
                        return;
                    }

                    s.sendMessage("Dispositivo con IP" + (String) args.get("indirizzo") + " eliminato con successo.");
                });


        CommandAPICommand deviceLista = new CommandAPICommand("lista")
                .executes((s, args) -> {
                    List<Device> devices = deviceManager.listaDevices();
                    if (devices.isEmpty()) {
                        s.sendMessage("----------------------------------------");
                        s.sendMessage("\nNessun dispositivo trovato.");
                        s.sendMessage("\n----------------------------------------");
                        return;
                    }

                    s.sendMessage("----------------------------------------");
                    for (Device device : devices)
                        s.sendMessage("| " + device.getNome() +
                                " IP: " + device.getAddress() + "\n");
                    s.sendMessage("----------------------------------------");
                });

        return new CommandAPICommand("device")
                .withAliases("dispositivo")
                .withSubcommand(deviceCrea)
                .withSubcommand(deviceElimina)
                .withSubcommand(deviceLista);
    }
}
