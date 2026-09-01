package dev.reveryy.revnet.models;

import dev.reveryy.revdata.Id;
import dev.reveryy.revdata.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("devices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    @Id
    private String nome;
    private String indirizzo;
    private String rete;

}
