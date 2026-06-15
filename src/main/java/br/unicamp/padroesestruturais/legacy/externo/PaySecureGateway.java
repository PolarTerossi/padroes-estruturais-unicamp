package br.unicamp.padroesestruturais.legacy.externo;

import java.util.Map;
import java.util.UUID;

/**
 * SDK do gateway de pagamento externo PaySecure, utilizado para processar
 * cobrancas via Cartao de Credito.
 *
 * ATENCAO: representa uma biblioteca de terceiros (ja integrada ao sistema)
 * e nao deve ser modificada.
 */
public class PaySecureGateway {

    private static final double LIMITE_APROVACAO_AUTOMATICA = 10000.0;

    public TransacaoExterna processarTransacao(Map<String, Object> dadosTransacao) throws GatewayIndisponivelException {
        Object orderId = dadosTransacao.get("orderId");
        Object amountObj = dadosTransacao.get("amount");

        if (orderId == null || amountObj == null) {
            throw new GatewayIndisponivelException("Dados de transacao incompletos");
        }

        double amount = ((Number) amountObj).doubleValue();

        if (amount <= 0) {
            throw new GatewayIndisponivelException("Valor de transacao invalido para o pedido " + orderId);
        }

        String moeda = String.valueOf(dadosTransacao.getOrDefault("currency", "BRL"));
        String referenciaExterna = "PSEC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        int codigoStatus = amount > LIMITE_APROVACAO_AUTOMATICA ? 402 : 200;

        return new TransacaoExterna(referenciaExterna, codigoStatus, amount, moeda);
    }
}
