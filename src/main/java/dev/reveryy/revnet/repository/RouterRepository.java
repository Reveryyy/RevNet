package dev.reveryy.revnet.repository;

import dev.reveryy.revdata.Repository;
import dev.reveryy.revnet.models.Router;

import java.util.Optional;

public interface RouterRepository extends Repository<Router, Long> {
    Optional<Router> findByNome(String nome);
}
