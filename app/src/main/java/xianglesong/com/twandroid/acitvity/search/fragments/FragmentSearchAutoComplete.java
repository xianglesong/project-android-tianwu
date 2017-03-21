package xianglesong.com.twandroid.acitvity.search.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.jsoup.helper.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.acitvity.search.adapter.SearchAutoCompleteAdapter;
import xianglesong.com.twandroid.domain.search.SearchAutoCompleteDto;
import xianglesong.com.twandroid.utils.HttpUtils;

public class FragmentSearchAutoComplete extends Fragment {
    private static final String TAG = "searchAutoComplete";

    //搜索结果页面地址的前缀
    private static final String SEARCH_PAGE_PREFIX = "http://isearch.91tianwu.com/solr/collection_autoComplete/pinyinSelect?q=text:";
    //搜索结果页面地址的后缀
    private static final String SEARCH_PAGE_SUFFIX = "*&wt=json&indent=true&fl=keyword&sort=appearCount%20desc&query=false";


    ListView listView;

    private List<SearchAutoCompleteDto> mList;

    private SearchAutoCompleteAdapter searchAutoCompleteAdapter;

    private List<SearchAutoCompleteDto> search_auto_complete_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_autocomplete, container, false);
        // view set
        // list set
        listView = (ListView) view.findViewById(R.id.lv_search_autocomplete);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                EditText searchText = (EditText) getActivity().findViewById(R.id.search_text);
                searchText.setText(search_auto_complete_list.get(position).getKeyword());
                getActivity().findViewById(R.id.search_button).performClick();
            }
        });
        updateListViewData(null);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    //根据searchtext，得到一个搜索的url，提交给服务器
    public String getUrlBySearchText(String searchText) {
        String data = searchText;
        String url;
        try {
            data = URLEncoder.encode(searchText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url = SEARCH_PAGE_PREFIX + data + SEARCH_PAGE_SUFFIX;
        return url;
    }

    //解析结果得到keyword的List
    public List parseResult(String results) {
        // parse results
        List<SearchAutoCompleteDto> list = new ArrayList<SearchAutoCompleteDto>();
        if (results != null && !"".equals(results)) {
            results = results.replace("\u005B", ",");
            results = results.replace("]", "");
            results = results.replace("}", "");
            String[] rs = results.split(",");
            for (String s : rs) {
                Log.i(TAG, s);
                if (s.contains("keyword")) {
                    if (s.split(":").length >= 2) {
                        SearchAutoCompleteDto searchAutoCompleteDto = new SearchAutoCompleteDto();
                        searchAutoCompleteDto.setKeyword(s.split(":")[1].replaceAll("\"", ""));
                        list.add(searchAutoCompleteDto);
                    }
                }
            }
        }
        return list;
    }

    //更新listview数据
    public void updateListViewData(String searchContent) {
        if (!StringUtil.isBlank(searchContent)) {
            // TODO use http request
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }


            String url = getUrlBySearchText(searchContent);
            String results = HttpUtils.getUrlContent(url);
            search_auto_complete_list = parseResult(results);
            searchAutoCompleteAdapter = new SearchAutoCompleteAdapter(search_auto_complete_list, getActivity().getApplicationContext());
            listView.setAdapter(searchAutoCompleteAdapter);
        }
    }
}
