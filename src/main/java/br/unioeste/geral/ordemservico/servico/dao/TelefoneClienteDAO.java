package br.unioeste.geral.ordemservico.servico.dao;

import br.unioeste.apoio.bd.ConexaoBD;
import br.unioeste.geral.endereco.servico.exception.EnderecoException;
import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.pessoa.bo.ddd.DDD;
import br.unioeste.geral.pessoa.bo.ddi.DDI;
import br.unioeste.geral.pessoa.bo.email.Email;
import br.unioeste.geral.pessoa.bo.telefone.Telefone;
import br.unioeste.geral.pessoa.servico.dao.DDDDAO;
import br.unioeste.geral.pessoa.servico.service.UCPessoaServicos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TelefoneClienteDAO {
    private final ConexaoBD conexaoBD;

    private final UCPessoaServicos pessoaServicos;

    public TelefoneClienteDAO() {
        conexaoBD = new ConexaoBD();
        pessoaServicos = new UCPessoaServicos();
    }

    public List<Telefone> obterTelefonesCliente(Long idCliente) throws Exception {
        String sql = "SELECT * FROM telefone_cliente WHERE id_cliente = ?";

        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        List<Telefone> telefones = new ArrayList<>();

        try{
            conexao = conexaoBD.getConexaoBD();
            stmt = conexao.prepareStatement(sql);

            stmt.setLong(1, idCliente);

            conexao.setAutoCommit(false);

            resultSet = stmt.executeQuery();

            while (resultSet.next()){
                telefones.add(criarTelefoneBO(resultSet));
            }

            conexao.commit();
        }
        catch(SQLException e){
            throw new EnderecoException("Não foi possível encontrar telefones para o cliente com ID: " + idCliente);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível estabelecer conexão com o banco de dados");
        }
        finally {
            conexaoBD.encerrarConexoes(resultSet, stmt, conexao);
        }

        return telefones;
    }

    public void inserirTelefones(Long idCliente, List<Telefone> telefones, Connection conexao) throws Exception{
        String sql = "INSERT INTO telefone_cliente (numero, ddd_ddd, ddi_ddi, id_cliente) values (?, ?, ?, ?)";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){
            for(Telefone telefone : telefones){
                stmt.setString(1, telefone.getNumero());
                stmt.setString(2, telefone.getDdd().getNumero());
                stmt.setString(3, telefone.getDdi().getNumero());
                stmt.setLong(4, idCliente);
            }
        }
    }

    private Telefone criarTelefoneBO(ResultSet resultSet) throws Exception {
        long id = resultSet.getLong("id");
        String numero = resultSet.getString("numero");
        String numeroDDD = resultSet.getString("ddd");
        String numeroDDI = resultSet.getString("ddd");

        DDD ddd = pessoaServicos.obterDDD(numeroDDD);
        DDI ddi = pessoaServicos.obterDDI(numeroDDI);

        return new Telefone(id, numero, ddd, ddi);
    }
}
