package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.externo.GatewayIndisponivelException;
import br.unicamp.padroesestruturais.legacy.externo.PaySecureGateway;
import br.unicamp.padroesestruturais.legacy.externo.TransacaoExterna;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaySecureGatewayTest {

    @Test
    void deveAprovarTransacaoDentroDoLimite() throws GatewayIndisponivelException {
        PaySecureGateway gateway = new PaySecureGateway();

        Map<String, Object> dados = new HashMap<>();
        dados.put("orderId", "PED-001");
        dados.put("customerName", "Joao Silva");
        dados.put("amount", 500.0);
        dados.put("currency", "BRL");

        TransacaoExterna transacao = gateway.processarTransacao(dados);

        assertEquals(200, transacao.getCodigoStatus());
        assertNotNull(transacao.getReferenciaExterna());
        assertTrue(transacao.getReferenciaExterna().startsWith("PSEC-"));
        assertEquals(500.0, transacao.getValorProcessado());
        assertEquals("BRL", transacao.getMoeda());
    }

    @Test
    void deveRecusarTransacaoAcimaDoLimite() throws GatewayIndisponivelException {
        PaySecureGateway gateway = new PaySecureGateway();

        Map<String, Object> dados = new HashMap<>();
        dados.put("orderId", "PED-003");
        dados.put("customerName", "Construtora ABC Ltda");
        dados.put("amount", 15000.0);
        dados.put("currency", "BRL");

        TransacaoExterna transacao = gateway.processarTransacao(dados);

        assertEquals(402, transacao.getCodigoStatus());
    }

    @Test
    void deveLancarExcecaoParaValorInvalido() {
        PaySecureGateway gateway = new PaySecureGateway();

        Map<String, Object> dados = new HashMap<>();
        dados.put("orderId", "PED-004");
        dados.put("amount", -10.0);

        assertThrows(GatewayIndisponivelException.class, () -> gateway.processarTransacao(dados));
    }

    @Test
    void deveLancarExcecaoParaDadosIncompletos() {
        PaySecureGateway gateway = new PaySecureGateway();
        Map<String, Object> dados = new HashMap<>();

        assertThrows(GatewayIndisponivelException.class, () -> gateway.processarTransacao(dados));
    }
}
