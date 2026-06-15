package br.unicamp.padroesestruturais.legacy.externo;

/**
 * SDK da carteira digital WalletPay.
 *
 * ATENCAO: representa uma biblioteca de terceiros e nao deve ser modificada.
 * O SDK ja foi adicionado as dependencias do projeto, porem ainda nao esta
 * integrado ao sistema de cobranca.
 */
public class WalletPaySDK {

    private static final long LIMITE_APROVACAO_AUTOMATICA_CENTAVOS = 1_000_000L; // R$ 10.000,00

    public ChargeResponse charge(ChargeRequest request) {
        if (request.getAmountInCents() <= 0) {
            return new ChargeResponse(ChargeStatus.FAILED, null);
        }

        String walletTransactionId = "WPAY-" + Long.toHexString(System.nanoTime()).toUpperCase();
        ChargeStatus status = request.getAmountInCents() > LIMITE_APROVACAO_AUTOMATICA_CENTAVOS
                ? ChargeStatus.DECLINED
                : ChargeStatus.CONFIRMED;

        return new ChargeResponse(status, walletTransactionId);
    }
}
