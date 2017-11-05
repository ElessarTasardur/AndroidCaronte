package gal.caronte.caronte.util;

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

}
