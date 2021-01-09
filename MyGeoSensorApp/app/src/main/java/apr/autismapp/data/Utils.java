package apr.autismapp.data;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
    static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    static public String t02S(long t0){
        return format.format(new Date(t0));
    }

    static public int nDay(long t1, long t0){
        return (int)((t1-t0)/(1000L*60L*60L*24L));

    }

    static SimpleDateFormat miniformat = new SimpleDateFormat("dd/MM HH:mm");
    static public String minit02S(long t0){
        return miniformat.format(new Date(t0));
    }

    static int roundF(float v){
        return (int)v;
    }

    public static void setDynamicHeight(ListView mListView) {
        ListAdapter mListAdapter = mListView.getAdapter();
        if (mListAdapter == null) {
            // when adapter is null
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < mListAdapter.getCount(); i++) {
            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }
}
