package br.unicamp.padroesestruturais.legacy.domain;

public class Pedido {

    private String id;
    private String cliente;
    private String descricao;
    private double valorBase;

    public Pedido(String id, String cliente, String descricao, double valorBase) {
        this.id = id;
        this.cliente = cliente;
        this.descricao = descricao;
        this.valorBase = valorBase;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValorBase() {
        return valorBase;
    }

    public void setValorBase(double valorBase) {
        this.valorBase = valorBase;
    }
}
