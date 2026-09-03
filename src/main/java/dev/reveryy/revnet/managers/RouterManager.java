package dev.reveryy.revnet.managers;


import dev.reveryy.revnet.enums.NetError;
import dev.reveryy.revnet.models.Network;
import dev.reveryy.revnet.models.Result;
import dev.reveryy.revnet.models.Router;
import dev.reveryy.revnet.repository.RouterRepository;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RouterManager {
    private DeviceManager deviceManager;
    private NetworkManager networkManager;
    private RouterRepository routerRepository;

    public RouterManager(NetworkManager networkManager, DeviceManager deviceManager, RouterRepository routerRepository) {
        this.networkManager = networkManager;
        this.deviceManager = deviceManager;
        this.routerRepository = routerRepository;
    }

    public NetError crea(String nome) {
        Optional<Router> existing = routerRepository.findByNome(nome);

        if (existing.isPresent()) return NetError.ROUTER_CONFLICT;

        routerRepository.save(existing.get());
        return NetError.OK;
    }

    public NetError elimina(String nome) {
        Optional<Router> existing = routerRepository.findByNome(nome);

        if (existing.isEmpty()) return NetError.ROUTER_NOT_FOUND;

        routerRepository.delete(existing.get());
        return NetError.OK;
    }

    public NetError aggiungi(String nomeRouter, String indirizzo, int cidr) {
        Optional<Router> routerOpt = routerRepository.findByNome(nomeRouter);
        if (routerOpt.isEmpty()) return NetError.ROUTER_NOT_FOUND;

        Router router = routerOpt.get();

        if (!Network.isValidIPv4(indirizzo)) return NetError.INVALID_IP;
        if (!Network.isValidCidr(cidr)) return NetError.INVALID_CIDR;

        IPAddress address = new IPAddressString(indirizzo)
                .getAddress()
                .toIPv4()
                .toPrefixBlock(cidr);

        List<Network> networks = networkManager.listaNetwork();

        Optional<Network> found = networks.stream()
                .filter(n -> n.getNet().withoutPrefixLength().equals(address.withoutPrefixLength()))
                .filter(n -> Objects.equals(n.getNet().getPrefixLength(), address.getPrefixLength()))
                .findFirst();
        if (found.isPresent()) return NetError.NET_CONFLICT;

        Network net = found.get();

        router.getNetworks().add(net);
        routerRepository.save(router);
        return NetError.OK;
    }

    public NetError rimuovi(String nomeRouter, String indirizzo, int cidr) {
        Optional<Router> routerOpt = routerRepository.findByNome(nomeRouter);
        if (routerOpt.isEmpty()) return NetError.ROUTER_NOT_FOUND;

        Router router = routerOpt.get();

        if (!Network.isValidIPv4(indirizzo)) return NetError.INVALID_IP;
        if (!Network.isValidCidr(cidr)) return NetError.INVALID_CIDR;

        IPAddress address = new IPAddressString(indirizzo)
                .getAddress()
                .toIPv4()
                .toPrefixBlock(cidr);

        List<Network> networks = router.getNetworks();

        Optional<Network> found = networks.stream()
                .filter(n -> n.getNet().withoutPrefixLength().equals(address.withoutPrefixLength()))
                .filter(n -> Objects.equals(n.getNet().getPrefixLength(), address.getPrefixLength()))
                .findFirst();
        if (found.isEmpty()) return NetError.NET_NOT_FOUND;

        Network net = found.get();

        router.getNetworks().remove(net);
        routerRepository.save(router);
        return NetError.OK;
    }

    public List<Router> listaRouter() {
        return routerRepository.findAll();
    }


}
