package br.unioeste.geral.ordemservico.servico.dao.tiposervico;

import br.unioeste.apoio.bd.ConexaoBD;
import br.unioeste.geral.ordemservico.bo.tiposervico.TipoServico;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TipoServicoDAO {
    private final ConexaoBD conexaoBD;
    private final Connection conexao;

    public TipoServicoDAO(Connection conexao) {
        this.conexao = conexao;
        this.conexaoBD = new ConexaoBD();
    }

    public TipoServico obterTipoServicoPorID(Long id) throws Exception {
        String sql = "SELECT * from tipo_servico WHERE id = ?";

        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        TipoServico tipoServico = null;

        try{
            stmt = conexao.prepareStatement(sql);
            resultSet = stmt.executeQuery();

            if(resultSet.next()){
                tipoServico = instanciarTipoServicoBO(resultSet);
            }
        }
        catch (Exception exception){
            throw new OrdemServicoException("Não foi possível obter todos os tipos de servicos");
        }
        finally {
            conexaoBD.encerrarConexoes(resultSet, stmt,  null);
        }

        return tipoServico;
    }

    public List<TipoServico> obterTiposServicos() throws OrdemServicoException {
        String sql = "SELECT * from tipo_servico";

        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        List<TipoServico> tiposServicos = new ArrayList<>();

        try{
            stmt = conexao.prepareStatement(sql);
            resultSet = stmt.executeQuery();

            while (resultSet.next()){
                tiposServicos.add(instanciarTipoServicoBO(resultSet));
            }
        }
        catch (Exception exception){
            throw new OrdemServicoException("Não foi possível obter todos os tipos de servicos");
        }

        return tiposServicos;
    }

    public Long inserirTipoServico(TipoServico tipoServico) throws OrdemServicoException {
        String sql = "INSERT INTO tipo_servico (nome, valor_referencia) VALUES (?, ?)";

        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        try{
            stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            stmt.setString(1, tipoServico.getNome());
            stmt.setDouble(2, tipoServico.getValorReferencia());

            int resultado = stmt.executeUpdate();

            if(resultado == 0){
                throw new OrdemServicoException("Não foi possível cadastrrar o cliente");
            }

            resultSet = stmt.getGeneratedKeys();

            if(resultSet.next()){
                long id = resultSet.getLong(1);

                tipoServico.setId(id);
            }
        }
        catch (Exception ex){
            throw new OrdemServicoException("Não foi possível cadastrrar o cliente");
        }

        return tipoServico.getId();
    }

    private TipoServico instanciarTipoServicoBO(ResultSet resultSet) throws Exception {
        long id = resultSet.getLong("id");
        String nome = resultSet.getString("nome");
        double valorRef = resultSet.getDouble("valor_referencia");

        return new TipoServico(id, nome, valorRef);
    }
}
