package com.example.mislugares;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by USUARIO on 26/05/2015.
 */
public class Mapa extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener{
    private GoogleMap mapa;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);
        mapa = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa)).getMap();
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.setMyLocationEnabled(true);
        mapa.getUiSettings().setZoomControlsEnabled(true);
        mapa.getUiSettings().setCompassEnabled(true);
        boolean primero = true;
        Cursor c = Lugares.listado();
        while (c.moveToNext()){
            //GeoPunto p = new GeoPunto(c.getDouble(c.getColumnIndex("latitud")),c.getColumnIndex("longitud"));
            GeoPunto p = new GeoPunto(c.getDouble(3), c.getDouble(4));
            if(p!=null && p.getLatitud()!=0){
                if(primero){
                    mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(p.getLatitud(),p.getLongitud()),12));
                    primero=false;
                }
                BitmapDrawable iconoDrawable = (BitmapDrawable) getResources()
                        .getDrawable(TipoLugar.values()[c.getInt(5)].getRecurso());
                Bitmap iGrande = iconoDrawable.getBitmap();
                Bitmap icono = Bitmap.createScaledBitmap(iGrande,
                        iGrande.getWidth() / 7, iGrande.getHeight() / 7, false);
                mapa.addMarker(new MarkerOptions()
                        .position(new LatLng(p.getLatitud(), p.getLongitud()))
                        .title(c.getString(1)).snippet(c.getString(2))
                        .icon(BitmapDescriptorFactory.fromBitmap(icono)));
            }
        }
        mapa.setOnInfoWindowClickListener(this);
    }
        /*almacenamiento arrays
        if (Lugares.vectorLugares.size() > 0) {
            GeoPunto p = Lugares.vectorLugares.get(0).getPosicion();
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(p.getLatitud(), p.getLongitud()), 12));
        }

        for (Lugar lugar : Lugares.vectorLugares) {
            GeoPunto p = lugar.getPosicion();
            if (p != null && p.getLatitud() != 0) {
                BitmapDrawable iconoDrawable = (BitmapDrawable) getResources()
                        .getDrawable(lugar.getTipo().getRecurso());
                Bitmap iGrande = iconoDrawable.getBitmap();
                Bitmap icono = Bitmap.createScaledBitmap(iGrande,
                        iGrande.getWidth() / 7, iGrande.getHeight() / 7, false);
                mapa.addMarker(new MarkerOptions()
                        .position(new LatLng(p.getLatitud(), p.getLongitud()))
                        .title(lugar.getNombre()).snippet(lugar.getDireccion())
                        .icon(BitmapDescriptorFactory.fromBitmap(icono)));
            }

        }

        mapa.setOnInfoWindowClickListener(this);
    }*/
    //Se llama a este método cuando se pulse sobre cualquier ventana de información.
    // Para averiguar el marcador al que corresponde se pasa el objeto Marker que se ha pulsado.
    // En el marcador hemos introducido alguna información sobre el lugar (como el nombre),
    // sin embargo lo que necesitamos es el id del lugar.
    // No resulta sencillo introducir este id en un objeto Marker.
    // Para resolverlo hemos introducido un bucle donde se busca un lugar cuyo nombre coincida con el título de marcador.
    // Cuando se encuentre una coincidencia, se crea una intención para lanzar la actividad corespondiente.
    //NOTA: Lo más correcto para resolverlo sería crear un descendiente de Marker que añada este id.
    // Sin embargo, la clase Marker ha sido ha sido marcada como final por lo que no es posible crear descendientes.
    @Override
    public void onInfoWindowClick(Marker marker) {

    /*for (int id=0;id < Lugares.vectorLugares.size();id++){
    Lugar lugar = Lugares.elemento(id);
        if(lugar.getNombre().equals(marker.getTitle())) {
            Intent intent = new Intent(this,VistaLugar.class);
            intent.putExtra("id",(long)id);
            startActivity(intent);
        }
    }*/
        int id = Lugares.buscarNombre(marker.getTitle());
        if (id!=-1){
            Intent intent = new Intent(this, VistaLugar.class);
            intent.putExtra("id", (long) id);
            startActivity(intent);
        }
    }
}
