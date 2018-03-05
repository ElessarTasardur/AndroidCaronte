package gal.caronte.caronte.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import gal.caronte.caronte.R;
import gal.caronte.caronte.activity.MapaActivity;

/**
 * Created by ElessarTasardur on 18/02/2018.
 */

public class InfoWindowAdapterGuiado implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "InfoWindowAdapterGuiado";
    private LayoutInflater inflater;
    private MapaActivity mapaActivity;

    public InfoWindowAdapterGuiado(LayoutInflater inflater, MapaActivity mapaActivity) {
        this.inflater = inflater;
        this.mapaActivity = mapaActivity;
    }

    @Override
    public View getInfoContents(final Marker m) {
        //Carga layout personalizado.
        View v = inflater.inflate(R.layout.layout_info_marcador, null);
        ((TextView) v.findViewById(R.id.info_marcador_nome)).setText(m.getTitle());
        return v;
    }

    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }

}
