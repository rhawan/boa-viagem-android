package com.android.rhawan.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(R.id.menuSair == item.getItemId()) {
            finish();
        }
        return true;
    }

    public void selecionarOpcao(View v) {
        switch (v.getId()) {
            case R.id.textViewNovaViagem:
                startActivity(new Intent(this, NovaViagemActivity.class));
                break;
            case R.id.textViewNovoGasto:
                startActivity(new Intent(this, NovoGastoActivity.class));
                break;
            case R.id.textViewMinhasViagens:
                startActivity(new Intent(this, ViagemListActivity.class));
                break;
            case R.id.textViewConfiguracoes:
                startActivity(new Intent(this, ConfiguracoesActivity.class));
                break;
        }
    }
}
