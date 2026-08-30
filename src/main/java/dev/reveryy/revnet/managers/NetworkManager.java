package dev.reveryy.revnet.managers;

import dev.reveryy.revnet.enums.NetError;
import dev.reveryy.revnet.models.Device;
import dev.reveryy.revnet.models.Network;
import dev.reveryy.revnet.models.Result;

import java.util.HashMap;
import java.util.Optional;

public class NetworkManager {
    HashMap<String, Network> reti = new HashMap<>();

    public NetError crea(String nome, String indirzzamento) {
        if (reti.containsKey(nome)) return NetError.NET_CONFLICT;
        Network network = Network.builder()
                    .nome(nome)
                    .indirizzamento(indirzzamento)
                    .build();
        reti.put(nome, network);
        System.out.println("[RETE] Nuova rete creata: " + nome + " Ind: " + indirzzamento);
        return NetError.OK;
    }

    public NetError elimina(String nome) {
        return Optional.ofNullable(reti.remove(nome))
                .map(rete -> {
                    System.out.println("[RETE] Rete " + rete.getNome() + " eliminata.");
                    return NetError.OK;
                }).orElse(NetError.NET_NOT_FOUND);
    }

    public Optional<Network> cerca(String nome) {
        return Optional.ofNullable(this.reti.get(nome));
    }

    public Result<Device> cercaDevice(String nomeRete, String nomeDevice) {
        return cerca(nomeRete)
                .map(rete -> rete.getDevices().stream()
                        .filter(d -> d.getNome().equalsIgnoreCase(nomeDevice))
                        .findFirst()
                        .<Result<Device>>map(Result::ok)
                        .orElse(Result.error(NetError.DEVICE_NOT_FOUND)))
                .orElse(Result.error(NetError.NET_NOT_FOUND));
    }

    public NetError aggiungiDevice(String nomeRete, String nomeDevice, String indirizzoDevice) {
        return cerca(nomeRete)
                .map(rete -> {
                    if (rete.getDevices().stream().anyMatch(d -> d.getNome().equalsIgnoreCase(nomeDevice))) return NetError.DEVICE_CONFLICT;

                    Device device = Device.builder()
                            .nome(nomeDevice)
                            .indirizzo(indirizzoDevice)
                            .rete(nomeRete)
                            .build();

                    rete.getDevices().add(device);
                    return NetError.OK;
                })
                .orElse(NetError.NET_NOT_FOUND);
    }

    public NetError rimuoviDevice(String nomeRete, String nomeDevice) {
        return cerca(nomeRete)
                .map(rete -> {

                    if (rete.getDevices().removeIf(d -> d.getNome().equalsIgnoreCase(nomeDevice))) return NetError.OK;
                    else return NetError.DEVICE_NOT_FOUND;
                })
                .orElse(NetError.NET_NOT_FOUND);
    }



}
