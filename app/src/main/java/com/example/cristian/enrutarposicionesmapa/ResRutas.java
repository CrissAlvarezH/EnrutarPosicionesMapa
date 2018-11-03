package com.example.cristian.enrutarposicionesmapa;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResRutas {

    private ArrayList<Ruta> routes;
    private String status;

    public ArrayList<Ruta> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Ruta> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    /*******************************    [ INICIO ] Subclases **************************************/
    public class Ruta {
        private Limite bounds;

        @SerializedName("overview_polyline")
        private Polilinea overviewPolyline;

        public Limite getBounds() {
            return bounds;
        }

        public void setBounds(Limite bounds) {
            this.bounds = bounds;
        }

        public Polilinea getOverviewPolyline() {
            return overviewPolyline;
        }

        public void setOverviewPolyline(Polilinea overviewPolyline) {
            this.overviewPolyline = overviewPolyline;
        }
    }

    public class Limite {
        private Posicion northeast;
        private Posicion southwest;

        public Posicion getNortheast() {
            return northeast;
        }

        public void setNortheast(Posicion northeast) {
            this.northeast = northeast;
        }

        public Posicion getSouthwest() {
            return southwest;
        }

        public void setSouthwest(Posicion southwest) {
            this.southwest = southwest;
        }
    }

    public class Posicion {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    public class Polilinea {
        private String points;

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }
    }
    /*******************************    [ FIN ] Subclases   ***************************************/
}
