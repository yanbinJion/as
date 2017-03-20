package com.wisdomrouter.app.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.message.proguard.T;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.bean.AppConfigDao;
import com.wisdomrouter.app.fragment.bean.ArticleContentDao;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.fragment.bean.CommError;
import com.wisdomrouter.app.fragment.bean.CommentDao;
import com.wisdomrouter.app.fragment.bean.CountryListDao;
import com.wisdomrouter.app.fragment.bean.CustomDao;
import com.wisdomrouter.app.fragment.bean.FocusDao;
import com.wisdomrouter.app.fragment.bean.HomeListDao;
import com.wisdomrouter.app.fragment.bean.ImageContentDao;
import com.wisdomrouter.app.fragment.bean.LiveListDao;
import com.wisdomrouter.app.fragment.bean.MyFavouriteDao;
import com.wisdomrouter.app.fragment.bean.NewArticleListDao;
import com.wisdomrouter.app.fragment.bean.ProjectListDao;
import com.wisdomrouter.app.fragment.bean.ResultDao;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.fragment.bean.VoteDao;
import com.wisdomrouter.app.tools.VolleyUtils.GsonRequestGet;
import com.wisdomrouter.app.tools.VolleyUtils.MyVolley;
import com.wisdomrouter.app.tools.VolleyUtils.StrErrListener;
import com.wisdomrouter.app.utils.DomParseService;
import com.wisdomrouter.app.utils.HttpUtil;
import com.wisdomrouter.app.utils.MD5Util;
import com.wisdomrouter.app.utils.SharePreferenceUtil;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.WarnUtils;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.password;

@SuppressLint("NewApi")
public class GlobalTools {
    private Context mContext;
    private static Gson gson = null;
    private static boolean netFlag = false; // 判断是否获取数据

    public GlobalTools(Context context) {
        this.mContext = context;
        this.gson = new Gson();
    }

