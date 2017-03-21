package xianglesong.com.twandroid.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xianglesong.logcollector.manager.LogManager;
import com.xianglesong.logcollector.utils.LogUtil;

import java.io.File;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.utils.FileUtils;

@SuppressLint("SdCardPath")
public class FragmentProfile extends BaseFragment {

    public static final String TAG = "FragmentProfile";

    private ImageView headPicture;
    private TextView userName;
    private View view;
    private RelativeLayout loginRelativeLayout;
    private RelativeLayout logoutRelativeLayout;
    private Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        headPicture = (ImageView) view.findViewById(R.id.head_pic_img);
        userName = (TextView) view.findViewById(R.id.user_name_text);
        loginRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_login);
        logoutRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_logout);
        logoutButton = (Button) view.findViewById(R.id.logout_button);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 响应用户登陆刷新页面
     */
    public void refleshByLogIn() {
        File file = new File(getActivity().getFilesDir(), "head.png");
        if (file != null) {
            // 设置微信头图
            headPicture.setImageBitmap(FileUtils.getLocalBitmap(file));
        }
        SharedPreferences sp = getActivity().getSharedPreferences(Constants.SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
        String name = sp.getString("username", null);
        userName.setText("昵称：" + name);

        loginRelativeLayout.setVisibility(View.VISIBLE);
        logoutRelativeLayout.setVisibility(View.GONE);
        logoutButton.setVisibility(View.VISIBLE);
    }

    /**
     * 用户未登录
     */
    public void refleshByLogOut() {
        headPicture.setImageResource(R.drawable.default_useravatar);
        userName.setText("");

        loginRelativeLayout.setVisibility(View.GONE);
        logoutRelativeLayout.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.GONE);
    }

    /**
     * fragment切换时回调的函数
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            LogManager.log(TAG, "close", LogUtil.TYPE_FRAGMENT);
        } else {
            LogManager.log(TAG, "open", LogUtil.TYPE_FRAGMENT);
        }
    }
}
