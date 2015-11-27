package br.com.ufpe;

import java.util.ArrayList;
import java.util.List;

import br.com.ufpe.objects.Alteracao;
import br.com.ufpe.objects.Tabela;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class NewTableActivity extends Activity {

	private EditText edtNewTableName;
	private Button btnNewTable;
	private ListView listTables;
	private TextView txtDBName;
	private Button btnBack;
	private ArrayAdapter<String> adapter;

	private ArrayList<String> namesTables;
	private String nomeDB;

	//banco de dados do sistema
	private DBHelper database;

	//alteração em alguma tabela
	private String nomeTabelaAAlterar;
	private ArrayList<Alteracao> listaAlteracao;
	private boolean flagBancoCriado;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_table);

		Intent i = getIntent();
		nomeDB = i.getExtras().getString("DBName");
		listaAlteracao = new ArrayList<Alteracao>();
		iniciarComponentes();

		//pega o nome do banco selecionado via extras do intent

		btnNewTable.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/* Nesse clique:
				 * - adicionar o nome do novo banco na lista de nomes de bancos
				 * - criar a nova tabela caso não exista (se já existir, informar)
				 */
				if(!edtNewTableName.getText().toString().equals("")){
					if(!flagBancoCriado){ //se o banco não foi criado ainda, insere normalmente
						boolean criou = criarTabela(edtNewTableName.getText().toString(), nomeDB);

						if(criou){
							namesTables.add(edtNewTableName.getText().toString());
							adapter.notifyDataSetChanged();

							//não starta nova activity pq ele pode criar n tabelas,
							//e depois acessar a que deseja para prosseguir
						}else{
							Toast.makeText(getBaseContext(), "A database with this name already exists!", Toast.LENGTH_SHORT).show();
						}
					}else{ //se o banco já foi criado, a nova tabela entra em alteração
						Tabela aux = new Tabela(edtNewTableName.getText().toString(), nomeDB);
						listaAlteracao.add(new Alteracao("addTabela", null, null, null, null, null, null, null, null, null, aux, null));

						//ainda assim cria, mas na hora do save & view tem que verificar se ta na lista de alteração ou não
						boolean criou = criarTabela(edtNewTableName.getText().toString(), nomeDB);

						if(criou){
							namesTables.add(edtNewTableName.getText().toString());
							adapter.notifyDataSetChanged();

							//não starta nova activity pq ele pode criar n tabelas,
							//e depois acessar a que deseja para prosseguir
						}else{
							Toast.makeText(getBaseContext(), "A database with this name already exists!", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		});

		//clique para ir para a tela de listagem de colunas da tabela
		listTables.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/* Nesse clique:
				 * - iniciar tela de criação de colunas da tabela escolhida
				 */
				Intent i = new Intent(getBaseContext(), NewColumnActivity.class);
				i.putExtra("nameTable", (String) parent.getAdapter().getItem(position));
				i.putExtra("tablesList", namesTables);
				i.putExtra("DBName", nomeDB);
				i.putExtra("listaAlteracao", listaAlteracao);
				startActivity(i);

			}
		});

		//clique para aparecer a opção de editar a tabela
		listTables.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				nomeTabelaAAlterar = (String) parent.getAdapter().getItem(position);
				return false;
			}
		});

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getBaseContext(), NewDatabaseActivity.class);
				i.putExtra("DBName", nomeDB);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_table, menu);
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

	}

	@Override
	public boolean onContextItemSelected(MenuItem item){
		super.onContextItemSelected(item);

		if(item.getTitle().equals("Edit")){
			//escolha de edição no menu de tabelas
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
					listaAlteracao.add(new Alteracao("altNomeTabela", nomeTabelaAAlterar, userInput.getText().toString(), null, null, null, null, null, null, null, null, null));
					database.updateTabela(nomeTabelaAAlterar, userInput.getText().toString(), nomeDB);
					database.updateReferenciaColunaTabela(userInput.getText().toString(), nomeTabelaAAlterar);
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
			listaAlteracao.add(new Alteracao("delTabela", null, null, null, null, nomeTabelaAAlterar, null, null, null, null, null, null));
			database.deleteReferenciaColunaTabela(nomeTabelaAAlterar);
			database.deleteTabela(nomeTabelaAAlterar, nomeDB);
			Toast.makeText(this, "Table deleted!", Toast.LENGTH_SHORT).show();
		}

		return true;
	}

	public void iniciarComponentes(){

		database = new DBHelper(getBaseContext());

		edtNewTableName = (EditText) findViewById(R.id.edtNewTableName);
		btnNewTable = (Button) findViewById(R.id.btnNewTable);
		listTables = (ListView) findViewById(R.id.listTables);
		btnBack = (Button) findViewById(R.id.btnBack);

		//seta o nome do DB atual
		txtDBName = (TextView) findViewById(R.id.txtDBName);
		txtDBName.setText("Database: " + nomeDB);

		//preencher namesTables com as tabelas vindas do sistema (que o usuário já inseriu previamente)
		namesTables = new ArrayList<String>();
		ArrayList<Tabela> tabelas = new ArrayList<Tabela>();
		tabelas = (ArrayList<Tabela>) database.getTabelasByBanco(nomeDB);
		for (int i = 0; i < tabelas.size(); i++) {
			namesTables.add(tabelas.get(i).getNome());
		}

		adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, namesTables){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text = (TextView) view.findViewById(android.R.id.text1);
				text.setTextColor(Color.WHITE);
				return view;
			}
		};
		listTables.setAdapter(adapter);

		//registrando para no clique longo aparecer menu de contexto
		registerForContextMenu(listTables);

		flagBancoCriado = database.getFlagCriado(nomeDB);

	}

	public boolean criarTabela(String tabela, String banco){
		//Método responsável por adicionar as tabelas que o usuário deseja criar 
		//no banco do sistema

		long result = database.insertTabela(tabela, banco);

		if(result == -1){
			return false;
		}else{
			return true;
		}
	}
}
