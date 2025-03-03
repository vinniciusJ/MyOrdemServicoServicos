package br.unioeste.geral.ordemservico.servico.service.cliente;

import br.unioeste.apoio.bd.ConexaoBD;
import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.ordemservico.servico.col.cliente.ClienteCOL;
import br.unioeste.geral.ordemservico.servico.dao.cliente.ClienteDAO;
import br.unioeste.geral.ordemservico.servico.dao.cliente.EmailClienteDAO;
import br.unioeste.geral.ordemservico.servico.dao.cliente.TelefoneClienteDAO;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;
import br.unioeste.geral.pessoa.bo.email.Email;
import br.unioeste.geral.pessoa.bo.telefone.Telefone;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UCClienteServicos {
    private final ConexaoBD conexaoBD;

    private final ClienteCOL clienteCOL;

    public UCClienteServicos(){
        conexaoBD = new ConexaoBD();
        clienteCOL = new ClienteCOL();
    }

    public List<Cliente> obterClientes() throws Exception {
        List<Cliente> clientes = new ArrayList<>();

        try(Connection conexao = conexaoBD.getConexaoBD()){
            ClienteDAO funcionarioDAO = new ClienteDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                clientes = funcionarioDAO.obterClientes();

                conexao.commit();
            }
            catch(Exception exception){
                gerenciarException(exception);
            }
        }

        return clientes;
    }

    public Cliente obterClientePorID(Long id) throws Exception {
        if(!clienteCOL.validarID(id)){
            throw new OrdemServicoException("ID inválido: " + id);
        }

        Cliente cliente = null;

        try(Connection conexao = conexaoBD.getConexaoBD()){
            ClienteDAO funcionarioDAO = new ClienteDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                cliente = funcionarioDAO.obterClientePorID(id);

                conexao.commit();

                if(cliente == null){
                    throw new OrdemServicoException("Não foi possível encontrar cliente com ID " + id);
                }
            }
            catch(Exception exception){
                gerenciarException(exception);
            }
        }

        return cliente;
    }

    public Cliente cadastrarCliente(Cliente cliente) throws Exception{
        if(!clienteCOL.validarCliente(cliente)){
            throw new OrdemServicoException("Cliente inválido");
        }

        try(Connection conexao = conexaoBD.getConexaoBD()){
            ClienteDAO funcionarioDAO = new ClienteDAO(conexao);
            EmailClienteDAO emailClienteDAO = new EmailClienteDAO(conexao);
            TelefoneClienteDAO telefoneClienteDAO = new TelefoneClienteDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                Long idCliente = funcionarioDAO.inserirCliente(cliente);

                cliente.setId(idCliente);

                for(Email email: cliente.getEmails()){
                    Long idEmail = emailClienteDAO.inserirEmail(idCliente, email);
                    email.setId(idEmail);
                }

                for(Telefone telefone: cliente.getTelefones()){
                    Long idTelefone = telefoneClienteDAO.inserirTelefone(idCliente, telefone);
                    telefone.setId(idTelefone);
                }

                conexao.commit();
            }
            catch (Exception exception){
                conexao.rollback();

                gerenciarException(exception);
            }
        }

        return cliente;
    }

    private void gerenciarException(Exception exception) throws Exception{
        if(exception instanceof OrdemServicoException){
            throw exception;
        }

        throw new OrdemServicoException("Ocorreu um erro interno: " + exception.getMessage());
    }
}
