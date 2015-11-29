package br.com.ufpe;

import java.io.IOException;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.ufpe.objects.Alteracao;
import br.com.ufpe.objects.Coluna;
import br.com.ufpe.objects.Tabela;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaPlayer;
import android.net.Uri;
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
			createTable = "create table if not exists " + tabelas.get(i).getNome() + " (";

			//for para adicionar as colunas na string
			for (int j = 0; j < colunas.size(); j++) {
				//se a coluna pertencer a tabela em questão
				if(colunas.get(j).getNomeTabela().equals(tabelas.get(i).getNome())){
					//tratamento do blob para virar apenas a url 
					createTable = createTable + colunas.get(j).getNome() +" " + (colunas.get(j).getTipo().equals("BLOB") ? "Varchar":colunas.get(j).getTipo());

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

	public void onUpdateSchema(ArrayList<Alteracao> listAlteracao){
		//pega o database
		database = getWritableDatabase();
		//por segurança, deleta a tabela auxiliar se existir
		database.execSQL("DROP TABLE IF EXISTS auxEditColumn");

		//ve o tipo da alteracao
		//para cada tipo, monta um execSQL diferente, com alter
		for (int i = 0; i < listAlteracao.size(); i++) {
			Alteracao aux = listAlteracao.get(i);

			if(aux.getTipoAlteracao().equals("altNomeTabela")){
				database.execSQL("alter table " +aux.getNomeVelhoTabela() + " rename to " + aux.getNomeNovoTabela());
			}else if(aux.getTipoAlteracao().equals("delTabela")){
				database.execSQL("DROP TABLE IF EXISTS " + aux.getDelTabela());
			}else if(aux.getTipoAlteracao().equals("addTabela")){
				//para adicionar as tabelas tem que ter as colunas, então se o comando addTabela for encontrado, as tabelas do addColuna são buscadas
				//pega a tabela e do banco dela e coloca nessa lista
				ArrayList<Tabela> auxTabelas = new ArrayList<Tabela>();
				auxTabelas.add(aux.getCreateTabela());

				ArrayList<Coluna> auxColunas = new ArrayList<Coluna>();

				//busca as colunas da criação dessa tabela especifica, que também estarão na alteração
				for (int j = 0; j < listAlteracao.size(); j++) {
					if(listAlteracao.get(j).getTipoAlteracao().equals("addColuna")){
						if(listAlteracao.get(j).getCreateColuna().getNomeTabela().equals(aux.getCreateTabela().getNome())){
							auxColunas.add(listAlteracao.get(j).getCreateColuna());
						}
					}
				}

				criarTabela(auxColunas, auxTabelas);

			}else if(aux.getTipoAlteracao().equals("altNomeColuna")){
				//pega o nome da coluna que vai ser alterada
				String nomeColunaEdit = aux.getNomeVelhoColuna();

				//pega a lista de colunas
				ArrayList<Coluna> auxColunas = aux.getColunas();

				//pega o nome do banco de dados
				String auxBanco = aux.getDBName();

				//pega o nome da tabela e do banco da coluna e coloca nessa lista como objeto tabela
				ArrayList<Tabela> auxTabelas = new ArrayList<Tabela>();
				Tabela newTable = new Tabela("auxEditColumn", auxBanco);
				auxTabelas.add(newTable);

				//pega o nome da tabela atual da coluna a ser editada
				String tabelaAtual = "";

				//pega todas os nomes de colunas, separadas por virgula, em unica string
				String stringColunasVelhas = "";
				String stringColunasNovas = "";

				//muda a coluna que quer ser editada para ter o novo nome e nova tabela
				for (int j = 0; j < auxColunas.size(); j++) {
					tabelaAtual = auxColunas.get(j).getNomeTabela();

					if(j+1 >= auxColunas.size()){ //sem virgula
						if(auxColunas.get(j).getNome().equals(aux.getNomeNovoColuna())){ //se o atual da lista e o que sabemos que vamos editar baterem, salvamos eles com o formato novo e velho nas strings
							stringColunasVelhas = stringColunasVelhas + aux.getNomeVelhoColuna();
							stringColunasNovas = stringColunasNovas + aux.getNomeNovoColuna();
						}else{
							stringColunasVelhas = stringColunasVelhas + auxColunas.get(j).getNome();
							stringColunasNovas = stringColunasNovas + auxColunas.get(j).getNome();
						}
					}else{ //com virgula
						if(auxColunas.get(j).getNome().equals(aux.getNomeNovoColuna())){ //se o atual da lista e o que sabemos que vamos editar baterem, salvamos eles com o formato novo e velho nas strings
							stringColunasVelhas = stringColunasVelhas + aux.getNomeVelhoColuna() + ",";
							stringColunasNovas = stringColunasNovas + aux.getNomeNovoColuna() + ",";
						}else{
							stringColunasVelhas = stringColunasVelhas + auxColunas.get(j).getNome() + ",";
							stringColunasNovas = stringColunasNovas + auxColunas.get(j).getNome() + ",";
						}
					}

					auxColunas.get(j).setNomeTabela("auxEditColumn");
				}

				//cria uma tabela auxiliar e passa os dados da primeira tabela pra segunda
				criarTabela(auxColunas, auxTabelas);

				//passar os dados da tabela velha pra a aux
				database.execSQL("INSERT INTO auxEditColumn(" + stringColunasNovas + ") SELECT "
						+ stringColunasVelhas + " FROM " + tabelaAtual + ";");

				//deleta a primeira tabela (verificar se deu erro ou n por foreign key)
				try{
					database.execSQL("DROP TABLE IF EXISTS " + tabelaAtual);
				}catch(Exception e){
					//se entrar aqui, deu erro pra deletar
					//avisar que a atualização falhou
				}

				//renomear nova tabela para nome antigo
				database.execSQL("alter table auxEditColumn rename to " + tabelaAtual);

			}else if(aux.getTipoAlteracao().equals("addColuna")){
				//verifica se só tem addColuna na lista de alteração. Se sim, é uma inserção de nova tabela e isso não deve ser feito
				boolean insercaoNovaTabela = false; // inserção de nova tabela com banco ainda não criado. Se ele permanecer falso, quer dizer que um addcoluna n deve ser feito pq o banco ainda n foi criado, entao é outro caso
				for (int j = 0; j < listAlteracao.size(); j++) {
					if(!listAlteracao.get(j).getTipoAlteracao().equals("addColuna")){
						insercaoNovaTabela = true;
					}
				}

				//aqui só serão tratados os casos de adição de coluna a uma tabela já existente
				//verifica se a tabela dessa coluna não está na lista de alterações, porque se não tiver quer dizer que é uma tabela já criada
				boolean tabelaJaCriada = true;
				for (int j = 0; j < listAlteracao.size(); j++) {
					if(listAlteracao.get(j).getTipoAlteracao().equals("addTabela")){
						if(listAlteracao.get(j).getCreateTabela().getNome().equals(aux.getCreateColuna().getNomeTabela())){
							tabelaJaCriada = false;
						}
					}
				}

				//se a tabela ja ta criada, só adiciona a coluna
				if(tabelaJaCriada || !insercaoNovaTabela){
					database.execSQL("alter table " + aux.getCreateColuna().getNomeTabela() + " add column " + aux.getCreateColuna().getNome() + " " + aux.getCreateColuna().getTipo());
				}

			}else{
				//delColuna
				//pega o nome da coluna que vai ser deletada
				String nomeColunaDel = aux.getDelColuna();

				//pega a lista de colunas
				ArrayList<Coluna> auxColunas = aux.getColunas();

				//pega o nome do banco de dados
				String auxBanco = aux.getDBName();

				//pega o nome da tabela e do banco da coluna e coloca nessa lista como objeto tabela
				ArrayList<Tabela> auxTabelas = new ArrayList<Tabela>();
				Tabela newTable = new Tabela("auxEditColumn", auxBanco);
				auxTabelas.add(newTable);

				//pega o nome da tabela atual da coluna a ser editada
				String tabelaAtual = "";

				//pega todas os nomes de colunas, separadas por virgula, em unica string
				String stringColunas = "";

				//muda a coluna que quer ser editada para ter o novo nome e nova tabela
				for (int j = 0; j < auxColunas.size(); j++) {
					tabelaAtual = auxColunas.get(j).getNomeTabela();
					if(!auxColunas.get(j).getNome().equals(nomeColunaDel)){//não pega a coluna que vai ser deletada
						if(j+1 >= auxColunas.size()){
							stringColunas = stringColunas + auxColunas.get(j).getNome();
						}else{
							stringColunas = stringColunas + auxColunas.get(j).getNome() + ",";
						}
					}else{
						if(j+1 >= auxColunas.size()){
							//tirando uma possivel virgula no final
							stringColunas = stringColunas.substring(0, stringColunas.length()-1);
						}
					}

					auxColunas.get(j).setNomeTabela("auxEditColumn");
				}

				//retirar a coluna que se deseja eliminar em auxColunas
				for (int j = 0; j < auxColunas.size(); j++) {
					if(auxColunas.get(j).getNome().equals(aux.getDelColuna())){
						auxColunas.remove(j);
					}
				}

				//cria uma tabela auxiliar e passa os dados da primeira tabela pra segunda
				criarTabela(auxColunas, auxTabelas);

				//passar os dados da tabela velha pra a aux
				database.execSQL("INSERT INTO auxEditColumn(" + stringColunas + ") SELECT "
						+ stringColunas + " FROM " + tabelaAtual + ";");

				//deleta a primeira tabela (verificar se deu erro ou n por foreign key)
				try{
					database.execSQL("DROP TABLE IF EXISTS " + tabelaAtual);
				}catch(Exception e){
					//se entrar aqui, deu erro pra deletar
					//avisar que a atualização falhou
				}

				//renomear nova tabela para nome antigo
				database.execSQL("alter table auxEditColumn rename to " + tabelaAtual);
			}
		}
	}

	public void callOnCreate(){
		database = this.getWritableDatabase();
	}

	public long insert(ArrayList<Coluna> colunas, ArrayList<Object> dados, String nomeTabela){
		database = this.getWritableDatabase();
		//para habilitar verificação de foreign key
		database.execSQL("PRAGMA foreign_keys = ON;");
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
			}else if(colunas.get(i).getTipo().equals("Datetime")){
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				initialValues.put(colunas.get(i).getNome(), ((Date) dados.get(i)).getTime());
			}else{
				//BLOB
				initialValues.put(colunas.get(i).getNome(), (String) dados.get(i));
			}

		}

		long retorno = database.insert(nomeTabela, null, initialValues);
		return retorno;
	}

	public Long update(ArrayList<Coluna> colunas, ArrayList<Object> dados, String nomeTabela){
		database = getWritableDatabase();
		//para habilitar verificação de foreign key
		database.execSQL("PRAGMA foreign_keys = ON;");
		String query = "update " + nomeTabela + " set ";

		//construção da string de update
		for (int i = 0; i < colunas.size(); i++) {
			if(!colunas.get(i).isPK()){ //só edita dados que não forem chave primária
				if(colunas.get(i).getTipo().equals("Varchar") || colunas.get(i).getTipo().equals("Text")){
					query = query + colunas.get(i).getNome() + " = '" + dados.get(i) + "',";
				}else if(colunas.get(i).getTipo().equals("Double")){
					query = query + colunas.get(i).getNome() + " = " + dados.get(i) + ",";
				}else if(colunas.get(i).getTipo().equals("Float")){
					query = query + colunas.get(i).getNome() + " = " + dados.get(i) + ",";
				}else if(colunas.get(i).getTipo().equals("Integer")){
					query = query + colunas.get(i).getNome() + " = " + dados.get(i) + ",";
				}else if(colunas.get(i).getTipo().equals("Boolean")){
					query = query + colunas.get(i).getNome() + " = '" + dados.get(i) + "',";
				}else if(colunas.get(i).getTipo().equals("Datetime")){
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					//query = query + colunas.get(i).getNome() + " = '" + dateFormat.format(dados.get(i)) + "',";
					query = query + colunas.get(i).getNome() + " = " + ((Date) dados.get(i)).getTime() + ",";
				}else{
					//BLOB
					query = query + colunas.get(i).getNome() + " = '" + dados.get(i) + "',";
				}
			}
		}
		
		//retira a virgula do final e colocando o where
		query = query.substring(0, query.length()-1) + " where ";
		
		int countPK = 0;
		for (int i = 0; i < colunas.size(); i++) {
			if(colunas.get(i).isPK()){
				countPK++;
			}
		}

		for (int i = 0; i < colunas.size() && countPK > 0; i++) {
			if(colunas.get(i).isPK()){
				if(countPK == 1){
					if(colunas.get(i).getTipo().equals("Varchar") || colunas.get(i).getTipo().equals("Text")){
						query = query + colunas.get(i).getNome() + " = " + dados.get(i) + "' ";
					}else{
						query = query + colunas.get(i).getNome() + " = " + dados.get(i) + " ";
					}
					countPK--;
				}else{
					if(colunas.get(i).getTipo().equals("Varchar") || colunas.get(i).getTipo().equals("Text")){
						query = query + colunas.get(i).getNome() + " = " + dados.get(i) + "' and ";
					}else{
						query = query + colunas.get(i).getNome() + " = " + dados.get(i) + " and ";
					}
					countPK--;
				}
			}
		}

		try{
			database.execSQL(query);
			return (long) 1;
		}catch(Exception e){
			return (long) -1;
		}
	}
	
	public Long deleteRow(ArrayList<Coluna> colunas, ArrayList<Object> dados, String tableName){
		database = getWritableDatabase();
		//para habilitar verificação de foreign key
		database.execSQL("PRAGMA foreign_keys = ON;");
		int countPk = 0;
		String query = "delete from " +tableName+ " where ";
		for (int l = 0; l < colunas.size(); l++) {
			if(colunas.get(l).isPK()){
				countPk++;
			}
		}
		
		for (int l = 0; l < colunas.size() && countPk > 0; l++) {
			if(countPk == 1){
				if(colunas.get(l).getTipo().equals("Varchar") || colunas.get(l).getTipo().equals("Text")){
					query = query + colunas.get(l).getNome() + " = '" + dados.get(l) + "';";
				}else{
					query = query + colunas.get(l).getNome() + " = " + dados.get(l) + ";";
				}
				countPk--;
			}else{
				if(colunas.get(l).getTipo().equals("Varchar") || colunas.get(l).getTipo().equals("Text")){
					query = query + colunas.get(l).getNome() + " = '" + dados.get(l) + "' and ";
				}else{
					query = query + colunas.get(l).getNome() + " = " + dados.get(l) + " and ";
				}
				countPk--;
			}
		}
		
		try{
			database.execSQL(query);
			return (long) 1;
		}catch(Exception e){
			return (long) -1;
		}
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
					}else if(colunas.get(i).getTipo().equals("Datetime")){
						retorno.put(hashmapkey, new Date(cursor.getLong(cursor.getColumnIndex(nomesC[i]))));
					}else{
						//blob
						retorno.put(hashmapkey, cursor.getString(cursor.getColumnIndex(nomesC[i])));
					}

				}
				contadorLinhas++;
			}while(cursor.moveToNext());
		}
		return retorno;
	}

	public void criarTabela(ArrayList<Coluna> colunas, ArrayList<Tabela> tabelas){
		ArrayList<String> criacaoTabelas = new ArrayList<String>();
		String createTable = "";
		boolean entrouAutoincrement = false;

		for (int i = 0; i < tabelas.size(); i++) {
			createTable = "";
			createTable = "create table if not exists " + tabelas.get(i).getNome() + " (";

			//for para adicionar as colunas na string
			for (int j = 0; j < colunas.size(); j++) {
				//se a coluna pertencer a tabela em questão
				if(colunas.get(j).getNomeTabela().equals(tabelas.get(i).getNome())){
					//tratamento do blob para virar apenas a url 
					createTable = createTable + colunas.get(j).getNome() +" " + (colunas.get(j).getTipo().equals("BLOB") ? "Varchar":colunas.get(j).getTipo());

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
			database.execSQL(criacaoTabelas.get(j));
		}
	}
}
