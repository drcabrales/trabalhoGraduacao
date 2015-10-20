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
             "create table "+ TABLE1_NAME +" (nome text not null, PRIMARY KEY (nome));";
     
     private static final String DATABASE_CREATE_TABELA =
             "create table "+ TABLE2_NAME +" (nome text not null, nomeBanco text not null, PRIMARY KEY (nome), FOREIGN KEY(nomeBanco) REFERENCES "+TABLE1_NAME+" (nome));";
     
     private static final String DATABASE_CREATE_COLUNA =
             "create table "+ TABLE3_NAME +" (nome text not null, tipo text not null, nomeTabela text not null, isPK integer, isAutoincrement integer, isFK integer, nomeTabelaFK text, nomeColunaFK text, PRIMARY KEY (nome), FOREIGN KEY(nomeTabela) REFERENCES "+TABLE2_NAME+" (nome));";
     
     
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
        return database.insert(TABLE1_NAME, null, initialValues);
	}
	
	public long insertTabela(String nomeTabela, String nomeBanco){
		database = this.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
        initialValues.put("nome", nomeTabela);
        initialValues.put("nomeBanco", nomeBanco);
        return database.insert(TABLE2_NAME, null, initialValues);
	}
	
	public long insertColuna(String nomeColuna, String tipo, String nomeTabela, int isPK, int isAutoincrement, int isFK, String nomeTabelaFK, String nomeColunaFK){
		database = this.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
        initialValues.put("nome", nomeColuna);
        initialValues.put("tipo", tipo);
        initialValues.put("nomeTabela", nomeTabela);
        initialValues.put("isPK", isPK);
        initialValues.put("isAutoincrement", isAutoincrement);
        initialValues.put("isFK", isFK);
        
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
        Cursor cursor = database.query(TABLE1_NAME, new String[] { "nome" }, null, null, null, null, null);

        List<Banco> retorno = new ArrayList<Banco>();

        while(cursor.moveToNext()){
        	Banco banco = new Banco(cursor.getString(cursor.getColumnIndex("nome")));
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
        Cursor cursor = database.query(TABLE3_NAME, new String[] { "nome", "nomeTabela", "isPK", "isAutoincrement", "isFK", "nomeTabelaFK", "nomeColunaFK" }, null, null, null, null, null);

        List<Coluna> retorno = new ArrayList<Coluna>();

        while(cursor.moveToNext()){
        	int pk = cursor.getInt(cursor.getColumnIndex("isPK"));
        	int autoincrement = cursor.getInt(cursor.getColumnIndex("isAutoincrement"));
        	int fk = cursor.getInt(cursor.getColumnIndex("isFK"));
        	
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
        			Bpk, BAutoincrement, Bfk, cursor.getString(cursor.getColumnIndex("nomeTabelaFK")), cursor.getString(cursor.getColumnIndex("nomeColunaFK")));
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
        			Bpk, BAutoincrement, Bfk, cursor.getString(cursor.getColumnIndex("nomeTabelaFK")), cursor.getString(cursor.getColumnIndex("nomeColunaFK")));
        	retorno.add(coluna);
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
