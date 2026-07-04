package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.gateway.PaySecureGatewayAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaySecureGatewayTest {

    @Test
    void deveAprovarTransacaoDentroDoLimite() {
        // Agora instanciamos o Adapter
        PaySecureGatewayAdapter adapter = new PaySecureGatewayAdapter();
        Pedido pedido = new Pedido("PED-001", "Joao Silva", "Item de Teste", 500.0);

        // O adapter recebe o Pedido e formata o HashMap internamente
        ResultadoCobranca resultado = adapter.processar(pedido, 500.0, FormaPagamento.CARTAO_CREDITO);

        assertEquals("APROVADA", resultado.getStatus());
        assertNotNull(resultado.getReferencia());
        assertTrue(resultado.getReferencia().startsWith("PSEC-"));
        assertEquals(500.0, resultado.getValorCobrado());
        assertEquals(FormaPagamento.CARTAO_CREDITO, resultado.getFormaPagamento());
    }

    @Test
    void deveRecusarTransacaoAcimaDoLimite() {
        PaySecureGatewayAdapter adapter = new PaySecureGatewayAdapter();
        Pedido pedido = new Pedido("PED-003", "Construtora ABC Ltda", "Servidor", 15000.0);

        ResultadoCobranca resultado = adapter.processar(pedido, 15000.0, FormaPagamento.CARTAO_CREDITO);

        // O código 402 da API externa é traduzido para "RECUSADA"
        assertEquals("RECUSADA", resultado.getStatus());
    }

    @Test
    void deveRecusarTransacaoParaValorInvalido() {
        PaySecureGatewayAdapter adapter = new PaySecureGatewayAdapter();
        Pedido pedido = new Pedido("PED-004", "Cliente", "Item", -10.0);

        // O adapter captura a GatewayIndisponivelException e protege o sistema retornando "RECUSADA"
        ResultadoCobranca resultado = adapter.processar(pedido, -10.0, FormaPagamento.CARTAO_CREDITO);

        assertEquals("RECUSADA", resultado.getStatus());
    }

    @Test
    void deveRecusarParaDadosIncompletos() {
        PaySecureGatewayAdapter adapter = new PaySecureGatewayAdapter();
        // Criamos um pedido com dados nulos para simular a falha de dados incompletos na API externa
        Pedido pedido = new Pedido(null, null, "Item", 100.0);

        ResultadoCobranca resultado = adapter.processar(pedido, 100.0, FormaPagamento.CARTAO_CREDITO);

        assertEquals("RECUSADA", resultado.getStatus());
    }
}