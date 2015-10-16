package br.com.ufpe.objects;

public class Coluna {
	private String nome;
	private String tipo;
	private String nomeTabela;
	private boolean PK;
	private boolean autoincrement;
	private boolean FK;
	private String nomeTabelaFK;
	private String nomeColunaFK;
	
	
	public Coluna(String nome, String tipo, String nomeTabela, boolean PK, boolean autoincrement, boolean FK, String nomeTabelaFK, String nomeColunaFK){
		this.nome = nome;
		this.tipo = tipo;
		this.nomeTabela = nomeTabela;
		this.PK = PK;
		this.autoincrement = autoincrement;
		this.FK = FK;
		this.nomeTabelaFK = nomeTabelaFK;
		this.nomeColunaFK = nomeColunaFK;
	}
	
	public Coluna(){}

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

	public boolean isPK() {
		return PK;
	}

	public void setPK(boolean pK) {
		PK = pK;
	}

	public boolean isAutoincrement() {
		return autoincrement;
	}

	public void setAutoincrement(boolean autoincrement) {
		this.autoincrement = autoincrement;
	}

	public boolean isFK() {
		return FK;
	}

	public void setFK(boolean fK) {
		FK = fK;
	}

	public String getNomeTabelaFK() {
		return nomeTabelaFK;
	}

	public void setNomeTabelaFK(String nomeTabelaFK) {
		this.nomeTabelaFK = nomeTabelaFK;
	}

	public String getNomeColunaFK() {
		return nomeColunaFK;
	}

	public void setNomeColunaFK(String nomeColunaFK) {
		this.nomeColunaFK = nomeColunaFK;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}
