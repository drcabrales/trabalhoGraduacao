package br.com.ufpe;

import java.util.ArrayList;
import java.util.Map;

import br.com.ufpe.objects.Coluna;
import br.com.ufpe.objects.Tabela;
import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DataViewActivity extends Activity {
	
	private TextView tableName;
	private TableLayout headerTable;
	private TableLayout dataTable;
	private ScrollView scrollVertical;
	
	private ArrayList<Coluna> colunas;
	private ArrayList<String> namesTables;
	private ArrayList<Tabela> tabelas;
	private String nameTabela;
	private String DBName;
	
	private DBHelper database;
	private DBHelperUsuario dbHelperUsuario;
	
	private Button btnNovoDado;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_view);
		
		Intent i = getIntent();
		nameTabela = i.getExtras().getString("nameTable");
		namesTables = (ArrayList<String>) i.getExtras().get("tablesList");
		DBName = i.getExtras().getString("DBName");
		iniciarComponentes();
		
		//nesse ponto já sabemos o nome da tabela e as colunas. 
		//agora tem que preencher a tabela com os dados do usuário
		
		//preenchendo o header com o nome das tabelas
		TableRow headerNomesColunas = new TableRow(this);
		//headerNomesColunas.setBackgroundColor(Color.rgb(136, 93, 178));
		for (int j = 0; j < colunas.size(); j++) {
			TextView nomeColuna = new TextView(this);
			nomeColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
			nomeColuna.setPadding(10, 10, 10, 10);
			nomeColuna.setText(colunas.get(j).getNome());
			nomeColuna.setTextColor(Color.WHITE);
			nomeColuna.setGravity(Gravity.CENTER);
			headerNomesColunas.addView(nomeColuna);
		}
		
		headerTable.addView(headerNomesColunas);
		
		//setar os parametros do scroll
		scrollVertical.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		//adiciona o table layout dos dados no scroll
		dataTable.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		//for para adicionar os table row de dados no table layout datatable
		//tem que pegar a lista de dados para usar nesse for. aqui vai entrar tb a logica do multimidia (botao diferenciado)
		Map<String, Object> dados = dbHelperUsuario.getAllDataFromTable(nameTabela, colunas);
		
		for (int j = 0; j < (dados.size()/colunas.size()); j++) { //quantidade de linhas (dados totais / colunas)
			TableRow linha = new TableRow(this);
			
			for (int k = 0; k < colunas.size(); k++) { //quantidade colunas
				TextView dadoColuna = new TextView(this);
				dadoColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
				dadoColuna.setPadding(10, 10, 10, 10);
				
				if(!colunas.get(k).getTipo().equals("BLOB")){
					if(colunas.get(k).getTipo().equals("Varchar") || colunas.get(k).getTipo().equals("Text")){
						String hashmapkey = colunas.get(k).getNome() + j+ k;
						dadoColuna.setText((String) dados.get(hashmapkey));
					}else if(colunas.get(k).getTipo().equals("Integer")){
						String hashmapkey = colunas.get(k).getNome() + j+ k;
						dadoColuna.setText((Integer) dados.get(hashmapkey) + "");
					}else if(colunas.get(k).getTipo().equals("Double")){
						String hashmapkey = colunas.get(k).getNome() + j+ k;
						dadoColuna.setText((Double) dados.get(hashmapkey) + "");
					}else if(colunas.get(k).getTipo().equals("Float")){
						String hashmapkey = colunas.get(k).getNome() + j+ k;
						dadoColuna.setText((Float) dados.get(hashmapkey) + "");
					}else{
						//boolean
						String hashmapkey = colunas.get(k).getNome() + j+ k;
						dadoColuna.setText((Boolean) dados.get(hashmapkey) + "");
					}
					
				}else{
					//coloca a indicação do blob na tabela
				}
				
				dadoColuna.setTextColor(Color.WHITE);
				dadoColuna.setGravity(Gravity.CENTER);
				linha.addView(dadoColuna);
			}
			
			dataTable.addView(linha);
			
		}
		
		scrollVertical.addView(dataTable);
		headerTable.addView(scrollVertical);
		
		
		btnNovoDado.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//vai para a tela de inserção da nova linha
				//passa: nome do banco, nome da tabela e colunas
				Intent i = new Intent(getBaseContext(), NewDataActivity.class);
				i.putExtra("DBName", DBName);
				i.putExtra("TableName", nameTabela);
				i.putExtra("ListaColunas", colunas);
				i.putExtra("tablesList", namesTables);
				i.putExtra("ListaTabelas", tabelas);
				startActivity(i);
			}
		});
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data_view, menu);
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
		
		headerTable = (TableLayout) findViewById(R.id.headerTable);
		scrollVertical = new ScrollView(this);
		dataTable = new TableLayout(this);
		
		tableName = (TextView) findViewById(R.id.txtTableViewName);
		tableName.setText("Table: " + nameTabela);
		
		btnNovoDado = (Button) findViewById(R.id.btnNewData);
		
		//preenchendo as colunas da visualização
		colunas = (ArrayList<Coluna>) database.getColunasByTabela(nameTabela);
		
		tabelas = new ArrayList<Tabela>();
		for (int i = 0; i < namesTables.size(); i++) {
			tabelas.add(new Tabela(namesTables.get(i), DBName));
		}
		
		dbHelperUsuario = new DBHelperUsuario(getBaseContext(), DBName, tabelas, colunas);
		
		
	}
}
