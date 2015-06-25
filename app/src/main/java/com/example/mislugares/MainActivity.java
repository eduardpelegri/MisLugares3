package com.example.mislugares;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements LocationListener {
    //public AdaptadorCursorLugares adaptador;
    private MediaPlayer mp;
    private LocationManager manejador;
    private Location mejorLocaliz;
    private static final long DOS_MINUTOS = 2 * 60 * 1000;
    private VistaLugarFragment fragmentVista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this,"oncreate",Toast.LENGTH_LONG).show();
        mp = MediaPlayer.create(this, R.raw.audio);
        mp.start();
        setContentView(R.layout.activity_main);
        /*adaptador = new ArrayAdapter (this,
                android.R.layout.simple_expandable_list_item_1,
                Lugares.listaNombres());*/
        /*adaptador = new ArrayAdapter(this,
                R.layout.elemento_lista,
                R.id.nombre,
                Lugares.listaNombres());*/
        //adaptador =new AdaptadorLugares(this);
        Lugares.indicializaBD(this);
        /*adaptador = new SimpleCursorAdapter(this,
                R.layout.elemento_lista,
                Lugares.listado(),
                new String[] { "nombre", "direccion"},
                new int[] { R.id.nombre, R.id.direccion}, 0);*/
        //adaptador = new AdaptadorCursorLugares(this, Lugares.listado());
        /* SE PASA AL FRAGMENT
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adaptador);
        listView.setOnItemClickListener(this);*/

        fragmentVista = (VistaLugarFragment) getSupportFragmentManager()
                .findFragmentById(R.id.vista_lugar_fragment);
        if (fragmentVista!=null){
            fragmentVista.actualizarVistas(Lugares.primerId());
        }
        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            actualizaMejorLocaliz(manejador.getLastKnownLocation(
                    LocationManager.GPS_PROVIDER));
        }
        if(manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            actualizaMejorLocaliz(manejador.getLastKnownLocation(
                    LocationManager.NETWORK_PROVIDER));
        }
        /*Button bEdicionLugar = (Button) findViewById(R.id.Button01);
        bEdicionLugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(this,EdicionLugar.class);
                startActivity(intent);
            }
        });
        Button bAcercaDe = (Button) findViewById(R.id.Button03);
        bAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarAcercaDe(null);
            }
        });
        Button bSalir =(Button) findViewById(R.id.Button04);
        bSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        */
    }


    //En la variable mejorLocaliz almacenaremos la mejor localización. Que será actualizada según cumpla unas condiciones:
    //si no ha sido inicializada, o la nueva localización tiene una precisión aceptable y guardaremos la posición en el lugar posicionActual
    private void actualizaMejorLocaliz(Location localiz) {
        if(localiz != null) {
            if (mejorLocaliz == null
                    || localiz.getAccuracy() < 2 * mejorLocaliz.getAccuracy()
                    || localiz.getTime() - mejorLocaliz.getTime() > DOS_MINUTOS) {
                Log.d(Lugares.TAG, "Nueva mejor localización");
                mejorLocaliz = localiz;
                Lugares.posicionActual.setLatitud(localiz.getLatitude());
                Lugares.posicionActual.setLongitud(localiz.getLongitude());
            }
        }
    }

    public void lanzarAcercaDe (View view) {
    Intent intent = new Intent(this,AcercaDe.class);
    startActivity(intent);
}

