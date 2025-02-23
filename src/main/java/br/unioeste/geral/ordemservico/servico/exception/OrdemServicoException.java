package br.unioeste.geral.ordemservico.servico.exception;

import java.io.Serial;

public class OrdemServicoException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public OrdemServicoException(String message){
        super(message);
    }
}
