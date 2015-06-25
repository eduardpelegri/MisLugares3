package com.example.mislugares;

public enum TipoLugar {
    OTROS("Otros",R.drawable.otros ),
    RESTAURANTE("Restaurante", R.drawable.restaurante),
    BAR("Bar", R.drawable.bar),
    COPAS("Copas", R.drawable.copas),
    ESPECTACULO("Espectáculo", R.drawable.espectaculos),
    HOTEL("Hotel", R.drawable.hotel),
    COMPRAS("Compras", R.drawable.compras),
    EDUCACION("Educación", R.drawable.educacion),
    DEPORTE("Deporte", R.drawable.deporte),
    NATURALEZA("Naturaleza", R.drawable.naturaleza),
    GASOLINERA("Gasolinera", R.drawable.gasolinera);

    private final String texto;
    private final int recurso;
    private static String[] nombres;

    TipoLugar(String texto, int recurso) {
        this.texto = texto;
        this.recurso = recurso;
    }

    public static String[] getNombres() {
        String[] nombres = new String[TipoLugar.values().length];
        for (TipoLugar tipo :TipoLugar.values()){
            nombres[tipo.ordinal()]=tipo.texto;
        }
        return nombres;
    }

    public String getTexto() { return texto; }

    public int getRecurso() { return recurso; }

}
