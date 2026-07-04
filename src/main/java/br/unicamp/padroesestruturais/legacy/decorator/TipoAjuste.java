package br.unicamp.padroesestruturais.legacy.decorator;

/**
 * Representa cada ajuste de valor combinável que pode ser aplicado a uma cobrança.
 * Substitui os antigos parâmetros booleanos (descontoFidelidade, jurosParcelamento, ...).
 * Adicionar um novo ajuste no futuro = adicionar uma constante aqui + seu Decorator,
 * sem alterar nenhuma assinatura de método existente.
 */
public enum TipoAjuste {
    DESCONTO_FIDELIDADE,
    JUROS_PARCELAMENTO,
    TAXA_INTERNACIONAL,
    SEGURO,
    TAXA_ANTECIPACAO_RECEBIVEIS,
    TAXA_EMISSAO_NF
}