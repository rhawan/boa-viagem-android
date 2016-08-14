package com.android.rhawan.boaviagem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.rhawan.boaviagem.domain.Gasto;
import com.android.rhawan.boaviagem.domain.Viagem;
import com.android.rhawan.boaviagem.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rhawan on 09/08/2016.
 */
public class GastoDAO {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    public GastoDAO(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    private SQLiteDatabase getDb() {
        if(this.db == null) {
            db = databaseHelper.getWritableDatabase();
        }
        return db;
    }

    public List<Gasto> listarGastos(Long idViagem) {
        String selection = DatabaseHelper.GastoConst.VIAGEM_ID + " = ?";
        String[] selectionArgs = new String[]{ idViagem.toString() };
        Cursor cursor = getDb().query(DatabaseHelper.GastoConst.TABELA,
                DatabaseHelper.GastoConst.COLUNAS,
                selection, selectionArgs,
                null, null, null);
        List<Gasto> gastos = new ArrayList<>();
        while(cursor.moveToNext()) {
            Gasto gasto = criarGasto(cursor);
            gastos.add(gasto);
        }
        cursor.close();
        return gastos;
    }

    public Gasto consultarGastoPorId(Long id) {
        String selection = DatabaseHelper.GastoConst._ID + " = ?";
        String[] selectionArgs = new String[]{ id.toString() };
        Cursor cursor = getDb().query(DatabaseHelper.GastoConst.TABELA,
                DatabaseHelper.GastoConst.COLUNAS,
                selection, selectionArgs,
                null, null, null);
        Gasto gasto = null;
        if(cursor.moveToFirst()) {
            gasto = criarGasto(cursor);
        }
        cursor.close();
        return gasto;
    }

    public long inserirGasto(Gasto gasto) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.GastoConst.DATA, gasto.getData().getTime());
        contentValues.put(DatabaseHelper.GastoConst.CATEGORIA, gasto.getCategoria());
        contentValues.put(DatabaseHelper.GastoConst.DESCRICAO, gasto.getDescricao());
        contentValues.put(DatabaseHelper.GastoConst.VALOR, gasto.getValor());
        contentValues.put(DatabaseHelper.GastoConst.LOCAL, gasto.getLocal());
        contentValues.put(DatabaseHelper.GastoConst.VIAGEM_ID, gasto.getViagemId());
        return getDb().insert(DatabaseHelper.GastoConst.TABELA, null, contentValues);
    }

    public long alterarGasto(Gasto gasto) {
        String whereClause = DatabaseHelper.GastoConst._ID + " = ?";
        String[] whereArgs = new String[]{ gasto.getId().toString() };
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.GastoConst.DATA, gasto.getData().getTime());
        contentValues.put(DatabaseHelper.GastoConst.CATEGORIA, gasto.getCategoria());
        contentValues.put(DatabaseHelper.GastoConst.DESCRICAO, gasto.getDescricao());
        contentValues.put(DatabaseHelper.GastoConst.VALOR, gasto.getValor());
        contentValues.put(DatabaseHelper.GastoConst.LOCAL, gasto.getLocal());
        contentValues.put(DatabaseHelper.GastoConst.VIAGEM_ID, gasto.getViagemId());
        return getDb().update(DatabaseHelper.GastoConst.TABELA, contentValues,
                whereClause, whereArgs);
    }

    public boolean excluirGasto(Gasto gasto) {
        String whereClause = DatabaseHelper.GastoConst._ID + " = ?";
        String[] whereArgs = new String[]{ gasto.getId().toString() };
        int quantidadeExcluida = getDb().delete(DatabaseHelper.GastoConst.TABELA,
                whereClause, whereArgs);
        return quantidadeExcluida > 0;
    }

    private Gasto criarGasto(Cursor cursor) {
        return new Gasto(
                cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GastoConst._ID)),
                new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GastoConst.DATA))),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.GastoConst.CATEGORIA)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.GastoConst.DESCRICAO)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.GastoConst.VALOR)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.GastoConst.LOCAL)),
                cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GastoConst.VIAGEM_ID))
        );
    }
}
