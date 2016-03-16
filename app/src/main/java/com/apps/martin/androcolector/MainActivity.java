package com.apps.martin.androcolector;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ConectarFragment();
                break;
            case 1:
                fragment = new CargarRutaFragment();
                break;
            case 2:
                fragment = new RecolectarDatosFragment();
                break;
            case 3:
                fragment = new VerMapaFragment();
                break;
            case 4:
                salir();
                break;
        }

        if (fragment != null) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        String[] section_titles = getResources().getStringArray(R.array.section_titles);
        if (number >= 1) {
            mTitle = section_titles[number - 1];
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
            startActivity(new Intent(MainActivity.this, ConfiguracionActivity.class));


        return super.onOptionsItemSelected(item);
    }

    public void mostrarRegistro(View view) {
        startActivity(new Intent(MainActivity.this, RegistrarseActivity.class));
    }

    public void conectar(View view) {
        EditText editText2 = (EditText) findViewById(R.id.editText2);
        EditText editText3 = (EditText) findViewById(R.id.editText3);
        String nombre = editText2.getText().toString();
        String clave = editText3.getText().toString();
        new EnBackground().execute(nombre, clave); //Loguearse SOAP o WEB restful (Comentario de prueba)
    }

    public void salir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Salir de la aplicación");
        builder.setMessage("Está seguro que desea salir de la aplicación?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences prefs = getSharedPreferences("Configuracion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("usuario");
                editor.commit();
                System.exit(0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    class EnBackground extends AsyncTask<String, String, String> {

        private String nombre;
        private String contrasenia;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Autenticando");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            nombre = params[0];
            contrasenia = params[1];
            ArrayList<NameValuePair> postValores = new ArrayList<NameValuePair>();
            postValores.add(new BasicNameValuePair("nombre",nombre));
            postValores.add(new BasicNameValuePair("contrasenia",contrasenia));
            String respuesta = null;
            try {
                SharedPreferences prefs = getSharedPreferences("Configuracion", Context.MODE_PRIVATE);
                String ip_server = prefs.getString("ip_server", "");
                respuesta = HttpCustomClient.executeHttpPost("http://"+ip_server+"/cronos/login.php",postValores);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.hide();
            if (result.equals("1"))
            {
                SharedPreferences prefs = getSharedPreferences("Configuracion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("usuario", nombre);
                editor.commit();
                RecolectarDatosFragment fragment = new RecolectarDatosFragment();
                Bundle args = new Bundle();
                args.putInt(ARG_SECTION_NUMBER, 2);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }
            else
            {
                String mensaje;
                if (result.equals(""))
                    mensaje= "Ocurrió un error en la aplicación!";
                else
                    mensaje = "El nombre de usuario o la contraseña es incorrecto/a";
                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
