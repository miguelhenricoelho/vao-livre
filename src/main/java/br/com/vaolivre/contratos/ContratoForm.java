package br.com.vaolivre.contratos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/** Alteração do contrato. Ação grave: vem com a senha de quem altera. */
public record ContratoForm(
    @NotNull(message = "Digite o número de vagas.")
        @Min(value = 1, message = "O contrato precisa de pelo menos 1 vaga.")
        @Max(value = 500, message = "Use até 500 vagas.")
        Integer vagas,
    @NotNull(message = "Digite o início do contrato.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate inicio,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
    @NotNull(message = "Escolha a situação.") SituacaoContrato situacao,
    @NotBlank(message = "Confirme com a sua senha.") String senha) {}
