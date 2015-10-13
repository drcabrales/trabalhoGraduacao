package br.com.ufpe;

import java.util.ArrayList;

import br.com.ufpe.objects.Banco;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
	
	//banco de dados do sistema
	private DBHelper database;
	

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
					
					boolean criou = criarBanco(edtNewDatabaseName.getText().toString());
					
					//criou aqui significa: armazenou no db do sistema que pode existir um novo banco com esse nome
					//a criação de fato ocorrerá quando ele adicionar uma tabela e uma coluna nela
					if(criou){
						namesDatabases.add(edtNewDatabaseName.getText().toString());
						adapter.notifyDataSetChanged();
						
						//starta a nova activity
						Intent intent = new Intent(getBaseContext(), NewTableActivity.class);
						intent.putExtra("DBName", edtNewDatabaseName.getText().toString());
						startActivity(intent);
						
					}else{
						Toast.makeText(getBaseContext(), "A database with this name already exists!", Toast.LENGTH_SHORT).show();
					}
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
				
				//starta a nova activity
				Intent intent = new Intent(getBaseContext(), NewTableActivity.class);
				intent.putExtra("DBName", (String) parent.getAdapter().getItem(position));
				startActivity(intent);
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
		
		database = new DBHelper(getBaseContext());
		
		edtNewDatabaseName = (EditText) findViewById(R.id.edtNewDBName);
		btnNewDatabase = (Button) findViewById(R.id.btnNewDB);
		listDatabases = (ListView) findViewById(R.id.listDatabases);
		
		//preencher namesDatabases com os bancos vindos do sistema (que o usuário já inseriu previamente)
		ArrayList<Banco> bancos = (ArrayList<Banco>) database.getAllNomesBancos();
		namesDatabases = new ArrayList<String>();
		for (int i = 0; i < bancos.size(); i++) {
			namesDatabases.add(bancos.get(i).getNome());
		}
		
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
	
	public boolean criarBanco(String nomeDB){
		long result = database.insertBanco(nomeDB);
		
		if(result == -1){
			return false;
		}else{
			return true;
		}
	}
}
