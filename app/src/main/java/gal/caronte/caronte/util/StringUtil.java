package gal.caronte.caronte.util;

import java.util.List;

/**
 * Created by ElessarTasardur on 08/10/2017.
 */

public class StringUtil {

    public static String creaString(Object... cadeas) {

        StringBuilder sb = new StringBuilder();
        if (cadeas != null) {
            for (Object cadea : cadeas) {
                sb.append(String.valueOf(cadea));
            }
        }

        return sb.toString();
    }

    public static String convertirListaIntegerCSV(List<Integer> listaEnteiros) {
        String retorno = "";
        if (listaEnteiros != null) {
            StringBuilder retornoSB = new StringBuilder();
            for (Integer enteiro : listaEnteiros) {
                retornoSB.append(enteiro);
                retornoSB.append(",");
            }
            retorno = retornoSB.substring(0, retornoSB.length() - 2);
        }
        return retorno;
    }

}
