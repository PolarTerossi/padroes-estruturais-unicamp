package br.unicamp.padroesestruturais.legacy.externo;

/**
 * Requisicao de cobranca do SDK WalletPay. O valor e informado em centavos.
 *
 * Faz parte da biblioteca de terceiros e nao deve ser modificada.
 */
public class ChargeRequest {

    private final String merchantOrderId;
    private final String payerName;
    private final long amountInCents;

    public ChargeRequest(String merchantOrderId, String payerName, long amountInCents) {
        this.merchantOrderId = merchantOrderId;
        this.payerName = payerName;
        this.amountInCents = amountInCents;
    }

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public String getPayerName() {
        return payerName;
    }

    public long getAmountInCents() {
        return amountInCents;
    }
}
