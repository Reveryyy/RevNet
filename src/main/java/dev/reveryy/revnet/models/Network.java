package dev.reveryy.revnet.models;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class Network {
    String nome;
    String indirizzamento;
    @Singular
    List<Device> devices;


    public static class NetworkBuilder {
        private List<Device> devices = new ArrayList<>();

        public NetworkBuilder device(Device device) {
            if (device == null) throw new IllegalArgumentException("Device nullo");

            device.setRete(nome);
            devices.add(device);

            return this;
        }
    }

}
