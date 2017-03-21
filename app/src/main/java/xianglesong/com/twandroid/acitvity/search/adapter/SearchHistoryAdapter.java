package xianglesong.com.twandroid.acitvity.search.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.domain.search.SearchHistoryDto;

public class SearchHistoryAdapter extends BaseAdapter {

    private Context mContext;
    private List<SearchHistoryDto> mdata;

    public SearchHistoryAdapter(List<SearchHistoryDto> data, Context context) {
        this.mContext = context;
        this.mdata = data;
    }

    public void refresh(List<SearchHistoryDto> data) {
        this.mdata = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mdata.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.search_history_lv_item
                    , null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView
                    .findViewById(R.id.search_history_tv);
            // holder.imageView = (ImageView) convertVie..findViewById(R.id.search_history_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SearchHistoryDto bean = mdata.get(position);
        holder.textView.setText(bean.getKeyword());
        //holder.imageView.setImageDrawable(bean.getImage());

        return convertView;
    }

    private static class ViewHolder {
        // ImageView imageView;
        TextView textView;
    }

}
