package br.com.ufpe;

import java.util.ArrayList;

import br.com.ufpe.objects.Alteracao;
import br.com.ufpe.objects.Coluna;
import br.com.ufpe.objects.Tabela;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewColumnActivity extends Activity {

	private Button btnNewColumn;
	private Button btnSaveAndView;
	private ListView listColumns;
	private TextView txtTableName;
	private Button btnBackToTables;
	private ArrayAdapter<String> adapter;

	private ArrayList<String> namesColumns;
	private ArrayList<String> namesTables;
	private String nomeTabela;
	private String DBName;
	private String nomeColunaAdicionada;

	//para o save and view
	private ArrayList<Tabela> tabelas;
	private ArrayList<Coluna> colunas;

	//banco de dados do sistema
	private DBHelper database;

	//alteracao de dados
	private ArrayList<Alteracao> listaAlteracao;
	private String nomeColunaAAlterar;
	private ArrayList<String> namesTablesAlt;
	private ArrayList<Coluna> colunasAlt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_column);

		Intent i = getIntent();
		nomeTabela = i.getExtras().getString("nameTable");
		namesTables = (ArrayList<String>) i.getExtras().get("tablesList");
		listaAlteracao = (ArrayList<Alteracao>) i.getExtras().get("listaAlteracao");
		DBName = i.getExtras().getString("DBName");
		iniciarComponentes();

		//pega o nome do banco selecionado via extras do intent

		btnNewColumn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/* Nesse clique:
				 * - abre o popup de criação de coluna
				 */
				Intent i = new Intent(getBaseContext(), PopUpNewColumnActivity.class);
				i.putExtra("nameTable", nomeTabela);
				i.putExtra("tablesList", namesTables);
				i.putExtra("listaAlteracao", listaAlteracao);
				i.putExtra("DBName", DBName);
				startActivity(i);
			}
		});

		btnSaveAndView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/* Nesse clique:
				 * - cria ou atualiza o banco e tabelas do usuário
				 * - vai para a tela de visualização de dados das colunas
				 */

				//preenchendo tabelas e colunas para finalmente criar o banco
				/*tabelas = (ArrayList<Tabela>) database.getTabelasByBanco(DBName);
				colunas = new ArrayList<Coluna>();

				for (int i = 0; i < tabelas.size(); i++) {
					colunas.addAll(database.getColunasByTabela(tabelas.get(i).getNome()));
				}

				//chamando método de criação do banco com tabelas e colunas
				database.createUserDatabase(getBaseContext(), DBName, tabelas, colunas);*/
				//------------------------------------------------------------------------
				
				
				//faz as alterações necessárias no banco
				for (int j = 0; j < listaAlteracao.size(); j++) {
					if(listaAlteracao.get(j).getTipoAlteracao().equals("altNomeColuna")){
						 database.updateColuna(listaAlteracao.get(j).getNomeVelhoColuna(), listaAlteracao.get(j).getNomeNovoColuna(), nomeTabela);
					}else if(listaAlteracao.get(j).getTipoAlteracao().equals("delColuna")){
						database.deleteColuna(listaAlteracao.get(j).getDelColuna(), nomeTabela);
					}
				}

				//CHAMAR A TELA DE VISUALIZAÇÃO/INSERÇÃO DE DADOS
				Intent i = new Intent(getBaseContext(), DataViewActivity.class);
				i.putExtra("nameTable", nomeTabela);
				i.putExtra("tablesList", namesTables);
				i.putExtra("DBName", DBName);
				i.putExtra("listaAlteracao", listaAlteracao);
				i.putExtra("columnsList", colunas);
				startActivity(i);
			}
		});

		//clique para aparecer a opção de editar a tabela
		listColumns.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				nomeColunaAAlterar = (String) parent.getAdapter().getItem(position);
				return false;
			}
		});

		btnBackToTables.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getBaseContext(), NewTableActivity.class);
				i.putExtra("DBName", DBName);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_column, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add("Edit");
		menu.add("Delete");
		
		colunasAlt = new ArrayList<Coluna>();
		for (int i = 0; i < colunas.size(); i++) {
			if(colunas.get(i).getNomeTabela().equals(nomeTabela)){
				Coluna aux = new Coluna();
				aux.setAutoincrement(colunas.get(i).isAutoincrement());
				aux.setFK(colunas.get(i).isFK());
				aux.setNome(colunas.get(i).getNome());
				aux.setNomeColunaFK(colunas.get(i).getNomeColunaFK());
				aux.setNomeTabela(colunas.get(i).getNomeTabela());
				aux.setNomeTabelaFK(colunas.get(i).getNomeTabelaFK());
				aux.setPK(colunas.get(i).isPK());
				aux.setTipo(colunas.get(i).getTipo());
				aux.setTipoBlob(colunas.get(i).getTipoBlob());
				colunasAlt.add(aux);
			}
		}
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		super.onContextItemSelected(item);
		
		if(item.getTitle().equals("Edit")){
			//escolha de edição no menu de colunas
			// get prompts.xml view
			LayoutInflater li = LayoutInflater.from(this);
			View promptsView = li.inflate(R.layout.prompts, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);

			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);

			final EditText userInput = (EditText) promptsView
					.findViewById(R.id.editTextDialogUserInput);

			// set dialog message
			alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					// get user input and set it to result
					// edit text
				    listaAlteracao.add(new Alteracao("altNomeColuna", null, null, nomeColunaAAlterar, userInput.getText().toString(), null, null, namesTables, colunasAlt, DBName));
				    
				    for (int i = 0; i < colunasAlt.size(); i++) {
						if(colunasAlt.get(i).getNome().equals(nomeColunaAAlterar)){
							Coluna aux = colunasAlt.get(i);
							aux.setNome(userInput.getText().toString());
							colunasAlt.remove(i);
							colunasAlt.add(aux);
						}
					}
				    
				    //FAZER ISSO NO SAVE AND VIEW
				    //database.updateColuna(nomeColunaAAlterar, userInput.getText().toString(), nomeTabela);
				    }
				  })
				.setNegativeButton("Cancel",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				    }
				  });

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}else{
			//escolha de delete no menu de tabelas
			listaAlteracao.add(new Alteracao("delColuna", null, null, null, null, null, nomeColunaAAlterar, namesTables, colunasAlt, DBName));
			
			for (int i = 0; i < colunasAlt.size(); i++) {
				if(colunasAlt.get(i).getNome().equals(nomeColunaAAlterar)){
					colunasAlt.remove(i);
				}
			}
			
			//FAZER ISSO NO SAVE AND VIEW
			//database.deleteColuna(nomeColunaAAlterar, nomeTabela);
		}
		
		return true;
	}

	public void iniciarComponentes(){

		database = new DBHelper(getBaseContext());

		btnNewColumn = (Button) findViewById(R.id.btnNewColumn);
		listColumns = (ListView) findViewById(R.id.listColumns);
		btnBackToTables = (Button) findViewById(R.id.btnBackToTables);
		btnSaveAndView = (Button) findViewById(R.id.btnSaveAndView);

		//seta o nome da tabela atual
		txtTableName = (TextView) findViewById(R.id.txtTableName);
		txtTableName.setText("Table: " + nomeTabela);

		//preencher namesColumns com as colunas vindas do sistema (que o usuário já inseriu previamente)
		namesColumns = new ArrayList<String>();
		colunas = new ArrayList<Coluna>();
		
		for (int i = 0; i < namesTables.size(); i++) {
			colunas.addAll((ArrayList<Coluna>) database.getColunasByTabela(namesTables.get(i)));
		}
		
		for (int i = 0; i < colunas.size(); i++) {
			if(colunas.get(i).getNomeTabela().equals(nomeTabela)){
				namesColumns.add(colunas.get(i).getNome());				
			}
		}

		adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, namesColumns){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text = (TextView) view.findViewById(android.R.id.text1);
				text.setTextColor(Color.WHITE);
				return view;
			}
		};
		listColumns.setAdapter(adapter);
		//registrando para no clique longo aparecer menu de contexto
		registerForContextMenu(listColumns);
		
		
		namesTablesAlt = new ArrayList<String>();
		for (int i = 0; i < namesTables.size(); i++) {
			namesTablesAlt.add(namesTables.get(i));
		}

	}
}
