package br.unioeste.geral.ordemservico.servico.dao.funcionario;

import br.unioeste.geral.endereco.bo.endereco.Endereco;
import br.unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import br.unioeste.geral.endereco.servico.dao.EnderecoDAO;
import br.unioeste.geral.ordemservico.bo.funcionario.Funcionario;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;
import br.unioeste.geral.pessoa.bo.email.Email;
import br.unioeste.geral.pessoa.bo.telefone.Telefone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {
    private final Connection conexao;

    private final EmailFuncionarioDAO emailFuncionarioDAO;
    private final TelefoneFuncionarioDAO telefoneFuncionarioDAO;
    private final EnderecoDAO enderecoDAO;

    public FuncionarioDAO(Connection conexao){
        this.conexao = conexao;

        this.emailFuncionarioDAO = new EmailFuncionarioDAO(conexao);
        this.telefoneFuncionarioDAO = new TelefoneFuncionarioDAO(conexao);
        this.enderecoDAO = new EnderecoDAO(conexao);
    }

    public List<Funcionario> obterFuncionarios() throws Exception {
        String sql = "SELECT * FROM funcionario";

        List<Funcionario> funcionarios = new ArrayList<>();

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            try(ResultSet resultSet = stmt.executeQuery()){
                while (resultSet.next()){
                    funcionarios.add(criarFuncionarioBO(resultSet));
                }
            }
        }
        catch (Exception e){
            throw new OrdemServicoException("Não foi possível obter todos os funcionarios");
        }

        return funcionarios;
    }

    public Funcionario obterFuncionarioPorID(Long id) throws Exception {
        String sql = "SELECT * FROM funcionario WHERE id = ?";

        Funcionario funcionario = null;

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setLong(1, id);

            try(ResultSet resultSet = stmt.executeQuery()){
                if (resultSet.next()){
                    funcionario = criarFuncionarioBO(resultSet);
                }
            }
        }
        catch (Exception e){
            throw new OrdemServicoException("Não foi possível obter o funcionário com ID " + id);
        }

        return funcionario;
    }

    public Funcionario obterFuncionariosPorCPF(String cpf) throws Exception {
        String sql = "SELECT * FROM funcionario WHERE cpf = ?";

        Funcionario funcionario = null;

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, cpf);

            try(ResultSet resultSet = stmt.executeQuery()){
                if (resultSet.next()){
                    funcionario = criarFuncionarioBO(resultSet);
                }
            }
        }
        catch (Exception e){
            throw new OrdemServicoException("Não foi possível obter o funcionário com CPF " + cpf);
        }

        return funcionario;
    }

    public Long inserirFuncionario(Funcionario funcionario) throws Exception {
        String sql = """
                    INSERT INTO funcionario (primeiro_nome, nome_do_meio, ultimo_nome, nome_social, complemento_endereco, numero_endereco, cpf, id_endereco)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            EnderecoEspecifico endereco = funcionario.getEndereco();

            stmt.setString(1, funcionario.getPrimeiroNome());
            stmt.setString(2, funcionario.getNomeDoMeio());
            stmt.setString(3, funcionario.getUltimoNome());
            stmt.setString(4, funcionario.getNomeSocial());
            stmt.setString(5, endereco.getComplementoEndereco());
            stmt.setString(6, endereco.getNumeroEndereco());
            stmt.setString(7, funcionario.getCpf());
            stmt.setLong(8, endereco.getEndereco().getId());

            int resultado = stmt.executeUpdate();

            if(resultado == 0){
                throw new OrdemServicoException("Não foi possível cadastrar o funcionario " + funcionario.getPrimeiroNome());
            }

            try(ResultSet resultSet = stmt.getGeneratedKeys()) {
                if(resultSet.next()){
                    funcionario.setId(resultSet.getLong(1));
                }
            }
        }
        catch (Exception e){
            throw new OrdemServicoException("Não foi possível cadastrar o funcionario " + funcionario.getPrimeiroNome());
        }

        return funcionario.getId();
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

        Endereco endereco = enderecoDAO.obterEnderecoPorID(idEndereco);
        EnderecoEspecifico enderecoEspecifico = new EnderecoEspecifico(numeroEndereco, complementoEndereco, endereco);

        List<Email> emails = emailFuncionarioDAO.obterEmailsFuncionario(id);
        List<Telefone> telefones = telefoneFuncionarioDAO.obterTelefonesCliente(id);

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
