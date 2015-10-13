package br.com.ufpe.objects;

public class Tabela {
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
