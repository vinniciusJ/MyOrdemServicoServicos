package br.unioeste.geral.ordemservico.servico.dao.funcionario;

import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;
import br.unioeste.geral.pessoa.bo.ddd.DDD;
import br.unioeste.geral.pessoa.bo.ddi.DDI;
import br.unioeste.geral.pessoa.bo.telefone.Telefone;
import br.unioeste.geral.pessoa.servico.dao.DDDDAO;
import br.unioeste.geral.pessoa.servico.dao.DDIDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TelefoneFuncionarioDAO {
    private final Connection conexao;

    private final DDDDAO dddDAO;
    private final DDIDAO ddiDAO;

    public TelefoneFuncionarioDAO(Connection conexao) {
        this.conexao = conexao;

        this.dddDAO = new DDDDAO(conexao);
        this.ddiDAO = new DDIDAO(conexao);
    }

    public List<Telefone> obterTelefonesFuncionario(Long idFuncionario) throws Exception {
        String sql = "SELECT * FROM telefone_funcionario WHERE id_funcionario = ?";

        List<Telefone> telefones = new ArrayList<>();

        try(PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idFuncionario);

            try(ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()){
                    telefones.add(criarTelefoneBO(resultSet));
                }
            }
        }
        catch (Exception e) {
            throw new OrdemServicoException("Não foi possível obter telefones para o funcionário com ID " + idFuncionario);
        }

        return telefones;
    }

    public Long inserirTelefone(Long idFuncionario, Telefone telefone) throws Exception {
        String sql = "INSERT INTO telefone_funcionario (numero, ddd_ddd, ddi_ddi, id_funcionario) values (?, ?, ?, ?)";

        try(PreparedStatement stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, telefone.getNumero());
            stmt.setString(2, telefone.getDdd().getNumero());
            stmt.setString(3, telefone.getDdi().getNumero());
            stmt.setLong(4, idFuncionario);

            int resultado = stmt.executeUpdate();

            if(resultado == 0){
                throw new OrdemServicoException("Não foi possível cadastrar o telefone " + telefone.getNumero());
            }

            try(ResultSet resultSet = stmt.getGeneratedKeys()) {
                if(resultSet.next()){
                    telefone.setId(resultSet.getLong(1));
                }
            }
        }
        catch (Exception e){
            throw new OrdemServicoException("Não foi possível cadastrar o telefone " + telefone.getNumero());
        }

        return telefone.getId();
    }

    private Telefone criarTelefoneBO(ResultSet resultSet) throws Exception {
        long id = resultSet.getLong("id");

        String numero = resultSet.getString("numero");
        String numeroDDD = resultSet.getString("ddd_ddd");
        String numeroDDI = resultSet.getString("ddi_ddi");

        DDD ddd = dddDAO.obterDDD(numeroDDD);
        DDI ddi = ddiDAO.obterDDI(numeroDDI);

        return new Telefone(id, numero, ddd, ddi);
    }
}
