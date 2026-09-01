package dev.reveryy.revnet.repository;

import dev.reveryy.revdata.Repository;
import dev.reveryy.revnet.models.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends Repository<Device, String> {
    Optional<Device> findByIndirizzo(String indirizzo);
    List<Device> findByRete(String rete);
    Optional<Device> findByNomeAndRete(String nome, String rete);
}
