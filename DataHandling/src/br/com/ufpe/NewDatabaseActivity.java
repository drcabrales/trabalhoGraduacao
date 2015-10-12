package br.com.ufpe;

import java.util.ArrayList;

import android.app.Activity;
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

public class NewDatabaseActivity extends Activity {
	
	private EditText edtNewDatabaseName;
	private Button btnNewDatabase;
	private ListView listDatabases;
	private ArrayAdapter<String> adapter;
	
	private ArrayList<String> namesDatabases;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_database);
		
		iniciarComponentes();
		
		btnNewDatabase.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/* Nesse clique:
				 * - adicionar o nome do novo banco na lista de nomes de bancos
				 * - criar o novo banco de dados caso não exista (se já existir, informar)
				 * - ir para a tela de criação de tabelas desse banco
				 */
				if(!edtNewDatabaseName.getText().toString().equals("")){
					namesDatabases.add(edtNewDatabaseName.getText().toString());
					adapter.notifyDataSetChanged();
				}
				
				boolean criou = criarBanco();
				
				if(criou){
					//iniciar tela de criação de tabelas desse banco
				}else{
					Toast.makeText(getBaseContext(), "A database with this name already exists!", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		listDatabases.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/* Nesse clique:
				 * - iniciar tela de criação de tabelas do banco escolhido
				 */
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
		edtNewDatabaseName = (EditText) findViewById(R.id.edtNewDBName);
		btnNewDatabase = (Button) findViewById(R.id.btnNewDB);
		listDatabases = (ListView) findViewById(R.id.listDatabases);
		
		//preencher namesDatabases com os bancos vindos do sistema (que o usuário já inseriu previamente)
		namesDatabases = new ArrayList<String>();
		
		adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, namesDatabases){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
		        TextView text = (TextView) view.findViewById(android.R.id.text1);
		        text.setTextColor(Color.WHITE);
		        return view;
			}
		};
		listDatabases.setAdapter(adapter);
		
	}
	
	public boolean criarBanco(){
		//Método responsável por criar o banco com o nome que o usuário deu
		return true;
	}
}
