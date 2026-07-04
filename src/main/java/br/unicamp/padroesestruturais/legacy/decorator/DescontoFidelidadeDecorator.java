package br.unicamp.padroesestruturais.legacy.decorator;

public class DescontoFidelidadeDecorator extends AjusteValorDecorator {

    public DescontoFidelidadeDecorator(CalculadorValor calculadorWrapper) {
        super(calculadorWrapper);
    }

    @Override
    public double calcular() {
        double valorCalculado = super.calcular();
        return valorCalculado - (valorCalculado * 0.05);
    }
}