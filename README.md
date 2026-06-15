# Sistema de Cobranca Corporativa — Versao Legado

> Atividade Pratica — Padroes Estruturais de Projeto
> Disciplina: Padroes de Projeto de Software | Faculdade de Tecnologia — Unicamp

---

## Sobre o sistema

Este repositorio contem o codigo-fonte de um sistema interno de cobranca corporativa desenvolvido para uma empresa fictícia. O sistema permite registrar pedidos e realizar cobrancas via Boleto, Pix e Cartao de Credito, aplicando ajustes ao valor cobrado como desconto de fidelidade, juros de parcelamento, taxa de operacao internacional e seguro de transacao.

A cobranca via Cartao de Credito e processada por meio do SDK de um gateway de pagamento externo (PaySecure), ja integrado ao sistema.

O sistema foi desenvolvido ao longo do tempo por diferentes equipes e encontra-se em producao. O codigo funciona corretamente, atende aos requisitos funcionais e possui cobertura de testes automatizados.

---

## Objetivo da atividade

Esta atividade propoe uma **refatoracao arquitetural** do sistema, com foco na aplicacao de padroes estruturais de projeto. O sistema atual, embora funcional, apresenta caracteristicas tipicas de codigo legado que dificultam sua manutencao e evolucao.

Voces deverao analisar o codigo existente, identificar os aspectos que dificultam a extensibilidade e a manutencao, e propor e implementar uma solucao utilizando os padroes **Adapter** e **Decorator**.


---

## Pre-requisitos

- Java 17 ou superior
- Apache Maven 3.8 ou superior

---

## Como executar

Compilar e empacotar:

```bash
mvn clean package -q
```

Executar o sistema interativo:

```bash
java -jar target/sistema-cobranca.jar
```

Alternativamente, sem gerar o JAR:

```bash
mvn exec:java -Dexec.mainClass="br.unicamp.padroesestruturais.legacy.Main"
```

---

## Como rodar os testes

```bash
mvn test
```

Para exibir o resultado detalhado de cada caso de teste:

```bash
mvn test -Dsurefire.useFile=false
```
