package com.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Logan on 2017/2/15.
 */

public class RemoteLoginAlertDialog extends AlertDialog.Builder {
    public RemoteLoginAlertDialog(Context context) {
        super(context);
        setTitle("异地登录")
                .setMessage("你的设备" + DateUtil.getDate() + "在别的设备登录，如果不是你本人操作，请及时修改密码。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        create().dismiss();
                    }
                })
                .create().show();
    }
}
