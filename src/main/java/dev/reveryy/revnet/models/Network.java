package dev.reveryy.revnet.models;

import dev.reveryy.revdata.*;
import dev.reveryy.revnet.enums.NetError;
import dev.reveryy.revnet.enums.Tipologia;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.format.standard.IPAddressDivision;
import inet.ipaddr.ipv4.IPv4Address;
import inet.ipaddr.ipv4.IPv4AddressNetwork;
import inet.ipaddr.ipv4.IPv4AddressStringParameters;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table("networks")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Network {
    @Id
    @GeneratedValue
    private Long id;
    IPAddress net;
    String nome;
    Tipologia tipologia;
    private Long parentId;

    public Network(String nome, String indirizzo, int cidr) {
        this.nome = nome;
        this.net = new IPAddressString(indirizzo)
                .getAddress()
                .toIPv4()
                .toPrefixBlock(cidr);
    }

    public static boolean isValidIPv4(String indirizzo) {
        IPAddressString addressString = new IPAddressString(indirizzo);

        if (addressString.getAddress() == null) return false;

        IPv4Address address = addressString.getAddress().toIPv4();

        return address != null;
    }

    public static boolean isValidCidr(int cidr) {
        return cidr < 32 && cidr > 0;
    }


    public boolean contains(IPAddress address) {
        return net.contains(address);
    }

    public boolean containsSubnet(IPAddress childSubnet) {
        return net.contains(childSubnet.getLower()) && net.contains(childSubnet.getUpper());
    }

    public boolean isUsable(IPAddress address) {
        IPAddress ip = address.withoutPrefixLength();

        return net.contains(ip)
                && !ip.equals(net.getLower())
                && !ip.equals(net.getUpper());
    }





}
