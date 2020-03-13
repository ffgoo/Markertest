package com.jinasoft.markertest;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Rectangle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.GroundOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PolygonOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    Rectangle rect;

    LatLngBounds bounds;
    NaverMap naverMapback;

    LatLng MarkerPosition;

    Bitmap markerBubble;

    Marker marker;
    final List<Marker> markers = new ArrayList<>();

    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {

        bounds = naverMap.getContentBounds();
        final GroundOverlay ground = new GroundOverlay();
        naverMapback = naverMap;

        // 카메라 초기 위치 설정
        LatLng initialPosition = new LatLng(37.566288, 127.977980);
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(initialPosition);
//        naverMap.moveCamera(CameraUpdate.fitBounds(bounds));

        // 마커들 위치 정의 (대충 1km 간격 동서남북 방향으로 만개씩, 총 4만개)


        // 카메라 이동 되면 호출 되는 이벤트
        naverMap.addOnCameraIdleListener(new NaverMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {


                bounds = naverMap.getContentBounds();
                freeActiveMarkers();

//                loop();

                backMarker task = new backMarker();
                task.execute();
                //                markerBubble = BitmapFactory.decodeResource(getResources(), R.drawable.bluemaker);

//                backMarker task = new backMarker();
//                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);




//                       new Thread(new Runnable() {
//
//
//                    @Override
//                    public void run() {
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                for(int i=0; i<markers.size();i++) {
//                                    Marker marker = markers.get(i);
//                                    marker.setMap(naverMapback);
//                                }
//                            }
//                        });
//
//
//                    }
//                }).start();

        }
  });
 }


 public void loop(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int i = 0; i <1000 ; i++) {

                        MarkerPosition = new LatLng(37.566288 + 0.001 * i, 126.977980 + 0.001 * i);
                        marker = new Marker();
                        if (bounds.contains(MarkerPosition)) {
                            //                        markerBubble = Bitmap.createScaledBitmap(markerBubble,  100, 100, true);

                            marker.setPosition(new LatLng(37.566288 + 0.001 * i, 126.977980 + 0.001 * i));
                            //                        marker.setIcon(OverlayImage.fromBitmap(markerBubble));
                            activeMarkers.add(marker);

                        }
//                        else {
//                            marker.setMap(null);
//                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < activeMarkers.size(); i++) {
                            Marker marker = activeMarkers.get(i);
                            marker.setMap(naverMapback);
                        }
                    }
                });

            }
        }).start();
 }

    class backMarker extends AsyncTask<String, Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {

            for(int i = 0; i <1000 ; i++) {

                MarkerPosition = new LatLng(37.566288 + 0.001 * i, 126.977980 + 0.001 * i);
                marker = new Marker();
                if (bounds.contains(MarkerPosition)) {
                    //                        markerBubble = Bitmap.createScaledBitmap(markerBubble,  100, 100, true);

                    marker.setPosition(new LatLng(37.566288 + 0.001 * i, 126.977980 + 0.001 * i));
                    //                        marker.setIcon(OverlayImage.fromBitmap(markerBubble));
                    activeMarkers.add(marker);

                }
//                        else {
//                            marker.setMap(null);
//                        }
            }




//            marker.setMap(null);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            marker.setMap(naverMapback);

            for (int i = 0; i < activeMarkers.size(); i++) {
                Marker marker = activeMarkers.get(i);
                marker.setMap(naverMapback);
            }

        }
    }




    // 마커 정보 저장시킬 변수들 선언
    private Vector<LatLng> markersPosition;
    private Vector<Marker> activeMarkers;

    // 현재 카메라가 보고있는 위치
    public LatLng getCurrentPosition(NaverMap naverMap) {
        CameraPosition cameraPosition = naverMap.getCameraPosition();
        return new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
    }



    // 지도상에 표시되고있는 마커들 지도에서 삭제
    private void freeActiveMarkers() {
        if (activeMarkers == null) {
            activeMarkers = new Vector<Marker>();
            return;
        }
        for (Marker activeMarker: activeMarkers) {
            activeMarker.setMap(null);
        }
        activeMarkers = new Vector<Marker>();
    }

}