package br.unicamp.padroesestruturais.legacy.gateway;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.externo.ChargeRequest;
import br.unicamp.padroesestruturais.legacy.externo.ChargeResponse;
import br.unicamp.padroesestruturais.legacy.externo.ChargeStatus;
import br.unicamp.padroesestruturais.legacy.externo.WalletPaySDK;

public class WalletPayAdapter implements GatewayPagamento {

    private WalletPaySDK walletSdk;

    public WalletPayAdapter() {
        this.walletSdk = new WalletPaySDK();
    }

    @Override
    public ResultadoCobranca processar(Pedido pedido, double valorFinal, FormaPagamento forma) {
        
        // 1. Converte o nosso valorFinal (double) para centavos (long)
        long valorEmCentavos = Math.round(valorFinal * 100.0);

        // 2. Prepara o request utilizando o construtor exato da biblioteca de terceiros
        ChargeRequest request = new ChargeRequest(
                pedido.getId(),
                pedido.getCliente(),
                valorEmCentavos
        );

        // 3. Executa a cobrança no SDK externo
        ChargeResponse response = walletSdk.charge(request);

        // 4. Traduz o status da biblioteca (ChargeStatus) para a string que nosso sistema espera
        String statusPadrao = (response.getStatus() == ChargeStatus.CONFIRMED) ? "APROVADA" : "RECUSADA";

        // 5. Monta e retorna o nosso objeto padronizado
        return new ResultadoCobranca(
                pedido.getId(),
                valorFinal,
                statusPadrao,
                response.getWalletTransactionId(),
                forma
        );
    }
}