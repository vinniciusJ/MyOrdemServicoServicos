package br.unioeste.geral.ordemservico.servico.service;

import br.unioeste.apoio.bd.ConexaoBD;
import br.unioeste.geral.ordemservico.bo.tiposervico.TipoServico;
import br.unioeste.geral.ordemservico.servico.col.TipoServicoCOL;
import br.unioeste.geral.ordemservico.servico.dao.tiposervico.TipoServicoDAO;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UCTipoServicoServicos {
    private final TipoServicoCOL tipoServicoCOL;
    private final ConexaoBD conexaoBD;

    public UCTipoServicoServicos(){
        tipoServicoCOL = new TipoServicoCOL();
        conexaoBD = new ConexaoBD();
    }

    public List<TipoServico> obterTiposServicos() throws Exception {
        List<TipoServico> tipoServicos = new ArrayList<>();

        try(Connection conexao = conexaoBD.getConexaoBD()){
            TipoServicoDAO tipoServicoDAO = new TipoServicoDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                tipoServicos = tipoServicoDAO.obterTiposServicos();

                conexao.commit();
            }
            catch (Exception exception){
                if(exception instanceof OrdemServicoException){
                    throw exception;
                }

                throw new OrdemServicoException("Houve um erro interno: " + exception.getMessage());
            }
        }

        return tipoServicos;
    }


    public TipoServico obterTipoServicoPorID(Long id) throws OrdemServicoException, Exception {
        if(!tipoServicoCOL.validarID(id)){
            throw new OrdemServicoException("ID inválido: " + id);
        }

        TipoServico tipoServico = null;

        try(Connection conexao = conexaoBD.getConexaoBD()){
            TipoServicoDAO tipoServicoDAO = new TipoServicoDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                tipoServico = tipoServicoDAO.obterTipoServicoPorID(id);

                conexao.commit();
            }
            catch (Exception exception){
                if(exception instanceof OrdemServicoException){
                    throw exception;
                }

                throw new OrdemServicoException("Houve um erro interno: " + exception.getMessage());
            }
        }

        return tipoServico;
    }

    public TipoServico cadastrarTipoServico(TipoServico tipoServico) throws OrdemServicoException, Exception {
        if(!tipoServicoCOL.validarTipoServico(tipoServico)){
            throw new OrdemServicoException("Tipo de serviço inválido");
        }

        try(Connection conexao = conexaoBD.getConexaoBD()){
            TipoServicoDAO tipoServicoDAO = new TipoServicoDAO(conexao);

            conexao.setAutoCommit(false);

            try{
                Long id = tipoServicoDAO.inserirTipoServico(tipoServico);

                if(id == null){
                    throw new OrdemServicoException("Não foi possível cadastrar o tipo de serviço");
                }

                tipoServico.setId(id);

                conexao.commit();
            }
            catch(Exception exception){
                conexao.rollback();

                if(exception instanceof OrdemServicoException){
                    throw exception;
                }

                throw new OrdemServicoException("Houve um problema interno: " + exception.getMessage());
            }
        }

        return tipoServico;
    }
}
