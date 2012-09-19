package com.novaeslucas.buscarPreco;

import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CadastrarProdutoActivity extends Activity{

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro);
        
        Button telaPesqProduto = (Button) findViewById(R.id.tela_pesquisa_produto);
		
        telaPesqProduto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent trocatela = new
				Intent(CadastrarProdutoActivity.this, BuscarPrecoActivity.class);
				CadastrarProdutoActivity.this.startActivity(trocatela);
				CadastrarProdutoActivity.this.finish();
			}
		});
        
        Button inserir = (Button) findViewById(R.id.button_insert);
		
        inserir.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ContentValues values = new ContentValues();
				EditText et1 = (EditText) findViewById(R.id.editText1);
				EditText et2 = (EditText) findViewById(R.id.editText2);
				EditText et3 = (EditText) findViewById(R.id.editText3);
				EditText et4 = (EditText) findViewById(R.id.editText4);
				
				values.put("nome", et1.getText().toString());
				values.put("categoria", et2.getText().toString());
				values.put("cod", et3.getText().toString());
				values.put("data_captura", new Date().toString());
				values.put("preco", et4.getText().toString());
				long retorno = getConnection().insert("produtos", null, values);
				
				Context context = getApplicationContext();
				String msg = "";
				
				if(retorno == -1){
					msg = "ERROR";
				}else{
					msg = "Produto Adicionado";
				}
				
				Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
				toast.show();
				getConnection().close();
		    }
		});
	}
	
	 public SQLiteDatabase getConnection(){
	    SQLiteHelper helper = new SQLiteHelper(getApplicationContext());
		return helper.getDatabase();
	 }
}
