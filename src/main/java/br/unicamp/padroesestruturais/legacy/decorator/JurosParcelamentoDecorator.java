package br.unicamp.padroesestruturais.legacy.decorator;

public class JurosParcelamentoDecorator extends AjusteValorDecorator {

    public JurosParcelamentoDecorator(CalculadorValor calculadorWrapper) {
        super(calculadorWrapper);
    }

    @Override
    public double calcular() {
        double valorCalculado = super.calcular();
        return valorCalculado + (valorCalculado * 0.0299);
    }
}