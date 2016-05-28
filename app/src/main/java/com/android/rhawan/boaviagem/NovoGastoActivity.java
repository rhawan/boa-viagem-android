package com.android.rhawan.boaviagem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Calendar;

public class NovoGastoActivity extends AppCompatActivity {

    private int ano, mes, dia;
    private Button dataGasto;
    private Spinner categoria;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_gasto);
        dataGasto = (Button) findViewById(R.id.buttonData);
        categoria = (Spinner) findViewById(R.id.spinnerCategoria);

        Calendar calendar = Calendar.getInstance();
        this.ano = calendar.get(Calendar.YEAR);
        this.mes = calendar.get(Calendar.MONTH);
        this.dia = calendar.get(Calendar.DAY_OF_MONTH);
        dataGasto.setText(String.format("%d / %d / %d", dia, (mes+1), ano));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categoria_gasto, android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(adapter);
    }

    public void selecionarData(View v) {
        new DatePickerDialog(this, new DatePickerDateSetListener(), ano, mes, dia).show();
    }

    public void salvarGasto(View v) {

    }

    private class DatePickerDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataGasto.setText(String.format("%d / %d / %d", dia, (mes+1), ano));
        }
    }



}
