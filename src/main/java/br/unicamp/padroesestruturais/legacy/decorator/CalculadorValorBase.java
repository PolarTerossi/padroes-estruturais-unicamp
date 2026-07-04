package br.unicamp.padroesestruturais.legacy.decorator;

public class CalculadorValorBase implements CalculadorValor {
    private final double valorBase;

    // Construtor: recebe o valor bruto do pedido e guarda ele
    public CalculadorValorBase(double valorBase) {
        this.valorBase = valorBase;
    }

    // Método calcular: apenas devolve o valor sem nenhuma taxa ou desconto ainda
    @Override
    public double calcular() {
        return this.valorBase;
    }
}