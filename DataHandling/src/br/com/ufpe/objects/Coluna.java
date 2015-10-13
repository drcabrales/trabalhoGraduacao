package br.com.ufpe.objects;

public class Coluna {
	private String nome;
	private String nomeTabela;
	
	public Coluna(String nome, String nomeTabela){
		this.nome = nome;
		this.nomeTabela = nomeTabela;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeTabela() {
		return nomeTabela;
	}

	public void setNomeTabela(String nomeTabela) {
		this.nomeTabela = nomeTabela;
	}
}
