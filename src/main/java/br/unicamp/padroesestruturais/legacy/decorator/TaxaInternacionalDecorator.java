package br.unicamp.padroesestruturais.legacy.decorator;

public class TaxaInternacionalDecorator extends AjusteValorDecorator {

    public TaxaInternacionalDecorator(CalculadorValor calculadorWrapper) {
        super(calculadorWrapper);
    }

    @Override
    public double calcular() {
        double valorCalculado = super.calcular();
        return valorCalculado + (valorCalculado * 0.05);
    }
}