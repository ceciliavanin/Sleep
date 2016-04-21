package br.univel.trabalho;

import br.dagostini.enums.tipos.EstadoCivil;
import br.univel.trabalho.Coluna;

public class Cliente {

	@Coluna(pk=true)
	private int id;

	@Coluna(nome="CLNOME")
	private String nome;
	
	@Coluna(endereco="END")
	private String endereco;

public static void main(String[] args) {

	// Enum simples.
	{
		EstadoCivil estadoCivil = EstadoCivil.CASADO;

		// Dessa forma se obt�m o nome do valor.
		System.out.println(estadoCivil.name());

		// Assim se obt�m o n�mero da ordem de declara��o, geralmente
		// utilizado para gravar no banco.
		System.out.println(estadoCivil.ordinal());
	}
}
