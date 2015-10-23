package br.com.ufpe;

import java.util.ArrayList;

import br.com.ufpe.objects.Coluna;
import br.com.ufpe.objects.Tabela;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//classe responsável por criar os bancos do usuário
public class DBHelperUsuario extends SQLiteOpenHelper{
     private static final int DATABASE_VERSION = 1;
     private ArrayList<Tabela> tabelas;
     private ArrayList<Coluna> colunas;
     
     private SQLiteDatabase database;
     
     
	public DBHelperUsuario(Context context, String databaseName, ArrayList<Tabela> tabelas, ArrayList<Coluna> colunas) {
		//criando o banco
        super(context, databaseName, null, DATABASE_VERSION);
        
        this.tabelas = tabelas;
        this.colunas = colunas;
	}
	
	public DBHelperUsuario(Context context, String databaseName){
		super(context, databaseName, null, DATABASE_VERSION);
	}
	
	@Override 
	public void onCreate(SQLiteDatabase db) { 
		//criando as tabelas e colunas
		//aqui vão ser gerados as strings de criação de tabelas do usuário, com suas colunas
		ArrayList<String> criacaoTabelas = new ArrayList<String>();
		String createTable = "";
		boolean entrouAutoincrement = false;
		
		for (int i = 0; i < tabelas.size(); i++) {
			createTable = "";
			createTable = "create table " + tabelas.get(i).getNome() + " (";
			
			//for para adicionar as colunas na string
			for (int j = 0; j < colunas.size(); j++) {
				//se a coluna pertencer a tabela em questão
				if(colunas.get(j).getNomeTabela().equals(tabelas.get(i).getNome())){
					createTable = createTable + colunas.get(j).getNome() +" " + colunas.get(j).getTipo();
					
					//se for chave primária, acrescenta o not null
					//ATENÇÃO: SE FOR AUTOINCREMENTO, TIRA A NECESSIDADE DE TER CHAVE PRIMÁRIA COMPOSTA (ver essa lógica)
					if(colunas.get(j).isPK()){
						if(colunas.get(j).isAutoincrement()){
							//ele só deve entrar aqui uma vez, mas a verificação disso vai
							//ser no momento da inserção das colunas!!
							//VER ESSA LOGICA
							createTable = createTable + " primary key autoincrement, ";
							entrouAutoincrement = true;
						}else{
							createTable = createTable + " not null, ";
						}
					}else{
						createTable = createTable + ",";
					}
				}
			}
			
			if(!entrouAutoincrement){
				
				createTable = createTable + " primary key (";
				
				//for para adicionar as chaves primárias
				for (int j = 0; j < colunas.size(); j++) {
					//se a coluna pertencer a tabela em questão
					if(colunas.get(j).getNomeTabela().equals(tabelas.get(i).getNome())){
						if(colunas.get(j).isPK()){
							createTable = createTable + colunas.get(j).getNome() + ", ";
						}
					}
				}
				
				//tirando a virgula do final
				createTable = createTable.substring(0,createTable.length()-2) + "), ";
			}
			
			
			boolean entrouForeignKey = false;
			
			//for para as chaves estrangeiras
			for (int j = 0; j < colunas.size(); j++) {
					if(colunas.get(j).getNomeTabela().equals(tabelas.get(i).getNome()) && colunas.get(j).isFK()){
						entrouForeignKey = true;
						createTable = createTable + "foreign key (" + colunas.get(j).getNome() + ") references " + 
								colunas.get(j).getNomeTabelaFK() + "(" + colunas.get(j).getNomeColunaFK() + "), ";
					}
			}
			
			if(entrouForeignKey){
				//tirando a virgula do final
				createTable = createTable.substring(0,createTable.length()-2) + "); ";
			}else{
				createTable = createTable.substring(0,createTable.length()-1) + "); ";
			}
			
			criacaoTabelas.add(createTable);
			entrouAutoincrement = false;
		}
 
	    //db.execSQL(DATABASE_CREATE_COLUNA);
	    database = db;
	} 
	@Override 
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
	          Log.w(DBHelper.class.getName(), "Upgrading database from version " + oldVersion + "to " + newVersion + ", which will destroy all old data"); 
	         // db.execSQL("DROP TABLE IF EXISTS " + TABLE1_NAME); 
	         // db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME); 
	         // db.execSQL("DROP TABLE IF EXISTS " + TABLE3_NAME); 
	onCreate(db); 
	}
	
	public void callOnCreate(){
		database = this.getWritableDatabase();
	}

}
