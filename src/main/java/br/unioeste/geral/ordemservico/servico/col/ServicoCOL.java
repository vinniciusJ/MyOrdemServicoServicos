package br.unioeste.geral.ordemservico.servico.col;

import br.unioeste.geral.ordemservico.bo.servico.Servico;
import br.unioeste.geral.ordemservico.bo.tiposervico.TipoServico;

public class ServicoCOL {
    private final TipoServicoCOL tipoServicoCOL;

    public ServicoCOL(){
        tipoServicoCOL = new TipoServicoCOL();
    }

    public boolean validarID(Long id){
        return id != null && id > 0;
    }

    public boolean validarValorCobrado(Double valorCobrado){
        return valorCobrado != null && valorCobrado > 0;
    }

    public boolean validarTipoServico(TipoServico tipoServico){
        return tipoServicoCOL.validarTipoServico(tipoServico);
    }

    public boolean validarServico(Servico servico){
        if(servico == null){
            return false;
        }

        return validarValorCobrado(servico.getValorCobrado()) && validarTipoServico(servico.getTipoServico());
    }
}
