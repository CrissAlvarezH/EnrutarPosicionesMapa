package com.example.cristian.enrutarposicionesmapa;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServicioWeb {

    @GET( Utils.Constantes.URL_RUTA_GOOGLE_MAPS )
    Call<ResRutas> pedirRuta(@Query("origin") String origen,
                             @Query("destination") String destino,
                             @Query("key") String llave);


}
