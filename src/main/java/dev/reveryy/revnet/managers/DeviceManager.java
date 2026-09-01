package dev.reveryy.revnet.managers;

import dev.reveryy.revnet.enums.NetError;
import dev.reveryy.revnet.models.Device;
import dev.reveryy.revnet.models.Result;
import dev.reveryy.revnet.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeviceManager {
    private final NetworkManager networkManager;
    private final DeviceRepository deviceRepository;

    public DeviceManager(NetworkManager networkManager, DeviceRepository deviceRepository) {
        this.networkManager = networkManager;
        this.deviceRepository = deviceRepository;
    }

    public NetError crea(String nome, String indirizzo) {
        if (deviceRepository.findById(nome).isPresent()) return NetError.DEVICE_CONFLICT;

        Device device = Device.builder()
                .nome(nome)
                .indirizzo(indirizzo)
                .build();
        deviceRepository.save(device);
        return NetError.OK;
    }

    public NetError elimina(String nome) {
        if (deviceRepository.findById(nome).isEmpty()) return NetError.DEVICE_NOT_FOUND;

        deviceRepository.deleteById(nome);
        return NetError.OK;
    }

    public Result<Device> cercaDevice(String nomeRete, String nomeDevice) {
        if (networkManager.cerca(nomeRete).isEmpty()) return Result.error(NetError.NET_NOT_FOUND);
        return deviceRepository.findById(nomeDevice)
                .map(Result::ok)
                .orElse(Result.error(NetError.NET_NOT_FOUND));
    }

    public List<Device> listaDevices() {
        return deviceRepository.findAll();
    }

    public List<Device> listaDevice(String rete) {
        return deviceRepository.findByRete(rete);
    }

    public NetError aggiungiRete(String nomeRete, Device device) {
        if (device.getRete() != null) return NetError.DEVICE_NET_CONFLICT;
        if (networkManager.cerca(nomeRete).isEmpty()) return NetError.NET_NOT_FOUND;

        if (deviceRepository.findByNomeAndRete(device.getNome(), nomeRete).isPresent()) return NetError.DEVICE_CONFLICT;
        device.setRete(nomeRete);
        deviceRepository.save(device);
        return NetError.OK;
    }

    public NetError rimuoviRete(Device device) {
        if (device.getRete() == null || device.getRete().isBlank()) return NetError.DEVICE_NET_NOT_FOUND;

        device.setRete(null);
        deviceRepository.save(device);
        return NetError.OK;
    }

    public NetError pingDevices(List<String> nomeDevices) {
        List<Device> devices = new ArrayList<>();

        for (String device : nomeDevices) {
            Optional<Device> deviceOpt = deviceRepository.findById(device);
            if (deviceOpt.isEmpty()) return NetError.DEVICE_NOT_FOUND;
            devices.add(deviceOpt.get());
        }

        String primaRete = devices.getFirst().getRete();
        if (!devices.stream()
                .allMatch(d -> d.getRete().equals(primaRete))) return NetError.DEVICE_PING_FAILURE;
        return NetError.DEVICE_PING_SUCCESS;
    }


}
