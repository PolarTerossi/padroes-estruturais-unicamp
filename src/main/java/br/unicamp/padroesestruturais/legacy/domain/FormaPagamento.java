package br.unicamp.padroesestruturais.legacy.domain;

public enum FormaPagamento {
    BOLETO,
    PIX,
    CARTAO_CREDITO,
    CARTEIRA_DIGITAL // Adicionado para suportar o WalletPaySDK
}
