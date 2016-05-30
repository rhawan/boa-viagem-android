package com.android.rhawan.boaviagem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String MANTER_CONECTADO = "manter_conectado";

    private EditText editTextUsuario;
    private EditText editTextSenha;
    private CheckBox checkBoxManterConectado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.editTextUsuario = (EditText) findViewById(R.id.editTextUsuario);
        this.editTextSenha = (EditText) findViewById(R.id.editTextSenha);
        this.checkBoxManterConectado = (CheckBox) findViewById(R.id.checkBoxManterConectado);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean conectado = preferences.getBoolean(MANTER_CONECTADO, false);
        if(conectado) {
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    public void processarLogin(View v) {
        String usuarioInformado = editTextUsuario.getText().toString();
        String senhaInformada = editTextSenha.getText().toString();
        if("leitor".equals(usuarioInformado) && "123".equals(senhaInformada)) {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(MANTER_CONECTADO, checkBoxManterConectado.isChecked());
            editor.apply();

            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        } else {
            String mensagemErro = getString(R.string.erro_autenticacao);
            Toast.makeText(this, mensagemErro, Toast.LENGTH_SHORT).show();
        }

    }

}
