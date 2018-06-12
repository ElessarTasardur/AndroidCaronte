package gal.caronte.util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import gal.caronte.R;

/**
 * Created by ElessarTasardur on 13/10/2017.
 */

public class PermisosUtil {

    private static final String TAG = PermisosUtil.class.getSimpleName();

    public static final int CODIGO_SOLICITUDE_PERMISO_LOCALIZACION = 1;
    public static final int CODIGO_SOLICITUDE_PERMISO_LECTURA_ALMACENAMENTO = 2;
    public static final int CODIGO_SOLICITUDE_PERMISO_ESCRITURA_ALMACENAMENTO = 3;
    public static final int CODIGO_SOLICITUDE_PERMISO_CAMARA = 4;

    public static boolean comprobarPermisos(AppCompatActivity actividad, String permiso, Integer codigoSolicitude, boolean finalizar) {

        boolean permisoConcedido = false;
        if (ContextCompat.checkSelfPermission(actividad, permiso) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, StringUtil.creaString("Sen permiso para ", permiso, ". Procedemos a solicitalo"));
            //Se debemos explicar a solicitude do permiso creamos un aviso e realizamola
            if (ActivityCompat.shouldShowRequestPermissionRationale(actividad, permiso)) {
                PermisosUtil.RationaleDialog.newInstance(codigoSolicitude, finalizar, permiso).show(actividad.getSupportFragmentManager(), "dialog");
            }
            //Se non precisamos explicacion para a solicitude, realizamola directamente
            else {
                ActivityCompat.requestPermissions(actividad, new String[]{permiso}, codigoSolicitude);
            }
        }
        else {
            Log.i(TAG, StringUtil.creaString("Permiso ", permiso, " xa obtido. Continuamos co proceso"));
            permisoConcedido = true;
        }

        return permisoConcedido;
    }

    public static boolean tenPermiso(String[] permisos, int[] resultados, String permiso) {
        for (int i = 0; i < permisos.length; i++) {
            if (permiso.equals(permisos[i])) {
                return resultados[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }


    public static class RationaleDialog extends DialogFragment {

        private static final String ARGUMENTO_CODIGO_SOLICITUDE_PERMISO = "codigoSolicitude";
        private static final String ARGUMENTO_CERRAR_ACTIVIDAD = "finalizar";
        private static final String ARGUMENTO_PERMISO = "permiso";

        private boolean finalizarActividad = false;

        /**
         * Creates a new instance of a dialog displaying the rationale for the use of the location
         * permission.
         * <p>
         * The permission is requested after clicking 'ok'.
         *
         * @param codigoSolicitude    Id of the request that is used to request the permission. It is
         *                       returned to the
         *                       {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}.
         * @param finalizarActividad Whether the calling Activity should be finished if the dialog is
         *                       cancelled.
         */
        public static RationaleDialog newInstance(int codigoSolicitude, boolean finalizarActividad, String permiso) {
            Bundle argumentos = new Bundle();
            argumentos.putInt(ARGUMENTO_CODIGO_SOLICITUDE_PERMISO, codigoSolicitude);
            argumentos.putBoolean(ARGUMENTO_CERRAR_ACTIVIDAD, finalizarActividad);
            argumentos.putString(ARGUMENTO_PERMISO, permiso);
            RationaleDialog dialogo = new RationaleDialog();
            dialogo.setArguments(argumentos);
            return dialogo;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle argumentos = getArguments();
            final int codigoSolicitude = argumentos.getInt(ARGUMENTO_CODIGO_SOLICITUDE_PERMISO);
            finalizarActividad = argumentos.getBoolean(ARGUMENTO_CERRAR_ACTIVIDAD);

            String[] permiso = null;
            if (codigoSolicitude == CODIGO_SOLICITUDE_PERMISO_LOCALIZACION) {
                permiso = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            }
            else if (codigoSolicitude == CODIGO_SOLICITUDE_PERMISO_LECTURA_ALMACENAMENTO) {
                permiso = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            }
            else if (codigoSolicitude == CODIGO_SOLICITUDE_PERMISO_ESCRITURA_ALMACENAMENTO) {
                permiso = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            }
            else if (codigoSolicitude == CODIGO_SOLICITUDE_PERMISO_CAMARA) {
                permiso = new String[]{Manifest.permission.CAMERA};
            }

            final String[] permisoDialogo = permiso;

            DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // After click on Ok, request the permission.
                    ActivityCompat.requestPermissions(getActivity(), permisoDialogo, codigoSolicitude);
                    // Do not finish the Activity while requesting permission.
                    finalizarActividad = false;
                }
            };

            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.permiso_motivo)
                    .setPositiveButton(android.R.string.ok, okListener)
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (finalizarActividad) {
                Toast.makeText(getActivity(), R.string.permiso_necesario, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }
}
