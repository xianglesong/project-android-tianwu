package xianglesong.com.twandroid.acitvity.search.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SearchHistoryDBHelp extends SQLiteOpenHelper {
    public static final String DB_NAME = "searchHistory.db";
    public static final String HISTORY_TABLENAME = "search_history";
    public static final String CREAT_HISTORY = "create table " + HISTORY_TABLENAME
            + "(id integer primary key autoincrement, search_text text not null)";

    private static final int DB_VERSION = 1;

    public SearchHistoryDBHelp(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
