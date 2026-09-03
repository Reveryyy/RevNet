package dev.reveryy.revnet.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.reveryy.revnet.enums.NetError;
import dev.reveryy.revnet.managers.DeviceManager;
import dev.reveryy.revnet.managers.NetworkManager;
import dev.reveryy.revnet.models.Network;
import org.bukkit.command.CommandSender;

import java.util.List;

public class NetworkCommand {

    public static CommandAPICommand getNetworkCommand(NetworkManager networkManager, DeviceManager deviceManager) {
        CommandAPICommand networkCrea = new CommandAPICommand("crea")
                .withArguments(new StringArgument("nome"), new StringArgument("indirizzo"), new IntegerArgument("cidr", 1, 31))
                .executes((s, args) -> {
                    NetError rs = networkManager.crea((String) args.get("nome"), (String) args.get("indirizzo"), (Integer) args.get("cidr"));
                    if (rs.equals(NetError.INVALID_IP)){
                        s.sendMessage("Errore: indirizzo non valido. (0-255.0-255.0-255.0-255)");
                        return;
                    }
                    if (rs.equals(NetError.INVALID_CIDR)) {
                        s.sendMessage("Errore: cidr non valido. (1-31)");
                        return;
                    }

                    if (rs.equals(NetError.IP_CONFLICT)) {
                        s.sendMessage("Errore: ip  " + (String) args.get("indirizzo") + "/" + (Integer) args.get("cidr") + " già occupato.");
                        return;
                    }

                    if (rs.equals(NetError.PARENT_NOT_FOUND)) {
                        s.sendMessage("Errore: rete padre con id: " + (Long) args.get("parentId") + " non trovata.");
                        return;
                    }

                    if (rs.equals(NetError.SUBNET_OUTSIDE_PARENT)) {
                        s.sendMessage("Errore: la rete " + (String) args.get("indirizzo") + "/" + (Integer) args.get("cidr") + " deve avere cidr maggiore della rete padre.");
                        return;
                    }

                    if (rs.equals(NetError.SUBNET_CONFLICT)) {
                        s.sendMessage("Errore: la subnet " + (String) args.get("indirizzo") + "/" + (Integer) args.get("cidr") + " è già esistente.");
                        return;
                    }

                    if (rs.equals(NetError.NET_CONFLICT)) {
                        s.sendMessage("Errore: la rete " + (String) args.get("indirizzo") + "/" + (Integer) args.get("cidr") + " esiste già.");
                        return;
                    }

                    s.sendMessage("Rete " + (String) args.get("nome") +
                            "  IP: " + (String) args.get("indirizzo") + "/" + (Integer) args.get("cidr") + " creata con successo.");
                });

        CommandAPICommand networkElimina = new CommandAPICommand("elimina")
                .withArguments(new StringArgument("indirizzo").replaceSuggestions(
                        ArgumentSuggestions.stringCollection(info ->
                                networkManager.listaNetwork().stream().map(n -> n.getNet().getLower().withoutPrefixLength().toString()).toList()
                        )
                ), new IntegerArgument("cidr", 1, 31))
                .executes((s, args) -> {
                    NetError rs = networkManager.elimina((String) args.get("indirizzo"), (Integer) args.get("cidr"));
                    if (rs.equals(NetError.INVALID_IP)){
                        s.sendMessage("Errore: indirizzo non valido. (0-255.0-255.0-255.0-255)");
                        return;
                    }
                    if (rs.equals(NetError.INVALID_CIDR)) {
                        s.sendMessage("Errore: cidr non valido. (1-31)");
                        return;
                    }

                    if (rs.equals(NetError.NET_NOT_FOUND)) {
                        s.sendMessage("Errore: la rete con IP:" + (String) args.get("indirizzo") + "/" + (Integer) args.get("cidr") + " non esistente.");
                        return;
                    }

                    s.sendMessage("Rete con IP: " + (String) args.get("indirizzo") + " eliminata con successo.");
                });

        CommandAPICommand networkLista = new CommandAPICommand("lista")
                .executes((s, args) -> {
                    List<Network> roots = networkManager.listaRoot();
                    if (roots.isEmpty()) {
                        s.sendMessage("----------------------------------------");
                        s.sendMessage("\nNessuna rete trovata.");
                        s.sendMessage("\n----------------------------------------");
                        return;
                    }

                    s.sendMessage("----------------------------------------");
                    for (Network network : roots) {
                        stampaNetwork(s, network, 0, networkManager, deviceManager);
                    }

                    s.sendMessage("\n----------------------------------------");
                });


        return new CommandAPICommand("network")
                .withAliases("rete")
                .withSubcommand(networkCrea)
                .withSubcommand(networkElimina)
                .withSubcommand(networkLista);
    }

    private static void stampaNetwork(CommandSender s, Network network, int livello, NetworkManager networkManager, DeviceManager deviceManager) {
        String indent = livello == 0 ? "" : "   ".repeat(livello);

        s.sendMessage("\n" + indent + "| " + network.getNome()
                + " IPs: " + network.getNet()
                + " Disp: " + deviceManager.listaDevice(network).size());

        for (Network child : networkManager.listaFigli(network.getId())) {
            stampaNetwork(s, child, livello + 1, networkManager, deviceManager);
        }
    }
}
