package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.gateway.GatewayPagamentoInternoAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GatewayPagamentoInternoTest {

    @Test
    void deveAprovarCobrancaViaBoleto() {
        // Agora instanciamos o Adapter que criamos
        GatewayPagamentoInternoAdapter adapter = new GatewayPagamentoInternoAdapter();
        
        // Criamos o objeto Pedido, pois o Adapter espera o objeto completo
        Pedido pedido = new Pedido("PED-001", "Joao Silva", "Item de Teste", 100.0);
        
        // Chamamos o método padronizado "processar"
        ResultadoCobranca resultado = adapter.processar(pedido, 100.0, FormaPagamento.BOLETO);

        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(100.0, resultado.getValorCobrado());
        assertEquals(FormaPagamento.BOLETO, resultado.getFormaPagamento());
        assertNotNull(resultado.getReferencia());
        assertTrue(resultado.getReferencia().startsWith("INT-"));
    }

    @Test
    void deveAprovarCobrancaViaPix() {
        GatewayPagamentoInternoAdapter adapter = new GatewayPagamentoInternoAdapter();
        Pedido pedido = new Pedido("PED-002", "Maria Santos", "Item de Teste", 250.0);
        
        ResultadoCobranca resultado = adapter.processar(pedido, 250.0, FormaPagamento.PIX);

        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(FormaPagamento.PIX, resultado.getFormaPagamento());
        assertEquals("PED-002", resultado.getPedidoId());
    }
}