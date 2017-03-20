package com.wisdomrouter.app.tools.VolleyUtils;

import android.content.Context;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.utils.WarnUtils;

//����ʧ�ܻص�
public class StrErrListener implements ErrorListener {

    private Context mContext;

    public StrErrListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onErrorResponse(VolleyError arg0) {
        String errorMessage = VolleyErrorHelper.getMessage(arg0, mContext);
        if (errorMessage != null) {
            WarnUtils.toast(mContext, errorMessage);
        } else {
            WarnUtils.toast(mContext, mContext.getResources().getString(
                    R.string.generic_server_down));
        }
    }

}