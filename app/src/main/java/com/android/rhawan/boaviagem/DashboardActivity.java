package com.android.rhawan.boaviagem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void selecionarOpcao(View v) {
        switch (v.getId()) {
            case R.id.textViewNovaViagem:
                startActivity(new Intent(this, NovaViagemActivity.class));
                break;
            case R.id.textViewNovoGasto:
                startActivity(new Intent(this, NovoGastoActivity.class));
                break;
        }
    }
}
