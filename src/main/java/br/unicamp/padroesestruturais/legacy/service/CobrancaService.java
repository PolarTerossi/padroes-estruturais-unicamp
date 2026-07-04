package br.unicamp.padroesestruturais.legacy.service;

import br.unicamp.padroesestruturais.legacy.decorator.*;
import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.gateway.GatewayPagamento;
import br.unicamp.padroesestruturais.legacy.gateway.GatewayPagamentoInternoAdapter;
import br.unicamp.padroesestruturais.legacy.gateway.PaySecureGatewayAdapter;
import br.unicamp.padroesestruturais.legacy.gateway.WalletPayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CobrancaService {

    private final Map<FormaPagamento, GatewayPagamento> gateways;

    public CobrancaService() {
        this.gateways = new HashMap<>();
        
        // Registro dos adaptadores de pagamento (Design Pattern: Adapter)
        GatewayPagamento internoAdapter = new GatewayPagamentoInternoAdapter();
        this.gateways.put(FormaPagamento.BOLETO, internoAdapter);
        this.gateways.put(FormaPagamento.PIX, internoAdapter);
        this.gateways.put(FormaPagamento.CARTAO_CREDITO, new PaySecureGatewayAdapter());
        this.gateways.put(FormaPagamento.CARTEIRA_DIGITAL, new WalletPayAdapter());
    }

    /**
     * Efetua a cobrança de um único pedido.
     * A cadeia do Decorator é montada dinamicamente a partir da LISTA de ajustes
     * escolhidos, na ordem em que aparecem — nenhum parâmetro booleano é usado,
     * e novos ajustes não exigem alterar esta assinatura.
     */
    public ResultadoCobranca cobrar(Pedido pedido, FormaPagamento forma, List<TipoAjuste> ajustes) {

        CalculadorValor calculador = montarCadeiaDeAjustes(pedido, ajustes);
        double valorFinal = calculador.calcular();

        return processarPagamento(pedido, valorFinal, forma);
    }

    /**
     * Efetua a cobrança em lote de todos os pedidos cadastrados na Main.
     */
    public List<ResultadoCobranca> cobrarEmLote(List<Pedido> pedidos, FormaPagamento forma, List<TipoAjuste> ajustes) {
        List<ResultadoCobranca> resultados = new ArrayList<>();

        // Reaproveita a lógica de cobrança individual para cada item da lista
        for (Pedido pedido : pedidos) {
            resultados.add(cobrar(pedido, forma, ajustes));
        }

        return resultados;
    }

    /**
     * Único ponto de montagem da cadeia de Decorators. Percorre a lista de
     * ajustes solicitados, na ordem informada, envelopando o calculador a
     * cada passo. Adicionar um novo ajuste = adicionar uma constante em
     * TipoAjuste + um "case" aqui, sem tocar em cobrar/cobrarEmLote.
     */
    private CalculadorValor montarCadeiaDeAjustes(Pedido pedido, List<TipoAjuste> ajustes) {
        CalculadorValor calculador = new CalculadorValorBase(pedido.getValorBase());

        for (TipoAjuste ajuste : ajustes) {
            calculador = switch (ajuste) {
                case DESCONTO_FIDELIDADE -> new DescontoFidelidadeDecorator(calculador);
                case JUROS_PARCELAMENTO -> new JurosParcelamentoDecorator(calculador);
                case TAXA_INTERNACIONAL -> new TaxaInternacionalDecorator(calculador);
                case SEGURO -> new SeguroDecorator(calculador);
                case TAXA_ANTECIPACAO_RECEBIVEIS -> new TaxaAntecipacaoDecorator(calculador);
                case TAXA_EMISSAO_NF -> new TaxaEmissaoNfDecorator(calculador);
            };
        }

        return calculador;
    }
    
    /**
     * Método auxiliar para centralizar o redirecionamento ao Adapter correto.
     */
    private ResultadoCobranca processarPagamento(Pedido pedido, double valorFinal, FormaPagamento forma) {
        GatewayPagamento gateway = gateways.get(forma);
        if (gateway == null) {
            throw new IllegalArgumentException("Forma de pagamento nao suportada: " + forma);
        }
        return gateway.processar(pedido, valorFinal, forma);
    }
}