package br.univel.trabalho;

import br.univel.annotations.Coluna;
import br.univel.enums.EstadoCivil;

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
}
