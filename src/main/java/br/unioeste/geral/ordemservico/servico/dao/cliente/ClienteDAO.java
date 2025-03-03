package br.unioeste.geral.ordemservico.servico.dao.cliente;

import br.unioeste.apoio.bd.ConexaoBD;
import br.unioeste.geral.endereco.bo.endereco.Endereco;
import br.unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import br.unioeste.geral.endereco.servico.exception.EnderecoException;
import br.unioeste.geral.endereco.servico.service.UCEnderecoServicos;
import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;
import br.unioeste.geral.pessoa.bo.email.Email;
import br.unioeste.geral.pessoa.bo.telefone.Telefone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private final ConexaoBD conexaoBD;

    private final EmailClienteDAO emailClienteDAO;
    private final TelefoneClienteDAO telefoneClienteDAO;
    private final UCEnderecoServicos enderecoServicos;

    public ClienteDAO(){
        conexaoBD = new ConexaoBD();

        emailClienteDAO = new EmailClienteDAO();
        telefoneClienteDAO = new TelefoneClienteDAO();

        enderecoServicos = new UCEnderecoServicos();
    }

    public List<Cliente> obterClientes() throws Exception {
        String sql = "SELECT * FROM cliente";

        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        List<Cliente> clientes = new ArrayList<>();

        try{
            conexao = conexaoBD.getConexaoBD();
            stmt = conexao.prepareStatement(sql);

            conexao.setAutoCommit(false);

            resultSet = stmt.executeQuery();

            while (resultSet.next()){
                clientes.add(criarClienteBO(resultSet));
            }

            conexao.commit();
        }
        catch(SQLException e){
            throw new EnderecoException("Não foi possível buscar todos os clientes");
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível estabelecer conexão com o banco de dados");
        }
        finally {
            conexaoBD.encerrarConexoes(resultSet, stmt, conexao);
        }

        return clientes;
    }

    public Cliente obterClientePorID(Long id) throws Exception {
        String sql = "SELECT * FROM cliente WHERE id = ?";

        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        Cliente cliente = null;

        try{
            conexao = conexaoBD.getConexaoBD();
            stmt = conexao.prepareStatement(sql);

            conexao.setAutoCommit(false);

            stmt.setLong(1, id);
            resultSet = stmt.executeQuery();

            if(resultSet.next()){
                cliente = criarClienteBO(resultSet);
            }

            conexao.commit();
        }
        catch(SQLException e){
            throw new EnderecoException("Não foi possível buscar todos os clientes");
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível estabelecer conexão com o banco de dados");
        }
        finally {
            conexaoBD.encerrarConexoes(resultSet, stmt, conexao);
        }

        return cliente;
    }

    public Cliente obterClientePorCPF(String cpf) throws Exception {
        String sql = "SELECT * FROM cliente WHERE cpf = ?";

        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        try{
            conexao = conexaoBD.getConexaoBD();
            stmt = conexao.prepareStatement(sql);

            conexao.setAutoCommit(false);

            stmt.setString(1, cpf);
            resultSet = stmt.executeQuery();

            if(resultSet.next()){
                return criarClienteBO(resultSet);
            }

            conexao.commit();
        }
        catch(SQLException e){
            throw new EnderecoException("Não foi possível buscar todos os clientes");
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível estabelecer conexão com o banco de dados");
        }
        finally {
            conexaoBD.encerrarConexoes(resultSet, stmt, conexao);
        }

        return null;
    }

    public Cliente inserirCliente(Cliente cliente) throws Exception {
        String sql = """
                    INSERT INTO cliente (primeiro_nome, nome_do_meio, ultimo_nome, nome_social, complemento_endereco, numero_endereco, cpf, id_endereco)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;

        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        try{
            conexao = conexaoBD.getConexaoBD();
            stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            conexao.setAutoCommit(false);

            EnderecoEspecifico endereco = cliente.getEndereco();

            stmt.setString(1, cliente.getPrimeiroNome());
            stmt.setString(2, cliente.getNomeDoMeio());
            stmt.setString(3, cliente.getUltimoNome());
            stmt.setString(4, cliente.getNomeSocial());
            stmt.setString(5, endereco.getComplementoEndereco());
            stmt.setString(6, endereco.getNumeroEndereco());
            stmt.setString(7, cliente.getCpf());
            stmt.setLong(8, endereco.getEndereco().getId());

            int registrosInseridos = stmt.executeUpdate();

            if(registrosInseridos == 0){
                throw new OrdemServicoException("Não foi possível cadastrar o cliente");
            }

            resultSet = stmt.getGeneratedKeys();

            if(resultSet.next()){
                long id = resultSet.getLong(1);

                cliente.setId(id);

                emailClienteDAO.inserirEmails(id, cliente.getEmails(), conexao);
                telefoneClienteDAO.inserirTelefones(id, cliente.getTelefones(), conexao);

                conexao.commit();
            }
            else {
                throw new OrdemServicoException("Não foi possível cadastrar o cliente");
            }
        }
        catch(SQLException e){
            if(conexao != null){
                conexao.rollback();
            }

            throw new OrdemServicoException("Não foi possível cadastrar o cliente");
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível estabelecer conexão com o banco de dados");
        }
        finally {
           conexaoBD.encerrarConexoes(resultSet, stmt, conexao);
        }

        return null;
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

        Endereco endereco = enderecoServicos.obterEnderecoPorID(idEndereco);
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
