package br.com.ufpe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.ufpe.objects.Alteracao;
import br.com.ufpe.objects.Coluna;
import br.com.ufpe.objects.Tabela;
import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
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
	private ArrayList<Coluna> colunasParaVisualizacao;
	private ArrayList<String> namesTables;
	private ArrayList<Tabela> tabelas;
	private String nameTabela;
	private String DBName;

	private DBHelper database;
	private DBHelperUsuario dbHelperUsuario;

	private Button btnNovoDado;

	private Map<Integer, String> auxDadosBlob;

	//alteracao de esquema
	private ArrayList<Alteracao> listaAlteracao;

	//alteração de dados
	private ArrayList<TableRow> linhasDeDados;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_view);

		Intent i = getIntent();
		nameTabela = i.getExtras().getString("nameTable");
		namesTables = (ArrayList<String>) i.getExtras().get("tablesList");
		DBName = i.getExtras().getString("DBName");
		listaAlteracao = (ArrayList<Alteracao>) i.getExtras().get("listaAlteracao");
		iniciarComponentes();

		//nesse ponto já sabemos o nome da tabela e as colunas. 
		//agora tem que preencher a tabela com os dados do usuário

		//preenchendo o header com o nome das tabelas
		TableRow headerNomesColunas = new TableRow(this);
		//headerNomesColunas.setBackgroundColor(Color.rgb(136, 93, 178));
		for (int j = 0; j < colunasParaVisualizacao.size(); j++) {
			TextView nomeColuna = new TextView(this);
			nomeColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
			nomeColuna.setPadding(10, 10, 10, 10);
			nomeColuna.setText(colunasParaVisualizacao.get(j).getNome());
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
		final Map<String, Object> dados = dbHelperUsuario.getAllDataFromTable(nameTabela, colunasParaVisualizacao);

		for (int j = 0; j < (dados.size()/colunasParaVisualizacao.size()); j++) { //quantidade de linhas (dados totais / colunas)
			TableRow linha = new TableRow(this);
			linha.setId(j);
			linhasDeDados.add(linha);

			for (int k = 0; k < colunasParaVisualizacao.size(); k++) { //quantidade colunas
				TextView dadoColuna = new TextView(this);
				dadoColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
				dadoColuna.setPadding(10, 10, 10, 10);

				if(!colunasParaVisualizacao.get(k).getTipo().equals("BLOB")){
					if(colunasParaVisualizacao.get(k).getTipo().equals("Varchar") || colunasParaVisualizacao.get(k).getTipo().equals("Text")){
						String hashmapkey = colunasParaVisualizacao.get(k).getNome() + j+ k;
						dadoColuna.setText((String) dados.get(hashmapkey));
					}else if(colunasParaVisualizacao.get(k).getTipo().equals("Integer")){
						String hashmapkey = colunasParaVisualizacao.get(k).getNome() + j+ k;
						dadoColuna.setText((Integer) dados.get(hashmapkey) + "");
					}else if(colunasParaVisualizacao.get(k).getTipo().equals("Double")){
						String hashmapkey = colunasParaVisualizacao.get(k).getNome() + j+ k;
						dadoColuna.setText((Double) dados.get(hashmapkey) + "");
					}else if(colunasParaVisualizacao.get(k).getTipo().equals("Float")){
						String hashmapkey = colunasParaVisualizacao.get(k).getNome() + j+ k;
						dadoColuna.setText((Float) dados.get(hashmapkey) + "");
					}else if(colunasParaVisualizacao.get(k).getTipo().equals("Datetime")){
						String hashmapkey = colunasParaVisualizacao.get(k).getNome() + j+ k;
						SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
						dadoColuna.setText(format.format(dados.get(hashmapkey)) + "");
					}else{
						//boolean
						String hashmapkey = colunasParaVisualizacao.get(k).getNome() + j+ k;
						dadoColuna.setText((Boolean) dados.get(hashmapkey) + "");
					}

				}else{
					//coloca a indicação do blob na tabela
					//por enquanto fica o path, pra ver se deu certo
					String hashmapkey = colunasParaVisualizacao.get(k).getNome() + j+ k;

					final String path = (String) dados.get(hashmapkey) + "";

					//salvando o path para a coluna correta
					auxDadosBlob.put(Integer.parseInt(j+""+k), path);

					if(path.substring(path.length()-3, path.length()).equals("png") || path.substring(path.length()-3, path.length()).equals("jpg") || path.substring(path.length()-4, path.length()).equals("jpeg")){ //imagem
						final Button btnVerBlob = new Button(this);
						btnVerBlob.setText("IMG");
						btnVerBlob.setId(Integer.parseInt(j + "" + k));

						btnVerBlob.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								//teste com imagem 


								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								Bitmap inImage = BitmapFactory.decodeFile(auxDadosBlob.get(btnVerBlob.getId()));
								ByteArrayOutputStream bytes = new ByteArrayOutputStream();
								inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
								String path = Images.Media.insertImage(getBaseContext().getContentResolver(), inImage, "Title", null);

								intent.setDataAndType(Uri.parse(path), "image/*");
								startActivity(intent);

							}
						});

						linha.addView(btnVerBlob);

					}else if(path.substring(path.length()-3, path.length()).equals("mp3") || path.substring(path.length()-3, path.length()).equals("ogg") || path.substring(path.length()-3, path.length()).equals("aac")){ //musica
						final Button btnVerBlob = new Button(this);
						btnVerBlob.setText("MUS");
						btnVerBlob.setId(Integer.parseInt(j + "" + k));

						btnVerBlob.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								//teste com audio

								play(getBaseContext(), Uri.parse(auxDadosBlob.get(btnVerBlob.getId()))); 

							}
						});

						linha.addView(btnVerBlob);

					}else{ //video
						final Button btnVerBlob = new Button(this);
						btnVerBlob.setText("VID");
						btnVerBlob.setId(Integer.parseInt(j + "" + k));

						btnVerBlob.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								//teste com video


								Intent intentVideoPlayer = new Intent(Intent.ACTION_VIEW, Uri.parse(auxDadosBlob.get(btnVerBlob.getId())));
								intentVideoPlayer.setType("video/*");
								intentVideoPlayer.setData(Uri.parse(path));
								startActivity(intentVideoPlayer);

							}
						});

						linha.addView(btnVerBlob);
					}

				}

				dadoColuna.setTextColor(Color.WHITE);
				dadoColuna.setGravity(Gravity.CENTER);
				if(!colunasParaVisualizacao.get(k).getTipo().equals("BLOB")){
					linha.addView(dadoColuna);
				}

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
				i.putExtra("ListaColunas", colunasParaVisualizacao);
				i.putExtra("tablesList", namesTables);
				i.putExtra("ListaTabelas", tabelas);
				i.putExtra("listaAlteracao", listaAlteracao);
				startActivity(i);
			}
		});


		//CLIQUE PARA EDIÇÃO DE LINHAS
		for (int j = 0; j < linhasDeDados.size(); j++) {
			TableRow row = linhasDeDados.get(j);

			//ATENÇÃO: MUDAR PARA LONGCLICK COM A ESCOLHA DE EDITAR OU DELETAR!
			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(getBaseContext(), EditDataActivity.class);
					
					//passa os dados da linha para a EditDataActivity, através de um hashmap("nomeDaColuna", valor)
					HashMap<String, Object> dataRow = new HashMap<String, Object>();
					for (int l = 0; l < colunasParaVisualizacao.size(); l++) {
						TableRow aux = (TableRow) v;
						
						if(!colunasParaVisualizacao.get(l).getTipo().equals("BLOB")){
							if(colunasParaVisualizacao.get(l).getTipo().equals("Varchar") || colunasParaVisualizacao.get(l).getTipo().equals("Text")){
								
								dataRow.put(colunasParaVisualizacao.get(l).getNome(), ((TextView) aux.getChildAt(l)).getText().toString());
								
							}else if(colunasParaVisualizacao.get(l).getTipo().equals("Integer")){
								
								dataRow.put(colunasParaVisualizacao.get(l).getNome(), Integer.parseInt(((TextView) aux.getChildAt(l)).getText().toString()));
								
							}else if(colunasParaVisualizacao.get(l).getTipo().equals("Double")){
								
								dataRow.put(colunasParaVisualizacao.get(l).getNome(), Double.parseDouble(((TextView) aux.getChildAt(l)).getText().toString()));
								
							}else if(colunasParaVisualizacao.get(l).getTipo().equals("Float")){
								
								dataRow.put(colunasParaVisualizacao.get(l).getNome(), Float.parseFloat(((TextView) aux.getChildAt(l)).getText().toString()));
								
							}else if(colunasParaVisualizacao.get(l).getTipo().equals("Datetime")){
								
								SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
								try {
									dataRow.put(colunasParaVisualizacao.get(l).getNome(), format.parse(((TextView) aux.getChildAt(l)).getText().toString()));
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}else{
								//boolean
								//esse é passado como string mesmo porque na edição do dado ele é vindo de um spinner
								dataRow.put(colunasParaVisualizacao.get(l).getNome(), ((TextView) aux.getChildAt(l)).getText().toString());
								
							}

						}else{ //blob
							
							String recoveryKey = aux.getId() + "" + l; //para pegar da mesma forma que pega a URI no botão, chave feita por numLinha e numColuna
							dataRow.put(colunasParaVisualizacao.get(l).getNome(), auxDadosBlob.get(Integer.parseInt(recoveryKey)));
							
						}
					}
					
					//passa a hashmap de dados e a lista de colunas de visualização para a EditDataActivity
					i.putExtra("ListaColunas", colunasParaVisualizacao);
					i.putExtra("Linha", dataRow);
					
					
					//também passa essas coisas para não dar erro na volta
					i.putExtra("DBName", DBName);
					i.putExtra("TableName", nameTabela);
					i.putExtra("tablesList", namesTables);
					i.putExtra("ListaTabelas", tabelas);
					i.putExtra("listaAlteracao", listaAlteracao);
					
					//starta a activity
					startActivity(i);
				}
			});
		}



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
		linhasDeDados = new ArrayList<TableRow>();

		//preenchendo as colunas da visualização
		Intent intent = getIntent();
		colunas = (ArrayList<Coluna>) intent.getExtras().get("columnsList");

		//colunas especificas da tabela clicada
		colunasParaVisualizacao = (ArrayList<Coluna>) database.getColunasByTabela(nameTabela);

		tabelas = new ArrayList<Tabela>();
		for (int i = 0; i < namesTables.size(); i++) {
			tabelas.add(new Tabela(namesTables.get(i), DBName));
		}

		//TEM QUE VERIFICAR SE AS TABELAS E COLUNAS QUE ESTAO SENDO PASSADAS AQUI NÃO SAO AS DE ALTERAÇÃO!
		ArrayList<Tabela> tabelasSemAlt = new ArrayList<Tabela>();
		for (int i = 0; i < tabelas.size(); i++) {
			Tabela aux = new Tabela(tabelas.get(i).getNome(), tabelas.get(i).getNomeBanco());
			tabelasSemAlt.add(aux);
		}

		ArrayList<Coluna> colunasSemAlt = new ArrayList<Coluna>();
		for (int i = 0; i < colunas.size(); i++) {
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
			colunasSemAlt.add(aux);
		}

		for (int i = 0; i < listaAlteracao.size(); i++) {
			if(listaAlteracao.get(i).getTipoAlteracao().equals("addTabela")){
				for (int j = 0; j < tabelasSemAlt.size(); j++) {
					if(tabelasSemAlt.get(j).getNome().equals(listaAlteracao.get(i).getCreateTabela().getNome())){
						tabelasSemAlt.remove(j);
					}
				}
			}

			if(database.getFlagCriado(DBName)){ //se o banco ainda n foi criado, quer dizer que as colunas que estão em alteração são colunas que estão sendo executadas pela primeira vez, e não devem sair da lista de colunas
				if(listaAlteracao.get(i).getTipoAlteracao().equals("addColuna")){
					for (int j = 0; j < colunasSemAlt.size(); j++) {
						if(colunasSemAlt.get(j).getNome().equals(listaAlteracao.get(i).getCreateColuna().getNome())){
							colunasSemAlt.remove(j);
						}
					}
				}
			}
		}
		//================================================================================================

		//para criar o DB, tem que usar todas as tabelas e colunas (que não são da alteração)
		dbHelperUsuario = new DBHelperUsuario(getBaseContext(), DBName, tabelasSemAlt, colunasSemAlt);

		//depois que cria o banco de dados padrao, verifica se tem alterações

		//ve se a lista de alteracoes ta vazia
		//só faz alteraçao caso o banco ja tenha sido criado
		if(database.getFlagCriado(DBName)){
			if(listaAlteracao != null){
				if(listaAlteracao.size() > 0){
					//tem alteração, então atualiza
					dbHelperUsuario.onUpdateSchema(listaAlteracao);

					//pegar "tabelas" e "colunas" novas para os atributos correspondentes, alem de mudar namestables
					tabelas = (ArrayList<Tabela>) database.getAllNomesTabelas();
					namesTables = new ArrayList<String>();
					for (int i = 0; i < tabelas.size(); i++) {
						namesTables.add(tabelas.get(i).getNome());
					}
					colunasParaVisualizacao = (ArrayList<Coluna>) database.getColunasByTabela(nameTabela);

				}
			}
		}

		database.setFlagCriado(1, DBName);
		auxDadosBlob = new HashMap<Integer, String>();


	}

	private void play(Context context, Uri uri) {

		try {
			MediaPlayer mp = new MediaPlayer();
			mp.setDataSource(context, uri);    
			mp.prepare();
			mp.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
