package com.android.rhawan.boaviagem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViagemListActivity extends ListActivity implements AdapterView.OnItemClickListener,
        DialogInterface.OnClickListener {

    private List<Map<String, Object>> viagens;

    private AlertDialog alertDialog;

    private AlertDialog dialogConfirmacao;

    private int viagemSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        switch (item) {
            case 0:
                startActivity(new Intent(this, NovaViagemActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, NovoGastoActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, GastoListActivity.class));
                break;
            case 3:
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
        viagens = new ArrayList<Map<String, Object>>();

        Map<String, Object> item = new HashMap<>();
        item.put("imagem", R.drawable.viagem_negocios);
        item.put("destino", "São Paulo");
        item.put("data", "02/02/2012 a 04/02/2012");
        item.put("gastoTotal", "Gasto total R$ 314,98");
        item.put("barraProgresso", new Double[]{500.0, 450.0, 314.98});
        viagens.add(item);

        item = new HashMap<>();
        item.put("imagem", R.drawable.viagem_lazer);
        item.put("destino", "Maceió");
        item.put("data", "14/05/2012 a 22/05/2012");
        item.put("gastoTotal", "Gasto total R$ 25834,67");
        item.put("barraProgresso", new Double[]{30000.00, 450.0, 25834.67});
        viagens.add(item);

        return viagens;
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
