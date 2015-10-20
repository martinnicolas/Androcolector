package com.apps.martin.androcolector;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.views.MapView;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerMapaFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public VerMapaFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        View verMapaView = inflater.inflate(R.layout.fragment_ver_mapa, container, false);
        MapView mv = (MapView) verMapaView.findViewById(R.id.mapview);
        // Setup UserLocation monitoring
        GpsLocationProvider locationProvider = new GpsLocationProvider(getActivity().getApplicationContext());
        UserLocationOverlay myLocationOverlay = new UserLocationOverlay(locationProvider, mv);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        mv.getOverlays().add(myLocationOverlay);
        double latitude = locationProvider.getLastKnownLocation().getLatitude();
        double longitud = locationProvider.getLastKnownLocation().getLongitude();
        LatLng myCoords = new LatLng(latitude,longitud);
        mv.setCenter(myCoords);
        //Agrego marcador en la ubicación obtenida (con el nombre del usuario logueado)
        SharedPreferences prefs = getActivity().getSharedPreferences("Configuracion", Context.MODE_PRIVATE);
        String usuario = prefs.getString("usuario", "Error loading user!");
        Marker marker = new Marker(mv, usuario, "Mi ubicación", myCoords);
        mv.addMarker(marker);
        mv.setZoom(16);
        return verMapaView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
