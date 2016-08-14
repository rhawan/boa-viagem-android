package com.android.rhawan.boaviagem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.rhawan.boaviagem.dao.GastoDAO;
import com.android.rhawan.boaviagem.domain.Constantes;
import com.android.rhawan.boaviagem.domain.Gasto;

import java.util.Calendar;
import java.util.Date;

public class NovoGastoActivity extends AppCompatActivity {

    private Button dataGastoButton;
    private Spinner categoria;
    private EditText valorGasto;
    private EditText descricaoGasto;
    private EditText localGasto;

    private int ano, mes, dia;
    private Date dataGasto;
    private Long idViagem;

    private GastoDAO gastoDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_gasto);

        dataGastoButton = (Button) findViewById(R.id.buttonData);
        categoria = (Spinner) findViewById(R.id.spinnerCategoria);
        valorGasto = (EditText) findViewById(R.id.editTextValor);
        descricaoGasto = (EditText) findViewById(R.id.editTextDescricao);
        localGasto = (EditText) findViewById(R.id.editTextLocal);

        Calendar calendar = Calendar.getInstance();
        this.ano = calendar.get(Calendar.YEAR);
        this.mes = calendar.get(Calendar.MONTH);
        this.dia = calendar.get(Calendar.DAY_OF_MONTH);
        dataGastoButton.setText(String.format("%d / %d / %d", dia, (mes+1), ano));

        this.gastoDAO = new GastoDAO(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categoria_gasto, android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(adapter);

        this.idViagem = getIntent().getLongExtra(Constantes.VIAGEM_ID, -1);
    }

    public void selecionarData(View v) {
        new DatePickerDialog(this, new DatePickerDateSetListener(), ano, mes, dia).show();
    }

    public void salvarGasto(View v) {
        if(validarCamposObrigatorios()) {
            return;
        }
        Gasto gasto = new Gasto();
        gasto.setCategoria((String) this.categoria.getSelectedItem());
        gasto.setData(dataGasto);
        gasto.setDescricao(this.descricaoGasto.getText().toString());
        gasto.setValor(Double.valueOf(this.valorGasto.getText().toString()));
        gasto.setLocal(this.localGasto.getText().toString());
        gasto.setViagemId(idViagem);

        long resultado = gastoDAO.inserirGasto(gasto);
        if(resultado != -1) {
            Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCamposObrigatorios() {
        boolean isInvalid = false;
        if(this.categoria.getSelectedItem() == null) {
            ((TextView)this.categoria.getSelectedView()).setError(getString(R.string.error_campo_obrigatorio));
            isInvalid = true;
        }
        if(this.dataGasto == null) {
            this.dataGastoButton.setError(getString(R.string.error_campo_obrigatorio));
            isInvalid = true;
        }
        if(this.valorGasto.getText() != null && this.valorGasto.getText().toString().isEmpty()) {
            this.valorGasto.setError(getString(R.string.error_campo_obrigatorio));
            isInvalid = true;
        }
        return isInvalid;
    }

    private class DatePickerDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {
            ano = anoSelecionado;
            mes = mesSelecionado;
            dia = diaSelecionado;
            dataGastoButton.setText(String.format("%d / %d / %d", diaSelecionado, (mesSelecionado+1), anoSelecionado));
            dataGasto = criarData(anoSelecionado, mesSelecionado, anoSelecionado);
        }
    }

    private Date criarData(int anoSelecionado, int mesSelecionado, int diaSelecionado) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(anoSelecionado, mesSelecionado, diaSelecionado);
        return calendar.getTime();
    }

}
