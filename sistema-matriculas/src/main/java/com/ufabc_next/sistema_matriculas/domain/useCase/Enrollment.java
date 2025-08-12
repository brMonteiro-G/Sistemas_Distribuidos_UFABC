package com.ufabc_next.sistema_matriculas.domain.useCase;

import lombok.Data;

import java.util.List;

@Data

public class Enrollment {

    private List<String> disciplinasDisponiveis;

    private Integer quantidadeDeUsuarios;

    private String quad;

}
