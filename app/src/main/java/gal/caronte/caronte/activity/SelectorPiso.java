package gal.caronte.caronte.activity;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gal.caronte.caronte.custom.Piso;
import gal.caronte.caronte.util.EModoMapa;

/**
 * Created by dpan on 29/12/2017.
 */

public class SelectorPiso {

    private static final int TAMANHO_BOTON = 10;

    private MapaActivity mapaActivity;
    private LinearLayout layoutNiveis;
    private String idPlantaActual;
    private List<Button> listaBotons = new ArrayList<>();
    private Map<Integer, Integer> mapaCor = new HashMap<>();

    public SelectorPiso(MapaActivity mapaActivity, LinearLayout layoutNiveis) {
        super();
        this.mapaActivity = mapaActivity;
        this.layoutNiveis = layoutNiveis;
    }

//    private void crearListaCor() {
//        this.listaCor.add(BitmapDescriptorFactory.HUE_GREEN);
//        this.listaCor.add(BitmapDescriptorFactory.HUE_YELLOW);
//        this.listaCor.add(BitmapDescriptorFactory.HUE_MAGENTA);
//        this.listaCor.add(BitmapDescriptorFactory.HUE_CYAN);
//        this.listaCor.add(BitmapDescriptorFactory.HUE_ORANGE);
//        this.listaCor.add(BitmapDescriptorFactory.HUE_ROSE);
//        this.listaCor.add(BitmapDescriptorFactory.HUE_BLUE);
//        this.listaCor.add(BitmapDescriptorFactory.HUE_RED);
//        this.listaCor.add(BitmapDescriptorFactory.HUE_VIOLET);
//        this.listaCor.add(BitmapDescriptorFactory.HUE_AZURE);
//    }

    public void ocultarBotons() {
        this.layoutNiveis.removeAllViewsInLayout();
    }

    public void amosarSelectorPiso(Collection<Piso> listaPiso, String idPlantaActual, Map<Integer, Float> mapaCorMarcador) {

        this.idPlantaActual = idPlantaActual;
        this.listaBotons.clear();

        this.mapaCor.clear();
        if (mapaCorMarcador != null) {
            for (Integer chave : mapaCorMarcador.keySet()) {
                Float valor = mapaCorMarcador.get(chave);
                float[] hsv = {valor, 1F, 1F};
                int cor = Color.HSVToColor(hsv);
                this.mapaCor.put(chave, cor);
            }
        }

        //Eliminamos los botones existentes para crear los nuevos
        ocultarBotons();
        if (listaPiso != null) {

            Button botonPiso;
            for (Piso piso : listaPiso) {
                botonPiso = new Button(this.mapaActivity);
                final String idPlantaBoton = piso.getPiso().getIdentifier();
                botonPiso.setId(Integer.valueOf(idPlantaBoton));
                botonPiso.setText(String.valueOf(piso.getPiso().getLevel()));
                botonPiso.setTextSize(12F);
                botonPiso.setWidth(TAMANHO_BOTON);
                botonPiso.setHeight(TAMANHO_BOTON);
                Integer cor = this.mapaCor.get(piso.getPiso().getLevel());
                botonPiso.setTextColor(cor);
                this.layoutNiveis.addView(botonPiso);
                botonPiso.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        SelectorPiso.this.mapaActivity.recuperarMapa(idPlantaBoton);
                        cambiarPisoSeleccionado(idPlantaBoton);
                    }
                });
                this.listaBotons.add(botonPiso);
            }
            cambiarPisoSeleccionado(idPlantaActual);
        }

    }

    public void cambiarPisoSeleccionado(String idPlanta) {

        this.idPlantaActual = idPlanta;

        if (this.mapaActivity.getModoMapa().equals(EModoMapa.CREAR_POI)) {
            this.mapaActivity.amosarPoiPiso();
        }

        for (Button boton : this.listaBotons) {
//            if (String.valueOf(boton.getId()).equals(idPlanta)) {
//                boton.setBackgroundColor();
//            }
//            else {
//                boton.setBackgroundColor();
//            }
        }

    }
}
