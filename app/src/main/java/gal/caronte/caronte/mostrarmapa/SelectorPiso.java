package gal.caronte.caronte.mostrarmapa;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gal.caronte.caronte.custom.Piso;

/**
 * Created by dpan on 29/12/2017.
 */

public class SelectorPiso {

    private MapaActivity mapaActivity;
    private LinearLayout layoutNiveis;
    private String idPlantaActual;
    private List<Button> listaBotons = new ArrayList<>();

    public SelectorPiso(MapaActivity mapaActivity, LinearLayout layoutNiveis) {
        super();
        this.mapaActivity = mapaActivity;
        this.layoutNiveis = layoutNiveis;
    }

    public void ocultarBotons() {
        this.layoutNiveis.removeAllViewsInLayout();
    }

    public void mostrarSelectorPiso(Collection<Piso> listaPiso, String idPlantaActual) {

        this.idPlantaActual = idPlantaActual;
        this.listaBotons.clear();

        //Eliminamos los botones existentes para crear los nuevos
        ocultarBotons();
        if (listaPiso != null) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            Button botonPiso;
            for (Piso piso : listaPiso) {
                botonPiso = new Button(this.mapaActivity);
                final int nivel = piso.getPiso().getLevel();
                final String idPlantaBoton = piso.getPiso().getIdentifier();
                botonPiso.setId(Integer.valueOf(idPlantaBoton));
                botonPiso.setText(nivel);
                this.layoutNiveis.addView(botonPiso, params);
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
