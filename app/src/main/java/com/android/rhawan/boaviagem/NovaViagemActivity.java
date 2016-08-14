package com.android.rhawan.boaviagem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.Toast;

import com.android.rhawan.boaviagem.dao.ViagemDAO;
import com.android.rhawan.boaviagem.domain.Constantes;
import com.android.rhawan.boaviagem.domain.Viagem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NovaViagemActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private Button dataChegadaButton, dataSaidaButton;
    private EditText destino, quantidadePessoas, orcamento;
    private RadioGroup radioGroup;

    private ViagemDAO viagemDAO;

    private int ano, mes, dia;
    private Long idViagem;
    private Date dataChegada, dataSaida;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_viagem);

        Calendar calendar = Calendar.getInstance();
        this.ano = calendar.get(Calendar.YEAR);
        this.mes = calendar.get(Calendar.MONTH);
        this.dia = calendar.get(Calendar.DAY_OF_MONTH);

        dataChegadaButton = (Button) findViewById(R.id.buttonDataChegada);
        dataSaidaButton = (Button) findViewById(R.id.buttonDataSaida);
        destino = (EditText) findViewById(R.id.editTextDestino);
        quantidadePessoas = (EditText) findViewById(R.id.editTextQuantidadePessoas);
        orcamento = (EditText) findViewById(R.id.editTextOrcamento);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupTipoViagem);

        viagemDAO = new ViagemDAO(this);

        this.idViagem = getIntent().getLongExtra(Constantes.VIAGEM_ID, -1);
        if (idViagem != -1) {
            this.prepararEdicao();
        }
    }

    @Override
    protected void onDestroy() {
        viagemDAO.close();
        super.onDestroy();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case R.id.buttonDataChegada:
                return new DatePickerDialog(this, dataChegadaListener, ano, mes, dia);
            case R.id.buttonDataSaida:
                return new DatePickerDialog(this, dataSaidaListener, ano, mes, dia);
        }
        return null;
    }

    public void selecionarData(View view) {
        showDialog(view.getId());
    }

    public void salvarViagem(View v) {
        if (this.validarCamposObrigatorios()) {
            return;
        }
        Viagem viagem = new Viagem();
        viagem.setId(idViagem);
        viagem.setDestino(destino.getText().toString());
        viagem.setDataChegada(dataChegada);
        viagem.setDataSaida(dataSaida);
        viagem.setOrcamento(Double.valueOf(orcamento.getText().toString()));
        viagem.setQuantidadePessoas(Integer.parseInt(quantidadePessoas.getText().toString()));

        int tipoViagem = radioGroup.getCheckedRadioButtonId();
        if (tipoViagem == R.id.radioButtonLazer) {
            viagem.setTipoViagem(Constantes.VIAGEM_LAZER);
        } else {
            viagem.setTipoViagem(Constantes.VIAGEM_NEGOCIOS);
        }

        long resultado;
        if (idViagem == -1) {
            resultado = viagemDAO.inserirViagem(viagem);
        } else {
            resultado = viagemDAO.alterarViagem(viagem);
        }

        if (resultado != -1) {
            Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCamposObrigatorios() {
        boolean isInvalid = false;
        if (this.destino.getText() != null && this.destino.getText().toString().isEmpty()) {
            this.destino.setError("Campo obrigatório!");
            isInvalid = true;
        }
        return isInvalid;
    }

    private void prepararEdicao() {
        Viagem viagem = viagemDAO.consultarViagemPorId(idViagem);

        if (viagem.getTipoViagem() == Constantes.VIAGEM_LAZER) {
            radioGroup.check(R.id.radioButtonLazer);
        } else {
            radioGroup.check(R.id.radioButtonNegocios);
        }

        destino.setText(viagem.getDestino());
        dataChegada = viagem.getDataChegada();
        dataSaida = viagem.getDataSaida();
        dataChegadaButton.setText(SDF.format(dataChegada));
        dataSaidaButton.setText(SDF.format(dataSaida));
        quantidadePessoas.setText(viagem.getQuantidadePessoas().toString());
        orcamento.setText(viagem.getOrcamento().toString());
    }

    private DatePickerDialog.OnDateSetListener dataChegadaListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {
            dataChegada = criarData(anoSelecionado, mesSelecionado, diaSelecionado);
            dataChegadaButton.setText(String.format("%d / %d / %d", diaSelecionado, (mesSelecionado + 1), anoSelecionado));
        }
    };

    private DatePickerDialog.OnDateSetListener dataSaidaListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {
            dataSaida = criarData(anoSelecionado, mesSelecionado, diaSelecionado);
            dataSaidaButton.setText(String.format("%d / %d / %d", diaSelecionado, (mesSelecionado + 1), anoSelecionado));
        }
    };

    private Date criarData(int anoSelecionado, int mesSelecionado, int diaSelecionado) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(anoSelecionado, mesSelecionado, diaSelecionado);
        return calendar.getTime();
    }

}
