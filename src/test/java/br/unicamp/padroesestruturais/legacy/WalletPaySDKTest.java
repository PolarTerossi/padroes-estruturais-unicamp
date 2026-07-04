package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.gateway.WalletPayAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalletPaySDKTest {

    @Test
    void deveConfirmarCobrancaDentroDoLimite() {
        WalletPayAdapter adapter = new WalletPayAdapter();
        // 500.0 double será convertido para 50000 centavos internamente
        Pedido pedido = new Pedido("PED-001", "Joao Silva", "Item", 500.0);

        ResultadoCobranca resultado = adapter.processar(pedido, 500.0, FormaPagamento.CARTEIRA_DIGITAL);

        assertEquals("APROVADA", resultado.getStatus());
        assertNotNull(resultado.getReferencia());
        assertTrue(resultado.getReferencia().startsWith("WPAY-"));
    }

    @Test
    void deveRecusarCobrancaAcimaDoLimite() {
        WalletPayAdapter adapter = new WalletPayAdapter();
        // 15000.0 double será convertido para 1.500.000 centavos, o que ultrapassa o limite da biblioteca
        Pedido pedido = new Pedido("PED-003", "Construtora ABC Ltda", "Item", 15000.0);

        ResultadoCobranca resultado = adapter.processar(pedido, 15000.0, FormaPagamento.CARTEIRA_DIGITAL);

        assertEquals("RECUSADA", resultado.getStatus());
    }

    @Test
    void deveFalharParaValorInvalido() {
        WalletPayAdapter adapter = new WalletPayAdapter();
        Pedido pedido = new Pedido("PED-004", "Cliente X", "Item", 0.0);

        ResultadoCobranca resultado = adapter.processar(pedido, 0.0, FormaPagamento.CARTEIRA_DIGITAL);

        assertEquals("RECUSADA", resultado.getStatus());
    }
}