    /**
     * 获取直播列表
     *
     * @param
     * @return
     * @throws Exception
     */
    public List<ArticleListDao> getzbList(String list_api, int page,
                                          int pageSize) {
        List<ArticleListDao> voArticle = new ArrayList<ArticleListDao>();
        String url = list_api + "?page=" + page + "&pagesize=" + pageSize;
        String strFromServer = null;
        try {
            netFlag = true;
            strFromServer = HttpUtil.queryStringForGet(url);
        } catch (Exception e) {
            netFlag = false;
        }
        if (netFlag == true && strFromServer != null
                && !strFromServer.contains("state")
                && !strFromServer.equals("{}")) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voArticle = gson.fromJson(strFromServer,
                    new TypeToken<List<ArticleListDao>>() {
                    }.getType());
        } else if (strFromServer.contains("state")) {
            CommError commError = new CommError();
            commError = gson.fromJson(strFromServer, CommError.class);
            ArticleListDao articleListDao = new ArticleListDao();
            articleListDao.setState(commError.getState() + "");
            articleListDao.setMessage(commError.getMessage());
            voArticle.add(0, articleListDao);
        }
        return voArticle;
    }

    /**
     * 获取发现列表
     *
     * @param
     * @return
     * @throws Exception
     */
    public NewArticleListDao getFindList() {
        NewArticleListDao voArticle = new NewArticleListDao();
        String url = Const.HTTP_HEADKZ + "/plugin/activity-api/getlist";
        String strFromServer = null;
        try {
            netFlag = true;
            strFromServer = HttpUtil.queryStringForGet(url);
        } catch (Exception e) {
            netFlag = false;
        }
        if (netFlag == true && strFromServer != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voArticle = gson.fromJson(strFromServer, NewArticleListDao.class);
        }
        return voArticle;
    }

    /**
     * 获取专题列表
     *
     * @param
     * @return
     * @throws Exception
     */
    public List<ArticleListDao> getProjectList(String key, String list_api,
                                               String page, String pagesize) {
        List<ArticleListDao> voArticle = new ArrayList<ArticleListDao>();
        String url = Const.HTTP_HEADKZ + "/plugin/juhe-api/ztlist?type=1";
        String strFromServer = null;
        try {
            netFlag = true;
            strFromServer = HttpUtil.queryStringForGet(url);
        } catch (Exception e) {
            netFlag = false;
        }
        if (netFlag == true && strFromServer != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voArticle = gson.fromJson(strFromServer,
                    new TypeToken<List<ArticleListDao>>() {
                    }.getType());
        }
        return voArticle;
    }

    /**
     * 获取新闻列表,老版 图片列表 视频列表
     *
     * @param key
     * @return
     * @throws Exception
     */
    public List<ArticleListDao> getArticleList(String key, String list_api,
                                               String page, String pagesize) {
        List<ArticleListDao> voArticle = new ArrayList<ArticleListDao>();
        String url = Const.HTTP_HEAD + list_api + "?key=" + key + "&page="
                + page + "&pagesize=" + pagesize;
        String strFromServer = null;
        try {
            netFlag = true;
            strFromServer = HttpUtil.queryStringForGet(url);
        } catch (Exception e) {
            netFlag = false;
        }
        if (netFlag == true && strFromServer != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voArticle = gson.fromJson(strFromServer,
                    new TypeToken<List<ArticleListDao>>() {
                    }.getType());
        }
        return voArticle;
    }

    /**
     * 获取新闻列表 新版样式
     *
     * @param key
     * @return
     * @throws Exception
     */
    public NewArticleListDao getArticleListNew(String key, String list_api,
                                               String page, String pagesize) {
        NewArticleListDao voArticle = new NewArticleListDao();
        String url = Const.HTTP_HEAD + list_api + "?key=" + key + "&page="
                + page + "&pagesize=" + pagesize + "&newdata=1";
        String strFromServer = null;
        try {
            netFlag = true;
            strFromServer = HttpUtil.queryStringForGet(url);
        } catch (Exception e) {
            netFlag = false;
        }
        if (netFlag == true && strFromServer != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voArticle = gson.fromJson(strFromServer, NewArticleListDao.class);
        }
        return voArticle;
    }

    /**
     * 获取新闻内容
     *
     * @param
     * @return
     * @throws Exception
     */
    public ArticleContentDao getNewsById(String key, String content_api) {
        ArticleContentDao voArticle = null;
        String url;
        if (HandApplication.user != null
                && HandApplication.user.getOpenid() != null
                && !StringUtil.isEmpty(HandApplication.user.getOpenid())) {
            url = Const.HTTP_HEAD + content_api + "?key=" + key
                    + "&user_openid=" + HandApplication.user.getOpenid();
        } else {
            url = Const.HTTP_HEAD + content_api + "?key=" + key;
        }
        String strFromServer = null;
        strFromServer = HttpUtil.queryStringForGet(url);
        netFlag = true;
        if (netFlag == true && strFromServer != null
                && !strFromServer.equals("{}")) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voArticle = gson.fromJson(strFromServer, ArticleContentDao.class);
        }
        return voArticle;
    }

    /**
     * @param key
     * @param content_api /imag/list
     * @return
     * @description: 获取图集的内容页
     * @author:wangfanghui
     * @return:ArticleContentDao
     */
    public ImageContentDao getImageByKey(String key, String content_api) {
        ImageContentDao voArticle = null;
        if (Const.HTTP_HEAD == null) {
            Const.HTTP_HEAD = "http://open.tmtsp.com/app";
        }
        String url;
        if (HandApplication.user != null
                && !HandApplication.user.getOpenid().isEmpty()) {
            url = Const.HTTP_HEAD + content_api + "?key=" + key
                    + "&user_openid=" + HandApplication.user.getOpenid();
        } else {
            url = Const.HTTP_HEAD + content_api + "?key=" + key;
        }
        String strFromServer = null;
        strFromServer = HttpUtil.queryStringForGet(url);
        netFlag = true;
        if (netFlag == true && strFromServer != null
                && !strFromServer.equals("{}")) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voArticle = gson.fromJson(strFromServer, ImageContentDao.class);
        }
        return voArticle;
    }

    /**
     * @return
     * @throws Exception
     * @description:注册
     * @author:wangfanghui
     * @return:UserDao
     */
    public UserDao Register(String likename, String password, String email,
                            String mobile, String sex) throws Exception {
        String url = Const.HTTP_HEAD + "/user/reg";

        String res = null;
        try {
            Map<String, String> map = new HashMap<String, String>();
            map.put("likename", likename);
            map.put("password", password);
            map.put("email", email);
            map.put("mobile", mobile);
            map.put("sex", sex);
            map.put("client", Const.APPSHAREID);
            res = HttpUtil.doPost(url, map);

        } catch (Exception e) {
            throw new Exception("网络错误");
        }

        UserDao voUser = null;
        if (res != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voUser = gson.fromJson(res, UserDao.class);
        }
        return voUser;
    }

    /**
     * 登录
     *
     * @param sinaid
     * @param password
     * @return
     * @throws Exception
     */
    public UserDao LoginBqxx(String sinaid, String sinaname, String mobile,
                             String password, String email, String facepic) {
        String url = Const.HTTP_HEAD + "/user/twologin";
        String res = null;
        Map<String, String> map = new HashMap<>();
        map.put("sinaid", sinaid);
        map.put("sinaname", sinaname);
        map.put("mobile", mobile);
        map.put("password", MD5Util.md5Encode(password));
        map.put("facepic", facepic);
        if (!email.isEmpty()) {
            map.put("email", email);
        }

        try {
            res = HttpUtil.doPost(url, map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserDao voUser = null;
        if (res != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voUser = gson.fromJson(res, UserDao.class);
        }
        return voUser;
    }


    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public UserDao Login(String username, String password) {
        String url = Const.HTTP_HEAD + "/user/login?username=" + username
                + "&password=" + password + "&client=" + Const.APPSHAREID;

        String res = null;

        res = HttpUtil.queryStringForGet(url);

        UserDao voUser = null;
        if (res != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voUser = gson.fromJson(res, UserDao.class);
        }
        return voUser;
    }

    /**
     * 登录
     *
     * @param sinaId
     * @param sinaName
     * @return
     * @throws Exception
     */
    public UserDao sinaLogin(String sinaId, String sinaName) {

        // String url = Const.HTTP_HEAD + "/user/onelogin?sinaid=" + sinaId
        // + "&sinaname=" + sinaName;
        Map<String, Object> params = new HashMap<>();
        params.put("sinaid", sinaId);
        params.put("sinaname", sinaName);
        params.put("client", Const.APPSHAREID);

        String url = Const.HTTP_HEAD + "/user/onelogin?" + urlencode(params);
        String res = null;
        res = HttpUtil.queryStringForGet(url);

        UserDao voUser = null;
        if (res != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voUser = gson.fromJson(res, UserDao.class);
        }
        return voUser;
    }

    // 将map型转为请求参数型
    public static String urlencode(Map<String, ?> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ?> i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=")
                        .append(URLEncoder.encode(i.getValue() + "", "UTF-8"))
                        .append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 重置密码
     *
     * @param phone
     * @param password
     * @return
     * @throws Exception
     */
    public UserDao ResetPass(String phone, String password) {
        String url = Const.HTTP_HEAD + "/user/findpwd?mobile=" + phone
                + "&password=" + password + "";

        String res = null;

        res = HttpUtil.queryStringForGet(url);

        UserDao voUser = null;
        if (res != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voUser = gson.fromJson(res, UserDao.class);
        }
        return voUser;
    }

    /**
     * @param key
     * @return
     * @description: 登录
     * @author:wangfanghui
     * @return:UserDao
     */
    public UserDao LoginGetkey(String key) {
        String url = Const.HTTP_HEAD + "/user/access?key=" + key + "";

        String res = null;

        res = HttpUtil.queryStringForGet(url);

        UserDao voUser = null;
        if (res != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voUser = gson.fromJson(res, UserDao.class);
        }
        return voUser;
    }


    /**
     * @param userId
     * @param page
     * @param pagesize
     * @return
     * @throws Exception
     * @description:收藏列表
     * @author:wangfanghui
     * @return:List<MyFavouriteDao>
     */
    public List<MyFavouriteDao> getFavourListByUserId(String userId,
                                                      String page, String pagesize) throws Exception {
        List<MyFavouriteDao> voGlobal = null;
        String url = Const.HTTP_HEADKZ
                + "/plugin/readactive-api/favorites?openid=" + userId
                + "&page=" + page + "&pagesize=" + pagesize;
        String strFromServer = null;
        try {
            strFromServer = HttpUtil.queryStringForGet(url);
            netFlag = true;
        } catch (Exception e) {
            WarnUtils.toast(mContext, R.string.toast_webexception);
            netFlag = false;
            e.printStackTrace();
        }
        if (netFlag == true && strFromServer != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voGlobal = gson.fromJson(strFromServer,
                    new TypeToken<List<MyFavouriteDao>>() {
                    }.getType());
        }
        return voGlobal;
    }

    /**
     * 提交收藏
     *
     * @param userid
     * @param infoid
     * @return
     * @throws Exception
     */
    public String setFavourByUserIdAndInfoId(String userid, String infoid,
                                             String classtype, String isCollect) throws Exception {
        ResultDao voResult = null;
        String url;
        if (isCollect.equals(Const.NOCOLLECT)) {
            url = Const.HTTP_HEADKZ
                    + "/plugin/readactive-api/uncollecting?openid=" + userid
                    + "&key=" + infoid + "&class=" + classtype;
        } else
            url = Const.HTTP_HEADKZ
                    + "/plugin/readactive-api/collecting?openid=" + userid
                    + "&key=" + infoid + "&class=" + classtype;
        String strFromServer = null;
        try {
            strFromServer = HttpUtil.queryStringForGet(url);
            netFlag = true;
        } catch (Exception e) {
            WarnUtils.toast(mContext, R.string.toast_webexception);
            netFlag = false;
            e.printStackTrace();
        }
        if (netFlag == true && strFromServer != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voResult = gson.fromJson(strFromServer, ResultDao.class);
        }

        String result = null;
        if (voResult.getState() != null && voResult.getMessage() != null) {
            result = voResult.getMessage();
        }
        return result;
    }

    /**
     * 提交赞
     *
     * @param userid
     * @param infoid
     * @return
     * @throws Exception
     */
    public String setDiggByUserIdAndInfoId(String userid, String infoid,
                                           String classType) throws Exception {
        ResultDao voResult = null;
        String url = Const.HTTP_HEADKZ
                + "/plugin/readactive-api/like?openid=" + userid + "&key="
                + infoid + "&class=" + classType;
        String strFromServer = null;
        try {
            strFromServer = HttpUtil.queryStringForGet(url);
            netFlag = true;
        } catch (Exception e) {
            WarnUtils.toast(mContext, R.string.toast_webexception);
            netFlag = false;
            e.printStackTrace();
        }
        if (netFlag == true && strFromServer != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voResult = gson.fromJson(strFromServer, ResultDao.class);
        }

        String result = null;
        if (voResult.getState() != null && voResult.getMessage() != null) {
            result = voResult.getMessage();
        }
        return result;
    }

    /**
     * @param flag
     * @param id
     * @param info_class
     * @param page
     * @param pagesize
     * @return
     * @throws Exception
     * @description:获取评论列表
     * @author:wangfanghui
     * @return:List<CommentDao>
     */
    public List<CommentDao> getCommentByIdOrinfokey(boolean flag, String id,
                                                    String info_class, String page, String pagesize) throws Exception {
        List<CommentDao> voGlobal = null;
        String url = null;
        // flag = true为某用户所有评论， flag = false 为单个子项评论
        if (flag)
            url = Const.HTTP_HEADKZ
                    + "/plugin/comment-api/my?user_openid=" + id + "&page="
                    + page + "&pagesize=" + pagesize;
        else
            url = Const.HTTP_HEADKZ
                    + "/plugin/comment-api/list?info_key=" + id
                    + "&info_class=" + info_class + "&page=" + page
                    + "&pagesize=" + pagesize;

        String strFromServer = null;
        try {
            strFromServer = HttpUtil.queryStringForGet(url);
            netFlag = true;
        } catch (Exception e) {
            WarnUtils.toast(mContext, R.string.toast_webexception);
            netFlag = false;
            e.printStackTrace();
        }
        if (netFlag == true && strFromServer != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voGlobal = gson.fromJson(strFromServer,
                    new TypeToken<List<CommentDao>>() {
                    }.getType());
        }
        return voGlobal;
    }

    /**
     * 对内容进行评论
     *
     * @return
     * @throws Exception
     */
    public String ToComment(String info_key, String content, String userId)
            throws Exception {
        String url = Const.HTTP_HEADKZ
                + "/plugin/comment-api/post";

        String res = null;
        try {
            Map<String, String> map = new HashMap<String, String>();

            map.put("user_openid", userId);
            map.put("info_key", info_key);
            map.put("content", content);

            res = HttpUtil.doPost(url, map);

        } catch (Exception e) {
            throw new Exception("网络错误");
        }

        ResultDao voResult = null;
        if (res != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voResult = gson.fromJson(res, ResultDao.class);
        }

        String result = null;
        if (voResult.getState() != null && voResult.getMessage() != null) {
            if (voResult.getState().equals("1")) {
                result = voResult.getMessage();
            }
            result = voResult.getMessage();
        }
        return result;
    }
    /**
     * 对内容进行评论
     *
     * @return
     * @throws Exception
     */
    public ResultDao ToComment2(String info_key, String content, String userId)
            throws Exception {
        String url = Const.HTTP_HEADKZ
                + "/plugin/comment-api/post";

        String res = null;
        try {
            Map<String, String> map = new HashMap<String, String>();

            map.put("user_openid", userId);
            map.put("info_key", info_key);
            map.put("content", content);

            res = HttpUtil.doPost(url, map);

        } catch (Exception e) {
            throw new Exception("网络错误");
        }

        ResultDao voResult = null;
        if (res != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voResult = gson.fromJson(res, ResultDao.class);
        }
        return voResult;
    }

    /**
     * 搜索
     *
     * @param key
     * @return
     * @throws Exception
     */
    public List<ArticleListDao> Search(String key, int page) throws Exception {
        List<ArticleListDao> voArticle = null;
        String url = Const.HTTP_HEADKZ
                + "/plugin/basesearch-api/search?keyword=" + key + "&page="
                + page;
        String strFromServer = null;
        try {
            strFromServer = HttpUtil.queryStringForGet(url);
            netFlag = true;
        } catch (Exception e) {
            WarnUtils.toast(mContext, R.string.toast_webexception);
            netFlag = false;
            e.printStackTrace();
        }
        if (netFlag == true && strFromServer != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            voArticle = gson.fromJson(strFromServer,
                    new TypeToken<List<ArticleListDao>>() {
                    }.getType());
        }
        return voArticle;
    }


    /**
     * 获取app配置信息
     *
     * @return
     * @throws Exception
     */
    public AppConfigDao getAppConfig() {
        AppConfigDao appConfig = null;
        String url = Const.HTTP_CONFIG;
        String strFromServer = null;
        try {
            strFromServer = HttpUtil.queryStringForGet(url);
            netFlag = true;
        } catch (Exception e) {
            WarnUtils.toast(mContext, R.string.toast_webexception);
            netFlag = false;
            e.printStackTrace();
        }
        if (netFlag == true && strFromServer != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            if (strFromServer.contains("message")) {
                appConfig = gson.fromJson(strFromServer, AppConfigDao.class);
            } else if (strFromServer.contains("网络异常")) {
                appConfig.setState("0");
                appConfig.setMessage("网络异常");
            } else {
//                strFromServer=strFromServer.replaceAll("class","classname");
//                strFromServer=strFromServer.replaceAll("-","");
//
//                AppConfigBean  appConfig2=new Gson().fromJson(strFromServer,AppConfigBean.class);

                ByteArrayInputStream stream = new ByteArrayInputStream(
                        strFromServer.getBytes());
                try {
                    appConfig = DomParseService.getAppconfigByParseXml(stream);
                    //去重
                    appConfig = quchong(appConfig);
//                    HandApplication.getInstance().setConfig(appConfig);
                    SharePreferenceUtil.setAppconfig(strFromServer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return appConfig;

        }
        return appConfig;
    }


    /**
     * 去重图片视频
     *
     * @param appConfigDao
     * @return
     */
    private AppConfigDao quchong(AppConfigDao appConfigDao) {
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

    /**
     * 获取投票排行列表
     *
     * @return
     * @throws Exception
     */
    public List<VoteDao> getVotePhList(String key) throws Exception {
        List<VoteDao> list = null;
        String url = Const.HTTP_HEADKZ + "/plugin/vote2-api/ranking?key=" + key;
        String strFromServer = null;
        try {
            strFromServer = HttpUtil.queryStringForGet(url);
            netFlag = true;
        } catch (Exception e) {
            WarnUtils.toast(mContext, R.string.toast_webexception);
            netFlag = false;
            e.printStackTrace();
        }
        if (netFlag == true && strFromServer != null) {
            // 构造gson, 把服务器端传过来的 json 字符串转化为 java 对象
            list = gson.fromJson(strFromServer, new TypeToken<List<VoteDao>>() {
            }.getType());
        }
        return list;
    }

    /**
     * 镇区
     *
     * @param context
     * @param listener
     * @param errListener
     */
    public static void getCountry(Context context, Response.Listener<List<CountryListDao>> listener, StrErrListener errListener) {
        String url = Const.HTTP_HEADKZ + "/plugin/zq-api/towncountnews";
        Type type = new TypeToken<List<CountryListDao>>() {
        }.getType();
        GsonRequestGet<List<CountryListDao>> gsonRequestGet = new GsonRequestGet<List<CountryListDao>>(url, type, listener, errListener);
        MyVolley.getInstance(context).addToRequestQueue(gsonRequestGet, Const.TOWN);
    }

    /**
     * 镇区新闻
     *
     * @param context
     * @param page
     * @param pagesize
     * @param key
     * @param listener
     * @param errListener
     */
    public static void getCountryNews(Context context, int page, int pagesize, String key, Response.Listener<List<ArticleListDao>> listener, StrErrListener errListener) {
        String url = Const.HTTP_HEADKZ + "/plugin/juhe-api/getlist?key=" + key + "&page=" + page + "&pagesize=" + pagesize;
        Type type = new TypeToken<List<ArticleListDao>>() {
        }.getType();
        GsonRequestGet<List<ArticleListDao>> gsonRequestGet = new GsonRequestGet<>(url, type, listener, errListener);
        MyVolley.getInstance(context).addToRequestQueue(gsonRequestGet, Const.TOWNNEWS);
    }

    /**
     * 专题列表
     *
     * @param context
     * @param listener
     * @param errListener
     */
    public static void getProject(Context context, Response.Listener<List<ProjectListDao>> listener, StrErrListener errListener) {
        String url = Const.HTTP_HEADKZ + "/plugin/special-api/specialcountnews";
        Type type = new TypeToken<List<ProjectListDao>>() {
        }.getType();
        GsonRequestGet<List<ProjectListDao>> gsonRequestGet = new GsonRequestGet<>(url, type, listener, errListener);
        MyVolley.getInstance(context).addToRequestQueue(gsonRequestGet, Const.PROJECT);
    }

    /**
     * 自定义列表
     *
     * @param context
     * @param list_key
     * @param listener
     * @param errListener
     */
    public static void getCustomList(Context context, String list_key, int page, int pagesize, Response.Listener<List<CustomDao>> listener, StrErrListener errListener) {
        String url = Const.HTTP_HEADKZ + "/plugin/customlist-api/newslist?list_key=" + list_key + "&page=" + page + "&pagesize=" + pagesize;
        Type type = new TypeToken<List<CustomDao>>() {
        }.getType();
        GsonRequestGet<List<CustomDao>> gsonRequestGet = new GsonRequestGet<List<CustomDao>>(url, type, listener, errListener);
        MyVolley.getInstance(context).addToRequestQueue(gsonRequestGet, Const.CUSTOM);
    }

    /**
     * 焦点图
     *
     * @param context
     * @param id
     * @param listener
     * @param errListener
     */
    public static void getFocusList(Context context, String id, Response.Listener<List<FocusDao>> listener, StrErrListener errListener) {
        String url = Const.HTTP_HEADKZ + "/plugin/focus-api/contentlist?id=" + id;
        Type type = new TypeToken<List<FocusDao>>() {
        }.getType();
        GsonRequestGet<List<FocusDao>> gsonRequestGet = new GsonRequestGet<List<FocusDao>>(url, type, listener, errListener);
        MyVolley.getInstance(context).addToRequestQueue(gsonRequestGet, Const.FOCUS);
    }

    /**
     * 直播列表
     *
     * @param context
     * @param page
     * @param pageSize
     * @param listener
     * @param errListener
     */
    public static void getLiveList(Context context, int page, int pageSize, Response.Listener<List<LiveListDao>> listener, StrErrListener errListener) {
        String url = Const.HTTP_HEADKZ + "/plugin/weilive-api/list?page=" + page + "&pagesize=" + pageSize;
        Type type = new TypeToken<List<LiveListDao>>() {
        }.getType();
        GsonRequestGet<List<LiveListDao>> gsonRequestGet = new GsonRequestGet<List<LiveListDao>>(url, type, listener, errListener);
        MyVolley.getInstance(context).addToRequestQueue(gsonRequestGet, Const.LIVELIST);
    }

    /**
     * 获取Json 配置信息
     *
     * @param volleyRequest
     */
    public static void getAppconfig(VolleyHandler<String> volleyRequest) {
        VolleyHttpRequest.String_request(Const.HTTP_CONFIG_JSON, volleyRequest);
    }

    /**
     * 获取专题内容列表信息
     *
     * @param volleyRequest
     */
    public static void getProjectContentList(VolleyHandler<String> volleyRequest, String list_key) {
        String url = Const.HTTP_HEADKZ + "/plugin/special-api/contentlist?key=" + list_key;
        VolleyHttpRequest.String_request(url, volleyRequest);
    }

    /**
     * 获取镇区内容列表信息
     *
     * @param volleyRequest
     */
    public static void getCountryContentList(VolleyHandler<String> volleyRequest, String list_key) {
        String url = Const.HTTP_HEADKZ + "/plugin/zq-api/contentlist?key=" + list_key;
//        String url = Const.HTTP_HEADKZ + "/plugin/juhe-api/getlist?key=" + list_key;
        Log.e("url",url);
        VolleyHttpRequest.String_request(url, volleyRequest);
    }

    /**
     * 获取投票主题列表
     *
     * @param volleyRequest
     */
    public static void getVoteList(VolleyHandler<String> volleyRequest, int page, int pagesize) {
        String url = Const.HTTP_HEADKZ + "/plugin/vote2-api/list?page=" + page + "&pagesize=" + pagesize;
        VolleyHttpRequest.String_request(url, volleyRequest);
    }

    /**
     * 获取投票主题内容
     *
     * @param volleyRequest
     */
    public static void getVoteDetail(VolleyHandler<String> volleyRequest, Map<String, String> map) {
        String url = Const.HTTP_HEADKZ + "/plugin/vote2-api/content";
        url = url + "?" + urlencode(map);
        VolleyHttpRequest.String_request(url, volleyRequest);
    }

    /**
     * 获取投票接口
     *
     * @param volleyRequest
     */
    public static void sendVote(VolleyHandler<String> volleyRequest, Map<String, String> map) {
        String url = Const.HTTP_HEADKZ + "/plugin/vote2-api/post2";
        url = url + "?" + urlencode(map);
        VolleyHttpRequest.String_request(url, volleyRequest);
    }

/*
    接口地址：[接口根]/plugin/activity-api/content

    请求类型：get

    参数：

    必须参数：key，活动编号
    可选参数：user_openid，用户标识，用于判断用户是否已经报名*/

    /**
     * 获取活动详情
     *
     * @param volleyRequest
     * @param map
     */
    public static void getActiveDetail(VolleyHandler<String> volleyRequest, Map<String, String> map) {
        String url = Const.HTTP_HEADKZ + "/plugin/activity-api/content";
        url = url + "?" + urlencode(map);
        VolleyHttpRequest.String_request(url, volleyRequest);
    }

    /**
     * 发送验证码
     *
     * @param volleyRequest
     * @param map           接口地址：[接口根]/plugin/sms-api/vcode
     *                      参数：
     *                      必填参数：mobile，手机号，必填
     *                      可选参数：len，验证码长度，支持4位或6位验证码
     */
    public static void getVcode(VolleyHandler<String> volleyRequest, Map<String, String> map) {
        String url = Const.HTTP_HEADKZ + "/plugin/sms-api/vcode";
        url = url + "?" + urlencode(map);
        VolleyHttpRequest.String_request(url, volleyRequest);
    }

    /**
     * 获取积分
     *接口地址：[接口根]/plugin/integral-api/integral-score

     请求类型：get
     必填参数：user_openid， 用户标识；

     * @param volleyRequest
     * @param openid
     */
    public static void getScore(VolleyHandler<String> volleyRequest, String openid) {
        String url = Const.HTTP_HEADKZ + "/plugin/integral-api/integral-score";
        url = url + "?user_openid="+openid;
        VolleyHttpRequest.String_request(url, volleyRequest);
    }

    /**
     * 分享领积分
     *
     * @param volleyRequest
     * @param map
     */
    public static void getShareScore(VolleyHandler<String> volleyRequest,Map<String,String>map) {
        String url = Const.HTTP_HEADKZ + "/plugin/integral-api/integral-share";
        VolleyHttpRequest.String_request(url,map,volleyRequest);
    }
    /**
     * 积分签到
     *接口地址：[接口根]/plugin/integral-api/integral-sign
     *请求类型：post
     *必填参数：user_openid， 用户标识；
     * @param volleyRequest
     * @param map
     */
    public static void getSign(VolleyHandler<String> volleyRequest, Map<String, String> map) {
        String url = Const.HTTP_HEADKZ + "/plugin/integral-api/integral-sign";
//        url = url + "?" +"user_openid="+openid;
        VolleyHttpRequest.String_request(url,map, volleyRequest);
    }
    /**
     * 登录
     *
     * @param volleyRequest
     * @param map
     */
    public static void login(VolleyHandler<String> volleyRequest, Map<String, String> map) {
        String url = Const.HTTP_HEAD + "/user/login";
        url = url + "?" + urlencode(map);
        url=url.substring(0,url.length()-1);
        VolleyHttpRequest.String_request(url, volleyRequest);
    }


}
