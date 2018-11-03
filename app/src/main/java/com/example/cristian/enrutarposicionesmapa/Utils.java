package com.example.cristian.enrutarposicionesmapa;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Utils {

    interface Constantes  {
        String URL_RUTA_GOOGLE_MAPS = "maps/api/directions/json?";
        String BASE_URL_RUTAS = "https://maps.googleapis.com/";
    }

    /**
     * Decodifica el overview polyline devuelto por el API de google directions
     * @param encoded es la overview_polyline
     * @return un array de LatLng para crear la polilinea
     */
    public static ArrayList<LatLng> decodeOverviewPolyLinePonts(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        if (encoded != null && !encoded.isEmpty() && encoded.trim().length() > 0) {
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
        }
        return poly;
    }
}
