package dev.reveryy.revnet.repository;

import dev.reveryy.revdata.Repository;
import dev.reveryy.revnet.models.Device;
import dev.reveryy.revnet.models.Network;

public interface NetworkRepository extends Repository<Network, String> {
    int deleteByNome(String nome);
}
