package br.unioeste.geral.ordemservico.servico.service;

import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.ordemservico.servico.col.ClienteCOL;
import br.unioeste.geral.ordemservico.servico.dao.ClienteDAO;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;

import java.util.List;

public class UCClienteServicos {
    private final ClienteCOL clienteCOL;
    private final ClienteDAO clienteDAO;

    public UCClienteServicos(){
        clienteCOL = new ClienteCOL();
        clienteDAO = new ClienteDAO();
    }

    public List<Cliente> obterClientes() throws Exception {
        return clienteDAO.obterClientes();
    }

    public Cliente obterClientePorID(Long id) throws Exception {
        if(!clienteCOL.validarID(id)){
            throw new OrdemServicoException("ID inválido: " + id);
        }

        Cliente cliente = clienteDAO.obterClientePorID(id);

        if(cliente == null){
            throw new OrdemServicoException("Não foi possível encontrar cliente com ID: " + id);
        }

        return cliente;
    }

    public Cliente cadastrarCliente(Cliente cliente) throws Exception{
        if(!clienteCOL.validarCliente(cliente)){
            throw new OrdemServicoException("Cliente inválido");
        }

        if(!clienteCOL.validarClienteExiste(cliente)){
            throw new OrdemServicoException("Cliente já cadastrado com CPF: " + cliente.getCpf());
        }

        return clienteDAO.inserirCliente(cliente);
    }
}
