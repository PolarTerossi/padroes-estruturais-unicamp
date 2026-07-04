package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.decorator.*;
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
        // Passa false para todas as taxas e descontos
        ResultadoCobranca resultado = service.cobrar(pedido, FormaPagamento.BOLETO, List.of());

        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(1000.0, resultado.getValorCobrado(), 0.001);
        assertEquals(FormaPagamento.BOLETO, resultado.getFormaPagamento());
    }

    @Test
    void deveCobrarViaPixSemAjustes() {
        ResultadoCobranca resultado = service.cobrar(pedido, FormaPagamento.PIX, List.of());

        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(FormaPagamento.PIX, resultado.getFormaPagamento());
    }

    @Test
    void deveCobrarViaCartaoCreditoSemAjustes() {
        ResultadoCobranca resultado = service.cobrar(pedido, FormaPagamento.CARTAO_CREDITO, List.of());

        assertEquals("APROVADA", resultado.getStatus());
        assertNotNull(resultado.getReferencia());
        assertTrue(resultado.getReferencia().startsWith("PSEC-"));
    }

    @Test
    void deveRecusarCartaoCreditoParaValorAcimaDoLimite() {
        Pedido pedidoCaro = new Pedido("PED-003", "Construtora ABC Ltda", "Servidor", 15000.0);
        ResultadoCobranca resultado = service.cobrar(pedidoCaro, FormaPagamento.CARTAO_CREDITO, List.of());

        assertEquals("RECUSADA", resultado.getStatus());
    }

    @Test
    void deveLancarExcecaoParaFormaDePagamentoNaoSuportada() {
        assertThrows(IllegalArgumentException.class,
                () -> service.cobrar(pedido, null, List.of()));
    }

    @Test
    void naoAplicarNenhumAjusteMantemValorBase() {
        CalculadorValor calculo = new CalculadorValorBase(pedido.getValorBase()); // Pedido vale 1000.0
        double valor = calculo.calcular();
        
        assertEquals(1000.0, valor, 0.001);
    }

    @Test
    void deveAplicarDescontoDeFidelidade() {
        CalculadorValor calculo = new CalculadorValorBase(pedido.getValorBase());
        calculo = new DescontoFidelidadeDecorator(calculo);
        
        double valor = calculo.calcular();
        assertEquals(950.0, valor, 0.001);
    }

    @Test
    void deveAplicarJurosDeParcelamento() {
        CalculadorValor calculo = new CalculadorValorBase(pedido.getValorBase());
        calculo = new JurosParcelamentoDecorator(calculo);
        
        double valor = calculo.calcular();
        assertEquals(1029.9, valor, 0.001);
    }

    @Test
    void deveAplicarTaxaInternacional() {
        CalculadorValor calculo = new CalculadorValorBase(pedido.getValorBase());
        calculo = new TaxaInternacionalDecorator(calculo);
        
        double valor = calculo.calcular();
        assertEquals(1050.0, valor, 0.001);
    }

    @Test
    void deveAplicarSeguro() {
        CalculadorValor calculo = new CalculadorValorBase(pedido.getValorBase());
        calculo = new SeguroDecorator(calculo);
        
        double valor = calculo.calcular();
        assertEquals(1004.90, valor, 0.001);
    }

    @Test
    void deveAplicarTodosOsAjustesNaOrdemDefinida() {
        CalculadorValor calculo = new CalculadorValorBase(pedido.getValorBase());
        calculo = new DescontoFidelidadeDecorator(calculo);
        calculo = new JurosParcelamentoDecorator(calculo);
        calculo = new TaxaInternacionalDecorator(calculo);
        calculo = new SeguroDecorator(calculo);

        double valor = calculo.calcular();

        double esperado = 1000.0;
        esperado = esperado - (esperado * 0.05);
        esperado = esperado + (esperado * 0.0299);
        esperado = esperado + (esperado * 0.05);
        esperado = esperado + 4.90;

        assertEquals(esperado, valor, 0.001);
    }

    @Test
    void deveAplicarTaxaDeAntecipacaoDeRecebiveis() {
        CalculadorValor calculo = new CalculadorValorBase(pedido.getValorBase());
        calculo = new TaxaAntecipacaoDecorator(calculo);

        double valor = calculo.calcular();
        assertEquals(1015.0, valor, 0.001);
    }

    @Test
    void deveAplicarTaxaDeEmissaoDeNotaFiscal() {
        CalculadorValor calculo = new CalculadorValorBase(pedido.getValorBase());
        calculo = new TaxaEmissaoNfDecorator(calculo);

        double valor = calculo.calcular();
        assertEquals(1002.50, valor, 0.001);
    }

    @Test
    void deveGerarResultadosDiferentesConformeAOrdemDosDecorators() {
        // Desconto de fidelidade (5%) aplicado ANTES da taxa internacional (5%)
        CalculadorValor descontoPrimeiro = new CalculadorValorBase(1000.0);
        descontoPrimeiro = new DescontoFidelidadeDecorator(descontoPrimeiro);
        descontoPrimeiro = new TaxaInternacionalDecorator(descontoPrimeiro);

        // Taxa internacional (5%) aplicada ANTES do desconto de fidelidade (5%)
        CalculadorValor taxaPrimeiro = new CalculadorValorBase(1000.0);
        taxaPrimeiro = new TaxaInternacionalDecorator(taxaPrimeiro);
        taxaPrimeiro = new DescontoFidelidadeDecorator(taxaPrimeiro);

        // Ambos os caminhos partem do mesmo valor base e aplicam os mesmos
        // dois ajustes, mas em ordem inversa. Como os percentuais incidem
        // sobre o valor acumulado até aquele ponto da cadeia (e não sobre o
        // valor base original), o resultado final é diferente — o que prova
        // que a ordem dos decorators importa e é, de fato, respeitada.
        assertEquals(997.5, descontoPrimeiro.calcular(), 0.001);
        assertEquals(997.5, taxaPrimeiro.calcular(), 0.001);
        // Neste caso específico o valor final coincide (5% de -5% e vice-versa
        // são comutativos matematicamente), então validamos também com um
        // ajuste de valor fixo, onde a ordem realmente altera o resultado:
        CalculadorValor seguroDepoisDoDesconto = new CalculadorValorBase(1000.0);
        seguroDepoisDoDesconto = new DescontoFidelidadeDecorator(seguroDepoisDoDesconto);
        seguroDepoisDoDesconto = new SeguroDecorator(seguroDepoisDoDesconto);

        CalculadorValor descontoDepoisDoSeguro = new CalculadorValorBase(1000.0);
        descontoDepoisDoSeguro = new SeguroDecorator(descontoDepoisDoSeguro);
        descontoDepoisDoSeguro = new DescontoFidelidadeDecorator(descontoDepoisDoSeguro);

        assertEquals(954.90, seguroDepoisDoDesconto.calcular(), 0.001);
        assertEquals(954.655, descontoDepoisDoSeguro.calcular(), 0.001);
        assertNotEquals(seguroDepoisDoDesconto.calcular(), descontoDepoisDoSeguro.calcular());
    }

    @Test
    void cobrancaUnicaDeveCombinarMultiplosAjustesViaListaSemBooleanos() {
        List<TipoAjuste> ajustes = List.of(
                TipoAjuste.DESCONTO_FIDELIDADE,
                TipoAjuste.TAXA_ANTECIPACAO_RECEBIVEIS,
                TipoAjuste.TAXA_EMISSAO_NF
        );

        ResultadoCobranca resultado = service.cobrar(pedido, FormaPagamento.PIX, ajustes);

        double esperado = 1000.0;
        esperado = esperado - (esperado * 0.05);
        esperado = esperado + (esperado * 0.015);
        esperado = esperado + 2.50;

        assertEquals(esperado, resultado.getValorCobrado(), 0.001);
    }

    @Test
    void deveCobrarEmLoteParaTodosPedidos() {
        List<Pedido> pedidos = Arrays.asList(
                new Pedido("PED-001", "Joao Silva", "Notebook", 1000.0),
                new Pedido("PED-002", "Maria Santos", "Cadeira", 500.0)
        );

        // Chama o cobrarEmLote passando os parâmetros booleanos corretos
        List<ResultadoCobranca> resultados = service.cobrarEmLote(pedidos, FormaPagamento.PIX, List.of());

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

        // Ativa apenas o primeiro booleano (Desconto Fidelidade) como true
        List<ResultadoCobranca> resultados = service.cobrarEmLote(pedidos, FormaPagamento.BOLETO, List.of(TipoAjuste.DESCONTO_FIDELIDADE));

        assertEquals(950.0, resultados.get(0).getValorCobrado(), 0.001);
        assertEquals(1900.0, resultados.get(1).getValorCobrado(), 0.001);
    }
}