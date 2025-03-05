package br.unioeste.geral.ordemservico.servico.dao.servico;

import br.unioeste.geral.ordemservico.bo.servico.Servico;
import br.unioeste.geral.ordemservico.bo.tiposervico.TipoServico;
import br.unioeste.geral.ordemservico.servico.dao.tiposervico.TipoServicoDAO;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {
    private final Connection conexao;

    private final TipoServicoDAO tipoServicoDAO;

    public ServicoDAO(Connection conexao){
        this.conexao = conexao;

        this.tipoServicoDAO = new TipoServicoDAO(conexao);
    }

    public Servico obterServicoPorID(Long id) throws Exception {
        String sql = "SELECT * from servico WHERE id = ?";

        Servico servico = null;

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setLong(1, id);

            try(ResultSet resultSet = stmt.executeQuery()){
                if(resultSet.next()){
                    servico = criarServicoBO(resultSet);
                }
            }
        }
        catch (Exception exception){
            throw new OrdemServicoException("Não foi possível obter o servico com ID: " + id);
        }

        return servico;
    }

    public List<Servico> obterServicos() throws Exception {
        String sql = "SELECT * from servico";

        List<Servico> servicos = new ArrayList<>();

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            try(ResultSet resultSet = stmt.executeQuery()){
                while(resultSet.next()){
                    servicos.add(criarServicoBO(resultSet));
                }
            }
        }
        catch (Exception exception){
            throw new OrdemServicoException("Não foi possível obter todos os servicos");
        }

        return servicos;
    }

    public Long inserirServico(String numeroOrdemServico, Servico servico) throws Exception {
        String sql = "INSERT INTO servico (valor_cobrado, numero_ordem_servico, id_tipo_servico) VALUES (?, ?, ?)";

        try(PreparedStatement stmt = conexao.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS)){
            stmt.setDouble(1, servico.getValorCobrado());
            stmt.setString(2, numeroOrdemServico);
            stmt.setLong(3, servico.getTipoServico().getId());

            int resultado = stmt.executeUpdate();

            if(resultado == 0){
                throw new OrdemServicoException("Não foi possível cadastrar o serviço");
            }

            try(ResultSet resultSet = stmt.getGeneratedKeys()) {
                if(resultSet.next()){
                    servico.setId(resultSet.getLong(1));
                }
            }
        }
        catch (Exception e){
            throw new OrdemServicoException("Não foi possível cadastrar o serviço");
        }

        return servico.getId();
    }

    public List<Servico> obterServicosPorNumeroOrdemServico(String numero) throws Exception {
        String sql = "SELECT * from servico WHERE numero_ordem_servico = ?";

        List<Servico> servicos = new ArrayList<>();

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, numero);

            try(ResultSet resultSet = stmt.executeQuery()){
                while(resultSet.next()){
                    servicos.add(criarServicoBO(resultSet));
                }
            }
        }
        catch (Exception exception){
            throw new OrdemServicoException("Não foi possível obter todos os servicos da ordem de servico " + numero);
        }

        return servicos;
    }

    private Servico criarServicoBO(ResultSet resultSet) throws Exception {
        long id = resultSet.getLong("id");
        long idTipoServico = resultSet.getLong("id_tipo_servico");

        Double valorCobrado = resultSet.getDouble("valor_cobrado");

        TipoServico tipoServico = tipoServicoDAO.obterTipoServicoPorID(idTipoServico);

        return new Servico(id, valorCobrado, tipoServico);
    }
}
