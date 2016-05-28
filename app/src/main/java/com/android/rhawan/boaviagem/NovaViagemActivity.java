package com.android.rhawan.boaviagem;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

public class NovaViagemActivity extends AppCompatActivity {

    private Button dataChegada, dataSaida;
    private int ano, mes, dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_viagem);
        dataChegada = (Button) findViewById(R.id.buttonDataChegada);
        dataSaida = (Button) findViewById(R.id.buttonDataSaida);

        Calendar calendar = Calendar.getInstance();
        this.ano = calendar.get(Calendar.YEAR);
        this.mes = calendar.get(Calendar.MONTH);
        this.dia = calendar.get(Calendar.DAY_OF_MONTH);
        dataChegada.setText(String.format("%d / %d / %d", dia, (mes+1), ano));
        dataSaida.setText(String.format("%d / %d / %d", dia, (mes+1), ano));
    }

    public void selecionarData(View v) {
        new DatePickerDialog(this, new DatePickerDateSetListener(v), ano, mes, dia).show();
    }

    public void salvarViagem(View v) {

    }

    private class DatePickerDateSetListener implements DatePickerDialog.OnDateSetListener {

        private View v;

        private DatePickerDateSetListener(View v) {
            this.v = v;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            if(v.getId() == R.id.buttonDataChegada){
                dataChegada.setText(String.format("%d / %d / %d", dia, (mes+1), ano));
            } else {
                dataSaida.setText(String.format("%d / %d / %d", dia, (mes+1), ano));
            }
        }
    }
}
