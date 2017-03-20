package com.wisdomrouter.app.tools;

import com.android.volley.Response;
import com.android.volley.VolleyError;


/**
 * ����� �ɹ��ļ�����ʧ�ܵļ���
 * �����ص���Ϣ
 *
 * @param <T>
 * @author yuan
 */

public abstract class VolleyHandler<T> {


    public Response.Listener<T> reqLis;
    public Response.ErrorListener reqErr;

    public VolleyHandler() {
        // ��ʼ�� ����
        reqLis = new reqListener();
        reqErr = new reqErrorListener();

    }

    public abstract void reqSuccess(T response);

    public abstract void reqError(String error);

    /**
     * �ɹ���ļ���
     *
     * @author yuan
     */
    public class reqListener implements Response.Listener<T> {

        @Override
        public void onResponse(T response) {
            // ʹ�ó����� ���� �ص����� reqSuccess
            reqSuccess(response);
        }
    }

    /**
     * ʧ�ܺ�ļ���
     *
     * @author yuan
     */
    public class reqErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            // ���ûص����� ʹ�� ���󷽷� ReqError
            reqError(error.getMessage());
        }

    }

}
