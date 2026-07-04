package br.unicamp.padroesestruturais.legacy.decorator;

public interface CalculadorValor {
    // Agora o método sabe em cima de qual valor ele deve aplicar as taxas/descontos
    double calcular();
}