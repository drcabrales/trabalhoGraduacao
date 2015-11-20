package br.com.ufpe;

import java.util.ArrayList;
import java.util.List;

import br.com.ufpe.objects.Alteracao;
import br.com.ufpe.objects.Coluna;
import br.com.ufpe.objects.Tabela;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PopUpNewColumnActivity extends Activity {
	private EditText edtNomeColuna;
	private Spinner spnType;
	private Spinner spnTableFK;
	private Spinner spnColumnFK;
	private Spinner spnTipoBlob;
	private CheckBox chkPK;
	private CheckBox chkAutoincrement;
	private CheckBox chkFK;
	private Button btnOkNewColumn;
	private Button btnBackToColumns;

	private String nomeTabela;
	private ArrayList<String> nomesTabelas;
	
	//names tables de repasse e alteracoes
	private ArrayList<String> namesTables;
	private ArrayList<Alteracao> listaAlteracao;
	
	private Coluna coluna;
	private String DBName;
	private DBHelper database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pop_up_new_column);
		
		Intent i = getIntent();
		nomeTabela = i.getExtras().getString("nameTable");
		namesTables = (ArrayList<String>) i.getExtras().get("tablesList");
		listaAlteracao = (ArrayList<Alteracao>) i.getExtras().get("listaAlteracao");
		DBName = i.getExtras().getString("DBName");
		iniciarComponentes();

		iniciarComponentes();
		
		spnType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(position != 0){
					coluna.setTipo((String) parent.getAdapter().getItem(position));
					
					if(coluna.getTipo().equals("BLOB")){
						spnTipoBlob.setVisibility(View.VISIBLE);
					}else{
						spnTipoBlob.setVisibility(View.GONE);
						coluna.setTipoBlob(null);
					}
				}else{
					coluna.setTipo(null);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		spnTipoBlob.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(position != 0){
					coluna.setTipoBlob((String) parent.getAdapter().getItem(position));
				}else{
					coluna.setTipoBlob(null);
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});

		chkPK.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					chkAutoincrement.setVisibility(View.VISIBLE);
					coluna.setPK(isChecked);
				}else{
					chkAutoincrement.setVisibility(View.GONE);
					coluna.setPK(!isChecked);
				}
			}
		});

		chkFK.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// se é FK, habilita e preenche o spinner de tabelas desse banco
				if(isChecked){
					spnTableFK.setVisibility(View.VISIBLE);
					List<Tabela> tabelas = database.getAllNomesTabelas();
					
					for (int i = 0; i < tabelas.size(); i++) {
						nomesTabelas.add(tabelas.get(i).getNome());
					}

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(),
							android.R.layout.simple_spinner_item, nomesTabelas);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spnTableFK.setAdapter(dataAdapter);

					coluna.setFK(isChecked);
				}else{
					spnTableFK.setVisibility(View.GONE);
					spnColumnFK.setVisibility(View.GONE);
					coluna.setFK(!isChecked);
				}
			}
		});

		spnTableFK.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(position != 0){
					coluna.setNomeTabelaFK((String) parent.getAdapter().getItem(position));
					//quando selecionou uma tabela, seta a de colunasFK com as colunas dela
					spnColumnFK.setVisibility(View.VISIBLE);
					ArrayList<Coluna> colunas = (ArrayList<Coluna>) database.getColunasByTabela(coluna.getNomeTabelaFK());
					ArrayList<String> nomeColuna = new ArrayList<String>();
					nomeColuna.add("Select a FK column");
					for (int i = 0; i < colunas.size(); i++) {
						nomeColuna.add(colunas.get(i).getNome());
					}
					
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(),
							android.R.layout.simple_spinner_item, nomeColuna);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spnColumnFK.setAdapter(dataAdapter);
					
					
				}else{
					spnColumnFK.setVisibility(View.GONE);
					coluna.setNomeTabelaFK(null);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		spnColumnFK.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(position != 0){
					coluna.setNomeColunaFK((String) parent.getAdapter().getItem(position));
				}else{
					coluna.setNomeColunaFK(null);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});

		btnBackToColumns.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getBaseContext(), NewColumnActivity.class);
				i.putExtra("nameTable", nomeTabela);
				i.putExtra("tablesList", namesTables);
				i.putExtra("DBName", DBName);
				i.putExtra("listaAlteracao", listaAlteracao);
				startActivity(i);
			}
		});

		btnOkNewColumn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!edtNomeColuna.getText().toString().equals("")){
					coluna.setNome(edtNomeColuna.getText().toString());
					coluna.setAutoincrement(chkAutoincrement.isChecked());
					
					boolean criou = false;
					if(coluna.getTipo() != null){
						criou = criarColuna(coluna);
					}else{
						//ver pq n ta rolando
						Toast.makeText(getBaseContext(), "Select the column type", Toast.LENGTH_SHORT);
					}
					
					if(criou){
						Intent i = new Intent(getBaseContext(), NewColumnActivity.class);
						i.putExtra("nameTable", nomeTabela);
						i.putExtra("tablesList", namesTables);
						i.putExtra("DBName", DBName);
						i.putExtra("listaAlteracao", listaAlteracao);
						startActivity(i);
					}else{
						Toast.makeText(getBaseContext(), "It's not possible to create the column", Toast.LENGTH_SHORT);
					}
				}else{
					Toast.makeText(getBaseContext(), "Enter a column name", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pop_up_new_column, menu);
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
		coluna = new Coluna();

		edtNomeColuna = (EditText) findViewById(R.id.edtNomeColuna);

		spnType = (Spinner) findViewById(R.id.spnType);
		List<String> list = new ArrayList<String>();
		list.add("Select a type");
		list.add("BLOB");
		list.add("Boolean");
		list.add("Datetime");
		list.add("Double");
		list.add("Float");
		list.add("Integer");
		list.add("Varchar");
		list.add("Text");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnType.setAdapter(dataAdapter);

		spnTableFK = (Spinner) findViewById(R.id.spnTableFK);
		spnColumnFK = (Spinner) findViewById(R.id.spnColumnFK);
		//alteração para tipo do blob
		spnTipoBlob = (Spinner) findViewById(R.id.spnTypeBlob);
		List<String> listTBlob = new ArrayList<String>();
		listTBlob.add("Select a BLOB type");
		listTBlob.add("Image");
		listTBlob.add("Movie");
		listTBlob.add("Music");
		ArrayAdapter<String> dataAdapterTBlob = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listTBlob);
		dataAdapterTBlob.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnTipoBlob.setAdapter(dataAdapterTBlob);
		
		
		chkPK = (CheckBox) findViewById(R.id.chkPK);
		chkFK = (CheckBox) findViewById(R.id.chkFK);
		chkAutoincrement = (CheckBox) findViewById(R.id.chkAutoI);

		btnBackToColumns = (Button) findViewById(R.id.btnBackToColumns);
		btnOkNewColumn = (Button) findViewById(R.id.btnOkNewColumn);
		
		nomesTabelas = new ArrayList<String>();
		nomesTabelas.add("Select a table");
	}
	
	public boolean criarColuna(Coluna coluna){
		int pk = 0;
		int autoincrement = 0;
		int fk = 0;
		
		if(coluna.isPK()){
			pk = 1;
		}
		
		if(coluna.isAutoincrement()){
			autoincrement = 1;
		}
		
		if(coluna.isFK()){
			fk = 1;
		}
		
		long retorno = database.insertColuna(coluna.getNome(), coluna.getTipo(), nomeTabela, pk, autoincrement, fk, coluna.getNomeTabelaFK(), coluna.getNomeColunaFK(), coluna.getTipoBlob());
		
		if(retorno == -1){
			return false;
		}else{
			return true;
		}
	}
}
