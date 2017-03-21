package xianglesong.com.twandroid.acitvity.search;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.zxing.client.android.ScanConstants;
import com.xianglesong.logcollector.manager.LogManager;
import com.xianglesong.logcollector.utils.LogUtil;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.acitvity.search.db.SearchHistoryDBHelp;
import xianglesong.com.twandroid.acitvity.search.fragments.FragmentSearchAutoComplete;
import xianglesong.com.twandroid.acitvity.search.fragments.FragmentSearchHistory;
import xianglesong.com.twandroid.acitvity.search.fragments.FragmentSearchResults;
import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.widgets.AutoClearEditText;

public class SearchActivity extends FragmentActivity {
    private static final String TAG = "SearchActivity";


    FragmentSearchHistory fragmentSearchHistory;
    FragmentSearchAutoComplete fragmentSearchAutoComplete;
    FragmentSearchResults fragmentSearchResults;
    SearchHistoryDBHelp dbHelper;
    // history autocomplete results
    private Fragment[] fragments;
    private AutoClearEditText searchText;
    private int index = 0;
    private int currentTabIndex = 0;

    //非法字符的正则表达式
    String regEx = "\\\\|\\?|\\*|\\？|\\.|\\@|\\~|\\!|\\！|\\{|\\}|\\(|\\)|\\+|\\_|\\-|\\=|\\#|\\$|\\%|\\^|\\&|\\<|\\>|\\/|\\,|\\:|\\;" + "|“|”|";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        searchText = (AutoClearEditText) this.findViewById(R.id.search_text);

        if (Constants.SEACH_CONTENT_VALUE != null) {
            searchText.setHint(Constants.SEACH_CONTENT_VALUE);
        }

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                }
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // show autocomplete
                if (!"".equals(searchText.getText().toString())) {
                    autoComplete();
                    searchText.setClearIconVisible(true);
                } else if ("".equals(searchText.getText().toString())) {
                    fragmentSearchResults.clearWebviewHistory();
                    showhistory();
                    searchText.setClearIconVisible(false);
                } else {
                    //
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchText.requestFocus();

        initView();


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogManager.log(TAG, "open", LogUtil.TYPE_ACTIVITY);
        if (getIntent().getExtras() != null) {
            String barcode = getIntent().getExtras().getString(ScanConstants.BAR_CODE, null);
            if (barcode != null) {
                searchText.setText(barcode);
                search(searchText);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogManager.log(TAG, "close", LogUtil.TYPE_ACTIVITY);
    }

    public void initView() {
        fragmentSearchHistory = new FragmentSearchHistory();
        fragmentSearchAutoComplete = new FragmentSearchAutoComplete();
        fragmentSearchResults = new FragmentSearchResults();

        fragments = new Fragment[]{fragmentSearchHistory, fragmentSearchAutoComplete,
                fragmentSearchResults};

        // show first fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentSearchHistory)
                .add(R.id.fragment_container, fragmentSearchAutoComplete)
                .add(R.id.fragment_container, fragmentSearchResults)
                .hide(fragmentSearchAutoComplete)
                .hide(fragmentSearchResults).show(fragmentSearchHistory).commit();
    }

    public void search(View view) {
        String searchContent = searchText.getText().toString();
        if (searchContent == null || searchContent.equals("")) {
            //如果搜索内容为空，则搜索提示词
            searchContent = searchText.getHint().toString();
        } else {
            //判断输入的字符串是否为whitespace
            if (StringUtil.isBlank(searchContent)) {
                Toast.makeText(SearchActivity.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            //过滤非法字符
            searchContent = searchFilter(searchContent);
            if (StringUtil.isBlank(searchContent)) {
                Toast.makeText(SearchActivity.this, "搜索词含有特殊字符", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        index = 2;


        fragmentSearchResults.updateWebview(searchContent);
        getSupportFragmentManager().beginTransaction()
                .hide(fragments[currentTabIndex]).
                show(fragments[index]).commit();


        currentTabIndex = 2;
        // write 2 db
        insertSearchHistory(searchContent);
        LogManager.log(TAG, searchContent, LogUtil.TYPE_SEACH);

    }

    public void autoComplete() {
        String searchContent = searchText.getText().toString();
        // check

        index = 1;
        // async
        // if (currentTabIndex != index)
        fragmentSearchAutoComplete.updateListViewData(searchContent);
        getSupportFragmentManager().beginTransaction()
                .hide(fragments[currentTabIndex]).
                show(fragments[index]).commit();

        currentTabIndex = 1;
        // write 2 db
        // insertSearchHistory(searchContent);
    }

    public void showhistory() {
        index = 0;

        fragmentSearchHistory.updateListview();
        getSupportFragmentManager().beginTransaction()
                .hide(fragments[currentTabIndex]).
                show(fragments[index]).commit();

        currentTabIndex = 0;
    }

    public List<String> queryHistorySql() {
        dbHelper = new SearchHistoryDBHelp(getApplicationContext());
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "
                + SearchHistoryDBHelp.HISTORY_TABLENAME, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(cursor.getColumnIndex("search_text"));
            list.add(name);
            cursor.moveToNext();
        }
        db.close();
        return list;
    }

    public void insertSearchHistory(String search) {
        dbHelper = new SearchHistoryDBHelp(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        List<String> list = queryHistorySql();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(search)) {
                count++;
            }
        }
        if (count == 0) {
            db.execSQL("insert into " + SearchHistoryDBHelp.HISTORY_TABLENAME
                    + " values(?,?)", new Object[]{null, search});
        } else {
            // Toast.makeText(getApplicationContext(), "insert search history",
            // Toast.LENGTH_SHORT)
            // .show();
        }

        db.close();
    }

    public void back(View view) {
        finish();
    }


    //过滤非法字符
    public String searchFilter(String str) {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        String data = m.replaceAll("");
        return data;
    }

}
