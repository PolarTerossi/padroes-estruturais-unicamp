package br.unicamp.padroesestruturais.legacy.externo;

/**
 * Representa o retorno de uma transacao processada pelo SDK PaySecure.
 *
 * Faz parte da biblioteca de terceiros e nao deve ser modificada.
 */
public class TransacaoExterna {

    private final String referenciaExterna;
    private final int codigoStatus;
    private final double valorProcessado;
    private final String moeda;

    public TransacaoExterna(String referenciaExterna, int codigoStatus, double valorProcessado, String moeda) {
        this.referenciaExterna = referenciaExterna;
        this.codigoStatus = codigoStatus;
        this.valorProcessado = valorProcessado;
        this.moeda = moeda;
    }

    public String getReferenciaExterna() {
        return referenciaExterna;
    }

    public int getCodigoStatus() {
        return codigoStatus;
    }

    public double getValorProcessado() {
        return valorProcessado;
    }

    public String getMoeda() {
        return moeda;
    }
}
