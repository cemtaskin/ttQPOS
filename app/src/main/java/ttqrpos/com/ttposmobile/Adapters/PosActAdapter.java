package ttqrpos.com.ttposmobile.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ttqrpos.com.ttposmobile.Models.QrContent;
import ttqrpos.com.ttposmobile.R;

public class PosActAdapter  extends RecyclerView.Adapter<PosActAdapter.PosActActViewHolder> {

    public List<QrContent> list;


    public PosActAdapter(List<QrContent> list) {
        this.list = list;
    }

    @Override
    public PosActActViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.qr_content_item, parent, false);

        return new PosActActViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PosActActViewHolder holder, int position) {

        QrContent qrContent = list.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        holder.txtActDate.setText(sdf.format(qrContent.getActDate()));
        holder.txtRefNo.setText("Ref No: " + qrContent.getRefNo());
        holder.txtAmount.setText(String.format("%.2f TL",qrContent.getAmount()/100));
        holder.txtSerialId.setText("Serial No : " + qrContent.getSerialNo());
        holder.txtTerminalID.setText("Terimal ID :" +  qrContent.getTerminalID());
        holder.txtRemoteID.setText(String.format("Remote ID:%d",qrContent.getRemoteID()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public static class PosActActViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.qr_content_item_act_date) TextView txtActDate;
        @BindView(R.id.qr_content_item_act_amount) TextView txtAmount;
        @BindView(R.id.qr_content_item_ref_no) TextView txtRefNo;
        @BindView(R.id.qr_content_item_serial_id) TextView txtSerialId;
        @BindView(R.id.qr_content_item_termimal_id) TextView txtTerminalID;
        @BindView(R.id.qr_content_item_remote_id) TextView txtRemoteID;

        public PosActActViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}