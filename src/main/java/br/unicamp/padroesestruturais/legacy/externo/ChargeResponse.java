package br.unicamp.padroesestruturais.legacy.externo;

/**
 * Resposta de cobranca do SDK WalletPay.
 *
 * Faz parte da biblioteca de terceiros e nao deve ser modificada.
 */
public class ChargeResponse {

    private final ChargeStatus status;
    private final String walletTransactionId;

    public ChargeResponse(ChargeStatus status, String walletTransactionId) {
        this.status = status;
        this.walletTransactionId = walletTransactionId;
    }

    public ChargeStatus getStatus() {
        return status;
    }

    public String getWalletTransactionId() {
        return walletTransactionId;
    }
}
