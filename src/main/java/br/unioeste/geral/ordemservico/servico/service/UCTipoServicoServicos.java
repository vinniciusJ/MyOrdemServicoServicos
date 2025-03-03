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

    public List<TipoServico> obterTiposServicos() throws OrdemServicoException, Exception {
        Connection conexao = null;
        List<TipoServico> tipoServicos = new ArrayList<TipoServico>();

        try{
            conexao = conexaoBD.getConexaoBD();
            TipoServicoDAO tipoServicoDAO = new TipoServicoDAO(conexao);

            conexao.setAutoCommit(false);

            tipoServicos = tipoServicoDAO.obterTiposServicos();

            conexao.commit();
        }
        catch(Exception exception){
            if(exception instanceof OrdemServicoException){
                throw exception;
            }

            throw new OrdemServicoException("Houve um problema interno: " + exception.getMessage());
        }
        finally {
            conexaoBD.encerrarConexoes(null, null, conexao);
        }

        return tipoServicos;
    }

    public TipoServico obterTipoServicoPorID(Long id) throws OrdemServicoException, Exception {
        if(!tipoServicoCOL.validarID(id)){
            throw new OrdemServicoException("ID inválido: " + id);
        }

        Connection conexao = null;
        TipoServico tipoServico = null;

        try{
            conexao = conexaoBD.getConexaoBD();
            TipoServicoDAO tipoServicoDAO = new TipoServicoDAO(conexao);

            conexao.setAutoCommit(false);

            tipoServico = tipoServicoDAO.obterTipoServicoPorID(id);

            conexao.commit();
        }
        catch(Exception exception){
            if(exception instanceof OrdemServicoException){
                throw (OrdemServicoException) exception;
            }

            throw new OrdemServicoException("Houve um problema interno: " + exception.getMessage());
        }
        finally {
            conexaoBD.encerrarConexoes(null, null, conexao);
        }

        return tipoServico;
    }

    public TipoServico cadastrarTipoServico(TipoServico tipoServico) throws OrdemServicoException, Exception {
        if(!tipoServicoCOL.validarTipoServico(tipoServico)){
            throw new OrdemServicoException("Tipo de serviço inválido");
        }

        Connection conexao = null;

        try{
            conexao = conexaoBD.getConexaoBD();
            TipoServicoDAO tipoServicoDAO = new TipoServicoDAO(conexao);

            conexao.setAutoCommit(false);

            Long id = tipoServicoDAO.inserirTipoServico(tipoServico);

            if(id == null){
                throw new OrdemServicoException("Não foi possível cadastrar o tipo de serviço");
            }

            tipoServico.setId(id);

            conexao.commit();
        }
        catch(Exception exception){
            if(conexao != null){
                conexao.rollback();
            }

            if(exception instanceof OrdemServicoException){
                throw (OrdemServicoException) exception;
            }

            throw new OrdemServicoException("Houve um problema interno: " + exception.getMessage());
        }
        finally {
            conexaoBD.encerrarConexoes(null, null, conexao);
        }

        return tipoServico;
    }
}
