package br.com.ufpe;

import java.util.ArrayList;

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
					boolean criou = criarTabela("banco teste");
					
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
				 * - iniciar tela de criação de tabelas do banco escolhido
				 */
			}
		});
		
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getBaseContext(), NewDatabaseActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_database, menu);
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
		edtNewTableName = (EditText) findViewById(R.id.edtNewTableName);
		btnNewTable = (Button) findViewById(R.id.btnNewTable);
		listTables = (ListView) findViewById(R.id.listTables);
		btnBack = (Button) findViewById(R.id.btnBack);
		
		//seta o nome do DB atual
		txtDBName = (TextView) findViewById(R.id.txtDBName);
		txtDBName.setText("Database: " + nomeDB);
		
		//preencher namesDatabases com os bancos vindos do sistema (que o usuário já inseriu previamente)
		namesTables = new ArrayList<String>();
		
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
	
	public boolean criarTabela(String banco){
		//Método responsável por criar o banco com o nome que o usuário deu
		return true;
	}
}
