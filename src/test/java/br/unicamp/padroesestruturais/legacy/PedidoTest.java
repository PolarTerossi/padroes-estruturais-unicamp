package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    @Test
    void deveCriarPedidoComValoresInformados() {
        Pedido pedido = new Pedido("PED-001", "Joao Silva", "Notebook Dell XPS 15", 4500.00);

        assertEquals("PED-001", pedido.getId());
        assertEquals("Joao Silva", pedido.getCliente());
        assertEquals("Notebook Dell XPS 15", pedido.getDescricao());
        assertEquals(4500.00, pedido.getValorBase());
    }

    @Test
    void devePermitirAlteracaoDeValorBase() {
        Pedido pedido = new Pedido("PED-001", "Joao Silva", "Notebook", 4500.00);
        pedido.setValorBase(5000.00);

        assertEquals(5000.00, pedido.getValorBase());
    }

    @Test
    void devePermitirAlteracaoDeCliente() {
        Pedido pedido = new Pedido("PED-001", "Joao Silva", "Notebook", 4500.00);
        pedido.setCliente("Maria Santos");

        assertEquals("Maria Santos", pedido.getCliente());
    }
}
