package br.unioeste.geral.ordemservico.servico.dao;

import br.unioeste.apoio.bd.ConexaoBD;
import br.unioeste.geral.endereco.servico.exception.EnderecoException;
import br.unioeste.geral.pessoa.bo.email.Email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmailClienteDAO {
    private final ConexaoBD conexaoBD;

    public EmailClienteDAO() {
        conexaoBD = new ConexaoBD();
    }

    public List<Email> obterEmailsCliente(Long idCliente) throws Exception {
        String sql = "SELECT * FROM email_cliente WHERE id_cliente = ?";

        List<Email> emails = new ArrayList<Email>();

        try{
            Connection conexao = conexaoBD.getConexaoBD();
            PreparedStatement stmt = conexao.prepareStatement(sql);

            stmt.setLong(1, idCliente);

            conexao.setAutoCommit(false);

            try( ResultSet resultSet = stmt.executeQuery()){
                while (resultSet.next()){
                    emails.add(criarEmailBO(resultSet));
                }
            }

            conexao.commit();
        }
        catch(SQLException e){
            throw new EnderecoException("Não foi possível encontrar emails para o cliente com ID: " + idCliente);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível estabelecer conexão com o banco de dados");
        }

        return emails;
    }

    public void inserirEmails(Long idCliente, List<Email> emails, Connection conexao) throws Exception {
        String sql = "INSERT INTO email_cliente (endereco, id_cliente) VALUES (?,?)";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            for(Email email : emails){
                stmt.setString(1, email.getEndereco());
                stmt.setLong(2, idCliente);

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
