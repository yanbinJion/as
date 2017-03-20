package com.wisdomrouter.app.fragment.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;

import java.io.Serializable;
import java.util.List;

/**
 * appconfig
 */
public class AppConfigBean implements Serializable{
    @Expose
    private String id;
    @Expose
    private String config_hash;
    @Expose
    private String state;
    @Expose
    private String client_name;
    @Expose
    private String api_root;
    @Expose
    private String api_prefix;
    @Expose
    private String platform;
    @Expose
    private String os;
    @Expose
    private String version;
    @Expose
    private String versioncode;
    @Expose
    private String versionlogs;
    @Expose
    private String versionpath;
    @Expose
    private String force_update;
    @Expose
    private String message;
    @Expose
    private List<Module> app_main_btns;
    @Expose
    private List<Module> modules;
    @Expose
    @SerializedName("index-ads")
    private List<Indexads> indexads;
    @Expose
    @SerializedName("content-ads")
    private List<Contentads> contentads;

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public List<Indexads> getIndexads() {
        return indexads;
    }

    public void setIndexads(List<Indexads> indexads) {
        this.indexads = indexads;
    }

    public List<Contentads> getContentads() {
        return contentads;
    }

    public void setContentads(List<Contentads> contentads) {
        this.contentads = contentads;
    }

    public String getConfig_hash() {
        return config_hash;
    }

    public void setConfig_hash(String config_hash) {
        this.config_hash = config_hash;
    }

    public List<Module> getApp_main_btns() {
        return app_main_btns;
    }

    public void setApp_main_btns(List<Module> app_main_btns) {
        this.app_main_btns = app_main_btns;
    }


    public class Module {
        @Expose
        private String app_btn_name;
        @Expose
        private String app_btn_icon;
        @Expose
        private String app_btn_click_icon;
        @Expose
        @SerializedName("module-key")
        private String modulekey;
        @Expose
        @SerializedName("class")
        private String classname;
        @Expose
        @SerializedName("list-api")
        private String listapi;
        @Expose
        @SerializedName("content-api")
        private String contentapi;
        @Expose
        private String title;
        @Expose
        private String listmodel;
        @Expose
        private Plusdata plusdata;
        @Expose
        private List<Channel> channel;
        @Expose
        private List<MediaMoudels> media_modules;


        public List<Channel> getChannel() {
            return channel;
        }

        public void setChannel(List<Channel> channel) {
            this.channel = channel;
        }

        public String getModulekey() {
            return modulekey;
        }

        public void setModulekey(String modulekey) {
            this.modulekey = modulekey;
        }

        public String getClassname() {
            return classname;
        }

        public void setClassname(String classname) {
            this.classname = classname;
        }

        public String getListapi() {

            return listapi;
        }

        public void setListapi(String listapi) {
            this.listapi = listapi;
        }

        public String getContentapi() {
            return contentapi;
        }

        public void setContentapi(String contentapi) {
            this.contentapi = contentapi;
        }

        public String getListmodel() {
            return listmodel;
        }

        public void setListmodel(String listmodel) {
            this.listmodel = listmodel;
        }

        public Plusdata getPlusdata() {
            return plusdata;
        }

        public void setPlusdata(Plusdata plusdata) {
            this.plusdata = plusdata;
        }


        public List<MediaMoudels> getMedia_modules() {
            return media_modules;
        }

        public void setMedia_modules(List<MediaMoudels> media_modules) {
            this.media_modules = media_modules;
        }

        public String getApp_btn_icon() {
            return app_btn_icon;
        }

        public void setApp_btn_icon(String app_btn_icon) {
            this.app_btn_icon = app_btn_icon;
        }

        public String getApp_btn_name() {
            return app_btn_name;
        }

        public void setApp_btn_name(String app_btn_name) {
            this.app_btn_name = app_btn_name;
        }

        public String getApp_btn_click_icon() {
            return app_btn_click_icon;
        }

        public void setApp_btn_click_icon(String app_btn_click_icon) {
            this.app_btn_click_icon = app_btn_click_icon;
        }


