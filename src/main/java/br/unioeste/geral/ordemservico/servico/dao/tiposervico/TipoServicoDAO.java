package br.unioeste.geral.ordemservico.servico.dao.tiposervico;

import br.unioeste.apoio.bd.ConexaoBD;
import br.unioeste.geral.ordemservico.bo.servico.Servico;
import br.unioeste.geral.ordemservico.bo.tiposervico.TipoServico;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TipoServicoDAO {
    private final Connection conexao;

    public TipoServicoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public TipoServico obterTipoServicoPorID(Long id) throws Exception {
        String sql = "SELECT * from tipo_servico WHERE id = ?";

        TipoServico tipoServico = null;

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setLong(1, id);

            try(ResultSet resultSet = stmt.executeQuery()){
                if(resultSet.next()){
                    tipoServico = criarTipoServicoBO(resultSet);
                }
            }
        }
        catch (Exception exception){
            throw new OrdemServicoException("Não foi possível obter o tipo de serviço com ID: " + id);
        }

        return tipoServico;
    }

    public List<TipoServico> obterTiposServicos() throws OrdemServicoException {
        String sql = "SELECT * from tipo_servico";

        List<TipoServico> tiposServicos = new ArrayList<>();

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            try(ResultSet resultSet = stmt.executeQuery()){
                while(resultSet.next()){
                    tiposServicos.add(criarTipoServicoBO(resultSet));
                }
            }
        }
        catch (Exception exception){
            throw new OrdemServicoException("Não foi possível obter todos os tipos de servicos");
        }

        return tiposServicos;
    }

    public Long inserirTipoServico(TipoServico tipoServico) throws OrdemServicoException {
        String sql = "INSERT INTO tipo_servico (nome, valor_referencia) VALUES (?, ?)";

        try(PreparedStatement stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, tipoServico.getNome());
            stmt.setDouble(2, tipoServico.getValorReferencia());

            int resultado = stmt.executeUpdate();

            if(resultado == 0){
                throw new OrdemServicoException("Não foi possível cadastrar o tipo de serviço");
            }

            try(ResultSet resultSet = stmt.getGeneratedKeys()) {
                if(resultSet.next()){
                    tipoServico.setId(resultSet.getLong(1));
                }
            }
        }
        catch (Exception e){
            throw new OrdemServicoException("Não foi possível cadastrar o tipo de  serviço");
        }

        return tipoServico.getId();
    }

    private TipoServico criarTipoServicoBO(ResultSet resultSet) throws Exception {
        long id = resultSet.getLong("id");
        String nome = resultSet.getString("nome");
        double valorRef = resultSet.getDouble("valor_referencia");

        return new TipoServico(id, nome, valorRef);
    }
}
