package br.com.ufpe;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.ufpe.objects.Coluna;
import br.com.ufpe.objects.Tabela;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

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

			if(!entrouAutoincrement){
				createTable = createTable.substring(0,createTable.length()-4) + "); ";
			}

			criacaoTabelas.add(createTable);
			entrouAutoincrement = false;
		}

		for (int j = 0; j < criacaoTabelas.size(); j++) {
			db.execSQL(criacaoTabelas.get(j));
		}

		database = db;
	} 
	@Override 
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		Log.w(DBHelper.class.getName(), "Upgrading database from version " + oldVersion + "to " + newVersion + ", which will destroy all old data"); 
		for (int i = 0; i < tabelas.size(); i++) {
			db.execSQL("DROP TABLE IF EXISTS " + tabelas.get(i).getNome()); 
		}
		onCreate(db); 
	}

	public void callOnCreate(){
		database = this.getWritableDatabase();
	}

	public long insert(ArrayList<Coluna> colunas, ArrayList<Object> dados, String nomeTabela){
		database = this.getWritableDatabase();
		ContentValues initialValues = new ContentValues();

		for (int i = 0; i < colunas.size(); i++) {
			if(colunas.get(i).getTipo().equals("Varchar") || colunas.get(i).getTipo().equals("Text")){
				initialValues.put(colunas.get(i).getNome(), (String) dados.get(i));
			}else if(colunas.get(i).getTipo().equals("Double")){
				initialValues.put(colunas.get(i).getNome(), Double.parseDouble((String) dados.get(i)));
			}else if(colunas.get(i).getTipo().equals("Float")){
				initialValues.put(colunas.get(i).getNome(), Float.parseFloat((String)dados.get(i)));
			}else if(colunas.get(i).getTipo().equals("Integer")){
				initialValues.put(colunas.get(i).getNome(), Integer.parseInt((String) dados.get(i)));
			}else if(colunas.get(i).getTipo().equals("Boolean")){
				initialValues.put(colunas.get(i).getNome(), (Boolean) dados.get(i));
			}else{
				//BLOB
				initialValues.put(colunas.get(i).getNome(), (byte[]) dados.get(i));
			}

		}

		long retorno = database.insert(nomeTabela, null, initialValues);
		return retorno;
	}

	public Map<String, Object> getAllDataFromTable(String tablename, ArrayList<Coluna> colunas){
		Map<String, Object> retorno = new HashMap<String, Object>();
		database = this.getWritableDatabase();

		//pegando o nome das colunas
		String[] nomesC = new String[colunas.size()];
		for (int i = 0; i < colunas.size(); i++) {
			nomesC[i] = colunas.get(i).getNome();
		}

		Cursor cursor = database.query(tablename, null, null, null, null, null, null);
		cursor.getCount();
		int contadorLinhas = 0;
		if(cursor.moveToFirst()){
			do{
				for (int i = 0; i < nomesC.length; i++) {
					//formato da key: nomedaColuna[linha][coluna]
					//como nomesC tem os mesmo itens de colunas na mesma posição, posso fazer a verificação abaixo
					String hashmapkey = nomesC[i] + contadorLinhas + i;
					if(colunas.get(i).getTipo().equals("Varchar") || colunas.get(i).getTipo().equals("Text")){
						retorno.put(hashmapkey, cursor.getString(cursor.getColumnIndex(nomesC[i])));
					}else if(colunas.get(i).getTipo().equals("Integer")){
						retorno.put(hashmapkey, cursor.getInt(cursor.getColumnIndex(nomesC[i])));
					}else if(colunas.get(i).getTipo().equals("Double")){
						retorno.put(hashmapkey, cursor.getDouble(cursor.getColumnIndex(nomesC[i])));
					}else if(colunas.get(i).getTipo().equals("Float")){
						retorno.put(hashmapkey, cursor.getFloat(cursor.getColumnIndex(nomesC[i])));
					}else if(colunas.get(i).getTipo().equals("Boolean")){
						retorno.put(hashmapkey, cursor.getInt(cursor.getColumnIndex(nomesC[i])) == 1? true : false);
					}else{
						//blob
						retorno.put(hashmapkey, cursor.getBlob(cursor.getColumnIndex(nomesC[i])));
					}
					
				}
				contadorLinhas++;
			}while(cursor.moveToNext());
		}
		return retorno;
	}

}
