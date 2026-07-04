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
     * Monta a cadeia do Decorator dinamicamente com os booleans enviados pela Main.
     */
    public ResultadoCobranca cobrar(Pedido pedido, FormaPagamento forma, 
                                    boolean descontoFidelidade, boolean jurosParcelamento, 
                                    boolean taxaInternacional, boolean seguro) {
        
        // 1. Iniciamos a base do decorator injetando o valor bruto original do pedido
        CalculadorValor calculador = new CalculadorValorBase(pedido.getValorBase());

        // 2. Envelopamos dinamicamente na cadeia conforme as escolhas vindas da Main
        // IMPORTANTE: Se o nome das suas classes de decorator na pasta 'decorator' 
        // for ligeiramente diferente, ajuste os nomes dos "new" abaixo!
        if (descontoFidelidade) {
            calculador = new DescontoFidelidadeDecorator(calculador); 
        }
        if (jurosParcelamento) {
            calculador = new JurosParcelamentoDecorator(calculador); 
        }
        if (taxaInternacional) {
            calculador = new TaxaInternacionalDecorator(calculador); 
        }
        if (seguro) {
            calculador = new SeguroDecorator(calculador); 
        }

        // 3. Executa o método calcular() processando toda a cadeia de modificações de preço
        double valorFinal = calculador.calcular();

        // 4. Repassa para o gateway/adapter correto processar o pagamento
        return processarPagamento(pedido, valorFinal, forma);
    }

    /**
     * Efetua a cobrança em lote de todos os pedidos cadastrados na Main.
     */
    public List<ResultadoCobranca> cobrarEmLote(List<Pedido> pedidos, FormaPagamento forma, 
                                                boolean descontoFidelidade, boolean jurosParcelamento, 
                                                boolean taxaInternacional, boolean seguro) {
        List<ResultadoCobranca> resultados = new ArrayList<>();

        // Reaproveita a lógica de cobrança individual para cada item da lista
        for (Pedido pedido : pedidos) {
            resultados.add(cobrar(pedido, forma, descontoFidelidade, jurosParcelamento, taxaInternacional, seguro));
        }

        return resultados;
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