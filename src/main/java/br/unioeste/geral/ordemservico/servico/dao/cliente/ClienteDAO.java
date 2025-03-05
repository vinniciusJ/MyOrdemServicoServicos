package br.unioeste.geral.ordemservico.servico.dao.cliente;

import br.unioeste.geral.endereco.bo.endereco.Endereco;
import br.unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import br.unioeste.geral.endereco.servico.dao.EnderecoDAO;
import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;
import br.unioeste.geral.pessoa.bo.email.Email;
import br.unioeste.geral.pessoa.bo.telefone.Telefone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private final Connection conexao;

    private final EmailClienteDAO emailClienteDAO;
    private final TelefoneClienteDAO telefoneClienteDAO;
    private final EnderecoDAO enderecoDAO;

    public ClienteDAO(Connection conexao){
        this.conexao = conexao;

        this.emailClienteDAO = new EmailClienteDAO(conexao);
        this.telefoneClienteDAO = new TelefoneClienteDAO(conexao);
        this.enderecoDAO = new EnderecoDAO(conexao);
    }

    public List<Cliente> obterClientes() throws Exception {
        String sql = "SELECT * FROM cliente";

        List<Cliente> clientes = new ArrayList<>();

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            try(ResultSet resultSet = stmt.executeQuery()){
                while (resultSet.next()){
                    clientes.add(criarClienteBO(resultSet));
                }
            }
        }
        catch (Exception e){
            throw new OrdemServicoException("Não foi possível obter todos os clientes");
        }

        return clientes;
    }

    public Cliente obterClientePorID(Long id) throws Exception {
        String sql = "SELECT * FROM cliente WHERE id = ?";

        Cliente client = null;

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setLong(1, id);

            try(ResultSet resultSet = stmt.executeQuery()){
                if (resultSet.next()){
                    client = criarClienteBO(resultSet);
                }
            }
        }
        catch (Exception e){
            throw new OrdemServicoException("Não foi possível obter o cliente com ID " + id);
        }

        return client;
    }

    public Cliente obterClientePorCPF(String cpf) throws Exception {
        String sql = "SELECT * FROM cliente WHERE cpf = ?";

        Cliente client = null;

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, cpf);

            try(ResultSet resultSet = stmt.executeQuery()){
                if (resultSet.next()){
                    client = criarClienteBO(resultSet);
                }
            }
        }
        catch (Exception e){
            throw new OrdemServicoException("Não foi possível obter o cliente com CPF " + cpf);
        }

        return client;
    }

    public Long inserirCliente(Cliente cliente) throws Exception {
        String sql = """
                    INSERT INTO cliente (primeiro_nome, nome_do_meio, ultimo_nome, nome_social, complemento_endereco, numero_endereco, cpf, id_endereco)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;

        try(PreparedStatement stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            EnderecoEspecifico endereco = cliente.getEndereco();

            stmt.setString(1, cliente.getPrimeiroNome());
            stmt.setString(2, cliente.getNomeDoMeio());
            stmt.setString(3, cliente.getUltimoNome());
            stmt.setString(4, cliente.getNomeSocial());
            stmt.setString(5, endereco.getComplementoEndereco());
            stmt.setString(6, endereco.getNumeroEndereco());
            stmt.setString(7, cliente.getCpf());
            stmt.setLong(8, endereco.getEndereco().getId());

            int resultado = stmt.executeUpdate();

            if(resultado == 0){
                throw new OrdemServicoException("Não foi possível cadastrar o cliente " + cliente.getPrimeiroNome());
            }

            try(ResultSet resultSet = stmt.getGeneratedKeys()) {
                if(resultSet.next()){
                    cliente.setId(resultSet.getLong(1));
                }
            }
        }
        catch (Exception e){
            throw new OrdemServicoException("Não foi possível cadastrar o cliente " + cliente.getPrimeiroNome());
        }

        return cliente.getId();
    }

    private Cliente criarClienteBO(ResultSet resultSet) throws Exception {
        long id = resultSet.getLong("id");
        String primeiroNome = resultSet.getString("primeiro_nome");
        String nomeDoMeio = resultSet.getString("nome_do_meio");
        String ultimoNome = resultSet.getString("ultimo_nome");
        String nomeSocial = resultSet.getString("nome_social");
        String cpf = resultSet.getString("cpf");
        String complementoEndereco = resultSet.getString("complemento_endereco");
        String numeroEndereco = resultSet.getString("numero_endereco");
        long idEndereco = resultSet.getLong("id_endereco");

        Endereco endereco = enderecoDAO.obterEnderecoPorID(idEndereco);
        EnderecoEspecifico enderecoEspecifico = new EnderecoEspecifico(numeroEndereco, complementoEndereco, endereco);

        List<Email> emails = emailClienteDAO.obterEmailsCliente(id);
        List<Telefone> telefones = telefoneClienteDAO.obterTelefonesCliente(id);

        Cliente cliente = new Cliente();

        cliente.setId(id);
        cliente.setPrimeiroNome(primeiroNome);
        cliente.setNomeDoMeio(nomeDoMeio);
        cliente.setUltimoNome(ultimoNome);
        cliente.setNomeSocial(nomeSocial);
        cliente.setNome(primeiroNome + " " + nomeDoMeio + " " + ultimoNome);
        cliente.setCpf(cpf);
        cliente.setEndereco(enderecoEspecifico);
        cliente.setEmails(emails);
        cliente.setTelefones(telefones);

        return cliente;
    }

}
