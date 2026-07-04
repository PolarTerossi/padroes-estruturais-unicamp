package br.unicamp.padroesestruturais.legacy.decorator;

public class TaxaEmissaoNfDecorator extends AjusteValorDecorator {

    public TaxaEmissaoNfDecorator(CalculadorValor calculadorWrapper) {
        super(calculadorWrapper);
    }

    @Override
    public double calcular() {
        return super.calcular() + 2.50;
    }
}