        public class Channel {
            @Expose
            private String key;
            @Expose
            private String name;
            @Expose
            private String focus_map;
            @Expose
            private String indexpic;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getFocus_map() {
                return focus_map;
            }

            public void setFocus_map(String focus_map) {
                this.focus_map = focus_map;
            }

            public String getIndexpic() {
                return indexpic;
            }

            public void setIndexpic(String indexpic) {
                this.indexpic = indexpic;
            }
        }



        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }





    }

    public class Plusdata {
        @Expose
        private String link;
        @Expose
        private String linkmode;
        @Expose
        private String plugin_source;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getLinkmode() {
            return linkmode;
        }

        public void setLinkmode(String linkmode) {
            this.linkmode = linkmode;
        }

        public String getPlugin_source() {
            return plugin_source;
        }

        public void setPlugin_source(String plugin_source) {
            this.plugin_source = plugin_source;
        }
    }



    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getApi_prefix() {
        if (Const.APP_DEBUG){
            return "http://open.rmt.test.routeryuncs.com/app";
        }
        return api_prefix;
    }

    public void setApi_prefix(String api_prefix) {
        this.api_prefix = api_prefix;
    }

    public String getPlatform() {
        return platform;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVersion() {
        return version;
    }

    public String getApi_root() {
        if (Const.APP_DEBUG){
            return "http://open.rmt.test.routeryuncs.com";
        }
        return api_root;
    }

    public void setApi_root(String api_root) {
        this.api_root = api_root;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(String versioncode) {
        this.versioncode = versioncode;
    }

    public String getVersionlogs() {
        return versionlogs;
    }

    public void setVersionlogs(String versionlogs) {
        this.versionlogs = versionlogs;
    }

    public String getVersionpath() {
        return versionpath;
    }

    public void setVersionpath(String versionpath) {
        this.versionpath = versionpath;
    }

    public String getForce_update() {
        return force_update;
    }

    public void setForce_update(String force_update) {
        this.force_update = force_update;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Indexads {
        @Expose
        private String url;
        @Expose
        private String image;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

    }

    public class Contentads {
        @Expose
        private String url;
        @Expose
        private String image;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

    public class MediaMoudels {
        @Expose
        @SerializedName("module-key")
        private String modulekey;
        @Expose
        @SerializedName("class")
        private String classname;
        @Expose
        private String title;
        @Expose
        private List<Module.Channel> channel;


//        public List<MediaChannel> getChannel() {
//            return channel;
//        }
//
//        public void setChannel(List<MediaChannel> channel) {
//            this.channel = channel;
//        }


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getClassname() {
            return classname;
        }

        public void setClassname(String classname) {
            this.classname = classname;
        }

        public String getModulekey() {
            return modulekey;
        }

        public void setModulekey(String modulekey) {
            this.modulekey = modulekey;
        }

        public List<Module.Channel> getChannel() {
            return channel;
        }

        public void setChannel(List<Module.Channel> channel) {
            this.channel = channel;
        }
    }
    public class MediaChannel {
        @Expose
        private String key;
        @Expose
        private String name;
        @Expose
        private String focus_map;
        @Expose
        private String indexpic;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFocus_map() {
            return focus_map;
        }

        public void setFocus_map(String focus_map) {
            this.focus_map = focus_map;
        }

        public String getIndexpic() {
            return indexpic;
        }

        public void setIndexpic(String indexpic) {
            this.indexpic = indexpic;
        }
    }

//    public class MediaPlusdata
//    {
//        String link;
//        String linkmode;
//        String plugin_source;
//
//        public String getLink() {
//            return link;
//        }
//
//        public void setLink(String link) {
//            this.link = link;
//        }
//
//        public String getLinkmode() {
//            return linkmode;
//        }
//
//        public void setLinkmode(String linkmode) {
//            this.linkmode = linkmode;
//        }
//
//        public String getPlugin_source() {
//            return plugin_source;
//        }
//
//        public void setPlugin_source(String plugin_source) {
//            this.plugin_source = plugin_source;
//        }
//    }

}
