package br.unicamp.padroesestruturais.legacy.domain;

public class ResultadoCobranca {

    private String pedidoId;
    private double valorCobrado;
    private String status;
    private String referencia;
    private FormaPagamento formaPagamento;

    public ResultadoCobranca(String pedidoId, double valorCobrado, String status,
                              String referencia, FormaPagamento formaPagamento) {
        this.pedidoId = pedidoId;
        this.valorCobrado = valorCobrado;
        this.status = status;
        this.referencia = referencia;
        this.formaPagamento = formaPagamento;
    }

    public String getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(String pedidoId) {
        this.pedidoId = pedidoId;
    }

    public double getValorCobrado() {
        return valorCobrado;
    }

    public void setValorCobrado(double valorCobrado) {
        this.valorCobrado = valorCobrado;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
}
