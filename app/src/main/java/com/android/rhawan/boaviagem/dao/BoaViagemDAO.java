package com.android.rhawan.boaviagem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.android.rhawan.boaviagem.R;
import com.android.rhawan.boaviagem.domain.Viagem;
import com.android.rhawan.boaviagem.util.DatabaseHelper;

/**
 * Created by Rhawan on 13/07/2016.
 */
public class BoaViagemDAO {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    public BoaViagemDAO(Context context) {
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
}
