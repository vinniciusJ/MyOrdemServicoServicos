package br.unioeste.geral.ordemservico.servico.dao.cliente;

import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;
import br.unioeste.geral.pessoa.bo.email.Email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmailClienteDAO {
    private final Connection conexao;

    public EmailClienteDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public List<Email> obterEmailsCliente(Long idCliente) throws Exception {
        String sql = "SELECT * FROM email_cliente WHERE id_cliente = ?";

        List<Email> emails = new ArrayList<>();

        try(PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idCliente);

            try(ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()){
                    emails.add(criarEmailBO(resultSet));
                }
            }
        }
        catch(Exception e){
            throw new OrdemServicoException("Não foi possível obter todos os emails do cliente com ID: " + idCliente);
        }

        return emails;
    }

    public Long inserirEmail(Long idCliente, Email email) throws Exception {
        String sql = "INSERT INTO email_cliente (id_cliente, email) VALUES (?, ?)";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, email.getEndereco());
            stmt.setLong(2, idCliente);

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
