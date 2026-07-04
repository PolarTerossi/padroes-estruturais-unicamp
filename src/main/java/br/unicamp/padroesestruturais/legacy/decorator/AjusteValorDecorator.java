package br.unicamp.padroesestruturais.legacy.decorator;

public abstract class AjusteValorDecorator implements CalculadorValor {
    
    protected final CalculadorValor calculadorWrapper;

    public AjusteValorDecorator(CalculadorValor calculadorWrapper) {
        this.calculadorWrapper = calculadorWrapper;
    }

    @Override
    public double calcular() {
        // Por padrão, delega para o próximo objeto envelopado
        return calculadorWrapper.calcular();
    }
}