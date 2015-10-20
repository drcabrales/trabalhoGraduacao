package br.com.ufpe;

import java.util.ArrayList;

import br.com.ufpe.objects.Coluna;
import br.com.ufpe.objects.Tabela;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	private String nomeTabela;
	private String DBName;
	private String nomeColunaAdicionada;

	//banco de dados do sistema
	private DBHelper database;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_column);
		
		Intent i = getIntent();
		nomeTabela = i.getExtras().getString("nameTable");
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
				
				//TESTE DE FUNCIONAMENTO
				ArrayList<Tabela> tabelas = new ArrayList<Tabela>();
				tabelas.add(new Tabela("tabela1", "testandoDB"));
				tabelas.add(new Tabela("tabela2", "testandoDB"));
				
				ArrayList<Coluna> colunas = new ArrayList<Coluna>();
				colunas.add(new Coluna ("coluna1tb1", "integer", "tabela1", true, true, false, null, null)); //coluna chave primaria e auto incremental
				colunas.add(new Coluna ("coluna2tb1", "text", "tabela1", false, false, false, null, null)); //coluna extra
				
				colunas.add(new Coluna ("coluna1tb2", "text", "tabela2", true, false, false, null, null)); //coluna chave primaria sem auto incremento
				colunas.add(new Coluna ("coluna2tb2", "integer", "tabela2", false, false, true, "tabela1", "coluna1tb1")); //coluna FK que referencia a chave primária da primeira tabela
				database.createUserDatabase(getBaseContext(), "testanaDB09988", tabelas, colunas);
			}
		});
		
		listColumns.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/* Nesse clique:
				 * - editar a coluna escolhida
				 */
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
		ArrayList<Coluna> colunas = new ArrayList<Coluna>();
		colunas = (ArrayList<Coluna>) database.getColunasByTabela(nomeTabela);
		for (int i = 0; i < colunas.size(); i++) {
			namesColumns.add(colunas.get(i).getNome());
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
		
	}
}
