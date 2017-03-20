/**
 * @FILE:AppConfig.java
 * @AUTHOR:baifan
 * @DATE:2015-4-22 下午12:10:55
 **/
package com.wisdomrouter.app.fragment.bean;

import com.wisdomrouter.app.Const;

import java.io.Serializable;
import java.util.List;


/**
 * @COMPANY:南京路特软件有限公司
 * @CLASS:AppConfig
 * @DESCRIPTION:
 * @AUTHOR:wangfanghui
 * @VERSION:v1.0
 * @DATE:2015-4-22 下午12:20:23
 */
public class AppConfigDao{
    private String id;
    private String config_hash;
    private String state;
    private String client_name;
    private String api_root;
    private String api_prefix;
    private String copyright;
    private String platform;
    private String os;
    private String version;
    private String versioncode;
    private String versionlogs;
    private String versionpath;
    private String force_update;
    private String message;
    private List<Indexads> listIndexads;
    private List<Contentads> listContentads;
    private List<Modules> listModules;

    public class Modules {
        private String classname;
        private String list_api;
        private String content_api;
        private String title;
        private String icon;
        private String logo;
        private String listmodel;
        private String indexpic;
        private String modulekey;
        private Plusdata plusdata;
        private List<Channel> listChannels;
        private List<Mideamodel> listMediamodules;

        public List<Mideamodel> getListMediamodules() {
            return listMediamodules;
        }

        public void setListMediamodules(List<Mideamodel> listMediamodules) {
            this.listMediamodules = listMediamodules;
        }

        public String getListmodel() {
            return listmodel;
        }

        public void setListmodel(String listmodel) {
            this.listmodel = listmodel;
        }


        public String getClassname() {
            return classname;
        }

        public void setClassname(String classname) {
            this.classname = classname;
        }

        public String getList_api() {
            return list_api;
        }

        public void setList_api(String list_api) {
            this.list_api = list_api;
        }

        public String getContent_api() {
            return content_api;
        }

        public void setContent_api(String content_api) {
            this.content_api = content_api;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getIndexpic() {
            return indexpic;
        }

        public void setIndexpic(String indexpic) {
            this.indexpic = indexpic;
        }

        public Plusdata getPlusdata() {
            return plusdata;
        }

        public void setPlusdata(Plusdata plusdata) {
            this.plusdata = plusdata;
        }

        public List<Channel> getListChannels() {
            return listChannels;
        }

        public void setListChannels(List<Channel> listChannels) {
            this.listChannels = listChannels;
        }

        public String getModulekey() {
            return modulekey;
        }

        public void setModulekey(String modulekey) {
            this.modulekey = modulekey;
        }


    }

    public class Plusdata {
        private String link;
        private String linkmode;
        private String plugin_source;

        public String getPlugin_source() {
            return plugin_source;
        }

        public void setPlugin_source(String plugin_source) {
            this.plugin_source = plugin_source;
        }


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
    }

    public String getConfig_hash() {
        return config_hash;
    }

    public void setConfig_hash(String config_hash) {
        this.config_hash = config_hash;
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
        if (Const.APP_DEBUG) {
            return "http://open.rmt.test.routeryuncs.com/app";
        } else {
            return api_prefix;
        }
    }

    public void setApi_prefix(String api_prefix) {
        this.api_prefix = api_prefix;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
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

        //app_debug = false 说明是正式环境
        //app_debug = true 说明是测试环境
        if (Const.APP_DEBUG) {
            return "http://open.rmt.test.routeryuncs.com";
        } else {
            return api_root;
        }

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
        private String url;
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

    public List<Modules> getListModules() {
        return listModules;
    }

    public void setListModules(List<Modules> listModules) {
        this.listModules = listModules;
    }

    public List<Indexads> getListIndexads() {
        return listIndexads;
    }

    public void setListIndexads(List<Indexads> listIndexads) {
        this.listIndexads = listIndexads;
    }

    public class Contentads {
        private String url;
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

    public class Mideamodel {
        private String classname;
        private String list_api;
        private String content_api;
        private String title;
        private String icon;
        private String logo;
        private String listmodel;
        private String indexpic;
        private String modulekey;
        private Plusdata plusdata;
        private List<Channel> listChannels;

        public String getListmodel() {
            return listmodel;
        }

        public void setListmodel(String listmodel) {
            this.listmodel = listmodel;
        }


        public String getClassname() {
            return classname;
        }

        public void setClassname(String classname) {
            this.classname = classname;
        }

        public String getList_api() {
            return list_api;
        }

        public void setList_api(String list_api) {
            this.list_api = list_api;
        }

        public String getContent_api() {
            return content_api;
        }

        public void setContent_api(String content_api) {
            this.content_api = content_api;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getIndexpic() {
            return indexpic;
        }

        public void setIndexpic(String indexpic) {
            this.indexpic = indexpic;
        }

        public Plusdata getPlusdata() {
            return plusdata;
        }

        public void setPlusdata(Plusdata plusdata) {
            this.plusdata = plusdata;
        }

        public List<Channel> getListChannels() {
            return listChannels;
        }

        public void setListChannels(List<Channel> listChannels) {
            this.listChannels = listChannels;
        }

        public String getModulekey() {
            return modulekey;
        }

        public void setModulekey(String modulekey) {
            this.modulekey = modulekey;
        }
    }

    public class Channel {
        private String key;
        private String name;
        private String focus_map;
        private String indexpic;

        public String getIndexpic() {
            return indexpic;
        }

        public void setIndexpic(String indexpic) {
            this.indexpic = indexpic;
        }

        public String getFocus_map() {
            return focus_map;
        }

        public void setFocus_map(String focus_map) {
            this.focus_map = focus_map;
        }

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
    }
    public List<Contentads> getListContentads() {
        return listContentads;
    }

    public void setListContentads(List<Contentads> listContentads) {
        this.listContentads = listContentads;
    }
}
