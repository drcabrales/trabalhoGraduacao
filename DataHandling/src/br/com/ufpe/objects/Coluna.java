package br.com.ufpe.objects;

import java.io.Serializable;

public class Coluna implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nome;
	private String tipo;
	private String nomeTabela;
	private String nomeBanco;
	private boolean PK;
	private boolean autoincrement;
	private boolean FK;
	private String nomeTabelaFK;
	private String nomeColunaFK;
	
	//alteração para o blob
	//caso não seja blob, inserir nulo
	private String tipoBlob;
	
	public Coluna(String nome, String tipo, String nomeTabela, boolean PK, boolean autoincrement, boolean FK, String nomeTabelaFK, String nomeColunaFK, String tipoBlob, String nomeBanco){
		this.nome = nome;
		this.tipo = tipo;
		this.nomeTabela = nomeTabela;
		this.PK = PK;
		this.autoincrement = autoincrement;
		this.FK = FK;
		this.nomeTabelaFK = nomeTabelaFK;
		this.nomeColunaFK = nomeColunaFK;
		this.tipoBlob = tipoBlob;
		this.nomeBanco = nomeBanco;
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

	public String getTipoBlob() {
		return tipoBlob;
	}

	public void setTipoBlob(String tipoBlob) {
		this.tipoBlob = tipoBlob;
	}

	public String getNomeBanco() {
		return nomeBanco;
	}

	public void setNomeBanco(String nomeBanco) {
		this.nomeBanco = nomeBanco;
	}
}
