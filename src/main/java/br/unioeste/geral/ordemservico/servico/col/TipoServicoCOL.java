package br.unioeste.geral.ordemservico.servico.col;

import br.unioeste.geral.ordemservico.bo.tiposervico.TipoServico;

public class TipoServicoCOL {
    public boolean validarID(Long id){
        return id != null && id > 0;
    }

    public boolean validarNome(String nome){
        return nome != null && !nome.isBlank();
    }

    public boolean validarValorReferencia(Double valorReferencia){
        return valorReferencia != null && valorReferencia > 0;
    }

    public boolean validarTipoServico(TipoServico tipoServico){
        if(tipoServico == null){
            return false;
        }

        return validarNome(tipoServico.getNome()) && validarValorReferencia(tipoServico.getValorReferencia());
    }
}
