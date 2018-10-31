package cn.netkiller.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 12;
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    private TextView textViewAltitude;
    private TextView textViewSpeed;
    private TextView textViewTime;
    private TextView status;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private ListView listViewAddress;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLatitude = (TextView) findViewById(R.id.textViewLatitude);
        textViewLongitude = (TextView) findViewById(R.id.textViewLongitude);
        textViewAltitude = (TextView) findViewById(R.id.textViewAltitude);
        textViewSpeed = (TextView) findViewById(R.id.textViewSpeed);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        status = (TextView) findViewById(R.id.status);

        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list);
        listViewAddress = (ListView) findViewById(R.id.listViewAddress);
        listViewAddress.setAdapter(adapter);

        this.location();
    }

    private void loop() {

    }

    public void location() {

        //获取LocationManager对象
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d("Location", "GPS Status: " + gpsStatus);

        boolean networkStatus = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d("Location", "Network Status: " + networkStatus);

        //创建一个Criteria对象
        Criteria criteria = new Criteria();
        //设置粗略精确度
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        //设置是否需要返回海拔信息
        criteria.setAltitudeRequired(true);
        //设置是否需要返回方位信息
        criteria.setBearingRequired(true);
        //设置是否允许付费服务
        criteria.setCostAllowed(false);
        //设置电量消耗等级
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        //设置是否需要返回速度信息
        criteria.setSpeedRequired(true);

        Log.d("Location", "Criteria: " + criteria.toString());

        //获取最符合此标准的provider对象
//        String currentProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER).getName();


        //根据设置的Criteria对象，获取最符合此标准的provider对象
        String currentProvider = locationManager.getBestProvider(criteria, true);

        Log.d("Location", "currentProvider: " + currentProvider);
        status.setText(currentProvider);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_CODE);
            return;
        } else {
            status.setText("正在获取GPS坐标请稍候...");
        }

        locationManager.requestLocationUpdates(currentProvider, 0, 0, locationListener);
        //根据当前provider对象获取最后一次位置信息
        Location location = locationManager.getLastKnownLocation(currentProvider);

        //如果位置信息不为null，则请求更新位置信息
        if (location != null) {

            textViewLatitude.setText(location.getLatitude() + "");
            textViewLongitude.setText(location.getLongitude() + "");
            textViewAltitude.setText(location.getAltitude() + "");
            textViewSpeed.setText(location.getSpeed() + "");
            textViewTime.setText(location.getTime() + "");

            Log.d("Location", "Latitude: " + location.getLatitude());
            Log.d("Location", "location: " + location.getLongitude());

        } else {

            Log.d("Location", "Latitude: " + 0);
            Log.d("Location", "location: " + 0);

        }

    }

    //创建位置监听器
    private LocationListener locationListener = new LocationListener() {

        //位置发生改变时调用
        @Override
        public void onLocationChanged(Location location) {
            status.setText("onLocationChanged");

            //位置信息变化时触发
            Log.e("Location", "定位方式：" + location.getProvider());
            Log.e("Location", "纬度：" + location.getLatitude());
            Log.e("Location", "经度：" + location.getLongitude());
            Log.e("Location", "海拔：" + location.getAltitude());
            Log.e("Location", "时间：" + location.getTime());


            textViewLatitude.setText(location.getLatitude() + "");
            textViewLongitude.setText(location.getLongitude() + "");
            textViewAltitude.setText(location.getAltitude() + "");
            textViewSpeed.setText(location.getSpeed() + "");
            textViewTime.setText(dateFormat.format(new Date(location.getTime())) + "");

            //解析地址
            Geocoder geoCoder = new Geocoder(MainActivity.this, Locale.getDefault());

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            List<Address> locationList = null;
            try {
                locationList = geoCoder.getFromLocation(latitude, longitude, 5);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            Address address = locationList.get(0);//得到Address实例第一个地址
//            status.setText(address.toString());
//            String countryName = address.getCountryName();//得到国家名称，比如：中国
//            String locality = address.getLocality();//得到城市名称，比如：北京市

            list.clear();

            for (Address address : locationList) {

                for (int n = 0; address.getAddressLine(n) != null; n++) {
                    String addressLine = address.getAddressLine(n);//得到周边信息，包括街道等，i=0，得到街道名称
                    list.add(addressLine);
                    Log.i("Location", "addressLine = " + addressLine);
                    Log.d("Location", address.getCountryName() + address.getAdminArea() + address.getFeatureName());
                }
            }

            adapter.notifyDataSetChanged();

        }

        //provider失效时调用
        @Override
        public void onProviderDisabled(String provider) {

            Log.d("Location", "onProviderDisabled");
            status.setText("onProviderDisabled");

        }

        //provider启用时调用
        @Override
        public void onProviderEnabled(String provider) {

            Log.d("Location", "onProviderEnabled");
            status.setText("onProviderEnabled");

        }

        //状态改变时调用
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            Log.d("Location", "onStatusChanged");
            //GPS状态变化时触发
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.e("Location", "当前GPS状态为可见状态");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.e("Location", "当前GPS状态为服务区外状态");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.e("Location", "当前GPS状态为暂停服务状态");
                    break;
            }

        }

    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }


}
