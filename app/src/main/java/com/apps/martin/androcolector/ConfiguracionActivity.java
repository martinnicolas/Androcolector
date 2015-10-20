package com.apps.martin.androcolector;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfiguracionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        SharedPreferences prefs = getSharedPreferences("Configuracion", Context.MODE_PRIVATE);
        String ip_server = prefs.getString("ip_server", "");
        if (!ip_server.isEmpty()){
            EditText ip_cuarteto1 = (EditText) findViewById(R.id.editText7);
            EditText ip_cuarteto2 = (EditText) findViewById(R.id.editText8);
            EditText ip_cuarteto3 = (EditText) findViewById(R.id.editText9);
            EditText ip_cuarteto4 = (EditText) findViewById(R.id.editText10);

            String [] split = ip_server.split("\\.");

            ip_cuarteto1.setText(split[0]);
            ip_cuarteto2.setText(split[1]);
            ip_cuarteto3.setText(split[2]);
            ip_cuarteto4.setText(split[3]);
        }
    }

    public void guardarConfiguracion(View view) {
        EditText ip_cuarteto1 = (EditText) findViewById(R.id.editText7);
        EditText ip_cuarteto2 = (EditText) findViewById(R.id.editText8);
        EditText ip_cuarteto3 = (EditText) findViewById(R.id.editText9);
        EditText ip_cuarteto4 = (EditText) findViewById(R.id.editText10);

        String ip_server = ip_cuarteto1.getText().toString()+"."+ip_cuarteto2.getText().toString()
                +"."+ip_cuarteto3.getText().toString()+"."+ip_cuarteto4.getText().toString();

        String mensaje;
        if (validarDatos(ip_server)) {
            SharedPreferences prefs = getSharedPreferences("Configuracion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("ip_server", ip_server);
            editor.commit();
            mensaje = "Datos guardados!.";
        }
        else
            mensaje = "La Direccion IP ingresada no es válida.\nVerifique los datos ingresados!.";
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private boolean validarDatos(String ip_server){
        String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ip_server);
        return matcher.find();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuracion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_configuracion) {
            Toast.makeText(this, "Muy pronto, más opciones de configuración", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
