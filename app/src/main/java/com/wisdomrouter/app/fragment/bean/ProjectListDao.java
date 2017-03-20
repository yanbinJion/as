package com.wisdomrouter.app.fragment.bean;

import java.io.Serializable;
import java.util.List;

public class ProjectListDao implements Serializable{
    private static final long serialVersionUID = 1L;
    private String key;
    private String name;
    private String desc;
    private String createtime;
    private String indexpic;
    private String count;
    private String pic;
    private List<Detail> detail;
    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getIndexpic() {
        return indexpic;
    }

    public void setIndexpic(String indexpic) {
        this.indexpic = indexpic;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<Detail> getDetail() {
        return detail;
    }

    public void setDetail(List<Detail> detail) {
        this.detail = detail;
    }

    public class Detail implements Serializable{
        private static final long serialVersionUID = 1L;
        private String key;
        private String name;
        private String listname;
        private String infocount;

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

        public String getListname() {
            return listname;
        }

        public void setListname(String listname) {
            this.listname = listname;
        }

        public String getInfocount() {
            return infocount;
        }

        public void setInfocount(String infocount) {
            this.infocount = infocount;
        }
    }
}
