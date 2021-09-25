package ttqrpos.com.ttposmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ttqrpos.com.ttposmobile.Adapters.PosActAdapter;
import ttqrpos.com.ttposmobile.Models.QrContent;

public class PosActList extends AppCompatActivity {


    @BindView(R.id.posActGrdList)
    RecyclerView grdList;

    public Date filterDate;
    Context context;

    PosActAdapter adapter;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos_act_list);
        ButterKnife.bind(this);

        context=this;


        Calendar cal = Calendar.getInstance();
        filterDate=new Timestamp(cal.getTimeInMillis());


        grdList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        grdList.setLayoutManager(llm);

        refreshList();


    }

    private void refreshList(){

        adapter=new PosActAdapter(QrContent.getListByDate(filterDate));
        grdList.setAdapter(adapter);
    }


    @OnClick(R.id.btnSelectDate)
    public void selectDate() {
        dialog=new Dialog(context);
        dialog.setContentView(R.layout.dialog_date);
        dialog.setTitle("Lütfen tarih seçimi yapınız...");

        final Button btnSelectDate = (Button) dialog.findViewById(R.id.btnSelectDateSave);
        final DatePicker txtDatePicker = (DatePicker)dialog.findViewById(R.id.dialog_date_picker_selected_date);

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDate= new Date(txtDatePicker.getYear()-1900,txtDatePicker.getMonth(),txtDatePicker.getDayOfMonth());
                refreshList();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}