package com.wisdomrouter.app.fragment.bean;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class ProjectDelListBean {
    //    {
//        "pic": "http://storage.tmtsp.com/57aa98ae89b81cc414000029/20160909/b9eca028d51c6024a057e9a73ef308dc.jpg",
//            "desc": "9yue9ri新建专题9yue9ri新建专题9yue9ri新建专题9yue9ri新建专题",
//            "name": [
//        {
//            "key": "57d2714389b81c442b000029",
//                "name": "gxx",
//                "listname": "郭兴欣新闻",
//                "infocount": 10
//        },
//        {
//            "key": "57d2716589b81c682b00002b",
//                "name": "gxxx",
//                "listname": "郭兴欣新闻2",
//                "infocount": 15
//        }
//        ]
//    }
    @Expose
    private String pic;
    @Expose
    private String specialname;
    @Expose
    private String desc;
    @Expose
    private List<Detail> detail;


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public List<Detail> getDetail() {
        return detail;
    }

    public void setDetail(List<Detail> detail) {
        this.detail = detail;
    }

    public String getSpecialname() {
        return specialname;
    }

    public void setSpecialname(String specialname) {
        this.specialname = specialname;
    }

    public  class Detail {
        @Expose
        private String key;
        @Expose
        private String name;
        @Expose
        private String listname;
        @Expose
        private String infocount;

        public String getInfocount() {
            return infocount;
        }

        public void setInfocount(String infocount) {
            this.infocount = infocount;
        }

        public String getListname() {
            return listname;
        }

        public void setListname(String listname) {
            this.listname = listname;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

}
