package dev.reveryy.revnet.managers;

import dev.reveryy.revnet.enums.NetError;
import dev.reveryy.revnet.models.Device;
import dev.reveryy.revnet.models.Network;
import dev.reveryy.revnet.models.Result;
import dev.reveryy.revnet.repository.DeviceRepository;
import dev.reveryy.revnet.repository.NetworkRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class NetworkManager {
    private DeviceRepository deviceRepository;
    private NetworkRepository networkRepository;

    public NetworkManager(DeviceRepository deviceRepository, NetworkRepository networkRepository) {
        this.networkRepository = networkRepository;
        this.deviceRepository = deviceRepository;
    }


    public NetError crea(String nome, String indirzzamento) {
        if (networkRepository.findById(nome).isPresent()) {
            return NetError.NET_CONFLICT;
        }

        Network network = Network.builder()
                    .nome(nome)
                    .indirizzamento(indirzzamento)
                    .build();
        networkRepository.save(network);
        System.out.println("[RETE] Nuova rete creata: " + nome + " Ind: " + indirzzamento);
        return NetError.OK;
    }

    public NetError elimina(String nome) {
        for (Device device : deviceRepository.findByRete(nome)) {device.setRete(null); deviceRepository.save(device); }
        return networkRepository.deleteByNome(nome) > 0 ? NetError.OK : NetError.NET_NOT_FOUND;
    }

    public Optional<Network> cerca(String nome) {
        return networkRepository.findById(nome);
    }

    public List<Network> listaNetwork() {
        return networkRepository.findAll();
    }



}
