package com.example.mislugares;

/**
 * Created by USUARIO on 07/04/2015.
 */
public class GeoPunto {
    //atributos
    double longitud,latitud;
    //constructor
    public GeoPunto(double longitud, double latitud){
        this.longitud = longitud;
        this.latitud = latitud;
    }

    //Getters y Setters

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    //métodos
    public String toString() {
        return new String("longitud:" + longitud + ", latitud:"+ latitud);
    }
    public double distancia (GeoPunto punto) {
        /*
        R = radio medio de la Tierra (6,378km)
        lat = lat2 lat1
        long = long2 long1
        a = sin2(lat/2) + cos(lat1) cos(lat2) sin2(long/2)
        c = 2 atan2(a, (1/a))
        d = R c
         */
        final double RADIO_TIERRA= 6376000;
        double dLat = Math.toRadians(latitud - punto.latitud);
        double dLon = Math.toRadians (longitud - punto.longitud);
        double lat1 = Math.toRadians(punto.latitud);
        double lat2 = Math.toRadians(latitud);
        double a = Math.sin(dLat/2)* Math.sin(dLat/2)+
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
        return c * RADIO_TIERRA;
    }
}
