package dev.reveryy.revnet;

import dev.reveryy.revconfig.Comment;
import dev.reveryy.revconfig.Config;

import java.util.List;

@Config("test.yml")
public class TestConfig {
    @Comment("Nome che il server mostra ai giocatori")
    public String nomeServer = "Neos Arena";

    public int retiMassime = 50;

    //Lista ez
    public List<String> domainVietati = List.of("esempio-vietato.it", "altro.vietato.it", "palle-grosse.it");

    // Enum
    public AlgoritmoCifratura algoritmo = AlgoritmoCifratura.AES256;

    public enum AlgoritmoCifratura {
        AES128,
        AES256,
        CHACHA20
    }

    public VpnSetting vpn = new VpnSetting();
}
