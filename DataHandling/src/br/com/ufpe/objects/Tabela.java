package br.com.ufpe.objects;

import java.io.Serializable;

public class Tabela implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nome;
	private String nomeBanco;
	
	public Tabela(String nome, String nomeBanco){
		this.nome = nome;
		this.nomeBanco = nomeBanco;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeBanco() {
		return nomeBanco;
	}

	public void setNomeBanco(String nomeBanco) {
		this.nomeBanco = nomeBanco;
	}
}
