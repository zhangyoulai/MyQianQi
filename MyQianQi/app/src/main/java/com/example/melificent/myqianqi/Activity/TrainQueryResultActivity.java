package com.example.melificent.myqianqi.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.melificent.myqianqi.Adapter.Train_byStation_Adapter;
import com.example.melificent.myqianqi.Bean.QueryTrainInfoByStation.TrainInfoByStationResultList;
import com.example.melificent.myqianqi.Presenter.IGetTrainInfoByStationPresenter;
import com.example.melificent.myqianqi.Presenter.Impl.IGetTrainInfoByStationPresenterImpl;
import com.example.melificent.myqianqi.R;
import com.example.melificent.myqianqi.Utils.TimeFormatUtils;
import com.example.melificent.myqianqi.View.GetTrainInfoByStation;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by p on 2017/3/22.
 */

public class TrainQueryResultActivity extends AppCompatActivity implements GetTrainInfoByStation {
    @InjectView(R.id.train_start_station)
    TextView start_station;
    @InjectView(R.id.train_end_station)
    TextView end_station;
    @InjectView(R.id.train_total_size)
    TextView size;
    @InjectView(R.id.train_bystation_date)
    TextView date_bystation;
    @InjectView(R.id.train_result_bystation)
    RecyclerView recyclerView;
    @InjectView(R.id.trainstationgoback)
    ImageView back;
    @InjectView(R.id.train_result_station_beforedate)
    LinearLayout Date_before;
    @InjectView(R.id.train_result_station_nextdate)
    LinearLayout Date_next;
    @InjectView(R.id.train_station_result_calendar)
    ImageView calendar;
    List<TrainInfoByStationResultList> lists;
    String satrt;
    String end;
    Date DATE;
    private PopupWindow popupWindow;
    Train_byStation_Adapter adapter;
    IGetTrainInfoByStationPresenter presenter = new IGetTrainInfoByStationPresenterImpl(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_query_result);
        ButterKnife.inject(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        getData();
        setData();
        adapter = new Train_byStation_Adapter(lists);
        recyclerView.setAdapter(adapter);
        setButtonListener();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
    }

    private void setButtonListener() {
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.express_pop,null);
                popupWindow = new PopupWindow(view , LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT,true);
                popupWindow.setAnimationStyle(R.style.AnimationFade);
                popupWindow.showAtLocation(v, Gravity.RIGHT,0,0);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (popupWindow!=null&&popupWindow.isShowing()){
                            popupWindow.dismiss();
                            popupWindow=null;
                        }
                        return false;
                    }
                });

                ListView listView = (ListView) view.findViewById(R.id.express_listview);
                String []data= new String[15];
                for (int i =0;i<15;i++){
                    Date date = new Date();
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    calendar.add(calendar.DATE,i);
                    date= calendar.getTime();
                    String nowDate = TimeFormatUtils.formatDate(date);
                    data[i] = nowDate;
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(v.getContext(),android.R.layout.simple_list_item_1,data);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Date date = new Date();
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(date);
                        calendar.add(calendar.DATE,position);
                        date= calendar.getTime();
                        String nowDate = TimeFormatUtils.formatDate(date);
                        date_bystation.setText(nowDate.substring(6,7)+"月"+nowDate.substring(8)+"日");
                        presenter.getTrainInfoByStation(satrt,end,nowDate);
                        if (popupWindow!=null&&popupWindow.isShowing()){
                            popupWindow.dismiss();
                            popupWindow=null;
                        }
                    }
                });
            }
        });
        Date_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(DATE);
                calendar.add(calendar.DATE,-1);
                Date date = new Date();
                date= calendar.getTime();
                Log.i("date111111111", "onClick: "+TimeFormatUtils.formatDate(date));
                date_bystation.setText(TimeFormatUtils.formatDate(date).substring(6,7)+"月"+TimeFormatUtils.formatDate(date).substring(8)+"日");
                presenter.getTrainInfoByStation(satrt,end,TimeFormatUtils.formatDate(date));
            }
        });
        Date_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(DATE);
                calendar.add(calendar.DATE,1);
                Date date = new Date();
                date= calendar.getTime();
                date_bystation.setText(TimeFormatUtils.formatDate(date).substring(6,7)+"月"+TimeFormatUtils.formatDate(date).substring(8)+"日");
                presenter.getTrainInfoByStation(satrt,end,TimeFormatUtils.formatDate(date));
            }
        });
    }

    private void setData() {
//        Date date = new Date();
//        Calendar calendar = new GregorianCalendar();
//        calendar.setTime(date);
//        calendar.add(calendar.DATE,1);
//        date= calendar.getTime();
//        String finalTime = TimeFormatUtils.formatDate(date).substring(5,7)+"月"+TimeFormatUtils.formatDate(date).substring(8)+"日";
        date_bystation.setText(TimeFormatUtils.formatDate(DATE).substring(6,7)+"月"+TimeFormatUtils.formatDate(DATE).substring(8)+"日");

        size.setText("(共"+lists.size()+"趟列车)");
        start_station.setText(satrt);
        end_station.setText(end);
    }

    private void getData() {
        Intent intent = getIntent();
        lists = (List<TrainInfoByStationResultList>) intent.getSerializableExtra("trainResult");
        satrt = intent.getStringExtra("start");
        end = intent.getStringExtra("end");
        DATE = new Date(intent.getLongExtra("date",0));
        Log.i("DATE", "getData: "+TimeFormatUtils.formatDate(DATE));


    }


    @Override
    public void getTrainInfoByStationSuccess(List<TrainInfoByStationResultList> lists) {
        if (lists != null ){
            adapter = new Train_byStation_Adapter(lists);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void getTrainInfoByStationFail(String Msg) {

    }
}
