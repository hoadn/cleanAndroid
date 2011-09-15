package com.cssweb.android.sz;

import java.util.ArrayList;
import java.util.HashMap;

import com.cssweb.android.common.Config;
import com.cssweb.android.main.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CustomSettingActivity extends SettingBaseActivity {
    private ListView baoJiaListView;

    private ListView huDongListView;

    private ListView yyTingListView;

    private ListView fengCaiListView;

    private ListView shangChengListView;

    private ListView luoPangListView;

    private ListView baoDianListView;

    private ArrayList<String> baojiaList = null;

    private ArrayList<String> hudongList = null;

    private ArrayList<String> yytingList = null;

    private ArrayList<String> fengcaiList = null;

    private ArrayList<String> shangchengList = null;

    private ArrayList<String> luopangList = null;

    private ArrayList<String> baodianList = null;

    private SharedPreferences sharedPreferences;

    private Editor editor;

    private String[] baojiaParams;

    private String[] hudongParams;

    private String[] yytingParams;

    private String[] fengcaiParams;

    private String[] shangchengParams;

    private String[] luopangParams;

    private String[] baodianParams;

    private float density = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_setting);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;

        baojiaList = new ArrayList<String>();
        hudongList = new ArrayList<String>();
        yytingList = new ArrayList<String>();
        fengcaiList = new ArrayList<String>();

        shangchengList = new ArrayList<String>();
        luopangList = new ArrayList<String>();
        baodianList = new ArrayList<String>();

        baojiaParams = getResources().getStringArray(R.array.njzq_baoJia);
        hudongParams = getResources().getStringArray(R.array.njzq_huDong);
        yytingParams = getResources().getStringArray(R.array.njzq_yyTing);
        fengcaiParams = getResources().getStringArray(R.array.njzq_fengCai);

        shangchengParams = getResources().getStringArray(
                R.array.njzq_shangCheng);
        luopangParams = getResources().getStringArray(R.array.njzq_luoPang);
        baodianParams = getResources().getStringArray(R.array.njzq_baoDian);

        sharedPreferences = getSharedPreferences("customsetting",
                Context.MODE_PRIVATE);
 
        Boolean saveFlag = sharedPreferences.getBoolean("saveFlag", false);
        editor = sharedPreferences.edit();// 获取编辑器
        String[] defaultSetting = getResources().getStringArray(
                R.array.njzq_default_setting);
        if (defaultSetting.length != 5) {// 对应默认长度

            defaultSetting = new String[5];
        }
        if (!saveFlag) {
            editor.putString("baojia", defaultSetting[0]);
            editor.putString("hudong", "");// 无
            editor.putString("yyting", defaultSetting[1]);
            editor.putString("fengcai", "");// 无
            editor.putString("shangcheng", defaultSetting[2]);
            editor.putString("luopang", defaultSetting[3]);
            editor.putString("baodian", defaultSetting[4]);
            editor.commit();// 提交修改
        }
        putValueInMap(baojiaList, sharedPreferences.getString("baojia",
                defaultSetting[0]));
        putValueInMap(hudongList, sharedPreferences.getString("hudong", ""));// 无默认项
        putValueInMap(yytingList, sharedPreferences.getString("yyting",
                defaultSetting[1]));
        putValueInMap(fengcaiList, sharedPreferences.getString("fengcai", ""));// 无默认项
        putValueInMap(shangchengList, sharedPreferences.getString("shangcheng",
                defaultSetting[2]));
        putValueInMap(luopangList, sharedPreferences.getString("luopang",
                defaultSetting[3]));
        putValueInMap(baodianList, sharedPreferences.getString("baodian",
                defaultSetting[4]));

        baoJiaListView = (ListView) findViewById(R.id.baojia);
        huDongListView = (ListView) findViewById(R.id.hudong);
        yyTingListView = (ListView) findViewById(R.id.yyting);
        fengCaiListView = (ListView) findViewById(R.id.fengcai);

        shangChengListView = (ListView) findViewById(R.id.shangcheng);
        luoPangListView = (ListView) findViewById(R.id.luopang);
        baoDianListView = (ListView) findViewById(R.id.baodian);

        setAdapter(baoJiaListView, baojiaParams, getPosition(baojiaParams,
                sharedPreferences.getString("baojia", "行情预警")), density);
        setAdapter(huDongListView, hudongParams, getPosition(hudongParams,
                sharedPreferences.getString("hudong", "专家解盘")), density);
        setAdapter(yyTingListView, yytingParams, getPosition(yytingParams,
                sharedPreferences.getString("yyting", "预约开户")), density);
        setAdapter(fengCaiListView, fengcaiParams, getPosition(fengcaiParams,
                sharedPreferences.getString("fengcai", "宁证动态")), density);

        setAdapter(shangChengListView, shangchengParams, getPosition(
                shangchengParams, sharedPreferences.getString("shangcheng",
                        "积分乐园")), density);
        setAdapter(luoPangListView, luopangParams, getPosition(luopangParams,
                sharedPreferences.getString("luopang", "操盘必读")), density);
        setAdapter(baoDianListView, baodianParams, getPosition(baodianParams,
                sharedPreferences.getString("baodian", "精选证券池")), density);

        initPopupWindow();
        initTitle(R.drawable.njzq_title_left_back, 0, "自定义快捷方式");
    }

    private void putValueInMap(ArrayList<String> list, String itemName) {
        String[] items = itemName.split(",");
        for (String item : items) {
            list.add(item);
        }
    }

    OnItemClickListener listener = new OnItemClickListener() {
        @SuppressWarnings("unchecked")
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            HashMap<String, Object> adapterMap = (HashMap<String, Object>) parent
                    .getItemAtPosition(position);
            LinearLayout layout = (LinearLayout) view;
            ImageView imageView = (ImageView) layout.getChildAt(2);
            String itemName = (String) adapterMap.get("itemName");

            switch (parent.getId()) {
            case R.id.baojia:
                contains(baojiaList, itemName, imageView);
                break;
            case R.id.hudong:
                contains(hudongList, itemName, imageView);
                break;
            case R.id.yyting:
                contains(yytingList, itemName, imageView);
                break;
            case R.id.fengcai:
                contains(fengcaiList, itemName, imageView);
                break;
            case R.id.shangcheng:
                contains(shangchengList, itemName, imageView);
                break;
            case R.id.luopang:
                contains(luopangList, itemName, imageView);
                break;
            case R.id.baodian:
                contains(baodianList, itemName, imageView);
                break;
            default:
                break;
            }
        }
    };

    private ArrayList<Integer> getPosition(String[] params, String itemName) {
        ArrayList<Integer> positions = new ArrayList<Integer>();
        String[] item = itemName.split(",");

        for (int i = 0; i < params.length; i++) {
            for (String tmp : item) {
                if (tmp.equals(params[i])) {
                    positions.add(i);
                }
            }
        }
        return positions;
    }

    private void contains(ArrayList<String> list, String itemName,
            ImageView imageView) {
        if (list.contains(itemName)) {
            list.remove(itemName);
            imageView.setVisibility(View.INVISIBLE);
        } else {
            list.add(itemName);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setListener(ListView listView) {
        listView.setOnItemClickListener(listener);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        editor.putBoolean("saveFlag", true);
        editor.commit();// 提交修改

        saveMenu(baojiaList, "baojia");
        saveMenu(hudongList, "hudong");
        saveMenu(yytingList, "yyting");
        saveMenu(fengcaiList, "fengcai");
        saveMenu(shangchengList, "shangcheng");
        saveMenu(luopangList, "luopang");
        saveMenu(baodianList, "baodian");

        Config.mapBitmap.clear();
        // TODO
    }

    private void saveMenu(ArrayList<String> list, String preferencesName) {
        StringBuffer sb = new StringBuffer();
        for (String itemName : list) {
            sb.append(itemName).append(",");
        }

        editor.putString(preferencesName, sb.toString());
        editor.commit();// 提交修改
    }
}
