package br.unioeste.geral.ordemservico.servico.col;

import br.unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import br.unioeste.geral.ordemservico.bo.funcionario.Funcionario;
import br.unioeste.geral.ordemservico.servico.dao.funcionario.FuncionarioDAO;
import br.unioeste.geral.pessoa.bo.email.Email;
import br.unioeste.geral.pessoa.bo.telefone.Telefone;
import br.unioeste.geral.pessoa.servico.col.CPFCOL;
import br.unioeste.geral.pessoa.servico.col.EmailCOL;
import br.unioeste.geral.pessoa.servico.col.EnderecoEspecificoCOL;
import br.unioeste.geral.pessoa.servico.col.TelefoneCOL;

import java.util.List;

public class FuncionarioCOL {
    private final TelefoneCOL telefoneCOL;
    private final EmailCOL emailCOL;
    private final EnderecoEspecificoCOL enderecoEspecificoCOL;
    private final CPFCOL cpfCOL;

    public FuncionarioCOL(){
        telefoneCOL = new TelefoneCOL();
        emailCOL = new EmailCOL();
        enderecoEspecificoCOL = new EnderecoEspecificoCOL();
        cpfCOL = new CPFCOL();
    }

    public boolean validarID(Long id){
        return id != null && id > 0;
    }

    public boolean validarFuncionario(Funcionario funcionario) throws Exception {
        if(funcionario == null){
            return false;
        }

        EnderecoEspecifico enderecoEspecifico = funcionario.getEndereco();
        List<Email> emails = funcionario.getEmails();
        List<Telefone> telefones = funcionario.getTelefones();

        return validarStringVaziaOuNula(funcionario.getPrimeiroNome()) &&
                validarStringVaziaOuNula(funcionario.getUltimoNome()) &&
                cpfCOL.validarCPF(funcionario.getCpf()) &&
                enderecoEspecificoCOL.validarEnderecoEspecifico(enderecoEspecifico) &&
                emailCOL.validarEmails(emails) &&
                telefoneCOL.validarTelefones(telefones);
    }

    private boolean validarStringVaziaOuNula(String string){
        return string != null && !string.trim().isEmpty();
    }
}
