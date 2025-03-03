package br.unioeste.geral.ordemservico.servico.service;

import br.unioeste.apoio.bd.ConexaoBD;
import br.unioeste.geral.ordemservico.bo.funcionario.Funcionario;
import br.unioeste.geral.ordemservico.servico.col.FuncionarioCOL;
import br.unioeste.geral.ordemservico.servico.dao.funcionario.EmailFuncionarioDAO;
import br.unioeste.geral.ordemservico.servico.dao.funcionario.FuncionarioDAO;
import br.unioeste.geral.ordemservico.servico.dao.funcionario.TelefoneFuncionarioDAO;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;
import br.unioeste.geral.pessoa.bo.email.Email;
import br.unioeste.geral.pessoa.bo.telefone.Telefone;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UCFuncionarioServicos {
    private final ConexaoBD conexaoBD;

    private final FuncionarioCOL funcionarioCOL;

    public UCFuncionarioServicos(){
        conexaoBD = new ConexaoBD();
        funcionarioCOL = new FuncionarioCOL();
    }

    public List<Funcionario> obterFuncionarios() throws Exception {
        List<Funcionario> funcionarios = new ArrayList<>();

        try(Connection conexao = conexaoBD.getConexaoBD()){
            FuncionarioDAO funcionarioDAO = new FuncionarioDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                funcionarios = funcionarioDAO.obterFuncionarios();

                conexao.commit();
            }
            catch(Exception exception){
                gerenciarException(exception);
            }
        }

        return funcionarios;
    }

    public Funcionario obterFuncionarioPorID(Long id) throws Exception {
        if(!funcionarioCOL.validarID(id)){
            throw new OrdemServicoException("ID inválido: " + id);
        }

        Funcionario funcionario = null;

        try(Connection conexao = conexaoBD.getConexaoBD()){
            FuncionarioDAO funcionarioDAO = new FuncionarioDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                funcionario = funcionarioDAO.obterFuncionarioPorID(id);

                conexao.commit();

                if(funcionario == null){
                    throw new OrdemServicoException("Não foi possível encontrar funcionário com ID " + id);
                }
            }
            catch(Exception exception){
                gerenciarException(exception);
            }
        }

        return funcionario;
    }

    public Funcionario cadastrarFuncionario(Funcionario funcionario) throws Exception{
        if(!funcionarioCOL.validarFuncionario(funcionario)){
            throw new OrdemServicoException("Funcionário inválido");
        }

        try(Connection conexao = conexaoBD.getConexaoBD()){
            FuncionarioDAO funcionarioDAO = new FuncionarioDAO(conexao);
            EmailFuncionarioDAO emailFuncionarioDAO = new EmailFuncionarioDAO(conexao);
            TelefoneFuncionarioDAO telefoneFuncionarioDAO = new TelefoneFuncionarioDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                Long idFuncionario = funcionarioDAO.inserirFuncionario(funcionario);

                funcionario.setId(idFuncionario);

                for(Email email: funcionario.getEmails()){
                    Long idEmail = emailFuncionarioDAO.inserirEmail(idFuncionario, email);
                    email.setId(idEmail);
                }

                for(Telefone telefone: funcionario.getTelefones()){
                    Long idTelefone = telefoneFuncionarioDAO.inserirTelefone(idFuncionario, telefone);
                    telefone.setId(idTelefone);
                }

                conexao.commit();
            }
            catch (Exception exception){
                conexao.rollback();

                gerenciarException(exception);
            }
        }

        return funcionario;
    }

    private void gerenciarException(Exception exception) throws Exception{
        if(exception instanceof OrdemServicoException){
            throw exception;
        }

        throw new OrdemServicoException("Ocorreu um erro interno: " + exception.getMessage());
    }
}
