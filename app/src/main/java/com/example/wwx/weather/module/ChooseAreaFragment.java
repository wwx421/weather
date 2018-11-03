package com.example.wwx.weather.module;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.wwx.weather.R;
import com.example.wwx.weather.db.City;
import com.example.wwx.weather.db.County;
import com.example.wwx.weather.db.Province;
import com.example.wwx.weather.util.HttpUtil;
import com.example.wwx.weather.util.MyApplication;
import com.example.wwx.weather.util.ToastUtil;
import com.example.wwx.weather.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wwx on 2018/11/2.
 * 选择区域
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_COUNTY = 3;

    private TextView txtTitle;
    private Button btnBack;
    private RecyclerView recyclerView;
    private ChooseAreaAdapter adapter;
    private List<String> dataList = new ArrayList<>();

    private ProgressDialog progressDialog;

    //当前选中的级别
    private int currentLevel;
    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;
    //选中的省份
    private Province selectProvince;
    //选中的城市
    private City selectCity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        txtTitle = view.findViewById(R.id.txt_title);
        btnBack = view.findViewById(R.id.btn_back);
        recyclerView = view.findViewById(R.id.recycle_view);
        adapter = new ChooseAreaAdapter(MyApplication.getContext(), dataList);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                MyApplication.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置recyclerView的item分割线
//        DividerItemDecoration decoration = new DividerItemDecoration(
//                MyApplication.getContext(), DividerItemDecoration.VERTICAL);
//        decoration.setDrawable(new ColorDrawable(ContextCompat.getColor(MyApplication.getContext(), R.color.colorBlack)));
//        recyclerView.addItemDecoration(decoration);
        btnBack.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter.setOnItemClickListener(new ChooseAreaAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(position);
                    queryCity();
                } else if (currentLevel == LEVEL_CITY) {
                    selectCity = cityList.get(position);
                    queryCounty();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_CITY) {
                    queryProvince();
                } else if (currentLevel == LEVEL_COUNTY) {
                    queryCity();
                }
            }
        });

        queryProvince();
    }

    /**
     * 查询所有的省
     */
    private void queryProvince() {
        txtTitle.setText("中国");
        btnBack.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.provinceName);
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询省下的所有市
     */
    private void queryCity() {
        txtTitle.setText(selectProvince.provinceName);
        btnBack.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceid=?",
                String.valueOf(selectProvince.id)).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.cityName);
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectProvince.provinceCode;
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    private void queryCounty() {
        txtTitle.setText(selectCity.cityName);
        btnBack.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityid=?",
                String.valueOf(selectCity.id)).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.countyName);
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectProvince.provinceCode;
            int cityCode = selectCity.cityCode;
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的类型和地址从服务器上查询数据
     *
     * @param address 地址
     * @param type    类型
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;

                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(selectProvince.id, responseText);
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(selectCity.id, responseText);
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvince();
                            } else if ("city".equals(type)) {
                                queryCity();
                            } else if ("county".equals(type)) {
                                queryCounty();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                /*
                 * 通过runOnUiThread()方法回到主线程处理逻辑
                 */
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        ToastUtil.showToast(MyApplication.getContext(), "加载失败");
                    }
                });
            }
        });
    }

    /**
     * 显示进度条对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {

            /*
                Dialog是需要依赖一个View，而View是对应于Activity的。
                context是整个应用的上下文
             */
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度条对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
