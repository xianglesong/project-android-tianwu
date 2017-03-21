package xianglesong.com.twandroid.acitvity.search.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.acitvity.search.adapter.SearchHistoryAdapter;
import xianglesong.com.twandroid.acitvity.search.db.SearchHistoryDBHelp;
import xianglesong.com.twandroid.domain.search.SearchHistoryDto;

public class FragmentSearchHistory extends Fragment {
    private static final String TAG = "FragmentSearchHistory";
    SearchHistoryDBHelp dbHelper;
    ListView listView;
    Button clearButton;
    View view;
    private SearchHistoryAdapter search_history_adapter;

    private List<SearchHistoryDto> search_history_list;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    search_history_adapter.refresh(querySearchHistoryData());
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_history, container, false);

        // read from db
        search_history_list = querySearchHistoryData();


        listView = (ListView) view.findViewById(R.id.lv_search_history);
        View header = inflater.inflate(R.layout.search_history_lv_item, null);
        TextView headerText = (TextView) header.findViewById(R.id.search_history_tv);
        headerText.setText("历史搜索");
        headerText.setTextColor(Color.GRAY);
        View footButton = inflater.inflate(R.layout.fragmen_history_listview_footview, null);
        listView.addHeaderView(header, null, false);
        listView.setHeaderDividersEnabled(true);
        listView.addFooterView(footButton);
        search_history_adapter = new SearchHistoryAdapter(search_history_list, getActivity().getApplicationContext());
        listView.setAdapter(search_history_adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView listView = (ListView) parent;
                // Toast.makeText(getActivity(), search_history_list.get(position).getKeyword(), Toast.LENGTH_LONG).show();
                // enter search page
                EditText searchText = (EditText) getActivity().findViewById(R.id.search_text);
                searchText.setText(search_history_list.get(position - 1).getKeyword());
                // trigger search
                getActivity().findViewById(R.id.search_button).performClick();
            }
        });

        if (search_history_list.size() == 0) {
            listView.setVisibility(View.GONE);
        }

        clearButton = (Button) view.findViewById(R.id.bt_clear_history);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearHistory();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private List<SearchHistoryDto> querySearchHistoryData() {
        dbHelper = new SearchHistoryDBHelp(getActivity().getApplicationContext());
        List<SearchHistoryDto> his_list = new ArrayList<SearchHistoryDto>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor his_c = db.rawQuery("select * from "
                + SearchHistoryDBHelp.HISTORY_TABLENAME, null);

        his_c.moveToFirst();
        while (!his_c.isAfterLast()) {
            int h_id = his_c.getInt(his_c.getColumnIndex("id"));
            String h_name = his_c.getString(his_c.getColumnIndex("search_text"));
            final SearchHistoryDto his_bean = new SearchHistoryDto();
            his_bean.setKeyword(h_name);

            /*his_bean.setImage(getResources().getDrawable(
                    R.drawable.history_icon));*/

            his_list.add(his_bean);
            his_c.moveToNext();
        }
        if (his_list.size() == 0) {

        }
        db.close();
        Collections.reverse(his_list);
        return his_list;

    }

    private void deleteHistory() {
        dbHelper = new SearchHistoryDBHelp(getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from " + SearchHistoryDBHelp.HISTORY_TABLENAME);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }).start();
        db.close();

        //清除历史记录时，隐藏页面
        view.setVisibility(View.GONE);
    }

    public void clearHistory() {
        deleteHistory();
    }

    public void updateListview() {
        search_history_list = querySearchHistoryData();
        if (search_history_list.size() == 0) {
            listView.setVisibility(View.GONE);
            return;
        }
        search_history_adapter = new SearchHistoryAdapter(search_history_list, getActivity().getApplicationContext());
        listView.setAdapter(search_history_adapter);
        listView.setVisibility(View.VISIBLE);
    }

}
