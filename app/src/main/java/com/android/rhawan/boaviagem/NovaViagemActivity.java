package com.android.rhawan.boaviagem;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.rhawan.boaviagem.dao.BoaViagemDAO;
import com.android.rhawan.boaviagem.domain.Constantes;
import com.android.rhawan.boaviagem.domain.Viagem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NovaViagemActivity extends AppCompatActivity {

    private Button dataChegada, dataSaida;
    private EditText destino, quantidadePessoas, orcamento;
    private RadioGroup radioGroup;

    private BoaViagemDAO boaViagemDAO;

    private int ano, mes, dia;
    private String idViagem;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_viagem);

        Calendar calendar = Calendar.getInstance();
        this.ano = calendar.get(Calendar.YEAR);
        this.mes = calendar.get(Calendar.MONTH);
        this.dia = calendar.get(Calendar.DAY_OF_MONTH);

        dataChegada = (Button) findViewById(R.id.buttonDataChegada);
        dataSaida = (Button) findViewById(R.id.buttonDataSaida);
        destino = (EditText) findViewById(R.id.editTextDestino) ;
        quantidadePessoas = (EditText) findViewById(R.id.editTextQuantidadePessoas);
        orcamento = (EditText) findViewById(R.id.editTextOrcamento);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupTipoViagem);

        dataChegada.setText(String.format("%d / %d / %d", dia, (mes+1), ano));
        dataSaida.setText(String.format("%d / %d / %d", dia, (mes+1), ano));

        boaViagemDAO = new BoaViagemDAO(this);

        this.idViagem = getIntent().getStringExtra(Constantes.VIAGEM_ID);
        if(idViagem != null) {
            this.prepararEdicao();
        }
    }

    @Override
    protected void onDestroy() {
        boaViagemDAO.close();
        super.onDestroy();
    }

    public void selecionarData(View v) {
        new DatePickerDialog(this, new DatePickerDateSetListener(v), ano, mes, dia).show();
    }

    public void salvarViagem(View v) {
        Viagem viagem = new Viagem();
        viagem.setDestino(destino.getText().toString());
        //viagem.setDataChegada(SDF.parse(dataChegada.getText().toString()));
        //viagem.setDataSaida();
        viagem.setOrcamento(Double.valueOf(orcamento.getText().toString()));
        viagem.setQuantidadePessoas(Integer.parseInt(quantidadePessoas.getText().toString()));

        int tipoViagem = radioGroup.getCheckedRadioButtonId();
        if(tipoViagem == R.id.radioButtonLazer) {
            viagem.setTipoViagem(Constantes.VIAGEM_LAZER);
        } else {
            viagem.setTipoViagem(Constantes.VIAGEM_NEGOCIOS);
        }

        long resultado;
        if(idViagem == null) {
            resultado = boaViagemDAO.inserirViagem(viagem);
        } else {
            resultado = boaViagemDAO.alterarViagem(viagem);
        }

        if(resultado != -1) {
            Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
        }
    }

    private void prepararEdicao() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tipo_viagem, destino, data_chegada, " +
                "data_saida, quantidade_pessoas, orcamento " +
                "FROM viagem WHERE _id = ?", new String[]{ this.idViagem });
        cursor.moveToFirst();

        if(cursor.getInt(0) == Constantes.VIAGEM_LAZER) {
            radioGroup.check(R.id.radioButtonLazer);
        } else {
            radioGroup.check(R.id.radioButtonNegocios);
        }

        destino.setText(cursor.getString(1));
        Date dataChegada = new Date(cursor.getLong(2));
        Date dataSaida = new Date(cursor.getLong(3));
        this.dataChegada.setText(SDF.format(dataChegada));
        this.dataSaida.setText(SDF.format(dataSaida));
        quantidadePessoas.setText(cursor.getString(4));
        orcamento.setText(cursor.getString(5));
        cursor.close();
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
