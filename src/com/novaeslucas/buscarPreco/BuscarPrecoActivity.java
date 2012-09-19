package com.novaeslucas.buscarPreco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BuscarPrecoActivity extends Activity {
    
	private TextView textReturn;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button scan = (Button) findViewById(R.id.button1);
		
		scan.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	    	      intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
	    	      intent.putExtra("SCAN_WIDTH", 800);
	    	      intent.putExtra("SCAN_HEIGHT", 200);
	    	      startActivityForResult(intent, 0);
			}
		});
		
		textReturn = (TextView) this.findViewById(R.id.textReturn);
		
		Button tela_cadastrar = (Button) findViewById(R.id.tela_cadastrar);
		
		tela_cadastrar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent trocatela = new
				Intent(BuscarPrecoActivity.this, CadastrarProdutoActivity.class);
				BuscarPrecoActivity.this.startActivity(trocatela);
				BuscarPrecoActivity.this.finish();
			}
		});
		
		Button listarNaoSin = (Button) findViewById(R.id.listar_nao_sinc);
		
		listarNaoSin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Cursor cursor = getConnection().query("produtos", new String[]{"id", "nome", "categoria", "cod", "preco", "data_captura"}, null, null, null, null, null);
				
				StringBuilder sb = new StringBuilder();
				cursor.moveToFirst();
				while(!cursor.isAfterLast()){
					String s0 = cursor.getString(0);
					String s1 = cursor.getString(1);
					String s2 = cursor.getString(2);
					String s3 = cursor.getString(3);
					String s4 = cursor.getString(4);
					String s5 = cursor.getString(5);
					
					sb.append(s0);
					sb.append(" - ");
					sb.append(s1);
					sb.append(" - ");
					sb.append(s2);
					sb.append(" - ");
					sb.append(s3);
					sb.append(" - ");
					sb.append(s4);
					sb.append(" - ");
					sb.append(s5);
					sb.append(";\n");
					cursor.moveToNext();
				}
				
				AlertDialog.Builder dialogo = new AlertDialog.Builder(BuscarPrecoActivity.this);
				dialogo.setTitle("Persistido Interno");
				dialogo.setMessage(sb.toString());
				dialogo.setNeutralButton("OK", null);
				dialogo.show();
				
				getConnection().close();
			}
		});
		
		Button sinc = (Button) findViewById(R.id.sinc);
		
        sinc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Cursor cursor = getConnection().query("produtos", new String[]{"nome", "categoria", "cod", "data_captura", "preco"}, null, null, null, null, null);
				Connection conn = connect();
				
				cursor.moveToFirst();
				while(!cursor.isAfterLast()){
					String s1 = cursor.getString(0);
					String s2 = cursor.getString(1);
					String s3 = cursor.getString(2);
					String s4 = cursor.getString(3);
					String s5 = cursor.getString(4);
					
					try {
						PreparedStatement pstm = conn.prepareStatement("insert into produtos(nome, categoria, cod, data_captura, preco) values (?,?,?,?,?)");
						pstm.setString(1, s1);
						pstm.setString(2, s2);
						pstm.setString(3, s3);
						pstm.setString(4, s4);
						pstm.setString(5, s5);
						
						int retorno1 = pstm.executeUpdate();
						
						if(retorno1 > 0){
							Context context = getApplicationContext();
							Toast toast = Toast.makeText(context, "Produto Adicionado na base externa", Toast.LENGTH_LONG);
							toast.show();
						}
						
						pstm.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
					cursor.moveToNext();
				}
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				getConnection().delete("produtos", null, null);
				getConnection().close();
				
			}
        });
		
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
        	if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");                             
                String result = buscarProduto(contents);
                textReturn.setText(result);                              
            }
        }
    }
    
    public Connection connect(){
    	Connection connection = null;
        try {
			String driverName = "com.mysql.jdbc.Driver";                        
			Class.forName(driverName);
            String serverName = "mysql.novaeslucas.com:3306/novaeslucas01";
	        String url = "jdbc:mysql://" + serverName;
	        String username = "novaeslucas01";     
	        String password = "Dcv77234";
	        connection = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
	        System.out.println("O driver expecificado nao foi encontrado.");
	    } catch (SQLException e) {
            System.out.println("Nao foi possivel conectar ao Banco de Dados.");
        }
        return connection;
    }
    
    public String buscarProduto(String codigo){
    	Connection conn = connect();
    	AlertDialog.Builder dialogo = new AlertDialog.Builder(BuscarPrecoActivity.this);
    	StringBuilder resultado = new StringBuilder();

    	try {
			PreparedStatement pstm = conn.prepareStatement("select * from produtos where cod = '"+codigo+"'");
			ResultSet rs = pstm.executeQuery();
			
			boolean existe = false;
			
			while(rs.next()){
				resultado.append(rs.getString(1));
				resultado.append(" - ");
				resultado.append(rs.getString(2));
				resultado.append("/");
				resultado.append(rs.getString(3));
				resultado.append("/ R$");
				resultado.append(rs.getString(6));
				resultado.append(";\n");
				existe = true;
			}
				
			if(existe == false){
				dialogo.setTitle("Aviso");
				dialogo.setMessage("Nenhum produto encontrado.");
				dialogo.setNeutralButton("OK", null);
				dialogo.show();
				resultado.append("No product.");
			}

			pstm.close();
			conn.close();
			
			
    	} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	return resultado.toString();
    }
    
    public SQLiteDatabase getConnection(){
	    SQLiteHelper helper = new SQLiteHelper(getApplicationContext());
		return helper.getDatabase();
	 }
}