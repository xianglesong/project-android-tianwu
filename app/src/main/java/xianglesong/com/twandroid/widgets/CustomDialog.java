package xianglesong.com.twandroid.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import xianglesong.com.twandroid.R;

/**
 * Created by LZY on 2015/12/14.
 */
public class CustomDialog extends Dialog implements View.OnClickListener {

    private Button positiveButton;
    private Button negativeButton;

    private TextView textView;
    private String mTitle;
    private CustomDialogClickListenr mCustomDialogClickListenr;


    public interface CustomDialogClickListenr {
        //响应确定按钮点击事件
        public void onPositiveButtonClick();

        //响应取消按钮点击事件
        public void onNegativeButtonClick();

    }

    /**
     * @param context
     * @param customDialogClickListenr 窗口的点击事件监听
     * @param title
     */
    public CustomDialog(Context context, CustomDialogClickListenr customDialogClickListenr, String title) {
        super(context);
        mCustomDialogClickListenr = customDialogClickListenr;
        mTitle = title;
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_dialog, null);

        textView = (TextView) view.findViewById(R.id.dialog_title);
        textView.setText(mTitle);
        positiveButton = (Button) view.findViewById(R.id.dialog_positive_button);
        negativeButton = (Button) view.findViewById(R.id.dialog_negative_button);
        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(view);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_positive_button:
                mCustomDialogClickListenr.onPositiveButtonClick();
                dismiss();
                break;
            case R.id.dialog_negative_button:
                mCustomDialogClickListenr.onNegativeButtonClick();
                dismiss();
                break;
            default:
        }
    }
}
