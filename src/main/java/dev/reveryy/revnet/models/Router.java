package dev.reveryy.revnet.models;

import dev.reveryy.revdata.GeneratedValue;
import dev.reveryy.revdata.Id;
import dev.reveryy.revdata.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Table("routers")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Router {
    @Id
    @GeneratedValue
    private Long id;
    private String nome;
    private List<Network> networks;

    public boolean contains(Network network) {
        return networks.contains(network);
    }
}
