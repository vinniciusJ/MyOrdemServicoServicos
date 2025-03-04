package br.unioeste.geral.ordemservico.servico.service.ordemservico;

import br.unioeste.apoio.bd.ConexaoBD;
import br.unioeste.geral.ordemservico.bo.ordemservico.OrdemServico;
import br.unioeste.geral.ordemservico.bo.servico.Servico;
import br.unioeste.geral.ordemservico.servico.col.ordemservico.OrdemServicoCOL;
import br.unioeste.geral.ordemservico.servico.dao.ordemservico.OrdemServicoDAO;
import br.unioeste.geral.ordemservico.servico.dao.servico.ServicoDAO;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UCOrdemServicoServicos {
    private final ConexaoBD conexaoBD;
    private final OrdemServicoCOL ordemServicoCOL;

    public UCOrdemServicoServicos() {
        conexaoBD = new ConexaoBD();
        ordemServicoCOL = new OrdemServicoCOL();
    }

    public List<OrdemServico> obterOrdemServicos() throws Exception {
        List<OrdemServico> ordemServicos = new ArrayList<>();

        try(Connection conexao = conexaoBD.getConexaoBD()){
            OrdemServicoDAO ordemServicoDAO = new OrdemServicoDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                ordemServicos = ordemServicoDAO.obterOrdemServicos();

                conexao.commit();
            }
            catch (Exception exception){
                if(exception instanceof OrdemServicoException){
                    throw exception;
                }

                throw new OrdemServicoException("Ocorreu um erro interno: " + exception.getMessage());
            }
        }

        return ordemServicos;
    }

    public OrdemServico obterOrdemServicoPorNumero(String numero) throws Exception {
        if(!ordemServicoCOL.validarNumero(numero)){
            throw new OrdemServicoException("Número inválido: " + numero);
        }

        OrdemServico ordemServico = null;

        try(Connection conexao = conexaoBD.getConexaoBD()){
            OrdemServicoDAO ordemServicoDAO = new OrdemServicoDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                ordemServico = ordemServicoDAO.obterOrdemServicoPorNumero(numero);

                conexao.commit();

                if(ordemServico == null){
                    throw new OrdemServicoException("Não foi possível encontrar ordem de serviço com número " + numero);
                }
            }
            catch (Exception exception){
                if(exception instanceof OrdemServicoException){
                    throw exception;
                }

                throw new OrdemServicoException("Ocorreu um erro interno: " + exception.getMessage());
            }
        }

        return ordemServico;
    }

    public OrdemServico cadastrarOrdemServico(OrdemServico ordemServico) throws Exception {
        if(!ordemServicoCOL.validarOrdemServico(ordemServico)){
            throw new OrdemServicoException("Ordem de serviço inválida");
        }

        try(Connection conexao = conexaoBD.getConexaoBD()){
            OrdemServicoDAO ordemServicoDAO = new OrdemServicoDAO(conexao);
            ServicoDAO servicoDAO = new ServicoDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                String numero = ordemServicoDAO.inserirOrdemServico(ordemServico);

                ordemServico.setNumero(numero);

                for(Servico servico: ordemServico.getServicosRealizados()){
                    servicoDAO.inserirServico(numero, servico);
                }

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

        return ordemServico;
    }
}
