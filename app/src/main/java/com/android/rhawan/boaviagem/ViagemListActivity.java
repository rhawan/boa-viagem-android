package com.android.rhawan.boaviagem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.rhawan.boaviagem.domain.Constantes;
import com.android.rhawan.boaviagem.util.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViagemListActivity extends ListActivity implements AdapterView.OnItemClickListener,
        DialogInterface.OnClickListener {

    private List<Map<String, Object>> viagens;

    private AlertDialog alertDialog;

    private AlertDialog dialogConfirmacao;

    private int viagemSelecionada;

    private Double valorLimite;

    private DatabaseHelper databaseHelper;

    private static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.databaseHelper = new DatabaseHelper(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String valor = preferences.getString("valor_limite", "-1");
        this.valorLimite = Double.valueOf(valor);

        String[] de = {"imagem", "destino", "data", "gastoTotal", "barraProgresso"};
        int[] para = {R.id.imageViewTipoViagem, R.id.textViewDestino, R.id.textViewData,
            R.id.textViewGastoTotal, R.id.progressBarGastos};
        SimpleAdapter adapter = new SimpleAdapter(this, listarViagens(),
                R.layout.activity_viagem_list, de, para);
        adapter.setViewBinder(new ViagemViewBinder());
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        this.alertDialog = criarAlertDialog();
        this.dialogConfirmacao = criarDialogConfirmacao();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, Object> map = viagens.get(position);
        String destino = (String) map.get("destino");
        String mensagem = "Viagem selecionada: " + destino;
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
        this.viagemSelecionada = position;
        alertDialog.show();
        //startActivity(new Intent(this, GastoListActivity.class));
    }

    @Override
    public void onClick(DialogInterface dialog, int item) {
        Intent intent;
        String idViagem = (String) viagens.get(viagemSelecionada).get("id");
        switch (item) {
            case 0: //Editar Viagem
                intent = new Intent(this, NovaViagemActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, idViagem);
                startActivity(intent);
                break;
            case 1: //Novo Gasto
                startActivity(new Intent(this, NovoGastoActivity.class));
                break;
            case 2: //Gastos Realizados
                startActivity(new Intent(this, GastoListActivity.class));
                break;
            case 3: //Excluir Viagem
                dialogConfirmacao.show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                viagens.remove(viagemSelecionada);
                getListView().invalidateViews();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialogConfirmacao.dismiss();
                break;
        }

    }

    private AlertDialog criarAlertDialog() {
        final CharSequence[] items = {
                getString(R.string.menuEditarViagem),
                getString(R.string.menuNovoGasto),
                getString(R.string.menuGastosRealizados),
                getString(R.string.menuExcluirViagem)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.opcoes);
        builder.setItems(items, this);
        return builder.create();
    }

    private AlertDialog criarDialogConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.menuConfirmaExclusaoViagem);
        builder.setPositiveButton(getString(R.string.sim), this);
        builder.setNegativeButton(getString(R.string.nao), this);
        return builder.create();
    }

    private List<Map<String, Object>> listarViagens() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, tipo_viagem, destino, " +
                "data_chegada, data_saida, orcamento FROM viagem", null);
        cursor.moveToFirst();

        viagens = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < cursor.getCount(); i++) {
            Map<String, Object> item = new HashMap<>();

            String id = cursor.getString(0);
            int tipoViagem = cursor.getInt(1);
            String destino = cursor.getString(2);
            long dataChegada = cursor.getLong(3);
            long dataSaida = cursor.getLong(4);
            double orcamento = cursor.getDouble(5);

            item.put("id", id);
            if(tipoViagem == Constantes.VIAGEM_LAZER) {
                item.put("imagem", R.drawable.viagem_lazer);
            } else {
                item.put("imagem", R.drawable.viagem_negocios);
            }
            item.put("destino", destino);

            Date dataChegadaDate = new Date(dataChegada);
            Date dataSaidaDate = new Date(dataSaida);
            String periodo = String.format("%s a %s",
                    SDF.format(dataChegadaDate), SDF.format(dataSaidaDate));
            item.put("data", periodo);

            double totalGasto = 0;
            item.put("total", "Gasto total R$ " + totalGasto);

            double alerta = orcamento * valorLimite / 100;
            Double[] valores = new Double[] {orcamento, alerta, totalGasto};
            item.put("barraProgresso", valores);

            viagens.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        return viagens;
    }

    private double calcularTotalGasto(SQLiteDatabase db, String id) {
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM gasto" +
                " WHERE viagem_id = ?", new String[]{id});
        cursor.moveToFirst();
        double total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    private void excluirViagem(String id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String where[] = new String[]{ id };
        db.delete("gasto", "viagem_id = ?", where);
        db.delete("viagem", "_id = ?", where);
    }

    private class ViagemViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if(view.getId() == R.id.progressBarGastos) {
                Double valores[] = (Double[]) data;
                ProgressBar progressBar = (ProgressBar) view;
                progressBar.setMax(valores[0].intValue());
                progressBar.setSecondaryProgress(valores[1].intValue());
                progressBar.setProgress(valores[2].intValue());
                return true;
            }
            return false;
        }
    }

}
