package br.unioeste.geral.ordemservico.servico.col;

import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.ordemservico.bo.funcionario.Funcionario;
import br.unioeste.geral.ordemservico.bo.ordemservico.OrdemServico;
import br.unioeste.geral.ordemservico.bo.servico.Servico;

import java.time.LocalDate;
import java.util.List;

public class OrdemServicoCOL {
    private final ServicoCOL servicoCOL;
    private final ClienteCOL clienteCOL;
    private final FuncionarioCOL funcionarioCOL;

    public OrdemServicoCOL() {
        servicoCOL = new ServicoCOL();
        clienteCOL = new ClienteCOL();
        funcionarioCOL = new FuncionarioCOL();
    }

    public boolean validarNumero(String numero) {
        return numero != null && !numero.isBlank();
    }

    public boolean validarDataEmissao(LocalDate dataEmissao){
        return dataEmissao != null;
    }

    public boolean validarDescricao(String descricao) {
        return descricao != null && !descricao.isBlank();
    }

    public boolean validarCliente(Cliente cliente) throws Exception {
        return clienteCOL.validarCliente(cliente);
    }

    public boolean validarFuncionario(Funcionario funcionario) throws Exception {
        return funcionarioCOL.validarFuncionario(funcionario);
    }

    public boolean validarServicos(List<Servico> servicos){
        return servicos.stream().allMatch(servicoCOL::validarServico);
    }

    public boolean validarOrdemServico(OrdemServico ordemServico) throws Exception {
        if(ordemServico == null){
            return false;
        }

        return validarDataEmissao(ordemServico.getDataEmissao()) &&
                validarDescricao(ordemServico.getDescricao()) &&
                validarCliente(ordemServico.getCliente()) &&
                validarFuncionario(ordemServico.getFuncionarioResponsavel()) &&
                validarServicos(ordemServico.getServicosRealizados());

    }
}
