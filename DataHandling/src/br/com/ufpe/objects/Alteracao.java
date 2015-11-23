package br.com.ufpe.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Alteracao implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String tipoAlteracao;
	/*tipoAlteracao pode ser:
	- altNomeTabela
	- delTabela
	- addTabela
	- altNomeColuna
	- delColuna
	- addColuna
	*/
	
	private String nomeVelhoTabela;
	private String nomeNovoTabela;
	
	private String nomeVelhoColuna;
	private String nomeNovoColuna;
	
	private Tabela createTabela;
	private Coluna createColuna;
	
	private String delTabela;
	private String delColuna;
	
	private ArrayList<String> tabelas;
	private ArrayList<Coluna> colunas;
	private String DBName;
	
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

	public ArrayList<String> getTabelas() {
		return tabelas;
	}

	public void setTabelas(ArrayList<String> tabelas) {
		this.tabelas = tabelas;
	}

	public ArrayList<Coluna> getColunas() {
		return colunas;
	}

	public void setColunas(ArrayList<Coluna> colunas) {
		this.colunas = colunas;
	}

	public String getDBName() {
		return DBName;
	}

	public void setDBName(String dBName) {
		DBName = dBName;
	}

	public Tabela getCreateTabela() {
		return createTabela;
	}

	public void setCreateTabela(Tabela createTabela) {
		this.createTabela = createTabela;
	}

	public Coluna getCreateColuna() {
		return createColuna;
	}

	public void setCreateColuna(Coluna createColuna) {
		this.createColuna = createColuna;
	}

	public Alteracao(String tipoAlteracao, String nomeVelhoTabela, String nomeNovoTabela, String nomeVelhoColuna, String nomeNovoColuna, String delTabela, String delColuna, ArrayList<String> tabelas, ArrayList<Coluna> colunas, String DBName, Tabela createTabela, Coluna createColuna){
		this.delColuna = delColuna;
		this.delTabela = delTabela;
		this.nomeNovoColuna = nomeNovoColuna;
		this.nomeNovoTabela = nomeNovoTabela;
		this.nomeVelhoColuna = nomeVelhoColuna;
		this.nomeVelhoTabela = nomeVelhoTabela;
		this.tipoAlteracao = tipoAlteracao;
		this.colunas = colunas;
		this.tabelas = tabelas;
		this.DBName = DBName;
		this.createColuna = createColuna;
		this.createTabela = createTabela;
	}
}
