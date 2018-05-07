package gal.caronte.activity;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gal.caronte.custom.Piso;
import gal.caronte.util.EModoMapa;

/**
 * Created by dpan on 29/12/2017.
 */

public class SelectorPiso {

    private static final int TAMANHO_BOTON = 10;

    private MapaActivity mapaActivity;
    private LinearLayout layoutNiveis;
    private String idPlantaActual;
    private List<Button> listaBotons = new ArrayList<>();
    private List<Integer> listaCor = new ArrayList<>();

    public SelectorPiso(MapaActivity mapaActivity, LinearLayout layoutNiveis) {
        super();
        this.mapaActivity = mapaActivity;
        this.layoutNiveis = layoutNiveis;
    }

    public void ocultarBotons() {
        this.layoutNiveis.removeAllViewsInLayout();
    }

    public void amosarSelectorPiso(Collection<Piso> listaPiso, String idPlantaActual, List<Float> listaCor) {

        this.idPlantaActual = idPlantaActual;
        this.listaBotons.clear();

        this.listaCor.clear();
        if (listaCor != null) {
            for (Float valor : listaCor) {
                float[] hsv = {valor, 1F, 1F};
                int cor = Color.HSVToColor(hsv);
                this.listaCor.add(cor);
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
                Integer cor = this.listaCor.get(piso.getPiso().getLevel() % this.listaCor.size());
                botonPiso.setTextColor(cor);
                this.layoutNiveis.addView(botonPiso);
                botonPiso.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        SelectorPiso.this.mapaActivity.recuperarMapa(idPlantaBoton, true);
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

        if (this.mapaActivity.getModoMapa().equals(EModoMapa.CREAR_POI)
                || this.mapaActivity.getModoMapa().equals(EModoMapa.CREAR_PERCORRIDO)
                || this.mapaActivity.getModoMapa().equals(EModoMapa.MODIFICAR_POI_PERCORRIDO)
                || this.mapaActivity.getModoMapa().equals(EModoMapa.ENGADIR_POI_PERCORRIDO)) {
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
