package br.unicamp.padroesestruturais.legacy.gateway;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;

/**
 * Gateway de pagamento interno, utilizado para cobrancas via Boleto e Pix.
 */
public class GatewayPagamentoInterno {

    public ResultadoCobranca cobrar(String pedidoId, String cliente, double valor, FormaPagamento forma) {
        String referencia = "INT-" + pedidoId + "-" + forma.name();
        return new ResultadoCobranca(pedidoId, valor, "APROVADA", referencia, forma);
    }
}
