package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.externo.ChargeRequest;
import br.unicamp.padroesestruturais.legacy.externo.ChargeResponse;
import br.unicamp.padroesestruturais.legacy.externo.ChargeStatus;
import br.unicamp.padroesestruturais.legacy.externo.WalletPaySDK;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalletPaySDKTest {

    @Test
    void deveConfirmarCobrancaDentroDoLimite() {
        WalletPaySDK sdk = new WalletPaySDK();

        ChargeResponse resposta = sdk.charge(new ChargeRequest("PED-001", "Joao Silva", 50000));

        assertEquals(ChargeStatus.CONFIRMED, resposta.getStatus());
        assertNotNull(resposta.getWalletTransactionId());
        assertTrue(resposta.getWalletTransactionId().startsWith("WPAY-"));
    }

    @Test
    void deveRecusarCobrancaAcimaDoLimite() {
        WalletPaySDK sdk = new WalletPaySDK();

        ChargeResponse resposta = sdk.charge(new ChargeRequest("PED-003", "Construtora ABC Ltda", 1_500_000));

        assertEquals(ChargeStatus.DECLINED, resposta.getStatus());
    }

    @Test
    void deveFalharParaValorInvalido() {
        WalletPaySDK sdk = new WalletPaySDK();

        ChargeResponse resposta = sdk.charge(new ChargeRequest("PED-004", "Cliente X", 0));

        assertEquals(ChargeStatus.FAILED, resposta.getStatus());
        assertNull(resposta.getWalletTransactionId());
    }
}
