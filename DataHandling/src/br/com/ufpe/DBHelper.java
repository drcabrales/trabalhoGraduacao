package br.com.ufpe;

import java.util.ArrayList;
import java.util.List;

import br.com.ufpe.objects.Banco;
import br.com.ufpe.objects.Coluna;
import br.com.ufpe.objects.Tabela;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	 private static final String DATABASE_NAME = "dataHandling";
     public static final String TABLE1_NAME = "Banco";
     public static final String TABLE2_NAME = "Tabela";
     public static final String TABLE3_NAME = "Coluna";
     private static final int DATABASE_VERSION = 1;
     
     private SQLiteDatabase database;
     
     private static final String DATABASE_CREATE_BANCO =
             "create table "+ TABLE1_NAME +" (nome text not null, flagCriado integer, PRIMARY KEY (nome));";
     
     private static final String DATABASE_CREATE_TABELA =
             "create table "+ TABLE2_NAME +" (nome text not null, nomeBanco text not null, PRIMARY KEY (nome, nomeBanco), FOREIGN KEY(nomeBanco) REFERENCES "+TABLE1_NAME+" (nome));";
     
     private static final String DATABASE_CREATE_COLUNA =
             "create table "+ TABLE3_NAME +" (nome text not null, tipo text not null, nomeTabela text not null, isPK integer, isAutoincrement integer, isFK integer, nomeTabelaFK text, nomeColunaFK text, tipoBlob text, PRIMARY KEY (nome, nomeTabela), FOREIGN KEY(nomeTabela) REFERENCES "+TABLE2_NAME+" (nome));";
     
     
	public DBHelper(Context context) {
		//criando o banco
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override 
	public void onCreate(SQLiteDatabase db) { 
			//criando as tabelas e colunas
	          db.execSQL(DATABASE_CREATE_BANCO);
	          db.execSQL(DATABASE_CREATE_TABELA); 
	          db.execSQL(DATABASE_CREATE_COLUNA);
	          database = db;
	} 
	@Override 
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
	          Log.w(DBHelper.class.getName(), "Upgrading database from version " + oldVersion + "to " + newVersion + ", which will destroy all old data"); 
	          db.execSQL("DROP TABLE IF EXISTS " + TABLE1_NAME); 
	          db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME); 
	          db.execSQL("DROP TABLE IF EXISTS " + TABLE3_NAME); 
	onCreate(db); 
	}
	
	public long insertBanco(String nomeBanco){
		database = this.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
        initialValues.put("nome", nomeBanco);
        initialValues.put("flagCriado", 0);
        return database.insert(TABLE1_NAME, null, initialValues);
	}
	
	public long insertTabela(String nomeTabela, String nomeBanco){
		database = this.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
        initialValues.put("nome", nomeTabela);
        initialValues.put("nomeBanco", nomeBanco);
        return database.insert(TABLE2_NAME, null, initialValues);
	}
	
	public long insertColuna(String nomeColuna, String tipo, String nomeTabela, int isPK, int isAutoincrement, int isFK, String nomeTabelaFK, String nomeColunaFK, String tipoBlob){
		database = this.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
        initialValues.put("nome", nomeColuna);
        initialValues.put("tipo", tipo);
        initialValues.put("nomeTabela", nomeTabela);
        initialValues.put("isPK", isPK);
        initialValues.put("isAutoincrement", isAutoincrement);
        initialValues.put("isFK", isFK);
        initialValues.put("tipoBlob", tipoBlob);
        
        if(nomeTabelaFK != null){
        	initialValues.put("nomeTabelaFK", nomeTabelaFK);
        }
        
        if(nomeColunaFK != null){
        	initialValues.put("nomeColunaFK", nomeColunaFK);
        }
        
        return database.insert(TABLE3_NAME, null, initialValues);
	}
	
	public List<Banco> getAllNomesBancos() {
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE1_NAME, new String[] { "nome", "flagCriado" }, null, null, null, null, null);

        List<Banco> retorno = new ArrayList<Banco>();

        while(cursor.moveToNext()){
        	Banco banco = new Banco(cursor.getString(cursor.getColumnIndex("nome")), cursor.getInt(cursor.getColumnIndex("flagCriado")));
            retorno.add(banco);
        }

        return retorno;
    }
	
	public List<Tabela> getAllNomesTabelas() {
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE2_NAME, new String[] { "nome", "nomeBanco" }, null, null, null, null, null);

        List<Tabela> retorno = new ArrayList<Tabela>();

        while(cursor.moveToNext()){
        	Tabela tabela = new Tabela(cursor.getString(cursor.getColumnIndex("nome")), cursor.getString(cursor.getColumnIndex("nomeBanco")));
            retorno.add(tabela);
        }

        return retorno;
    }
	
	public List<Coluna> getAllNomesColunas() {
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE3_NAME, new String[] { "nome", "nomeTabela", "isPK", "isAutoincrement", "isFK", "nomeTabelaFK", "nomeColunaFK", "tipoBlob" }, null, null, null, null, null);

        List<Coluna> retorno = new ArrayList<Coluna>();

        while(cursor.moveToNext()){
        	int pk = cursor.getInt(cursor.getColumnIndex("isPK"));
        	int autoincrement = cursor.getInt(cursor.getColumnIndex("isAutoincrement"));
        	int fk = cursor.getInt(cursor.getColumnIndex("isFK"));
        	String tipoBlob = cursor.getString(cursor.getColumnIndex("tipoBlob"));
        	
        	boolean Bpk, BAutoincrement, Bfk;
        	if(pk == 1){
        		Bpk = true;
        	}else{
        		Bpk = false;
        	}
        	
        	if(autoincrement == 1){
        		BAutoincrement = true;
        	}else{
        		BAutoincrement = false;
        	}
        	
        	if(fk == 1){
        		Bfk = true;
        	}else{
        		Bfk = false;
        	}
        	
        	Coluna coluna = new Coluna(cursor.getString(cursor.getColumnIndex("nome")), cursor.getString(cursor.getColumnIndex("tipo")), cursor.getString(cursor.getColumnIndex("nomeTabela")),
        			Bpk, BAutoincrement, Bfk, cursor.getString(cursor.getColumnIndex("nomeTabelaFK")), cursor.getString(cursor.getColumnIndex("nomeColunaFK")), tipoBlob);
            retorno.add(coluna);
        }

        return retorno;
    }
	
	public List<Tabela> getTabelasByBanco(String nomeBanco){
        database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from Tabela where nomeBanco = '" + nomeBanco + "'", null);

        List<Tabela> retorno = new ArrayList<Tabela>();
        while(cursor.moveToNext()){
        	Tabela tabela = new Tabela(cursor.getString(cursor.getColumnIndex("nome")), cursor.getString(cursor.getColumnIndex("nomeBanco")));
        	retorno.add(tabela);
        }

        return retorno;
    }
	
	public List<Coluna> getColunasByTabela(String nomeTabela){
        database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from Coluna where nomeTabela = '" + nomeTabela + "'", null);

        List<Coluna> retorno = new ArrayList<Coluna>();
        while(cursor.moveToNext()){
        	int pk = cursor.getInt(cursor.getColumnIndex("isPK"));
        	int autoincrement = cursor.getInt(cursor.getColumnIndex("isAutoincrement"));
        	int fk = cursor.getInt(cursor.getColumnIndex("isFK"));
        	String tipoBlob = cursor.getString(cursor.getColumnIndex("tipoBlob"));
        	
        	boolean Bpk, BAutoincrement, Bfk;
        	if(pk == 1){
        		Bpk = true;
        	}else{
        		Bpk = false;
        	}
        	
        	if(autoincrement == 1){
        		BAutoincrement = true;
        	}else{
        		BAutoincrement = false;
        	}
        	
        	if(fk == 1){
        		Bfk = true;
        	}else{
        		Bfk = false;
        	}
        	
        	Coluna coluna = new Coluna(cursor.getString(cursor.getColumnIndex("nome")), cursor.getString(cursor.getColumnIndex("tipo")), cursor.getString(cursor.getColumnIndex("nomeTabela")),
        			Bpk, BAutoincrement, Bfk, cursor.getString(cursor.getColumnIndex("nomeTabelaFK")), cursor.getString(cursor.getColumnIndex("nomeColunaFK")), tipoBlob);
        	retorno.add(coluna);
        }

        return retorno;
    }
	
	public boolean updateTabela(String tabelaAAlterar, String tabela, String DBName) {
        database = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("nome", tabela);
        return database.update("Tabela", initialValues, "nomeBanco" + "= '" + DBName + "' and nome = '" + tabelaAAlterar + "'", null) > 0;
    }
	
	public void updateReferenciaColunaTabela(String nomeNovaTabela, String nomeVelhaTabela){ //quando der update numa tabela, tem que dar update em suas referencias na coluna no campo "nomeTabela"
		 database = this.getWritableDatabase();
		 database.execSQL("update Coluna set nomeTabela = '" + nomeNovaTabela + "' where nomeTabela = '" + nomeVelhaTabela + "'");
	}
	
	public void deleteTabela(String tabela, String DBName){
        database = this.getWritableDatabase();
        database.execSQL("delete from Tabela where nome = '" + tabela + "' and nomeBanco = '"+ DBName +"';");
    }
	
	public void deleteReferenciaColunaTabela(String nomeVelhaTabela){ //quando deletar a tabela, deleta as colunas que tem essa referencias
		 database = this.getWritableDatabase();
	     database.execSQL("delete from Coluna where nomeTabela = '" + nomeVelhaTabela +"';");
	}
	
	public boolean updateColuna(String colunaAAlterar, String coluna, String nomeTabela) {
        database = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("nome", coluna);
        return database.update("Coluna", initialValues, "nomeTabela" + "= '" + nomeTabela + "' and nome = '" + colunaAAlterar + "'", null) > 0;
    }
	
	public void deleteColuna(String coluna, String nomeTabela){
        database = this.getWritableDatabase();
        database.execSQL("delete from Coluna where nome = '" + coluna + "' and nomeTabela = '"+ nomeTabela +"';");
    }
	
	public void setFlagCriado(int flagCriado, String DBName){
		 database = this.getWritableDatabase();
		 database.execSQL("update Banco set flagCriado = " + flagCriado + " where nome = '" + DBName + "'");
	}
	
	public boolean getFlagCriado(String DBName){
		database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select flagCriado from Banco where nome = '" + DBName + "'", null);

        int flagRetorno = -1;
        while(cursor.moveToNext()){
        	flagRetorno = cursor.getInt(cursor.getColumnIndex("flagCriado"));
        }

        if(flagRetorno == 1){
        	return true;
        }else{
        	return false;
        }
	}
	
	public boolean existePKAutoIncremental(String nomeTabela){
		boolean retorno = false;
		ArrayList<Coluna> colunasAtuais = (ArrayList<Coluna>) getColunasByTabela(nomeTabela);
		
		for (int i = 0; i < colunasAtuais.size(); i++) {
			if(colunasAtuais.get(i).isAutoincrement()){
				retorno = true;
			}
		}
		
		return retorno;
	}
	
	public boolean existePK(String nomeTabela){
		boolean retorno = false;
		ArrayList<Coluna> colunasAtuais = (ArrayList<Coluna>) getColunasByTabela(nomeTabela);
		
		for (int i = 0; i < colunasAtuais.size(); i++) {
			if(colunasAtuais.get(i).isPK()){
				retorno = true;
			}
		}
		
		return retorno;
	}
	
	//método responsável por criar os bancos de dados do usuário
	public DBHelperUsuario createUserDatabase(Context context, String nomeBanco, ArrayList<Tabela> tabelas, ArrayList<Coluna> colunas){
		DBHelperUsuario helperUsu = new DBHelperUsuario(context, nomeBanco, tabelas, colunas);
		//gambiarra pra teste
		helperUsu.callOnCreate();
		return helperUsu;
	}
}
