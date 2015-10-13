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
             "create table "+ TABLE3_NAME +" (nome text not null, nomeTabela text not null, PRIMARY KEY (nome), FOREIGN KEY(nomeTabela) REFERENCES "+TABLE2_NAME+" (nome));";
     
     
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
	
	public long insertColuna(String nomeColuna, String nomeTabela){
		database = this.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
        initialValues.put("nome", nomeColuna);
        initialValues.put("nomeTabela", nomeTabela);
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
        Cursor cursor = database.query(TABLE3_NAME, new String[] { "nome", "nomeTabela" }, null, null, null, null, null);

        List<Coluna> retorno = new ArrayList<Coluna>();

        while(cursor.moveToNext()){
        	Coluna coluna = new Coluna(cursor.getString(cursor.getColumnIndex("nome")), cursor.getString(cursor.getColumnIndex("nomeTabela")));
            retorno.add(coluna);
        }

        return retorno;
    }
}
