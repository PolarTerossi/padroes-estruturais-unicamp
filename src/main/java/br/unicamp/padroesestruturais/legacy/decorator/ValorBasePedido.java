package br.unicamp.padroesestruturais.legacy.decorator;

import br.unicamp.padroesestruturais.legacy.domain.Pedido;

public class ValorBasePedido implements CalculadorValor {
    
    private final Pedido pedido;

    public ValorBasePedido(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public double calcular() {
        return pedido.getValorBase();
    }
}