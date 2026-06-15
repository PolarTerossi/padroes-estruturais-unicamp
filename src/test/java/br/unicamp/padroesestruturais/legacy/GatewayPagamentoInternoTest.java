package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.gateway.GatewayPagamentoInterno;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GatewayPagamentoInternoTest {

    @Test
    void deveAprovarCobrancaViaBoleto() {
        GatewayPagamentoInterno gateway = new GatewayPagamentoInterno();
        ResultadoCobranca resultado = gateway.cobrar("PED-001", "Joao Silva", 100.0, FormaPagamento.BOLETO);

        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(100.0, resultado.getValorCobrado());
        assertEquals(FormaPagamento.BOLETO, resultado.getFormaPagamento());
        assertNotNull(resultado.getReferencia());
        assertTrue(resultado.getReferencia().startsWith("INT-"));
    }

    @Test
    void deveAprovarCobrancaViaPix() {
        GatewayPagamentoInterno gateway = new GatewayPagamentoInterno();
        ResultadoCobranca resultado = gateway.cobrar("PED-002", "Maria Santos", 250.0, FormaPagamento.PIX);

        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(FormaPagamento.PIX, resultado.getFormaPagamento());
        assertEquals("PED-002", resultado.getPedidoId());
    }
}
