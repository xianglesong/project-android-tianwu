package xianglesong.com.twandroid.acitvity.search.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.domain.search.SearchAutoCompleteDto;

public class SearchAutoCompleteAdapter extends BaseAdapter {

    private Context mContext;
    private List<SearchAutoCompleteDto> mdata;

    public SearchAutoCompleteAdapter(List<SearchAutoCompleteDto> data, Context context) {
        this.mContext = context;
        this.mdata = data;
    }

    public void refresh(List<SearchAutoCompleteDto> data) {
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
            convertView = View.inflate(mContext, R.layout.search_autocomplete_lv_item
                    , null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView
                    .findViewById(R.id.search_autocomplete_tv);
          /*  holder.imageView = (ImageView) convertView
                    .findViewById(R.id.search_autocomplete_iv);*/
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SearchAutoCompleteDto bean = mdata.get(position);
        holder.textView.setText(bean.getKeyword());
        /*holder.imageView.setImageDrawable(bean.getImage());*/

        return convertView;
    }

    private static class ViewHolder {
        /*ImageView imageView;*/
        TextView textView;
    }

}
