package com.novaeslucas.buscarPreco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	public SQLiteHelper(Context context) {
		super(context, "produtos.sqlite", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE produtos(" + "id INTEGER PRIMARY KEY AUTOINCREMENT, nome VARCHAR(100),"
				+ "categoria VARCHAR(100), cod VARCHAR(100), data_captura DATE, preco VARCHAR(45));");
	}

	public SQLiteDatabase getDatabase() {
		return this.getWritableDatabase();
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}
}