package br.unicamp.padroesestruturais.legacy.decorator;

public class SeguroDecorator extends AjusteValorDecorator {

    public SeguroDecorator(CalculadorValor calculadorWrapper) {
        super(calculadorWrapper);
    }

    @Override
    public double calcular() {
        return super.calcular() + 4.90;
    }
}