public void lanzarPreferencias (View view) {
    Intent intent = new Intent (this,Preferencias.class);
    startActivity(intent);
}
    public void mostrarPreferencias(View view){
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String s = "notificaciones: "+ pref.getBoolean("notificaciones",true)
                +", distancia mínima: " + pref.getString("distancia","?");
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    /*public void lanzarVistaLugar(View view){
        Intent i = new Intent(this, VistaLugar.class);
        i.putExtra("id", (long)0);
        startActivity(i);
    }*/
    public void lanzarVistaLugar (View view){
        final EditText entrada =new EditText(this);
        entrada.setText("0");
        new AlertDialog.Builder(this)
                .setTitle("Selección del Lugar")
                .setMessage("indica su id:")
                .setView(entrada)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long id = Long.parseLong(entrada.getText().toString());
                        Intent i = new Intent(MainActivity.this,VistaLugar.class);
                        i.putExtra("id",id);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.acercaDe) {
            lanzarAcercaDe(null);
            return true;
        }
        if (id== R.id.action_settings) {
            lanzarPreferencias(null);
            mostrarPreferencias(null);
            return true;
        }
        if(id == R.id.menu_buscar) {
            //lanzarVistaLugar(null);
            buscarLugar(null);
            return true;
        }
        if (id==R.id.menu_mapa) {
            Intent i = new Intent(this, Mapa.class);
            startActivityForResult(i,0);
        }
        if (id==R.id.accion_nuevo) {
            long id2 = Lugares.nuevo();
            Intent i= new Intent(this, EdicionLugar.class);
            i.putExtra("nuevo", true);
            i.putExtra("id", id2);
            startActivityForResult(i,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buscarLugar(View view) {
        final EditText entrada =new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Buscar el Lugar")
                .setMessage("indica parte de su nombre:")
                .setView(entrada)
                .setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nombre = entrada.getText().toString();
                        long id = Lugares.buscarParteNombre(nombre);
                        if (id != -1){
                            muestraLugar(id);
                        }else {
                            Toast.makeText(getApplicationContext(),"Lugar no encontrado",Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /*SE PASA AL FRAGMENT
    @Override
    public void onItemClick(AdapterView parent, View vista, int posicion, long id) {
        Intent i = new Intent(MainActivity.this,VistaLugar.class);
        i.putExtra("id",id);
        startActivityForResult(i,0);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*ListView listView = (ListView) findViewById(R.id.listView);
        AdaptadorCursorLugares adaptador =(AdaptadorCursorLugares) listView.getAdapter();
        adaptador.changeCursor(Lugares.listado());*/
        actualizaLista();
    }

    @Override
    protected void onStart(){
        super.onStart();
        Toast.makeText(this,"onStart",Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onResume(){
        super.onResume();
        Toast.makeText(this,"onResume",Toast.LENGTH_LONG).show();
        mp.start();
        activarProveedores();
    }

    private void activarProveedores() {
        if(manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            manejador.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    20 * 1000, 5, this);
        }

        if(manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            manejador.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    10 * 1000, 10, this);
        }
    }

    @Override
    protected void onPause (){
        Toast.makeText(this,"onPause",Toast.LENGTH_LONG).show();
        super.onPause();
        mp.pause();
        manejador.removeUpdates(this);


    }
    @Override
    protected void onStop (){
        super.onStop();
        Toast.makeText(this,"onStop",Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        Toast.makeText(this,"onRestart",Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Toast.makeText(this,"onDestroy",Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onSaveInstanceState(Bundle estadoGuardado){
        super.onSaveInstanceState(estadoGuardado);
        if (mp!=null){
            int pos = mp.getCurrentPosition();
            estadoGuardado.putInt("posicion",pos);
        }
    }
    @Override
    protected void onRestoreInstanceState (Bundle estadoGuardado){
        super.onRestoreInstanceState(estadoGuardado);
        if (mp!=null && estadoGuardado!=null) {
            int pos = estadoGuardado.getInt("posicion");
            mp.seekTo(pos);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(Lugares.TAG, "Nueva localización: " + location);
        actualizaMejorLocaliz(location);
    }

    @Override
    public void onStatusChanged(String proveedor, int estado, Bundle extras) {
        Log.d(Lugares.TAG, "Cambia estado: "+ proveedor);
        activarProveedores();
    }

    @Override
    public void onProviderEnabled(String proveedor) {
        Log.d(Lugares.TAG, "Se deshabilita: "+ proveedor);
        activarProveedores();
    }

    @Override
    public void onProviderDisabled(String proveedor) {
        Log.d(Lugares.TAG, "Se habilita: "+ proveedor);
        activarProveedores();
    }

    public void muestraLugar(long id) {
        if (fragmentVista != null) {
            fragmentVista.actualizarVistas(id);
        } else {
            Intent intent = new Intent(this, VistaLugar.class);
            intent.putExtra("id", id);
            startActivityForResult(intent, 0);
        }
    }

    public void actualizaLista() {
        ListView listView = (ListView) findViewById(R.id.listView);
        AdaptadorCursorLugares adaptador = (AdaptadorCursorLugares)listView.getAdapter();
        adaptador.changeCursor(Lugares.listado());
    }
}
