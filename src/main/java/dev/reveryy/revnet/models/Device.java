package dev.reveryy.revnet.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Device {
    private String nome;
    private String indirizzo;
    private String rete;

}
