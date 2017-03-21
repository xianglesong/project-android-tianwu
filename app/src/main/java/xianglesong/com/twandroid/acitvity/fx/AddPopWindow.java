package xianglesong.com.twandroid.acitvity.fx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.zxing.client.android.CaptureActivity;

import java.io.File;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.utils.FileUtils;
import xianglesong.com.twandroid.webview.WebViewInfo;
import xianglesong.com.twandroid.wxapi.WeixinShareDialog;

public class AddPopWindow extends PopupWindow {
    private View contentView;

    @SuppressLint("InflateParams")
    public AddPopWindow(final Activity context, final WebViewInfo webViewInfo) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popupwindow_add, null);

        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);


        RelativeLayout re_share = (RelativeLayout) contentView.findViewById(R.id.re_share_to_weixin);
        re_share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Dialog dialog = new WeixinShareDialog(context, webViewInfo);
                dialog.show();
                AddPopWindow.this.dismiss();
            }
        });

        RelativeLayout re_scan = (RelativeLayout) contentView.findViewById(R.id.re_scan);
        re_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CaptureActivity.class);
                context.startActivity(intent);
                AddPopWindow.this.dismiss();
            }
        });

        RelativeLayout re_image_search = (RelativeLayout) contentView.findViewById(R.id.re_image_search);
        re_image_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = null;
                File dir = new File(FileUtils.getFileStorgeDir(context) + "/" + "searchimage");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
                file = new File(dir, "image2Search" + ".jpg");
                Uri uri = Uri.fromFile(file);
                // 调用相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 如果设置uri,则不能获得缩略图，原图保存在uri和相册中，如果不设置uri，则获得缩略图，并且图片保存在相册中
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                context.startActivityForResult(intent, Constants.CAMERA_REQUEST_CODE);
                AddPopWindow.this.dismiss();
            }


        });

    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }
}
