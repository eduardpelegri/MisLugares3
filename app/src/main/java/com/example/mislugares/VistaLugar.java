package com.example.mislugares;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

public class VistaLugar extends ActionBarActivity {
    private long id;
    private Lugar lugar;
    private ImageView imageView;
    final static int RESULTADO_EDITAR= 1;
    final static int RESULTADO_GALERIA= 2;
    final static int RESULTADO_FOTO= 3;
    private Uri uriFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_lugar);
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);
        //lugar = Lugares.elemento((int) id);
        imageView = (ImageView) findViewById(R.id.foto);
        actualizarVistas();
    }
    private void actualizarVistas(){
        lugar = Lugares.elemento((int) id);
        TextView nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());
        ImageView logo_tipo = (ImageView) findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipo().getRecurso());
        TextView tipo = (TextView) findViewById(R.id.tipo);
        tipo.setText(lugar.getTipo().getTexto());
        if ( lugar.getDireccion().isEmpty()){
            findViewById(R.id.direccion).setVisibility(View.GONE);
        }else {
            TextView direccion = (TextView) findViewById(R.id.direccion);
            direccion.setText(lugar.getDireccion());
        }
        if (lugar.getTelefono()==0){
            findViewById(R.id.telefono).setVisibility(View.GONE);
        }else {
            TextView telefono = (TextView) findViewById(R.id.telefono);
            telefono.setText(Integer.toString(lugar.getTelefono()));
        }
        if (lugar.getUrl().isEmpty()){
            findViewById(R.id.url).setVisibility(View.GONE);
        }else {
            TextView url = (TextView) findViewById(R.id.url);
            url.setText(lugar.getUrl());
        }
        if (lugar.getComentario().isEmpty()){
            findViewById(R.id.comentario).setVisibility(View.GONE);
        }else {
            TextView comentario = (TextView) findViewById(R.id.comentario);
            comentario.setText(lugar.getComentario());
        }
        TextView fecha = (TextView) findViewById(R.id.fecha);
        fecha.setText(DateFormat.getDateInstance().format(
                new Date(lugar.getFecha())));
        TextView hora = (TextView) findViewById(R.id.hora);
        hora.setText(DateFormat.getTimeInstance().format(
                new Date(lugar.getFecha())));
        RatingBar valoracion = (RatingBar) findViewById(R.id.valoracion);
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar,
                                                float valor, boolean fromUser) {
                        lugar.setValoracion(valor);
                        Lugares.actualizaLugar((int) id, lugar);
                    }
                });
        ponerFoto(imageView, lugar.getFoto());
    }


@Override
    public boolean onCreateOptionsMenu (Menu menu){
    getMenuInflater().inflate(R.menu.vista_lugar,menu);
    return true;
}
@Override
    public boolean onOptionsItemSelected (MenuItem item){
    switch (item.getItemId()){
        case R.id.accion_compartir:
            Intent intent = new Intent (Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, lugar.getNombre() + " - " + lugar.getUrl());
            startActivity(intent);
            return true;
        case R.id.accion_llegar:
            verMapa(null);
            return true;
        case R.id.accion_editar:
            Intent i = new Intent(this, EdicionLugar.class);
            i.putExtra("id", id);
            startActivityForResult(i, RESULTADO_EDITAR);
            return true;
        case R.id.accion_borrar:
            new AlertDialog.Builder(this)
                    .setTitle("Borrado del Lugar")
                    .setMessage("¿Estas seguro que quieres eliminar el lugar?")
                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Lugares.borrar((int) id);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancelar",null)
                    .show();

            //finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
}

    public void verMapa(View view) {
        Uri uri;
        double lon = lugar.getPosicion().getLongitud();
        double lat = lugar.getPosicion().getLatitud();
        if (lon!=0 || lat!=0){
            uri= Uri.parse("geo:"+ lat + "," +lon);
        }else {
            uri=Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
        }

    public void llamadaTelefono (View view){
        startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+ lugar.getTelefono())));
    }
    public void pgWeb (View view){
        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(lugar.getUrl())));
    }

    public void galeria (View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, RESULTADO_GALERIA);
    }
    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri!= null ){
            imageView.setImageURI(Uri.parse(uri));
        }else {
            imageView.setImageBitmap(null);
        }
    }
    public void tomarFoto (View view) {
        //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uriFoto = Uri.fromFile(new File(Environment.getExternalStorageDirectory()+ File.separator
                +"img_"+(System.currentTimeMillis()/1000)+".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uriFoto);
        startActivityForResult(intent,RESULTADO_FOTO);
    }
    public void eliminarFoto(View view) {
        lugar.setFoto(null);
        ponerFoto(imageView, null);
        Lugares.actualizaLugar((int) id, lugar);
    }
    @Override
    protected void onActivityResult (int requestCode,int resultCode,Intent data ){
    if (requestCode==RESULTADO_EDITAR){
        actualizarVistas();
        findViewById(R.id.scrollView1).invalidate();
    }else if (requestCode==RESULTADO_GALERIA
                && resultCode==Activity.RESULT_OK) {
        lugar.setFoto(data.getDataString());
        Lugares.actualizaLugar((int) id, lugar);
        Toast.makeText(this,data.getDataString(),Toast.LENGTH_LONG).show();
        ponerFoto(imageView, lugar.getFoto());


    }else if (requestCode==RESULTADO_FOTO && resultCode==Activity.RESULT_OK
            && lugar!=null && uriFoto!=null){
        lugar.setFoto(uriFoto.toString());
        Lugares.actualizaLugar((int) id, lugar);
        ponerFoto(imageView,lugar.getFoto());
    }
    }



}