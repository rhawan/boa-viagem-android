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
 * Created by Rhawan on 13/07/2016.
 */
public class ViagemDAO {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    public ViagemDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    private SQLiteDatabase getDb() {
        if(db == null) {
            db = databaseHelper.getWritableDatabase();
        }
        return db;
    }

    public void close() {
        databaseHelper.close();
    }

    public List<Viagem> listarViagens() {
        Cursor cursor = getDb().query(DatabaseHelper.ViagemConst.TABELA,
                DatabaseHelper.ViagemConst.COLUNAS, null, null, null, null, null, null);
        List<Viagem> viagens = new ArrayList<>();
        while(cursor.moveToNext()) {
            Viagem viagem = criarViagem(cursor);
            viagens.add(viagem);
        }
        cursor.close();
        return viagens;
    }

    public Viagem consultarViagemPorId(Long id) {
        Cursor cursor = getDb().query(DatabaseHelper.ViagemConst.TABELA,
                DatabaseHelper.ViagemConst.COLUNAS,
                DatabaseHelper.ViagemConst._ID + " = ?",
                new String[]{ id.toString() },
                null, null, null);
        if(cursor.moveToNext()) {
            Viagem viagem = criarViagem(cursor);
            cursor.close();
            return viagem;
        }
        return null;
    }

    public double calcularTotalGasto(Viagem viagem) {
        Cursor cursor = getDb().rawQuery(
                "SELECT SUM(" + DatabaseHelper.GastoConst.VALOR + ") FROM " +
                        DatabaseHelper.GastoConst.TABELA + " WHERE " +
                        DatabaseHelper.GastoConst.VIAGEM_ID + " = ?",
                        new String[]{ viagem.getId().toString() });
        cursor.moveToFirst();
        double total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public long inserirViagem(Viagem viagem) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ViagemConst.DESTINO, viagem.getDestino());
        values.put(DatabaseHelper.ViagemConst.DATA_CHEGADA, viagem.getDataChegada().getTime());
        values.put(DatabaseHelper.ViagemConst.DATA_SAIDA, viagem.getDataSaida().getTime());
        values.put(DatabaseHelper.ViagemConst.ORCAMENTO, viagem.getOrcamento());
        values.put(DatabaseHelper.ViagemConst.QUANTIDADE_PESSOAS, viagem.getQuantidadePessoas());
        values.put(DatabaseHelper.ViagemConst.TIPO_VIAGEM, viagem.getTipoViagem());

        return getDb().insert(DatabaseHelper.ViagemConst.TABELA, null, values);
    }

    public long alterarViagem(Viagem viagem) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ViagemConst.DESTINO, viagem.getDestino());
        values.put(DatabaseHelper.ViagemConst.DATA_CHEGADA, viagem.getDataChegada().getTime());
        values.put(DatabaseHelper.ViagemConst.DATA_SAIDA, viagem.getDataSaida().getTime());
        values.put(DatabaseHelper.ViagemConst.ORCAMENTO, viagem.getOrcamento());
        values.put(DatabaseHelper.ViagemConst.QUANTIDADE_PESSOAS, viagem.getQuantidadePessoas());
        values.put(DatabaseHelper.ViagemConst.TIPO_VIAGEM, viagem.getTipoViagem());

        return getDb().update(DatabaseHelper.ViagemConst.TABELA, values,
                DatabaseHelper.ViagemConst._ID + " = ?", new String[]{ viagem.getId().toString()});
    }

    public boolean excluirViagem(Long id) {
        String whereClause = DatabaseHelper.ViagemConst._ID + " = ?";
        String[] whereArgs = new String[]{ id.toString() };
        int quantidadeExcluida = getDb().delete(DatabaseHelper.ViagemConst.TABELA,
                whereClause, whereArgs);
        return quantidadeExcluida > 0;
    }

    public boolean excluirGastosViagem(Long idViagem) {
        String whereClause = DatabaseHelper.GastoConst.VIAGEM_ID + " = ?";
        String[] whereArgs = new String[]{ idViagem.toString() };
        int quantidadeExcluida = getDb().delete(DatabaseHelper.GastoConst.TABELA,
                whereClause, whereArgs);
        return quantidadeExcluida > 0;
    }


    private Viagem criarViagem(Cursor cursor) {
        return new Viagem(
            cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ViagemConst._ID)),
            cursor.getString(cursor.getColumnIndex(DatabaseHelper.ViagemConst.DESTINO)),
            cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ViagemConst.TIPO_VIAGEM)),
            new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ViagemConst.DATA_CHEGADA))),
            new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ViagemConst.DATA_SAIDA))),
            cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.ViagemConst.ORCAMENTO)),
            cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ViagemConst.QUANTIDADE_PESSOAS))
        );
    }

}
