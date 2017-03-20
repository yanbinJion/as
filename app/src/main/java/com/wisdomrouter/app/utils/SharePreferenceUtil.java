package com.wisdomrouter.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.bean.AppConfigDao;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.fragment.bean.UserDao.Userinfo;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class SharePreferenceUtil {
    public static final String CITY_SHAREPRE_FILE = "city";
    public static final String LIGHTNESS = "lightness";// 调整之后的系统亮度
    private static final String SHARED_APP_CONFIG = "shared_app_config";// 读取系统配置文件
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    public Gson gson = new Gson();

    public SharePreferenceUtil(Context context, String file) {
        sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    // 查询字符窜
    public static void setSearchStr(String[] searchStr) {
        if (searchStr != null & searchStr.length > 0) {
            for (int i = 0; i < searchStr.length; i++) {
                editor.putString("search0" + i, searchStr[i]).commit();
            }
        }

    }

    // 查询字符窜
    @SuppressWarnings("null")
    public static String[] GetSearchStr() {
        String[] searchStr = null;
        for (int i = 0; i < 6; i++) {
            if (!sp.getString("search0" + i, "").equals("")) {
                searchStr[i] = sp.getString("search0" + i, "");
            } else {
                break;
            }
        }

        return searchStr;
    }

    // Appconfig
    public static void setAppconfig(String shared_app_config) {
        editor.putString(SHARED_APP_CONFIG, shared_app_config);
        editor.commit();
    }


    // wifi显示图片
    public void setIsShowImg(Boolean IsShowImg) {
        editor.putBoolean("IsShowImg", IsShowImg);
        editor.commit();
    }

    public Boolean getIsShowImg() {
        return sp.getBoolean("IsShowImg", true);

    }


    // 个人信息的保存
    public void saveAccount(Userinfo user) {
        if (user != null && !"".equals(user)) {
            editor.putString("openid", user.getOpenid()).commit();
            editor.putString("likename", user.getLikename()).commit();
            editor.putString("password", user.getPassword()).commit();
            editor.putString("email", user.getEmail()).commit();
            editor.putString("mobile", user.getMobile()).commit();
            editor.putString("facepic", user.getFacepic()).commit();
            editor.putString("promoter_complete", user.getPromoter_complete()).commit();
            editor.putString("usergroup", gson.toJson(user.getUsergroup())).commit();
            editor.putInt("is_sign", user.getIs_sign()).commit();
        } else {
            editor.putString("openid", null).commit();
            editor.putString("likename", null).commit();
            editor.putString("password", null).commit();
            editor.putString("email", null).commit();
            editor.putString("mobile", null).commit();
            editor.putString("facepic", null).commit();
            editor.putString("usergroup", null).commit();
            editor.putString("promoter_complete", "0").commit();
            editor.putInt("is_sign", 0).commit();
        }

    }

    // 个人登录信息的获取
    public Userinfo getAccount() {
        Userinfo user = new UserDao().new Userinfo();
        user.setOpenid(sp.getString("openid", ""));
        user.setLikename(sp.getString("likename", ""));
        user.setPassword(sp.getString("password", ""));
        user.setEmail(sp.getString("email", ""));
        user.setMobile(sp.getString("mobile", ""));
        user.setFacepic(sp.getString("facepic", ""));
        user.setPromoter_complete(sp.getString("promoter_complete", "0"));
        user.setIs_sign(sp.getInt("is_sign",0));
        String usergroup = sp.getString("usergroup", "");
        if (!usergroup.isEmpty()) {
            List<Userinfo.UserGroup> groups = new Gson().fromJson(usergroup,
                    new TypeToken<List<Userinfo.UserGroup>>() {
                    }.getType());
            user.setUsergroup(groups);
        }
        return user;
    }

    // 保存已读栏目
    public void saveReadcolumn(String newscolumn) {

        editor.putString("readcolumns2", newscolumn).commit();
        editor.commit();
    }

    // 读取已读栏目
    public List<AppConfigBean.Module.Channel> getReadcolumn() {
        return new Gson().fromJson(sp.getString("readcolumns2", "[]"),
                new TypeToken<List<AppConfigBean.Module.Channel>>() {
                }.getType());


    }

    // 保存未读栏目
    public void saveUnReadcolumn(String newscolumn) {

        editor.putString("unreadcolumns2", newscolumn).commit();
        editor.commit();
    }

    // 读取未读栏目
    public List<AppConfigBean.Module.Channel> getUnReadcolumn() {
        return new Gson().fromJson(sp.getString("unreadcolumns2", "[]"),
                new TypeToken<List<AppConfigBean.Module.Channel>>() {
                }.getType());

    }
    /**
     * 保存配置string
     * @return
     */
    public void saveAppConfigStr(String configStr) {
        editor.putString("configStr", configStr).commit();
        editor.commit();
    }

    /**
     * login password string
     * @return
     */
    public String getLoginPassword() {
        return sp.getString("password", "");
    }
    /**
     * login password string
     * @return
     */
    public void saveLoginPassword(String password) {
        editor.putString("password", password).commit();
        editor.commit();
    }

    /**
     * 读取配置
     * @return
     */
    public AppConfigBean getAppConfigBean() {
        return new Gson().fromJson(sp.getString("configStr", ""), AppConfigBean.class);
    }


    /**
     * 去重图片视频
     *
     * @param appConfigDao
     * @return
     */
    public static AppConfigDao quchong(AppConfigDao appConfigDao) {
        List<AppConfigDao.Modules> modulesList = appConfigDao.getListModules();
        List<AppConfigDao.Modules> modulesListnew = new ArrayList<>();
        List<AppConfigDao.Modules> mideaList = new ArrayList<>();
        List<AppConfigDao.Modules> ImagesList = new ArrayList<>();
        List<AppConfigDao.Modules> VideoList = new ArrayList<>();
        for (int i = 0; i < modulesList.size(); i++) {
            if (modulesList.get(i).getClassname().equals("media")) {
                mideaList.add(modulesList.get(i));
            } else if (modulesList.get(i).getClassname().equals("images")) {
                ImagesList.add(modulesList.get(i));
            } else if (modulesList.get(i).getClassname().equals("video")) {
                VideoList.add(modulesList.get(i));
            }
        }
        for (int i = 0; i < ImagesList.size(); i++) {//去除image中的重复的
            for (int j = 0; j < mideaList.size(); j++) {
                int length = mideaList.get(j).getListMediamodules().size();
                for (int k = 0; k < length; k++) {
                    if (ImagesList.get(i).getModulekey().equals(mideaList.get(j).getListMediamodules().get(k).getModulekey())) {
                        modulesListnew.add(ImagesList.get(i));
                    }
                }
            }
        }
        for (int i = 0; i < VideoList.size(); i++) {//去除video中的重复的
            for (int j = 0; j < mideaList.size(); j++) {
                int length = mideaList.get(j).getListMediamodules().size();
                for (int k = 0; k < length; k++) {
                    if (VideoList.get(i).getModulekey().equals(mideaList.get(j).getListMediamodules().get(k).getModulekey())) {
                        modulesListnew.add(VideoList.get(i));
                    }
                }

            }
        }
        modulesList.removeAll(modulesListnew);
        appConfigDao.setListModules(modulesList);
        return appConfigDao;
    }
    //清除标题缓存
    public static void cleartitle(){
        editor.remove("readcolumns2");
        editor.remove("unreadcolumns2");
        editor.commit();
    }
}
