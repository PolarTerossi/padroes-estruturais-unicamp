package br.unicamp.padroesestruturais.legacy.gateway;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;

public class GatewayPagamentoInternoAdapter implements GatewayPagamento {

    private GatewayPagamentoInterno gatewayInterno;

    public GatewayPagamentoInternoAdapter() {
        // Instancia o gateway legado internamente
        this.gatewayInterno = new GatewayPagamentoInterno();
    }

    @Override
    public ResultadoCobranca processar(Pedido pedido, double valorFinal, FormaPagamento forma) {
        // Apenas repassa a chamada traduzindo para o método original
        return gatewayInterno.cobrar(pedido.getId(), pedido.getCliente(), valorFinal, forma);
    }
}