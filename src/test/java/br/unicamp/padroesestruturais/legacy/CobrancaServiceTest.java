package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.service.CobrancaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CobrancaServiceTest {

    private CobrancaService service;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        service = new CobrancaService();
        pedido = new Pedido("PED-001", "Joao Silva", "Notebook Dell XPS 15", 1000.0);
    }

    @Test
    void deveCobrarViaBoletoSemAjustes() {
        ResultadoCobranca resultado = service.cobrar(pedido, FormaPagamento.BOLETO, false, false, false, false);

        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(1000.0, resultado.getValorCobrado(), 0.001);
        assertEquals(FormaPagamento.BOLETO, resultado.getFormaPagamento());
    }

    @Test
    void deveCobrarViaPixSemAjustes() {
        ResultadoCobranca resultado = service.cobrar(pedido, FormaPagamento.PIX, false, false, false, false);

        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(FormaPagamento.PIX, resultado.getFormaPagamento());
    }

    @Test
    void deveCobrarViaCartaoCreditoSemAjustes() {
        ResultadoCobranca resultado = service.cobrar(pedido, FormaPagamento.CARTAO_CREDITO, false, false, false, false);

        assertEquals("APROVADA", resultado.getStatus());
        assertNotNull(resultado.getReferencia());
        assertTrue(resultado.getReferencia().startsWith("PSEC-"));
    }

    @Test
    void deveRecusarCartaoCreditoParaValorAcimaDoLimite() {
        Pedido pedidoCaro = new Pedido("PED-003", "Construtora ABC Ltda", "Servidor", 15000.0);

        ResultadoCobranca resultado = service.cobrar(pedidoCaro, FormaPagamento.CARTAO_CREDITO, false, false, false, false);

        assertEquals("RECUSADA", resultado.getStatus());
    }

    @Test
    void deveLancarExcecaoParaFormaDePagamentoNaoSuportada() {
        assertThrows(IllegalArgumentException.class,
                () -> service.cobrar(pedido, null, false, false, false, false));
    }

    @Test
    void naoAplicarNenhumAjusteMantemValorBase() {
        double valor = service.calcularValorFinal(1000.0, false, false, false, false);
        assertEquals(1000.0, valor, 0.001);
    }

    @Test
    void deveAplicarDescontoDeFidelidade() {
        double valor = service.calcularValorFinal(1000.0, true, false, false, false);
        assertEquals(950.0, valor, 0.001);
    }

    @Test
    void deveAplicarJurosDeParcelamento() {
        double valor = service.calcularValorFinal(1000.0, false, true, false, false);
        assertEquals(1029.9, valor, 0.001);
    }

    @Test
    void deveAplicarTaxaInternacional() {
        double valor = service.calcularValorFinal(1000.0, false, false, true, false);
        assertEquals(1050.0, valor, 0.001);
    }

    @Test
    void deveAplicarSeguro() {
        double valor = service.calcularValorFinal(1000.0, false, false, false, true);
        assertEquals(1004.90, valor, 0.001);
    }

    @Test
    void deveAplicarTodosOsAjustesNaOrdemDefinida() {
        double valor = service.calcularValorFinal(1000.0, true, true, true, true);

        double esperado = 1000.0;
        esperado = esperado - (esperado * 0.05);
        esperado = esperado + (esperado * 0.0299);
        esperado = esperado + (esperado * 0.05);
        esperado = esperado + 4.90;

        assertEquals(esperado, valor, 0.001);
    }

    @Test
    void deveCobrarEmLoteParaTodosPedidos() {
        List<Pedido> pedidos = Arrays.asList(
                new Pedido("PED-001", "Joao Silva", "Notebook", 1000.0),
                new Pedido("PED-002", "Maria Santos", "Cadeira", 500.0)
        );

        List<ResultadoCobranca> resultados = service.cobrarEmLote(pedidos, FormaPagamento.PIX, false, false, false, false);

        assertEquals(2, resultados.size());
        for (ResultadoCobranca resultado : resultados) {
            assertEquals("APROVADA", resultado.getStatus());
        }
    }

    @Test
    void cobrancaEmLoteDeveAplicarAjustesATodosPedidos() {
        List<Pedido> pedidos = Arrays.asList(
                new Pedido("PED-001", "Joao Silva", "Notebook", 1000.0),
                new Pedido("PED-002", "Maria Santos", "Cadeira", 2000.0)
        );

        List<ResultadoCobranca> resultados = service.cobrarEmLote(pedidos, FormaPagamento.BOLETO, true, false, false, false);

        assertEquals(950.0, resultados.get(0).getValorCobrado(), 0.001);
        assertEquals(1900.0, resultados.get(1).getValorCobrado(), 0.001);
    }
}
