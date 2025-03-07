package br.unioeste.geral.ordemservico.servico.col.cliente;

import br.unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.pessoa.bo.email.Email;
import br.unioeste.geral.pessoa.bo.telefone.Telefone;
import br.unioeste.geral.pessoa.servico.col.CPFCOL;
import br.unioeste.geral.pessoa.servico.col.EmailCOL;
import br.unioeste.geral.pessoa.servico.col.EnderecoEspecificoCOL;
import br.unioeste.geral.pessoa.servico.col.TelefoneCOL;

import java.util.List;

public class ClienteCOL {
    private final TelefoneCOL telefoneCOL;
    private final EmailCOL emailCOL;
    private final EnderecoEspecificoCOL enderecoEspecificoCOL;
    private final CPFCOL cpfCOL;

    public ClienteCOL(){
        telefoneCOL = new TelefoneCOL();
        emailCOL = new EmailCOL();
        enderecoEspecificoCOL = new EnderecoEspecificoCOL();
        cpfCOL = new CPFCOL();
    }

    public boolean validarID(Long id){
        return id != null && id > 0;
    }

    public boolean validarCliente(Cliente cliente) throws Exception {
        if(cliente == null){
            return false;
        }

        EnderecoEspecifico enderecoEspecifico = cliente.getEndereco();
        List<Email> emails = cliente.getEmails();
        List<Telefone> telefones = cliente.getTelefones();

        return validarStringVaziaOuNula(cliente.getPrimeiroNome()) &&
                validarStringVaziaOuNula(cliente.getUltimoNome()) &&
                cpfCOL.validarCPF(cliente.getCpf())  &&
                enderecoEspecificoCOL.validarEnderecoEspecifico(enderecoEspecifico) &&
                emailCOL.validarEmails(emails) &&
                telefoneCOL.validarTelefones(telefones);
    }

    public boolean validarClienteForm(Cliente cliente) throws Exception {
        if(cliente == null){
            return false;
        }

        EnderecoEspecifico enderecoEspecifico = cliente.getEndereco();
        List<Email> emails = cliente.getEmails();
        List<Telefone> telefones = cliente.getTelefones();

        return validarStringVaziaOuNula(cliente.getPrimeiroNome()) &&
                validarStringVaziaOuNula(cliente.getUltimoNome()) &&
                cpfCOL.validarCPF(cliente.getCpf())  &&
                enderecoEspecificoCOL.validarEnderecoEspecificoForm(enderecoEspecifico) &&
                emailCOL.validarEmails(emails) &&
                telefoneCOL.validarTelefones(telefones);
    }

    private boolean validarStringVaziaOuNula(String string){
        return string != null && !string.trim().isEmpty();
    }
}
