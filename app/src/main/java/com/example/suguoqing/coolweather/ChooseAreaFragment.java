package com.example.suguoqing.coolweather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suguoqing.coolweather.db.City;
import com.example.suguoqing.coolweather.db.County;
import com.example.suguoqing.coolweather.db.Province;
import com.example.suguoqing.coolweather.gson.Weather;
import com.example.suguoqing.coolweather.util.HttpUtil;
import com.example.suguoqing.coolweather.util.Utillity;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    private static final String TAG = "ChooseAreaFragment";
    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CEITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();
    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;
    //选中的省
    private Province selectedProvince;
    //选中的市
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CEITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    String weatherid = countyList.get(position).getWeatherId();
                    //Log.d(TAG, "onItemClick: &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+weatherid);
                    if(getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weatherid",weatherid);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requsetWeather(weatherid);
                    }

                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            //对这个button进行监听，如果是，在县的界面，那么就返回市界面，就是在查一下市，
            //如果是在市的界面，那么就返回省，再查一下省
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CEITY){
                    queryProvice();
                }
            }
        });

        queryProvice();
    }

    /****
     * 查询一个省中所有的市，不行去服务器上找
     *
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        //条件查询
        //cityList = DataSupport.findAll(City.class);
        //cityList = DataSupport.where("provinceid = ?","2").find(City.class);
        cityList = DataSupport.where("provinceId=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList != null && cityList.size()>0){
            Log.d(TAG, "queryCities: ---------------------------------"+cityList.size());
            //清空datalist
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CEITY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFormServer(address,"city");
        }

    }


    /***
     * 查询一个市中的所有县
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        //这个就litepal中的条件查询
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList != null && countyList.size()>0){
            //先清空一下
            dataList.clear();
            //就把countlist中的数据取出来，save到数据库中
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);//列表定位到0开始
            currentLevel = LEVEL_COUNTY;
        }else{
            //在服务器上去查找county,是根据id找的
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFormServer(address,"county");
        }
    }

    /***
     * 查询全国所有的省，优先从数据库查询，如果没有查询到就到服务器上去查询
     */
    private void queryProvice() {

        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size() > 0){
            //先清空
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            //将列表移动到0处
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            //不行就从服务器上查找数据
            String address = "http://guolin.tech/api/china";
            queryFormServer(address,"province");
        }
    }

    /****
     * 从服务器上查找数据
     * @param address
     * @param type
     */

    private void queryFormServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //加载失败
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();//请求内容
                boolean result = false;
                if("province".equals(type)){
                    //如果想要从服务器上查  关于省  的内容
                    result = Utillity.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result = Utillity.handleCityResponse(responseText,selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utillity.handleCountyResponse(responseText,selectedCity.getId());
                }
                //如果获取内容成功
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭进程显示
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvice();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }

            }
        });
    }


    /***
     * show进程显示
     */
    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);

        }
        //progressDialog.show();
    }


    /***
     * close显示进程
     */
    private void closeProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
