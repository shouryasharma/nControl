package nemi.in;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import in.nemi.ncontrol.R;

/**
 * Created by Sanjay on 11-Oct-16.
 */
public class backup extends BaseAdapter {

    private Context mContext;
    private List<backupview> mbackupview;

    public backup(List<backupview> mbackupview, Context mContext) {
        this.mbackupview = mbackupview;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mbackupview.size();
    }

    @Override
    public Object getItem(int i) {
        return mbackupview.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(mContext, R.layout.backup_adp,null);
        TextView tvtrnid  = (TextView)v.findViewById(R.id.selected_trnid);
        TextView tvamount = (TextView)v.findViewById(R.id.selected_amount);
        TextView tvprice = (TextView)v.findViewById(R.id.selected_price);
        TextView tvqty = (TextView)v.findViewById(R.id.selected_qty);
        TextView tvcont = (TextView)v.findViewById(R.id.selected_contact);
        TextView tvname = (TextView)v.findViewById(R.id.selected_name);
        TextView tvitem = (TextView)v.findViewById(R.id.selected_item);

        tvtrnid.setText(mbackupview.get(i).gettrnid());
        tvamount.setText(mbackupview.get(i).getAmount());
        tvprice.setText(mbackupview.get(i).getprice());
        tvqty.setText(mbackupview.get(i).getQty());
        tvcont.setText(mbackupview.get(i).getCcont());
        tvname.setText(mbackupview.get(i).getCname());
        tvitem.setText(mbackupview.get(i).getitem());

        return v;
    }
}
