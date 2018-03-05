package gal.caronte.caronte.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import gal.caronte.caronte.R;

/**
 * Created by ElessarTasardur on 18/02/2018.
 */

public class ToolbarMapa extends Toolbar {

    public ToolbarMapa(Context context) {
        super(context);
    }

    public ToolbarMapa(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolbarMapa(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean accionSeleccionada(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.accion_selector_percorridos:
//                if (this.spinnerPercorrido.getVisibility() == View.INVISIBLE) {
//                    //Se os pois estan visibeis seleccionamos o primeiro
//                    if (this.spinnerPoi.getVisibility() == View.VISIBLE) {
//                        this.spinnerPoi.setSelection(0);
//                    }
//                    this.spinnerPercorrido.setVisibility(View.VISIBLE);
//                }
//                else {
//                    //Se os percorridos estan visibeis seleccionamos o primeiro
//                    this.spinnerPercorrido.setSelection(0);
//                    this.spinnerPercorrido.setVisibility(View.INVISIBLE);
//                }
//                this.spinnerPoi.setVisibility(View.INVISIBLE);
//                return true;
//
//            case R.id.accion_selector_pois:
//                if (this.spinnerPoi.getVisibility() == View.INVISIBLE) {
//                    //Se os percorridos estan visibeis seleccionamos o primeiro
//                    if (this.spinnerPercorrido.getVisibility() == View.VISIBLE) {
//                        this.spinnerPercorrido.setSelection(0);
//                    }
//                    this.spinnerPoi.setVisibility(View.VISIBLE);
//                }
//                else {
//                    //Se hai Pois no spinner seleccionamos o primeiro
//                    this.spinnerPoi.setSelection(0);
//                    this.spinnerPoi.setVisibility(View.INVISIBLE);
//                }
//                this.spinnerPercorrido.setVisibility(View.INVISIBLE);
//                return true;
//
//            case R.id.accion_todos_pois:
//                //Se estamos nun edificio mostramos ou ocultamos os POIs
//                if (this.idEdificioExterno != null) {
//                    if (this.todosPoisVisibeis) {
//                        ocultarTodosPoi();
//                    }
//                    else {
//                        mostrarTodosPoi();
//                    }
//                }
//                return true;
//
//            default:
//                // En caso de que non identifiquemos a accion
//                return super.onOptionsItemSelected(item);
//        }
        return true;
    }

    public void establecerModoConsulta() {

        //TODO


    }
}
