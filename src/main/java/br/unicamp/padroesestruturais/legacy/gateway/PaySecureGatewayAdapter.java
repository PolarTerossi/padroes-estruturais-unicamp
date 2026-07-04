package br.unicamp.padroesestruturais.legacy.gateway;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.externo.GatewayIndisponivelException;
import br.unicamp.padroesestruturais.legacy.externo.PaySecureGateway;
import br.unicamp.padroesestruturais.legacy.externo.TransacaoExterna;

import java.util.HashMap;
import java.util.Map;

public class PaySecureGatewayAdapter implements GatewayPagamento {

    private PaySecureGateway gatewayExterno;

    public PaySecureGatewayAdapter() {
        this.gatewayExterno = new PaySecureGateway();
    }

    @Override
    public ResultadoCobranca processar(Pedido pedido, double valorFinal, FormaPagamento forma) {
        Map<String, Object> dadosTransacao = new HashMap<>();
        dadosTransacao.put("orderId", pedido.getId());
        dadosTransacao.put("customerName", pedido.getCliente());
        dadosTransacao.put("amount", valorFinal);
        dadosTransacao.put("currency", "BRL");

        try {
            TransacaoExterna transacao = gatewayExterno.processarTransacao(dadosTransacao);
            String status = transacao.getCodigoStatus() == 200 ? "APROVADA" : "RECUSADA";
            return new ResultadoCobranca(pedido.getId(), valorFinal, status, transacao.getReferenciaExterna(), forma);

        } catch (GatewayIndisponivelException e) {
            return new ResultadoCobranca(pedido.getId(), valorFinal, "RECUSADA", null, forma);
        }
    }
}