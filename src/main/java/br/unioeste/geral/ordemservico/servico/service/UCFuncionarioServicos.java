package br.unioeste.geral.ordemservico.servico.service;

import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.ordemservico.bo.funcionario.Funcionario;
import br.unioeste.geral.ordemservico.servico.col.FuncionarioCOL;
import br.unioeste.geral.ordemservico.servico.dao.funcionario.FuncionarioDAO;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;

import java.util.List;

public class UCFuncionarioServicos {
    private final FuncionarioCOL funcionarioCOL;
    private final FuncionarioDAO funcionarioDAO;

    public UCFuncionarioServicos(){
        funcionarioCOL = new FuncionarioCOL();
        funcionarioDAO = new FuncionarioDAO();
    }

    public List<Funcionario> obterFuncionarios() throws Exception {
        return funcionarioDAO.obterFuncionarios();
    }

    public Funcionario obterFuncionarioPorID(Long id) throws Exception {
        if(!funcionarioCOL.validarID(id)){
            throw new OrdemServicoException("ID inválido: " + id);
        }

        Funcionario funcionario = funcionarioDAO.obterFuncionarioPorID(id);

        if(funcionario == null){
            throw new OrdemServicoException("Não foi possível encontrar funcionario com ID: " + id);
        }

        return funcionario;
    }

    public Funcionario cadastrarFuncionario(Funcionario funcionario) throws Exception{
        if(!funcionarioCOL.validarFuncionario(funcionario)){
            throw new OrdemServicoException("Funcionário inválido");
        }

        if(!funcionarioCOL.validarFuncionarioExiste(funcionario)){
            throw new OrdemServicoException("Funcionário já cadastrado com CPF: " + funcionario.getCpf());
        }

        return funcionarioDAO.inserirFuncionario(funcionario);
    }
}
