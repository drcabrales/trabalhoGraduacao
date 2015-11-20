package br.com.ufpe.objects;

import java.io.Serializable;

public class Alteracao implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String tipoAlteracao;
	/*tipoAlteracao pode ser:
	- altNomeTabela
	- delTabela
	- altNomeColuna
	- delColuna
	*/
	
	private String nomeVelhoTabela;
	private String nomeNovoTabela;
	
	private String nomeVelhoColuna;
	private String nomeNovoColuna;
	
	private String delTabela;
	private String delColuna;
	
	public String getTipoAlteracao() {
		return tipoAlteracao;
	}

	public void setTipoAlteracao(String tipoAlteracao) {
		this.tipoAlteracao = tipoAlteracao;
	}

	public String getNomeVelhoTabela() {
		return nomeVelhoTabela;
	}

	public void setNomeVelhoTabela(String nomeVelhoTabela) {
		this.nomeVelhoTabela = nomeVelhoTabela;
	}

	public String getNomeNovoTabela() {
		return nomeNovoTabela;
	}

	public void setNomeNovoTabela(String nomeNovoTabela) {
		this.nomeNovoTabela = nomeNovoTabela;
	}

	public String getNomeVelhoColuna() {
		return nomeVelhoColuna;
	}

	public void setNomeVelhoColuna(String nomeVelhoColuna) {
		this.nomeVelhoColuna = nomeVelhoColuna;
	}

	public String getNomeNovoColuna() {
		return nomeNovoColuna;
	}

	public void setNomeNovoColuna(String nomeNovoColuna) {
		this.nomeNovoColuna = nomeNovoColuna;
	}

	public String getDelTabela() {
		return delTabela;
	}

	public void setDelTabela(String delTabela) {
		this.delTabela = delTabela;
	}

	public String getDelColuna() {
		return delColuna;
	}

	public void setDelColuna(String delColuna) {
		this.delColuna = delColuna;
	}

	public Alteracao(String tipoAlteracao, String nomeVelhoTabela, String nomeNovoTabela, String nomeVelhoColuna, String nomeNovoColuna, String delTabela, String delColuna){
		this.delColuna = delColuna;
		this.delTabela = delTabela;
		this.nomeNovoColuna = nomeNovoColuna;
		this.nomeNovoTabela = nomeNovoTabela;
		this.nomeVelhoColuna = nomeVelhoColuna;
		this.nomeVelhoTabela = nomeVelhoTabela;
		this.tipoAlteracao = tipoAlteracao;
	}
}
