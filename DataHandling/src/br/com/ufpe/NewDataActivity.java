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
import br.com.ufpe.objects.Coluna;
import br.com.ufpe.objects.Tabela;

public class NewDataActivity extends Activity {
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

	//guardar todos os blobs, identificados pela coluna
	//private Map<String, Object> allblobs = new HashMap<String, Object>();

	private CheckBox chkImagem;
	private CheckBox chkVideo;
	private CheckBox chkMusica;

	private DBHelperUsuario dbUsuario;
	private ArrayList<Object> allData;
	private Coluna colunablob;
	private DatePicker datepicker;
	
	private String uriImagem;
	private String uriVideo;
	private String uriMusica;
	private Map<String, String> allUris;

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
				allEds.add(campo);
				layout.addView(campo);

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
				allEds.add(campo);
				layout.addView(campo);

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
				allEds.add(campo);
				layout.addView(campo);

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
				allSpns.add(campo);
				layout.addView(campo);

			}else if(colunas.get(j).getTipo().equals("BLOB")){
				TextView nomeColuna = new TextView(this);
				nomeColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				nomeColuna.setPadding(0, 0, 0, 10);
				nomeColuna.setText(colunas.get(j).getNome());
				nomeColuna.setTextColor(Color.WHITE);
				layout.addView(nomeColuna);

				//3 checkbox, cada um com um tipo de mídia para a pessoa escolher. Só marca um por vez
				//aqui vai entrar um botão que abrira a tela da galeria de imagens, de vídeos ou de músicas
				//quando ele escolher uma, a mídia deve ser salva de alguma forma

				chkImagem = new CheckBox(this);
				chkImagem.setText("Image");
				chkImagem.setTextColor(Color.WHITE);
				chkImagem.setPadding(0, 0, 10, 0);
				chkImagem.setChecked(true);

				chkVideo = new CheckBox(this);
				chkVideo.setText("Movie");
				chkVideo.setTextColor(Color.WHITE);
				chkVideo.setPadding(0, 0, 10, 0);
				chkVideo.setChecked(false);

				chkMusica = new CheckBox(this);
				chkMusica.setText("Music");
				chkMusica.setTextColor(Color.WHITE);
				chkMusica.setPadding(0, 0, 10, 0);
				chkMusica.setChecked(false);

				LinearLayout layoutCheckbox = new LinearLayout(this);
				layoutCheckbox.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				layoutCheckbox.setOrientation(LinearLayout.HORIZONTAL);
				layoutCheckbox.addView(chkImagem);
				layoutCheckbox.addView(chkVideo);
				layoutCheckbox.addView(chkMusica);

				layout.addView(layoutCheckbox);

				final Button btnImagem = new Button(this);
				btnImagem.setText("Select an image");
				btnImagem.setVisibility(View.VISIBLE);

				final Button btnVerImagem = new Button(this);
				btnVerImagem.setText("see the image");
				btnVerImagem.setVisibility(View.VISIBLE);

				final Button btnVideo = new Button(this);
				btnVideo.setText("Select a movie");
				btnVideo.setVisibility(View.GONE);

				final Button btnVerVideo = new Button(this);
				btnVerVideo.setText("See the movie");
				btnVerVideo.setVisibility(View.GONE);

				final Button btnMusica = new Button(this);
				btnMusica.setText("Select a music");
				btnMusica.setVisibility(View.GONE);

				final Button btnTocarMusica = new Button(this);
				btnTocarMusica.setText("start music");
				btnTocarMusica.setVisibility(View.GONE);

				final Button btnPausarMusica = new Button(this);
				btnPausarMusica.setText("pause music");
				btnPausarMusica.setVisibility(View.GONE);

				layout.addView(btnImagem);
				layout.addView(btnVerImagem);
				layout.addView(btnVideo);
				layout.addView(btnVerVideo);
				layout.addView(btnMusica);
				layout.addView(btnTocarMusica);
				layout.addView(btnPausarMusica);


				//clique dos checkboxes para mudar a visibilidade das coisas relacionadas a ele
				chkImagem.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						chkImagem.setChecked(isChecked);
						chkVideo.setChecked(false);
						chkMusica.setChecked(false);

						btnImagem.setVisibility(View.VISIBLE);
						btnVerImagem.setVisibility(View.VISIBLE);
						btnVideo.setVisibility(View.GONE);
						btnVerVideo.setVisibility(View.GONE);
						btnMusica.setVisibility(View.GONE);
						btnTocarMusica.setVisibility(View.GONE);
						btnPausarMusica.setVisibility(View.GONE);
					}
				});

				chkVideo.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						chkVideo.setChecked(isChecked);
						chkImagem.setChecked(false);
						chkMusica.setChecked(false);

						btnImagem.setVisibility(View.GONE);
						btnVerImagem.setVisibility(View.GONE);
						btnVideo.setVisibility(View.VISIBLE);
						btnVerVideo.setVisibility(View.VISIBLE);
						btnMusica.setVisibility(View.GONE);
						btnTocarMusica.setVisibility(View.GONE);
						btnPausarMusica.setVisibility(View.GONE);
					}
				});

				chkMusica.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						chkMusica.setChecked(isChecked);
						chkVideo.setChecked(false);
						chkImagem.setChecked(false);

						btnImagem.setVisibility(View.GONE);
						btnVerImagem.setVisibility(View.GONE);
						btnVideo.setVisibility(View.GONE);
						btnVerVideo.setVisibility(View.GONE);
						btnMusica.setVisibility(View.VISIBLE);
						btnTocarMusica.setVisibility(View.VISIBLE);
						btnPausarMusica.setVisibility(View.VISIBLE);
					}
				});


				//clique dos botões de escolha de arquivo e armazenamento deles
				colunablob = colunas.get(j);
				btnImagem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Create intent to Open Image applications like Gallery, Google Photos
						Intent galleryIntent = new Intent(Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						// Start the Intent
						startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
					}
				});

				btnVideo.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Create intent to Open Image applications like Gallery, Google Photos
						Intent videoIntent = new Intent(Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						videoIntent.setType("video/*");
						// Start the Intent
						startActivityForResult(videoIntent, 1);
					}
				});

				btnMusica.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(intent, 10);
					}
				});

				//clique do botao de visualizacao de multimidia
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

			}else{
				//datetime
				TextView nomeColuna = new TextView(this);
				nomeColuna.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				nomeColuna.setPadding(0, 0, 0, 10);
				nomeColuna.setText(colunas.get(j).getNome());
				nomeColuna.setTextColor(Color.WHITE);
				layout.addView(nomeColuna);

				datepicker = new DatePicker(this);
				final Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				datepicker.init(year, month, day, null);
				layout.addView(datepicker);
			}
		}

		Button inserirDado = new Button(this);
		inserirDado.setText("Insert data");
		layout.addView(inserirDado);

		inserirDado.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// FAZER A LÓGICA DE INSERÇÃO DE DADOS NO BANCO DE FATO
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
					}else{
						//allData.add(allblobs.get(colunas.get(i).getNome()));
						allData.add(allUris.get(colunas.get(i).getNome()));
					}

					//verificando o tipo de coluna para saber onde buscar seu dado corretamente
				}
				//chamar o database usuario e inserir
				dbUsuario.insert(colunas, allData, tableName);

				//voltar pra tela de visualização
				Intent i = new Intent(getBaseContext(), DataViewActivity.class);
				i.putExtra("DBName", DBName);
				i.putExtra("nameTable", tableName);
				i.putExtra("tablesList", namesTables);
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
					&& null != data && chkImagem.isChecked()) {
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

				allUris.put(colunablob.getNome(),uriImagem);
				
				
				/*Bitmap inImage = BitmapFactory.decodeFile(imgDecodableString);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
				byte[] bytesBlob = bytes.toByteArray();
				allblobs.put(colunablob.getNome(),bytesBlob);*/

				cursor.close();

			} else if(requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
					&& null != data && chkVideo.isChecked()){

				Uri dataVideo = data.getData();
				uriVideo = dataVideo.toString();
				
				allUris.put(colunablob.getNome(),uriVideo);

				intentVideoPlayer = new Intent(Intent.ACTION_VIEW, 
						dataVideo);
				intentVideoPlayer.setType("video/*");
				intentVideoPlayer.setData(data.getData());

				/*InputStream iStream;
				byte[] bytedados;
				try {
					iStream = getContentResolver().openInputStream(intentVideoPlayer.getData());
					bytedados = getBytes(iStream);
					allblobs.put(colunablob.getNome(),bytedados);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

			}else if(resultCode == RESULT_OK && requestCode == 10
					&& null != data && chkMusica.isChecked()){

				Uri uriSound=data.getData();
				uriMusica = getRealPathFromURI(getBaseContext(), uriSound);
				
				allUris.put(colunablob.getNome(),uriMusica);

				/*InputStream iStream;
				byte[] bytedados;
				try {
					iStream = getContentResolver().openInputStream(Uri.parse(pathFromMusic));
					bytedados = getBytes(iStream);
					allblobs.put(colunablob.getNome(), bytedados);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

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

		//passa esse construtor porque aqui o banco é chamado pela primeira vez e tem q ser criado
		dbUsuario = new DBHelperUsuario(this, DBName, tabelas, colunas);


		layout = (LinearLayout) findViewById(R.id.layoutprincipal);

		allEds = new ArrayList<EditText>();
		allSpns = new ArrayList<Spinner>();
		allUris = new HashMap<String, String>();

		allData = new ArrayList<Object>();
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
