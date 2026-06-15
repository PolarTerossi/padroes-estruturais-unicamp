package br.unicamp.padroesestruturais.legacy.service;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.externo.GatewayIndisponivelException;
import br.unicamp.padroesestruturais.legacy.externo.PaySecureGateway;
import br.unicamp.padroesestruturais.legacy.externo.TransacaoExterna;
import br.unicamp.padroesestruturais.legacy.gateway.GatewayPagamentoInterno;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CobrancaService {

    private static final double TAXA_DESCONTO_FIDELIDADE = 0.05;
    private static final double TAXA_JUROS_PARCELAMENTO = 0.0299;
    private static final double TAXA_OPERACAO_INTERNACIONAL = 0.05;
    private static final double VALOR_SEGURO = 4.90;

    public ResultadoCobranca cobrar(Pedido pedido, FormaPagamento forma,
                                     boolean aplicarDescontoFidelidade,
                                     boolean aplicarJurosParcelamento,
                                     boolean aplicarTaxaInternacional,
                                     boolean aplicarSeguro) {

        double valorFinal = calcularValorFinal(pedido.getValorBase(), aplicarDescontoFidelidade,
                aplicarJurosParcelamento, aplicarTaxaInternacional, aplicarSeguro);

        if (forma == FormaPagamento.BOLETO || forma == FormaPagamento.PIX) {
            GatewayPagamentoInterno gateway = new GatewayPagamentoInterno();
            return gateway.cobrar(pedido.getId(), pedido.getCliente(), valorFinal, forma);

        } else if (forma == FormaPagamento.CARTAO_CREDITO) {
            PaySecureGateway gateway = new PaySecureGateway();

            Map<String, Object> dadosTransacao = new HashMap<>();
            dadosTransacao.put("orderId", pedido.getId());
            dadosTransacao.put("customerName", pedido.getCliente());
            dadosTransacao.put("amount", valorFinal);
            dadosTransacao.put("currency", "BRL");

            try {
                TransacaoExterna transacao = gateway.processarTransacao(dadosTransacao);
                String status = transacao.getCodigoStatus() == 200 ? "APROVADA" : "RECUSADA";
                return new ResultadoCobranca(pedido.getId(), valorFinal, status, transacao.getReferenciaExterna(), forma);

            } catch (GatewayIndisponivelException e) {
                return new ResultadoCobranca(pedido.getId(), valorFinal, "RECUSADA", null, forma);
            }

        } else {
            throw new IllegalArgumentException("Forma de pagamento nao suportada: " + forma);
        }
    }

    public List<ResultadoCobranca> cobrarEmLote(List<Pedido> pedidos, FormaPagamento forma,
                                                  boolean aplicarDescontoFidelidade,
                                                  boolean aplicarJurosParcelamento,
                                                  boolean aplicarTaxaInternacional,
                                                  boolean aplicarSeguro) {

        List<ResultadoCobranca> resultados = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            double valorFinal = calcularValorFinal(pedido.getValorBase(), aplicarDescontoFidelidade,
                    aplicarJurosParcelamento, aplicarTaxaInternacional, aplicarSeguro);

            if (forma == FormaPagamento.BOLETO || forma == FormaPagamento.PIX) {
                GatewayPagamentoInterno gateway = new GatewayPagamentoInterno();
                resultados.add(gateway.cobrar(pedido.getId(), pedido.getCliente(), valorFinal, forma));

            } else if (forma == FormaPagamento.CARTAO_CREDITO) {
                PaySecureGateway gateway = new PaySecureGateway();

                Map<String, Object> dadosTransacao = new HashMap<>();
                dadosTransacao.put("orderId", pedido.getId());
                dadosTransacao.put("customerName", pedido.getCliente());
                dadosTransacao.put("amount", valorFinal);
                dadosTransacao.put("currency", "BRL");

                try {
                    TransacaoExterna transacao = gateway.processarTransacao(dadosTransacao);
                    String status = transacao.getCodigoStatus() == 200 ? "APROVADA" : "RECUSADA";
                    resultados.add(new ResultadoCobranca(pedido.getId(), valorFinal, status, transacao.getReferenciaExterna(), forma));

                } catch (GatewayIndisponivelException e) {
                    resultados.add(new ResultadoCobranca(pedido.getId(), valorFinal, "RECUSADA", null, forma));
                }

            } else {
                throw new IllegalArgumentException("Forma de pagamento nao suportada: " + forma);
            }
        }

        return resultados;
    }

    public double calcularValorFinal(double valorBase,
                                      boolean aplicarDescontoFidelidade,
                                      boolean aplicarJurosParcelamento,
                                      boolean aplicarTaxaInternacional,
                                      boolean aplicarSeguro) {

        double valor = valorBase;

        if (aplicarDescontoFidelidade) {
            valor = valor - (valor * TAXA_DESCONTO_FIDELIDADE);
        }

        if (aplicarJurosParcelamento) {
            valor = valor + (valor * TAXA_JUROS_PARCELAMENTO);
        }

        if (aplicarTaxaInternacional) {
            valor = valor + (valor * TAXA_OPERACAO_INTERNACIONAL);
        }

        if (aplicarSeguro) {
            valor = valor + VALOR_SEGURO;
        }

        return valor;
    }
}
