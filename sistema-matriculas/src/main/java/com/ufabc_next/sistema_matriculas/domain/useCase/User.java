package com.ufabc_next.sistema_matriculas.domain.useCase;

import lombok.Data;

import java.util.List;

@Data
public class User {

private String id;
private String estado;
private List<String> listaDeDisciplinas;

}
