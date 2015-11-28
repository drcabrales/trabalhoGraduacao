package br.com.ufpe;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import br.com.ufpe.objects.Alteracao;
import br.com.ufpe.objects.Coluna;
import br.com.ufpe.objects.Tabela;

public class EditDataActivity extends Activity {
	private static int RESULT_LOAD_IMG = 1;
	//String imgDecodableString;
	Intent intentVideoPlayer;
	//String pathFromMusic;
	MediaPlayer mp;

	private String DBName;
	private String tableName;
	private ArrayList<String> namesTables;
	private ArrayList<Coluna> colunas;
	private ArrayList<Tabela> tabelas;
	private LinearLayout layout;

	//salva todos os edit texts do layout
	private ArrayList<EditText> allEds;

	//salva todos os spinners do layout
	private ArrayList<Spinner> allSpns;

	private DBHelperUsuario dbUsuario;
	private ArrayList<Object> allData;
	private Coluna colunablob;
	private DatePicker datepicker;

	private String uriImagem;
	private String uriVideo;
	private String uriMusica;
	private Map<String, String> allUris;

	private Map<Integer, String> auxNomesColunas;
	private int ordemBotaoSelecionarBlob;
	private int ordemEscolhida;

	private String tipoBlobEscolhido;

	private ArrayList<Alteracao> listaAlteracao;
	private HashMap<String, Object> dataRow;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_data);

		inicarComponentes();

		//esse for vai ver de qual tipo as colunas são, para colocar o tipo de inserção apropriado
		//text, varchar: edittext
		//integer: edittext input number
		//double e float: edittext input number com mascara?
		//blob: botão para escolher qual tipo de blob é e de onde ele deve pegar o dado (arquivo do sistema)
		//boolean: spinner com true e false
		for (int j = 0; j < colunas.size(); j++) {
			if(colunas.get(j).getTipo().equals("Text") || colunas.get(j).getTipo().equals("Varchar")){
				TextView nomeColuna = new TextView(this);
				nomeColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				nomeColuna.setPadding(0, 0, 0, 10);
				nomeColuna.setText(colunas.get(j).getNome());
				nomeColuna.setTextColor(Color.WHITE);
				layout.addView(nomeColuna);

				EditText campo = new EditText(this);
				campo.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				campo.setText((CharSequence) dataRow.get(colunas.get(j).getNome()));
				campo.setTextColor(Color.WHITE);
				allEds.add(campo);
				layout.addView(campo);
				
				//se for PK tira da visualização
				if(colunas.get(j).isPK()){
					nomeColuna.setVisibility(View.GONE);
					campo.setVisibility(View.GONE);
				}

			}else if(colunas.get(j).getTipo().equals("Integer")){
				TextView nomeColuna = new TextView(this);
				nomeColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				nomeColuna.setPadding(0, 0, 0, 10);
				nomeColuna.setText(colunas.get(j).getNome());
				nomeColuna.setTextColor(Color.WHITE);
				layout.addView(nomeColuna);

				EditText campo = new EditText(this);
				campo.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				campo.setInputType(InputType.TYPE_CLASS_NUMBER);
				campo.setText(dataRow.get(colunas.get(j).getNome()) + "");
				campo.setTextColor(Color.WHITE);
				allEds.add(campo);
				layout.addView(campo);
				
				//se for PK tira da visualização
				if(colunas.get(j).isPK()){
					nomeColuna.setVisibility(View.GONE);
					campo.setVisibility(View.GONE);
				}

			}else if(colunas.get(j).getTipo().equals("Double") || colunas.get(j).getTipo().equals("Float")){
				TextView nomeColuna = new TextView(this);
				nomeColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				nomeColuna.setPadding(0, 0, 0, 10);
				nomeColuna.setText(colunas.get(j).getNome());
				nomeColuna.setTextColor(Color.WHITE);
				layout.addView(nomeColuna);

				EditText campo = new EditText(this);
				campo.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				campo.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				campo.setText(dataRow.get(colunas.get(j).getNome())  + "");
				campo.setTextColor(Color.WHITE);
				allEds.add(campo);
				layout.addView(campo);
				
				//se for PK tira da visualização
				if(colunas.get(j).isPK()){
					nomeColuna.setVisibility(View.GONE);
					campo.setVisibility(View.GONE);
				}

			}else if(colunas.get(j).getTipo().equals("Boolean")){
				TextView nomeColuna = new TextView(this);
				nomeColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				nomeColuna.setPadding(0, 0, 0, 10);
				nomeColuna.setText(colunas.get(j).getNome());
				nomeColuna.setTextColor(Color.WHITE);
				layout.addView(nomeColuna);

				Spinner campo = new Spinner(this);
				campo.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				ArrayList<String> listaBool = new ArrayList<String>();
				listaBool.add("True");
				listaBool.add("False");
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaBool);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				campo.setAdapter(adapter);

				if(((String) dataRow.get(colunas.get(j).getNome())).equals("True")){
					campo.setSelection(0);
				}else{
					campo.setSelection(1);
				}

				allSpns.add(campo);
				layout.addView(campo);
				
				//se for PK tira da visualização
				if(colunas.get(j).isPK()){
					nomeColuna.setVisibility(View.GONE);
					campo.setVisibility(View.GONE);
				}

			}else if(colunas.get(j).getTipo().equals("BLOB")){
				TextView nomeColuna = new TextView(this);
				nomeColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				nomeColuna.setPadding(0, 0, 0, 10);
				nomeColuna.setText(colunas.get(j).getNome());
				nomeColuna.setTextColor(Color.WHITE);
				layout.addView(nomeColuna);

				colunablob = colunas.get(j);
				if(colunas.get(j).getTipoBlob().equals("Image")){
					final Button btnImagem = new Button(this);
					btnImagem.setText("Select an image");
					btnImagem.setId(ordemBotaoSelecionarBlob);
					
					btnImagem.setTextColor(Color.WHITE);
					btnImagem.setTextSize(12);
					LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 80);
					params.setMargins(0, 0, 0, 10);
					btnImagem.setLayoutParams(params);
					Drawable d = getResources().getDrawable(R.drawable.buttonshape);
					btnImagem.setBackground(d);
					
					auxNomesColunas.put(ordemBotaoSelecionarBlob, colunas.get(j).getNome());

					final Button btnVerImagem = new Button(this);
					btnVerImagem.setText("See the image");
					btnVerImagem.setTextColor(Color.WHITE);
					btnVerImagem.setTextSize(12);
					btnVerImagem.setLayoutParams(params);
					btnVerImagem.setBackground(d);

					layout.addView(btnImagem);
					layout.addView(btnVerImagem);

					final int index = j;
					
					uriImagem = dataRow.get(colunas.get(index).getNome())  + "";
					allUris.put(colunas.get(j).getNome(),uriImagem);
					
					btnImagem.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// Create intent to Open Image applications like Gallery, Google Photos
							tipoBlobEscolhido = "Image";
							ordemEscolhida = btnImagem.getId();
							Intent galleryIntent = new Intent(Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							// Start the Intent
							startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
						}
					});

					btnVerImagem.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_VIEW);

							if(uriImagem != null){
								Bitmap inImage = BitmapFactory.decodeFile(uriImagem);
								ByteArrayOutputStream bytes = new ByteArrayOutputStream();
								inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
								String path = Images.Media.insertImage(getBaseContext().getContentResolver(), inImage, "Title", null);

								intent.setDataAndType(Uri.parse(path), "image/*");
								startActivity(intent);
							}else{
								Toast.makeText(getBaseContext(), "You haven't picked file", Toast.LENGTH_SHORT).show();
							}

						}
					});
				}else if(colunas.get(j).getTipoBlob().equals("Movie")){
					final Button btnVideo = new Button(this);
					btnVideo.setText("Select a movie");
					btnVideo.setId(ordemBotaoSelecionarBlob);
					
					btnVideo.setTextColor(Color.WHITE);
					btnVideo.setTextSize(12);
					LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 80);
					params.setMargins(0, 0, 0, 10);
					btnVideo.setLayoutParams(params);
					Drawable d = getResources().getDrawable(R.drawable.buttonshape);
					btnVideo.setBackground(d);
					
					auxNomesColunas.put(ordemBotaoSelecionarBlob, colunas.get(j).getNome());

					final Button btnVerVideo = new Button(this);
					btnVerVideo.setText("Watch the movie");
					btnVerVideo.setTextColor(Color.WHITE);
					btnVerVideo.setTextSize(12);
					btnVerVideo.setLayoutParams(params);
					btnVerVideo.setBackground(d);

					layout.addView(btnVideo);
					layout.addView(btnVerVideo);

					final int index = j;
					
					intentVideoPlayer = new Intent(Intent.ACTION_VIEW, 
							Uri.parse((String) dataRow.get(colunas.get(index).getNome())));
					intentVideoPlayer.setType("video/*");
					intentVideoPlayer.setData(Uri.parse((String) dataRow.get(colunas.get(index).getNome())));
					
					allUris.put(colunas.get(j).getNome(), (Uri.parse((String) dataRow.get(colunas.get(index).getNome()))).toString());
					
					btnVideo.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// Create intent to Open Image applications like Gallery, Google Photos
							tipoBlobEscolhido = "Movie";
							ordemEscolhida = btnVideo.getId();
							Intent videoIntent = new Intent(Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							videoIntent.setType("video/*");
							// Start the Intent
							startActivityForResult(videoIntent, 1);
						}
					});

					btnVerVideo.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(intentVideoPlayer != null){
								startActivity(intentVideoPlayer);
							}else{
								Toast.makeText(getBaseContext(), "You haven't picked file", Toast.LENGTH_SHORT).show();
							}

						}
					});
				}else{
					final Button btnMusica = new Button(this);
					btnMusica.setText("Select a music");
					btnMusica.setId(ordemBotaoSelecionarBlob);
					
					btnMusica.setTextColor(Color.WHITE);
					btnMusica.setTextSize(12);
					LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 80);
					params.setMargins(0, 0, 0, 10);
					btnMusica.setLayoutParams(params);
					Drawable d = getResources().getDrawable(R.drawable.buttonshape);
					btnMusica.setBackground(d);
					
					auxNomesColunas.put(ordemBotaoSelecionarBlob, colunas.get(j).getNome());

					final Button btnTocarMusica = new Button(this);
					btnTocarMusica.setText("Play music");
					btnTocarMusica.setTextColor(Color.WHITE);
					btnTocarMusica.setTextSize(12);
					btnTocarMusica.setLayoutParams(params);
					btnTocarMusica.setBackground(d);

					final Button btnPausarMusica = new Button(this);
					btnPausarMusica.setText("Pause music");
					btnPausarMusica.setTextColor(Color.WHITE);
					btnPausarMusica.setTextSize(12);
					btnPausarMusica.setLayoutParams(params);
					btnPausarMusica.setBackground(d);

					layout.addView(btnMusica);
					layout.addView(btnTocarMusica);
					layout.addView(btnPausarMusica);
					
					final int index = j;
					
					uriMusica = dataRow.get(colunas.get(index).getNome())  + "";
					allUris.put(colunas.get(j).getNome(),uriMusica);

					btnMusica.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							tipoBlobEscolhido = "Music";
							ordemEscolhida = btnMusica.getId();
							Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(intent, 10);
						}
					});

					btnTocarMusica.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if(uriMusica != null){
								play(getBaseContext(), Uri.parse(uriMusica)); 
							}else{
								Toast.makeText(getBaseContext(), "You haven't picked file", Toast.LENGTH_SHORT).show();
							}

						}
					});

					btnPausarMusica.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if(mp!= null){
								pause();
							}else{
								Toast.makeText(getBaseContext(), "You haven't picked file", Toast.LENGTH_SHORT).show();
							}
						}
					});

				}

				ordemBotaoSelecionarBlob++;
			}else{
				//datetime
				TextView nomeColuna = new TextView(this);
				nomeColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				nomeColuna.setPadding(0, 0, 0, 10);
				nomeColuna.setText(colunas.get(j).getNome());
				nomeColuna.setTextColor(Color.WHITE);
				layout.addView(nomeColuna);

				datepicker = new DatePicker(this);
				Date dataToUpdate = (Date) dataRow.get(colunas.get(j).getNome());
				Calendar calendarToUpdate = Calendar.getInstance();
				calendarToUpdate.setTime(dataToUpdate);
				int year = calendarToUpdate.get(Calendar.YEAR);
				int month = calendarToUpdate.get(Calendar.MONTH);
				int day = calendarToUpdate.get(Calendar.DAY_OF_MONTH);
				datepicker.init(year, month, day, null);
				layout.addView(datepicker);
			}
		}

		Button atualizarDado = new Button(this);
		atualizarDado.setText("Update data");
		
		atualizarDado.setTextColor(Color.WHITE);
		atualizarDado.setTextSize(12);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 80);
		params.setMargins(0, 0, 0, 10);
		atualizarDado.setLayoutParams(params);
		Drawable d = getResources().getDrawable(R.drawable.buttonshape);
		atualizarDado.setBackground(d);
		
		layout.addView(atualizarDado);

		atualizarDado.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//coletar todos os dados pro array de objetos na ordem correta das colunas
				int contadorEditText = 0;
				int contadorSpinner = 0;
				for (int i = 0; i < colunas.size(); i++) {
					Coluna aux = colunas.get(i);

					//pega edittext
					if(aux.getTipo().equals("Text") || aux.getTipo().equals("Varchar") || aux.getTipo().equals("Integer") ||
							aux.getTipo().equals("Double") || aux.getTipo().equals("Float")){
						allData.add(allEds.get(contadorEditText).getText().toString());
						contadorEditText++;
					}else if(aux.getTipo().equals("Boolean")){
						if(allSpns.get(contadorSpinner).getSelectedItem().toString().equals("True")){
							allData.add(true);
						}else{
							allData.add(false);
						}

						contadorSpinner++;
					}else if(aux.getTipo().equals("Datetime")){
						int day = datepicker.getDayOfMonth();
						int month = datepicker.getMonth();
						int year = datepicker.getYear();

						SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
						String finalDate = day + "/" + (month+1) + "/" + year;

						try {
							allData.add(format.parse(finalDate));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						//verificando o tipo de coluna para saber onde buscar seu dado corretamente
					}else{
						allData.add(allUris.get(colunas.get(i).getNome()));
					}
				}
				//chamar o database usuario e atualizar
				dbUsuario.update(colunas, allData, tableName);

				//voltar pra tela de visualização
				Intent i = new Intent(getBaseContext(), DataViewActivity.class);
				i.putExtra("DBName", DBName);
				i.putExtra("nameTable", tableName);
				i.putExtra("tablesList", namesTables);
				i.putExtra("columnsList", colunas);
				i.putExtra("listaAlteracao", new ArrayList<Alteracao>());
				startActivity(i);
			}
		});
		
		Button back = new Button(this);
		back.setText("Back");
		
		back.setTextColor(Color.WHITE);
		back.setTextSize(12);
		LayoutParams paramsF = new LayoutParams(LayoutParams.FILL_PARENT, 80);
		back.setLayoutParams(paramsF);
		Drawable dF = getResources().getDrawable(R.drawable.buttonshape);
		back.setBackground(dF);
		
		layout.addView(back);
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//voltar pra tela de visualização
				Intent i = new Intent(getBaseContext(), DataViewActivity.class);
				i.putExtra("DBName", DBName);
				i.putExtra("nameTable", tableName);
				i.putExtra("tablesList", namesTables);
				i.putExtra("columnsList", colunas);
				i.putExtra("listaAlteracao", new ArrayList<Alteracao>());
				startActivity(i);
				
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			// When an Image is picked
			if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
					&& null != data && tipoBlobEscolhido.equals("Image")) {
				// Get the Image from data

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				// Get the cursor
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				// Move to first row
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				uriImagem = cursor.getString(columnIndex);

				if(allUris.get(auxNomesColunas.get(ordemEscolhida)) == null){
					allUris.put(auxNomesColunas.get(ordemEscolhida),uriImagem);
				}else{
					allUris.remove(auxNomesColunas.get(ordemEscolhida));
					allUris.put(auxNomesColunas.get(ordemEscolhida),uriImagem);
				}
				

				cursor.close();

			} else if(requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
					&& null != data && tipoBlobEscolhido.equals("Movie")){

				Uri dataVideo = data.getData();
				uriVideo = dataVideo.toString();

				if(allUris.get(auxNomesColunas.get(ordemEscolhida)) == null){
					allUris.put(auxNomesColunas.get(ordemEscolhida),uriVideo);
				}else{
					allUris.remove(auxNomesColunas.get(ordemEscolhida));
					allUris.put(auxNomesColunas.get(ordemEscolhida),uriVideo);
				}
				

				intentVideoPlayer = new Intent(Intent.ACTION_VIEW, 
						dataVideo);
				intentVideoPlayer.setType("video/*");
				intentVideoPlayer.setData(data.getData());

			}else if(resultCode == RESULT_OK && requestCode == 10
					&& null != data && tipoBlobEscolhido.equals("Music")){

				Uri uriSound=data.getData();
				uriMusica = getRealPathFromURI(getBaseContext(), uriSound);
				
				if(allUris.get(auxNomesColunas.get(ordemEscolhida)) == null){
					allUris.put(auxNomesColunas.get(ordemEscolhida),uriMusica);
				}else{
					allUris.remove(auxNomesColunas.get(ordemEscolhida));
					allUris.put(auxNomesColunas.get(ordemEscolhida),uriMusica);
				}

			}else {
				Toast.makeText(this, "You haven't picked file",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
			.show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_data, menu);
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

	public void inicarComponentes(){
		Intent i = getIntent();
		DBName = i.getExtras().getString("DBName");
		tableName = i.getExtras().getString("TableName");
		namesTables = (ArrayList<String>) i.getExtras().get("tablesList");
		colunas = (ArrayList<Coluna>) i.getExtras().get("ListaColunas");
		tabelas = (ArrayList<Tabela>) i.getExtras().get("ListaTabelas");

		dataRow = (HashMap<String, Object>) i.getExtras().get("Linha");

		//passa esse construtor porque aqui o banco é chamado pela primeira vez e tem q ser criado
		dbUsuario = new DBHelperUsuario(this, DBName, tabelas, colunas);


		layout = (LinearLayout) findViewById(R.id.layoutprincipal);

		allEds = new ArrayList<EditText>();
		allSpns = new ArrayList<Spinner>();
		allUris = new HashMap<String, String>();

		allData = new ArrayList<Object>();

		auxNomesColunas = new HashMap<Integer, String>();
		ordemBotaoSelecionarBlob = 0;
		ordemEscolhida = 0;

		listaAlteracao = (ArrayList<Alteracao>) i.getExtras().get("listaAlteracao");
	}

	private void play(Context context, Uri uri) {

		try {
			mp = new MediaPlayer();
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

	private void pause(){
		mp.pause();
	}

	private String getRealPathFromURI(Context context, Uri contentUri) {
		String[] projection = { MediaStore.Audio.Media.DATA };
		CursorLoader loader = new CursorLoader(context, contentUri, projection, null, null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public byte[] getBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
		}
		return byteBuffer.toByteArray();
	}
}

