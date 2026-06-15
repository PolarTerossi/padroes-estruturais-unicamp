package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.service.CobrancaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        CobrancaService cobrancaService = new CobrancaService();
        List<Pedido> pedidos = criarPedidosExemplo();

        Scanner scanner = new Scanner(System.in);

        System.out.println("==================================================");
        System.out.println("   Sistema de Cobranca Corporativa v1.0");
        System.out.println("==================================================");
        System.out.println();

        boolean executando = true;
        while (executando) {
            exibirMenu();
            int opcao = lerInteiro(scanner);

            switch (opcao) {
                case 1 -> fluxoCobrancaUnica(scanner, pedidos, cobrancaService);
                case 2 -> fluxoCobrancaEmLote(scanner, pedidos, cobrancaService);
                case 3 -> exibirPedidos(pedidos);
                case 0 -> executando = false;
                default -> System.out.println("Opcao invalida. Tente novamente.");
            }
            System.out.println();
        }

        System.out.println("Sistema encerrado.");
        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("--- Menu Principal ---");
        System.out.println("1. Realizar cobranca de um pedido");
        System.out.println("2. Realizar cobranca em lote (todos os pedidos)");
        System.out.println("3. Listar pedidos cadastrados");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opcao: ");
    }

    private static void fluxoCobrancaUnica(Scanner scanner, List<Pedido> pedidos, CobrancaService service) {
        Pedido pedido = selecionarPedido(scanner, pedidos);
        if (pedido == null) return;

        FormaPagamento forma = selecionarFormaPagamento(scanner);
        if (forma == null) return;

        boolean descontoFidelidade = perguntarSimNao(scanner, "Aplicar desconto de fidelidade (5%)?");
        boolean jurosParcelamento = perguntarSimNao(scanner, "Aplicar juros de parcelamento (2,99%)?");
        boolean taxaInternacional = perguntarSimNao(scanner, "Aplicar taxa de operacao internacional (5%)?");
        boolean seguro = perguntarSimNao(scanner, "Aplicar seguro de transacao (R$ 4,90)?");

        ResultadoCobranca resultado = service.cobrar(pedido, forma,
                descontoFidelidade, jurosParcelamento, taxaInternacional, seguro);

        System.out.println();
        exibirResultado(pedido, resultado);
    }

    private static void fluxoCobrancaEmLote(Scanner scanner, List<Pedido> pedidos, CobrancaService service) {
        FormaPagamento forma = selecionarFormaPagamento(scanner);
        if (forma == null) return;

        boolean descontoFidelidade = perguntarSimNao(scanner, "Aplicar desconto de fidelidade (5%)?");
        boolean jurosParcelamento = perguntarSimNao(scanner, "Aplicar juros de parcelamento (2,99%)?");
        boolean taxaInternacional = perguntarSimNao(scanner, "Aplicar taxa de operacao internacional (5%)?");
        boolean seguro = perguntarSimNao(scanner, "Aplicar seguro de transacao (R$ 4,90)?");

        List<ResultadoCobranca> resultados = service.cobrarEmLote(pedidos, forma,
                descontoFidelidade, jurosParcelamento, taxaInternacional, seguro);

        System.out.println();
        for (int i = 0; i < pedidos.size(); i++) {
            exibirResultado(pedidos.get(i), resultados.get(i));
            System.out.println();
        }
    }

    private static void exibirPedidos(List<Pedido> pedidos) {
        System.out.println("=== Pedidos Cadastrados ===");
        for (int i = 0; i < pedidos.size(); i++) {
            Pedido pedido = pedidos.get(i);
            System.out.printf("%d. [%s] %s - %s - R$ %.2f%n",
                    i + 1, pedido.getId(), pedido.getDescricao(), pedido.getCliente(), pedido.getValorBase());
        }
        System.out.println("===========================");
    }

    private static void exibirResultado(Pedido pedido, ResultadoCobranca resultado) {
        System.out.println("=== Resultado da Cobranca ===");
        System.out.println("Pedido       : " + pedido.getId() + " - " + pedido.getDescricao());
        System.out.println("Cliente      : " + pedido.getCliente());
        System.out.printf("Valor base   : R$ %.2f%n", pedido.getValorBase());
        System.out.printf("Valor cobrado: R$ %.2f%n", resultado.getValorCobrado());
        System.out.println("Forma        : " + resultado.getFormaPagamento());
        System.out.println("Status       : " + resultado.getStatus());
        System.out.println("Referencia   : " + (resultado.getReferencia() != null ? resultado.getReferencia() : "-"));
        System.out.println("==============================");
    }

    private static Pedido selecionarPedido(Scanner scanner, List<Pedido> pedidos) {
        exibirPedidos(pedidos);
        System.out.print("Escolha o pedido pelo numero: ");
        int escolha = lerInteiro(scanner);

        if (escolha < 1 || escolha > pedidos.size()) {
            System.out.println("Pedido invalido.");
            return null;
        }
        return pedidos.get(escolha - 1);
    }

    private static FormaPagamento selecionarFormaPagamento(Scanner scanner) {
        System.out.println("Forma de pagamento:");
        System.out.println("  1. Boleto");
        System.out.println("  2. Pix");
        System.out.println("  3. Cartao de Credito");
        System.out.print("Escolha: ");

        return switch (lerInteiro(scanner)) {
            case 1 -> FormaPagamento.BOLETO;
            case 2 -> FormaPagamento.PIX;
            case 3 -> FormaPagamento.CARTAO_CREDITO;
            default -> {
                System.out.println("Forma de pagamento invalida.");
                yield null;
            }
        };
    }

    private static boolean perguntarSimNao(Scanner scanner, String pergunta) {
        System.out.print(pergunta + " (s/n): ");
        String resposta = scanner.nextLine().trim().toLowerCase();
        return resposta.equals("s") || resposta.equals("sim");
    }

    private static int lerInteiro(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static List<Pedido> criarPedidosExemplo() {
        List<Pedido> pedidos = new ArrayList<>();
        pedidos.add(new Pedido("PED-001", "Joao Silva", "Notebook Dell XPS 15", 4500.00));
        pedidos.add(new Pedido("PED-002", "Maria Santos", "Cadeira de Escritorio Ergonomica", 890.00));
        pedidos.add(new Pedido("PED-003", "Construtora ABC Ltda", "Servidor Dell PowerEdge R740", 18500.00));
        return pedidos;
    }
}
