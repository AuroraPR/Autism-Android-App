package jmq.uja.org.mygeosensorapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class CashMovementsAdapter extends BaseAdapter {
    private Context context;
    private List<CashMovement> list;

    public CashMovementsAdapter(Context context, List<CashMovement> list) {
        super();
        this.context = context;
        this.list = list;


    }
    public void update(List<CashMovement> list2){
        list=list2;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.row_session, parent, false);

            TextView tMoney = (TextView) convertView.findViewById(R.id.tMoney);
            TextView tConcept = (TextView) convertView.findViewById(R.id.tConcept);
            TextView tDate = (TextView) convertView.findViewById(R.id.tDate);
            ImageView iPay = (ImageView) convertView.findViewById(R.id.iPay);
            holder=new ViewHolder(tMoney,tConcept,tDate,iPay,position);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CashMovement data = list.get(position);

        holder.setData(data);
        return convertView;
    }

    static class ViewHolder{
        TextView tMoney;
        TextView tConcept;
        TextView tDate;
        ImageView iPay;
        int position;

        public ViewHolder(TextView tMoney,TextView tConcept, TextView tDate, ImageView iPay, int position){
            this.tDate=tDate;
            this.tConcept=tConcept;
            this.tMoney=tMoney;
            this.position=position;
            this.iPay=iPay;
        }

        public void setData(CashMovement data){
            this.tMoney.setText(Utils.round(data.money,2)+"€");
            this.tDate.setText(Utils.minit02S(data.time));
            this.tConcept.setText(data.concept);

            if(data.money<0)
                this.iPay.setImageResource(R.drawable.pay);
            else
                this.iPay.setImageResource(R.drawable.receive);
        }
    }

}