package com.android.rhawan.boaviagem;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.rhawan.boaviagem.dao.GastoDAO;
import com.android.rhawan.boaviagem.domain.Constantes;
import com.android.rhawan.boaviagem.domain.Gasto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GastoListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private List<Map<String, Object>> gastos;
    private String dataAnterior = "";
    private Long idViagem;

    private GastoDAO gastoDAO;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.gastoDAO = new GastoDAO(this);
        this.idViagem = getIntent().getLongExtra(Constantes.VIAGEM_ID, -1);

        String[] de = {"data", "descricao", "valor", "categoria"};
        int[] para = {R.id.textViewDataGastoList, R.id.textViewDescricao, R.id.textViewValor,
            R.id.linearLayoutCategoria};
        SimpleAdapter adapter = new SimpleAdapter(this, listarGastos(),
                R.layout.activity_gasto_list, de, para);
        adapter.setViewBinder(new GastoViewBinder());
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        registerForContextMenu(getListView());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, Object> map = gastos.get(position);
        String descricao = (String) map.get("descricao");
        String mensagem = "Gasto selecionado: " + descricao;
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gasto_list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menuExcluirGasto) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            gastos.remove(info.position);
            getListView().invalidateViews();
            dataAnterior = "";
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private List<Map<String, Object>> listarGastos() {
        gastos = new ArrayList<>();
        List<Gasto> gastosBD = gastoDAO.listarGastos(idViagem);
        for (Gasto gasto : gastosBD) {
            Map<String, Object> item = new HashMap<>();
            item.put("data", SDF.format(gasto.getData()));
            item.put("descricao", gasto.getDescricao());
            item.put("valor", gasto.getValor());
            item.put("categoria", R.color.categoria_alimentacao);
            //item.put("categoria", gasto.getCategoria());
            gastos.add(item);
        }
        return gastos;
    }

    private class GastoViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if(view.getId() == R.id.textViewDataGastoList) {
                if(!dataAnterior.equals(data)) {
                    TextView textView = (TextView) view;
                    textView.setText(textRepresentation);
                    dataAnterior = textRepresentation;
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
                return true;
            } else if(view.getId() == R.id.linearLayoutCategoria) {
                Integer id = (Integer) data;
                view.setBackgroundColor(getResources().getColor(id));
                return true;
            }
            return false;
        }
    }
}
