package br.com.ufpe;

import java.util.ArrayList;

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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DataViewActivity extends Activity {
	
	private TableLayout headerTable;
	private TableLayout dataTable;
	private ScrollView scrollVertical;
	
	private ArrayList<Coluna> colunas;
	private String nameTabela;
	
	private DBHelper database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_view);
		
		Intent i = getIntent();
		nameTabela = i.getExtras().getString("nameTable");
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
		for (int j = 0; j < 25; j++) { //quantidade de linhas (dados)
			TableRow linha = new TableRow(this);
			
			for (int k = 0; k < colunas.size(); k++) { //quantidade colunas
				TextView dadoColuna = new TextView(this);
				dadoColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
				dadoColuna.setPadding(10, 10, 10, 10);
				dadoColuna.setText("AAA");
				dadoColuna.setTextColor(Color.WHITE);
				dadoColuna.setGravity(Gravity.CENTER);
				linha.addView(dadoColuna);
			}
			
			dataTable.addView(linha);
			
		}
		
		scrollVertical.addView(dataTable);
		headerTable.addView(scrollVertical);
		
		
		
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
		
		//preenchendo as colunas da visualização
		colunas = (ArrayList<Coluna>) database.getColunasByTabela(nameTabela);
		
		
	}
}
