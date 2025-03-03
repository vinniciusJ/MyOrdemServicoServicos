package br.unioeste.geral.ordemservico.servico.dao.funcionario;

import br.unioeste.apoio.bd.ConexaoBD;
import br.unioeste.geral.endereco.bo.endereco.Endereco;
import br.unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import br.unioeste.geral.endereco.servico.exception.EnderecoException;
import br.unioeste.geral.endereco.servico.service.UCEnderecoServicos;
import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.ordemservico.bo.funcionario.Funcionario;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;
import br.unioeste.geral.pessoa.bo.email.Email;
import br.unioeste.geral.pessoa.bo.telefone.Telefone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {
    private final ConexaoBD conexaoBD;

    private final EmailFuncionarioDAO emailFuncionarioDAO;
    private final TelefoneFuncionarioDAO telefoneFuncionarioDAO;
    private final UCEnderecoServicos enderecoServicos;

    public FuncionarioDAO(){
        conexaoBD = new ConexaoBD();

        emailFuncionarioDAO = new EmailFuncionarioDAO();
        telefoneFuncionarioDAO = new TelefoneFuncionarioDAO();

        enderecoServicos = new UCEnderecoServicos();
    }

    public List<Funcionario> obterFuncionarios() throws Exception {
        String sql = "SELECT * FROM funcionario";

        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        List<Funcionario> funcionarios = new ArrayList<>();

        try{
            conexao = conexaoBD.getConexaoBD();
            stmt = conexao.prepareStatement(sql);

            conexao.setAutoCommit(false);

            resultSet = stmt.executeQuery();

            while (resultSet.next()){
                funcionarios.add(criarFuncionarioBO(resultSet));
            }

            conexao.commit();
        }
        catch(SQLException e){
            throw new EnderecoException("Não foi possível buscar todos os funcionários");
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível estabelecer conexão com o banco de dados");
        }
        finally {
            conexaoBD.encerrarConexoes(resultSet, stmt, conexao);
        }

        return funcionarios;
    }

    public Funcionario obterFuncionarioPorID(Long id) throws Exception {
        String sql = "SELECT * FROM funcionario WHERE id = ?";

        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        Funcionario funcionario = null;

        try{
            conexao = conexaoBD.getConexaoBD();
            stmt = conexao.prepareStatement(sql);

            conexao.setAutoCommit(false);

            stmt.setLong(1, id);
            resultSet = stmt.executeQuery();

            if(resultSet.next()){
                funcionario = criarFuncionarioBO(resultSet);
            }

            conexao.commit();
        }
        catch(SQLException e){
            throw new EnderecoException("Não foi possível buscar o funcionário com ID: " + id);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível estabelecer conexão com o banco de dados");
        }
        finally {
            conexaoBD.encerrarConexoes(resultSet, stmt, conexao);
        }

        return funcionario;
    }

    public Funcionario obterFuncionarioPorCPF(String cpf) throws Exception {
        String sql = "SELECT * FROM funcionario WHERE cpf = ?";

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
                return criarFuncionarioBO(resultSet);
            }

            conexao.commit();
        }
        catch(SQLException e){
            throw new EnderecoException("Não foi possível buscar todos os funcionários");
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível estabelecer conexão com o banco de dados");
        }
        finally {
            conexaoBD.encerrarConexoes(resultSet, stmt, conexao);
        }

        return null;
    }

    public Funcionario inserirFuncionario(Funcionario funcionario) throws Exception {
        String sql = """
                    INSERT INTO funcionario (primeiro_nome, nome_do_meio, ultimo_nome, nome_social, complemento_endereco, numero_endereco, cpf, id_endereco)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;

        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        try{
            conexao = conexaoBD.getConexaoBD();
            stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            conexao.setAutoCommit(false);

            EnderecoEspecifico endereco = funcionario.getEndereco();

            stmt.setString(1, funcionario.getPrimeiroNome());
            stmt.setString(2, funcionario.getNomeDoMeio());
            stmt.setString(3, funcionario.getUltimoNome());
            stmt.setString(4, funcionario.getNomeSocial());
            stmt.setString(5, endereco.getComplementoEndereco());
            stmt.setString(6, endereco.getNumeroEndereco());
            stmt.setString(7, funcionario.getCpf());
            stmt.setLong(8, endereco.getEndereco().getId());

            int registrosInseridos = stmt.executeUpdate();

            if(registrosInseridos == 0){
                throw new OrdemServicoException("Não foi possível cadastrar o cliente");
            }

            resultSet = stmt.getGeneratedKeys();

            if(resultSet.next()){
                long id = resultSet.getLong(1);

                funcionario.setId(id);

                emailFuncionarioDAO.inserirEmails(id, funcionario.getEmails(), conexao);
                telefoneFuncionarioDAO.inserirTelefones(id, funcionario.getTelefones(), conexao);

                conexao.commit();
            }
            else {
                throw new OrdemServicoException("Não foi possível cadastrar o funcionário");
            }
        }
        catch(SQLException e){
            if(conexao != null){
                conexao.rollback();
            }

            throw new OrdemServicoException("Não foi possível cadastrar o funcionário");
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível estabelecer conexão com o banco de dados");
        }
        finally {
           conexaoBD.encerrarConexoes(resultSet, stmt, conexao);
        }

        return null;
    }

    private Funcionario criarFuncionarioBO(ResultSet resultSet) throws Exception {
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

        List<Email> emails = emailFuncionarioDAO.obterEmailsFuncionario(id);
        List<Telefone> telefones = telefoneFuncionarioDAO.obterTelefonesFuncionario(id);

        Funcionario funcionario = new Funcionario();

        funcionario.setId(id);
        funcionario.setPrimeiroNome(primeiroNome);
        funcionario.setNomeDoMeio(nomeDoMeio);
        funcionario.setUltimoNome(ultimoNome);
        funcionario.setNomeSocial(nomeSocial);
        funcionario.setNome(primeiroNome + " " + nomeDoMeio + " " + ultimoNome);
        funcionario.setCpf(cpf);
        funcionario.setEndereco(enderecoEspecifico);
        funcionario.setEmails(emails);
        funcionario.setTelefones(telefones);

        return funcionario;
    }

}
