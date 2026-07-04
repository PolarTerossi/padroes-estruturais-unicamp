package br.unicamp.padroesestruturais.legacy.decorator;

public class TaxaAntecipacaoDecorator extends AjusteValorDecorator {

    public TaxaAntecipacaoDecorator(CalculadorValor calculadorWrapper) {
        super(calculadorWrapper);
    }

    @Override
    public double calcular() {
        double valorCalculado = super.calcular();
        return valorCalculado + (valorCalculado * 0.015);
    }
}