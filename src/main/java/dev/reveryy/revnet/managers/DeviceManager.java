package dev.reveryy.revnet.managers;

import dev.reveryy.revnet.enums.NetError;
import dev.reveryy.revnet.models.Device;
import dev.reveryy.revnet.models.Network;
import dev.reveryy.revnet.models.Result;
import dev.reveryy.revnet.repository.DeviceRepository;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

import java.util.*;
import java.util.stream.Collectors;

public class DeviceManager {
    private final NetworkManager networkManager;
    private final DeviceRepository deviceRepository;

    public DeviceManager(NetworkManager networkManager, DeviceRepository deviceRepository) {
        this.networkManager = networkManager;
        this.deviceRepository = deviceRepository;
    }

    public NetError crea(String nome, String indirizzo, int cidr) {
        if (!Network.isValidIPv4(indirizzo)) return NetError.INVALID_IP;
        if (!Network.isValidCidr(cidr)) return NetError.INVALID_CIDR;


        IPAddress address = new IPAddressString(indirizzo)
                .getAddress().toIPv4();

        Optional<Device> existing = deviceRepository.findAll().stream()
                .filter(d -> d.getAddress().equals(address))
                .findFirst();

        if (existing.isPresent()) return NetError.IP_CONFLICT;

        IPAddress subnet = address.toPrefixBlock(cidr);

        Optional<Network> network = networkManager.listaNetwork().stream()
                .filter(n -> n.getNet().withoutPrefixLength()
                        .equals(subnet.withoutPrefixLength()))
                .filter(n -> Objects.equals(n.getNet().getPrefixLength(), subnet.getPrefixLength()))
                .findFirst();

        if (network.isEmpty()) return NetError.NET_NOT_FOUND;
        if (!network.get().isUsable(address)) return NetError.INVALID_IP;

        Device device = Device.builder()
                .nome(nome)
                .address(address)
                .networkId(network.get().getId())
                .build();
        deviceRepository.save(device);
        return NetError.OK;
    }

    public NetError elimina(String indirizzo) {
        if (!Network.isValidIPv4(indirizzo)) return NetError.INVALID_IP;

        IPAddress address = new IPAddressString(indirizzo).getAddress().toIPv4();

        Optional<Device> found = deviceRepository.findAll().stream()
                .filter(d -> d.getAddress().getLower().withoutPrefixLength().equals(address.withoutPrefixLength()))
                .findFirst();

        if (found.isEmpty()) return NetError.DEVICE_NOT_FOUND;

        deviceRepository.deleteById(found.get().getId());
        return NetError.OK;
    }

    public Result<Device> cerca(String indirizzo) {
        if (!Network.isValidIPv4(indirizzo)) return Result.error(NetError.INVALID_IP);

        IPAddress address = new IPAddressString(indirizzo).getAddress().toIPv4();
        Optional<Device> found = deviceRepository.findAll().stream()
                .filter(d -> d.getAddress()
                        .getLower()
                        .withoutPrefixLength()
                        .equals(address.withoutPrefixLength()))
                .findFirst();


        return found
                .map(Result::ok)
                .orElse(Result.error(NetError.DEVICE_NOT_FOUND));
    }

    public List<Device> listaDevices() {
        return deviceRepository.findAll();
    }

    public List<Device> listaDevice(Network network) {
        return deviceRepository.findAll()
                .stream()
                .filter(device -> Objects.equals(device.getNetworkId(), network.getId()))
                .toList();
    }


}
