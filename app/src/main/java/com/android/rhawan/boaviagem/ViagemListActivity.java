package com.android.rhawan.boaviagem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.rhawan.boaviagem.dao.ViagemDAO;
import com.android.rhawan.boaviagem.domain.Constantes;
import com.android.rhawan.boaviagem.domain.Viagem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViagemListActivity extends ListActivity implements AdapterView.OnItemClickListener,
        DialogInterface.OnClickListener {

    private List<Map<String, Object>> viagens;

    private AlertDialog alertDialog;

    private AlertDialog dialogConfirmacao;

    private int idViagemSelecionada;

    private Double valorLimite;

    private ViagemDAO viagemDAO;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.viagemDAO = new ViagemDAO(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String valor = preferences.getString("valor_limite", "-1");
        this.valorLimite = Double.valueOf(valor);

        this.alertDialog = criarAlertDialog();
        this.dialogConfirmacao = criarDialogConfirmacao();

        String[] de = {"imagem", "destino", "data", "gastoTotal", "barraProgresso"};
        int[] para = {R.id.imageViewTipoViagem, R.id.textViewDestino, R.id.textViewData,
            R.id.textViewGastoTotal, R.id.progressBarGastos};
        SimpleAdapter adapter = new SimpleAdapter(this, listarViagens(),
                R.layout.activity_viagem_list, de, para);
        adapter.setViewBinder(new ViagemViewBinder());
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, Object> map = viagens.get(position);
        String destino = (String) map.get("destino");
        String mensagem = "Viagem selecionada: " + destino;
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
        this.idViagemSelecionada = position;
        alertDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int item) {
        Intent intent;
        Long idViagem = (Long) viagens.get(idViagemSelecionada).get("id");
        switch (item) {
            case 0: //Editar Viagem
                intent = new Intent(this, NovaViagemActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, idViagem);
                startActivity(intent);
                break;
            case 1: //Novo Gasto
                intent = new Intent(this, NovoGastoActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, idViagem);
                startActivity(intent);
                break;
            case 2: //Gastos Realizados
                intent = new Intent(this, GastoListActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, idViagem);
                startActivity(intent);
                break;
            case 3: //Excluir Viagem
                dialogConfirmacao.show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                viagens.remove(idViagemSelecionada);
                viagemDAO.excluirViagem(idViagem);
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
        List<Viagem> viagensBD = viagemDAO.listarViagens();
        for(Viagem viagem : viagensBD) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", viagem.getId());

            if(viagem.getTipoViagem() == Constantes.VIAGEM_LAZER) {
                item.put("imagem", R.drawable.viagem_lazer);
            } else {
                item.put("imagem", R.drawable.viagem_negocios);
            }
            item.put("destino", viagem.getDestino());

            String periodo = SDF.format(viagem.getDataChegada()) + " a "
                    + SDF.format(viagem.getDataSaida());
            item.put("data", periodo);

            double totalGasto = viagemDAO.calcularTotalGasto(viagem);
            item.put("total", "Gasto total R$ " + totalGasto);

            double alerta = viagem.getOrcamento() * valorLimite / 100;
            Double[] valores = new Double[]{ viagem.getOrcamento(), alerta, totalGasto } ;
            item.put("barraProgresso", valores);
            viagens.add(item);
        }
        return viagens;
    }

    private void excluirViagem(Long idViagem) {
        viagemDAO.excluirGastosViagem(idViagem);
        viagemDAO.excluirViagem(idViagem);
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
