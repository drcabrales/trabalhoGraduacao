package br.com.ufpe.objects;

public class Banco {
	private String nome;
	private int flagCriado;
	
	public Banco(String nome, int flagCriado){
		this.nome = nome;
		this.flagCriado = flagCriado;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getFlagCriado() {
		return flagCriado;
	}

	public void setFlagCriado(int flagCriado) {
		this.flagCriado = flagCriado;
	}
}
