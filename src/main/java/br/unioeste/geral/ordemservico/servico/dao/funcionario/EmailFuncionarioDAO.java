package br.unioeste.geral.ordemservico.servico.dao.funcionario;

import br.unioeste.apoio.bd.ConexaoBD;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;
import br.unioeste.geral.pessoa.bo.email.Email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmailFuncionarioDAO {
    private final ConexaoBD conexaoBD;

    public EmailFuncionarioDAO() {
        conexaoBD = new ConexaoBD();
    }

    public List<Email> obterEmailsFuncionario(Long idFuncionario) throws Exception {
        String sql = "SELECT * FROM email_funcionario WHERE id_funcionario = ?";

        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        List<Email> emails = new ArrayList<>();

        try{
            conexao = conexaoBD.getConexaoBD();
            stmt = conexao.prepareStatement(sql);

            stmt.setLong(1, idFuncionario);

            conexao.setAutoCommit(false);

            resultSet = stmt.executeQuery();

            while (resultSet.next()){
                emails.add(criarEmailBO(resultSet));
            }

            conexao.commit();
        }
        catch(SQLException e){
            throw new OrdemServicoException("Não foi possível encontrar emails para o funcionário com ID: " + idFuncionario);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível estabelecer conexão com o banco de dados");
        }
        finally {
            conexaoBD.encerrarConexoes(resultSet, stmt, conexao);
        }

        return emails;
    }

    public void inserirEmails(Long idFuncionario, List<Email> emails, Connection conexao) throws Exception {
        String sql = "INSERT INTO email_funcionario (endereco, id_funcionario) VALUES (?,?)";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            for(Email email : emails){
                stmt.setString(1, email.getEndereco());
                stmt.setLong(2, idFuncionario);

                stmt.executeUpdate();
            }
        }
    }

    private Email criarEmailBO(ResultSet resultSet) throws Exception {
        long id = resultSet.getLong("id");
        String enderecoEmail = resultSet.getString("endereco");

        return new Email(id, enderecoEmail);
    }
}
