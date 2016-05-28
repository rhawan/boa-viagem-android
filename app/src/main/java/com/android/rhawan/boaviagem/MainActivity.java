package com.android.rhawan.boaviagem;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsuario;
    private EditText editTextSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.editTextUsuario = (EditText) findViewById(R.id.editTextUsuario);
        this.editTextSenha = (EditText) findViewById(R.id.editTextSenha);
    }

    public void processarLogin(View v) {
        String usuarioInformado = editTextUsuario.getText().toString();
        String senhaInformada = editTextSenha.getText().toString();
        if("leitor".equals(usuarioInformado) && "123".equals(senhaInformada)) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        } else {
            String mensagemErro = getString(R.string.erro_autenticacao);
            Toast.makeText(this, mensagemErro, Toast.LENGTH_SHORT).show();
        }

    }

}
