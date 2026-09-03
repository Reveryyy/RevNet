package dev.reveryy.revnet.managers;

import dev.reveryy.revnet.enums.NetError;
import dev.reveryy.revnet.models.Device;
import dev.reveryy.revnet.models.Network;
import dev.reveryy.revnet.models.Result;
import dev.reveryy.revnet.repository.DeviceRepository;
import dev.reveryy.revnet.repository.NetworkRepository;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

import java.beans.SimpleBeanInfo;
import java.util.*;

public class NetworkManager {
    private DeviceRepository deviceRepository;
    private NetworkRepository networkRepository;

    public NetworkManager(DeviceRepository deviceRepository, NetworkRepository networkRepository) {
        this.networkRepository = networkRepository;
        this.deviceRepository = deviceRepository;
    }


    public NetError crea(String nome, String indirizzo, int cidr) {
        if (!Network.isValidIPv4(indirizzo)) return NetError.INVALID_IP;
        if (!Network.isValidCidr(cidr)) return NetError.INVALID_CIDR;

        IPAddress subnet = new IPAddressString(indirizzo)
                .getAddress()
                .toIPv4()
                .toPrefixBlock(cidr);

        List<Network> networks = networkRepository.findAll();

        // Controllo conflitto NET
        Optional<Network> existing = networks.stream()
                .filter(n -> n.getNet().withoutPrefixLength().equals(subnet.withoutPrefixLength()))
                .filter(n -> Objects.equals(n.getNet().getPrefixLength(), subnet.getPrefixLength()))
                .findFirst();

        if (existing.isPresent()) return NetError.NET_CONFLICT;

        // Rete padre
        Network parent = networks.stream()
                .filter(n -> n.containsSubnet(subnet))
                .max(Comparator.comparingInt(
                        n -> n.getNet().getPrefixLength()))
                .orElse(null);

        Long parentId = parent != null ? parent.getId() : null;

        List<Network> siblings = networks.stream()
                .filter(n -> Objects.equals(n.getParentId(), parentId))
                .toList();

        if (siblings.stream().anyMatch(s -> overlaps(s.getNet(), subnet)))
            return NetError.SUBNET_CONFLICT;

        Network network = Network.builder()
                .nome(nome)
                .net(subnet)
                .parentId(parentId)
                .build();

        networkRepository.save(network);
        return NetError.OK;
    }

    private boolean overlaps(IPAddress a, IPAddress b) {
        return a.contains(b.getLower())
                || a.contains(b.getUpper())
                || b.contains(a.getLower())
                || b.contains(a.getUpper());
    }

    public NetError elimina(String indirizzo, int cidr) {
        if (!Network.isValidIPv4(indirizzo)) return NetError.INVALID_IP;
        if (!Network.isValidCidr(cidr)) return NetError.INVALID_CIDR;

        IPAddress address = new IPAddressString(indirizzo).getAddress().toIPv4().toPrefixBlock(cidr);

        Optional<Network> found = networkRepository.findAll().stream()
                .filter(n -> n.getNet().withoutPrefixLength().equals(address.withoutPrefixLength()))
                .filter(n -> Objects.equals(n.getNet().getPrefixLength(), address.getPrefixLength()))
                .findFirst();


        if (found.isEmpty()) return NetError.NET_NOT_FOUND;

        networkRepository.deleteById(found.get().getId());
        return NetError.OK;
    }

    public Result<Network> cerca(String indirizzo, int cidr) {
        if (!Network.isValidIPv4(indirizzo)) return Result.error(NetError.INVALID_IP);
        if (!Network.isValidCidr(cidr)) return Result.error(NetError.INVALID_CIDR);

        IPAddress address = new IPAddressString(indirizzo).getAddress().toIPv4().toPrefixBlock(cidr);
        Optional<Network> found = networkRepository.findAll().stream()
                .filter(n -> n.getNet().withoutPrefixLength().equals(address.withoutPrefixLength()))
                .filter(n -> Objects.equals(n.getNet().getPrefixLength(), address.getPrefixLength()))
                .findFirst();

        return found
                .map(Result::ok)
                .orElse(Result.error(NetError.NET_NOT_FOUND));
    }

    public List<Network> listaNetwork() {
        return networkRepository.findAll();
    }

    public List<Network> listaRoot() {
        return networkRepository.findAll().stream().filter(n -> n.getParentId() == null).toList();
    }

    public List<Network> listaFigli(Long parentId) {
        return networkRepository.findAll().stream().filter(n -> Objects.equals(n.getParentId(), parentId)).toList();
    }

}
