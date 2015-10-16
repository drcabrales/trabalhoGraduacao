package br.com.ufpe;

import java.util.ArrayList;

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
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_table);
		
		Intent i = getIntent();
		nomeDB = i.getExtras().getString("DBName");
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
		});
		
		listTables.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/* Nesse clique:
				 * - iniciar tela de criação de colunas da tabela escolhida
				 */
				Intent i = new Intent(getBaseContext(), NewColumnActivity.class);
				i.putExtra("nameTable", (String) parent.getAdapter().getItem(position));
				i.putExtra("DBName", nomeDB);
				startActivity(i);
				
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
