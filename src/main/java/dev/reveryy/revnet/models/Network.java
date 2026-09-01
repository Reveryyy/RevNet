package dev.reveryy.revnet.models;

import dev.reveryy.revdata.Id;
import dev.reveryy.revdata.Table;
import dev.reveryy.revdata.Transient;
import dev.reveryy.revnet.enums.Tipologia;
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
    String nome;
    String indirizzamento;
    Tipologia tipologia;

}
