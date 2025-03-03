package br.unioeste.geral.ordemservico.servico.dao.ordemservico;

import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.ordemservico.bo.funcionario.Funcionario;
import br.unioeste.geral.ordemservico.bo.ordemservico.OrdemServico;
import br.unioeste.geral.ordemservico.bo.servico.Servico;
import br.unioeste.geral.ordemservico.servico.dao.cliente.ClienteDAO;
import br.unioeste.geral.ordemservico.servico.dao.funcionario.FuncionarioDAO;
import br.unioeste.geral.ordemservico.servico.dao.servico.ServicoDAO;
import br.unioeste.geral.ordemservico.servico.exception.OrdemServicoException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdemServicoDAO {
    private final Connection conexao;

    private final FuncionarioDAO funcionarioDAO;
    private final ClienteDAO clienteDAO;
    private final ServicoDAO servicoDAO;

    public OrdemServicoDAO(Connection conexao) {
        this.conexao = conexao;

        this.funcionarioDAO = new FuncionarioDAO(conexao);
        this.clienteDAO = new ClienteDAO(conexao);
        this.servicoDAO = new ServicoDAO(conexao);
    }

    public OrdemServico obterOrdemServicoPorNumero(String numero) throws Exception {
        String sql = "SELECT * from ordem_servico WHERE numero = ?";

        OrdemServico ordemServico = null;

        try(PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, numero);

            try(ResultSet resultSet = stmt.executeQuery()) {
                if(resultSet.next()) {
                    ordemServico = criarOrdemServicoBO(resultSet);
                }
            }
        }
        catch(Exception e) {
            throw new OrdemServicoException("Não foi possível obter ordem de serviço " + numero);
        }

        return ordemServico;
    }

    public List<OrdemServico> obterOrdemServicos() throws Exception {
        String sql = "SELECT * from ordem_servico";

        List<OrdemServico> ordemServicos = new ArrayList<>();

        try(PreparedStatement stmt = conexao.prepareStatement(sql)) {
            try(ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    ordemServicos.add(criarOrdemServicoBO(resultSet));
                }
            }
        }
        catch(Exception e) {
            throw new OrdemServicoException("Não foi possível obter todas ordens de serviço");
        }

        return ordemServicos;
    }

    public String inserirOrdemServico(OrdemServico ordemServico) throws Exception {
        String sql = "INSERT INTO ordem_servico(numero, data_emissao, descricao, id_funcionario, id_cliente) VALUES(?,?,?,?,?)";

        try(PreparedStatement stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ordemServico.getNumero().toString());
            stmt.setDate(2, Date.valueOf(ordemServico.getDataEmissao()));
            stmt.setString(3, ordemServico.getDescricao());
            stmt.setLong(4, ordemServico.getCliente().getId());
            stmt.setLong(5, ordemServico.getFuncionarioResponsavel().getId());

            int resultado = stmt.executeUpdate();

            if(resultado == 0){
                throw new OrdemServicoException("Não foi possível cadastrar a ordem de serviço");
            }

            try(ResultSet resultSet = stmt.getGeneratedKeys()) {
                if(resultSet.next()) {
                    ordemServico.setNumero(Long.parseLong(resultSet.getString(1)));
                }
            }
        }
        catch(Exception e) {
            throw new OrdemServicoException("Não foi possível cadastrar a ordem de serviço");
        }

        return ordemServico.getNumero().toString();
    }

    private OrdemServico criarOrdemServicoBO(ResultSet resultSet) throws Exception {
        String numero = resultSet.getString("numero");
        LocalDate dataEmissao = resultSet.getDate("data_emissao").toLocalDate();
        String descricao = resultSet.getString("descricao");

        long idFuncionario = resultSet.getLong("id_funcionario");
        long idCliente = resultSet.getLong("id_cliente");

        Cliente cliente = clienteDAO.obterClientePorID(idCliente);
        Funcionario funcionarioResponsavel = funcionarioDAO.obterFuncionarioPorID(idFuncionario);

        List<Servico> servicos = servicoDAO.obterServicosPorNumeroOrdemServico(numero);

        OrdemServico ordemServico = new OrdemServico();

        ordemServico.setNumero(Long.parseLong(numero));
        ordemServico.setDescricao(descricao);
        ordemServico.setDataEmissao(dataEmissao);
        ordemServico.setFuncionarioResponsavel(funcionarioResponsavel);
        ordemServico.setCliente(cliente);
        ordemServico.setServicosRealizados(servicos);

        return ordemServico;
    }
}
