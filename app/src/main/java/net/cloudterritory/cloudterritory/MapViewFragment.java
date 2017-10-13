package net.cloudterritory.cloudterritory;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment {

    private static final String FRAGMENT_NAME = "Map Zoom";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MapController mMapController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(FRAGMENT_NAME);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        ResourceProxyImpl mResourceProxy = new ResourceProxyImpl(getActivity());
        MapView mMapView = new MapView(getActivity(), 256, mResourceProxy);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        try{
            BingMapTileSource.retrieveBingKey(getActivity());
            String m_locale =   Locale.getDefault().getDisplayName();
            BingMapTileSource bing = new BingMapTileSource(m_locale);
            bing.setStyle(BingMapTileSource.IMAGERYSET_AERIAL);
            mMapView.setTileSource(bing);
        }
        catch(Exception e){
            e.printStackTrace();
        }


        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(5);
        GeoPoint gPt = new GeoPoint(-23.5784,-46.4078);
        mMapController.setCenter(gPt);


/*        BingMapTileSource.retrieveBingKey(getActivity());
        String m_locale =   Locale.getDefault().getDisplayName();
        BingMapTileSource bing = new BingMapTileSource(m_locale);
        //bing.setStyle(BingMapTileSource.IMAGERYSET_AERIAL);

        MapView mMapView;
        mMapView = (MapView) view.findViewById(R.id.mapview);
        mMapView.setTileSource(bing);
        mMapView.setMultiTouchControls(true);

        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(100);

        GeoPoint gPt = new GeoPoint(-23.5784,-46.4078);
        mMapController.setCenter(gPt);*/

        return view;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment .
     */
    // TODO: Rename and change types and number of parameters
    public static MapViewFragment newInstance(String param1, String param2) {
        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }




}
