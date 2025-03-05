package br.unioeste.geral.ordemservico.servico.dao.funcionario;

import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;
import br.unioeste.geral.pessoa.bo.email.Email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmailFuncionarioDAO {
    private final Connection conexao;

    public EmailFuncionarioDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public List<Email> obterEmailsFuncionario(Long idFuncionario) throws Exception {
        String sql = "SELECT * FROM email_funcionario WHERE id_funcionario = ?";

        List<Email> emails = new ArrayList<>();

        try(PreparedStatement stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, idFuncionario);

            try(ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()){
                    emails.add(criarEmailBO(resultSet));
                }
            }
        }
        catch(Exception e){
            throw new OrdemServicoException("Não foi possível obter todos os emails do funcionário com ID: " + idFuncionario);
        }

        return emails;
    }

    public Long inserirEmail(Long idFuncionario, Email email) throws Exception {
        String sql = "INSERT INTO email_funcionario (id_funcionario, endereco) VALUES (?, ?)";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setLong(1, idFuncionario);
            stmt.setString(2, email.getEndereco());

            int resultado = stmt.executeUpdate();

            if(resultado == 0){
                throw new OrdemServicoException("Não foi possível cadastrar o email " + email.getEndereco());
            }

            try(ResultSet resultSet = stmt.getGeneratedKeys()) {
                if(resultSet.next()){
                    email.setId(resultSet.getLong(1));
                }
            }
        }
        catch (Exception e){
            throw new OrdemServicoException("Não foi possível cadastrar o email " + email.getEndereco());
        }

        return email.getId();
    }

    private Email criarEmailBO(ResultSet resultSet) throws Exception {
        long id = resultSet.getLong("id");
        String enderecoEmail = resultSet.getString("endereco");

        return new Email(id, enderecoEmail);
    }
}
