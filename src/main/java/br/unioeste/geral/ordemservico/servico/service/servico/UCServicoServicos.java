package br.unioeste.geral.ordemservico.servico.service.servico;

import br.unioeste.apoio.bd.ConexaoBD;
import br.unioeste.geral.ordemservico.bo.servico.Servico;
import br.unioeste.geral.ordemservico.servico.col.servico.ServicoCOL;
import br.unioeste.geral.ordemservico.servico.dao.servico.ServicoDAO;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UCServicoServicos {
    private final ConexaoBD conexaoBD;
    private final ServicoCOL servicoCOL;

    public UCServicoServicos() {
        conexaoBD = new ConexaoBD();
        servicoCOL = new ServicoCOL();
    }

    public List<Servico> obterServicos() throws Exception {
        List<Servico> servicos = new ArrayList<>();

        try(Connection conexao = conexaoBD.getConexaoBD()){
            ServicoDAO servicoDAO = new ServicoDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                servicos = servicoDAO.obterServicos();

                conexao.commit();
            }
            catch (Exception exception){
                if(exception instanceof OrdemServicoException){
                    throw exception;
                }

                throw new OrdemServicoException("Houve um erro interno: " + exception.getMessage());
            }
        }

        return servicos;
    }

    public List<Servico> obterServicosPorNumeroOrdemServico(String numero) throws Exception {
        List<Servico> servicos = new ArrayList<>();

        try(Connection conexao = conexaoBD.getConexaoBD()){
            ServicoDAO servicoDAO = new ServicoDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                servicos = servicoDAO.obterServicosPorNumeroOrdemServico(numero);

                conexao.commit();
            }
            catch (Exception exception){
                if(exception instanceof OrdemServicoException){
                    throw exception;
                }

                throw new OrdemServicoException("Houve um erro interno: " + exception.getMessage());
            }
        }

        return servicos;
    }


    public Servico obterServicosPorID(Long id) throws Exception {
        Servico servico = null;

        try(Connection conexao = conexaoBD.getConexaoBD()){
            ServicoDAO servicoDAO = new ServicoDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                servico = servicoDAO.obterServicoPorID(id);

                conexao.commit();
            }
            catch (Exception exception){
                if(exception instanceof OrdemServicoException){
                    throw exception;
                }

                throw new OrdemServicoException("Houve um erro interno: " + exception.getMessage());
            }
        }

        return servico;
    }

    public Servico cadastrarServico(String numeroOrdemServico, Servico servico) throws Exception {
        if(!servicoCOL.validarServico(servico)){
            throw new OrdemServicoException("Serviço inválido");
        }

        try(Connection conexao = conexaoBD.getConexaoBD()){
            ServicoDAO servicoDAO = new ServicoDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                Long id = servicoDAO.inserirServico(numeroOrdemServico, servico);

                servico.setId(id);

                conexao.commit();
            }
            catch (Exception exception){
                conexao.rollback();

                if(exception instanceof OrdemServicoException){
                    throw exception;
                }

                throw new OrdemServicoException("Houve um erro interno: " + exception.getMessage());
            }
        }

        return servico;
    }
}
