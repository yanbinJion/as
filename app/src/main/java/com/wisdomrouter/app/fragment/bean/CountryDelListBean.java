package com.wisdomrouter.app.fragment.bean;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class CountryDelListBean {
    //    {
//        "townpic": [
//        {
//            "pic1": "http://storage.tmtsp.com/57aa98ae89b81cc414000029/20160919/5517ce6e834e01106ec22e17fe7073c0.jpg",
//                "desc1": "街道"
//        },
//        {
//            "pic2": "http://storage.tmtsp.com/57aa98ae89b81cc414000029/20160919/344f1855ea8826f041bae671aa090752.jpg",
//                "desc2": "社区"
//        },
//        {
//            "pic3": "http://storage.tmtsp.com/57aa98ae89b81cc414000029/20160919/344f1855ea8826f041bae671aa090752.jpg",
//                "desc3": "公告厕所"
//        }
//        ],
//        "towndesc": "我地老家",
//            "detail": [
//        {
//            "key": "57df74e089b81c181f000029",
//                "name": "list",
//                "listname": "交通小区",
//                "infocount": 17
//        },
//        {
//            "key": "57df751d89b81c841c000029",
//                "name": "list",
//                "listname": "建安新村",
//                "infocount": 17
//        }
//        ]
//    }
    @Expose
    private List<Townpic> townpic;
    @Expose
    private String towndesc;
    @Expose
    private String townname;
    @Expose
    private List<Detail> detail;

    public List<Detail> getDetail() {
        return detail;
    }

    public void setDetail(List<Detail> detail) {
        this.detail = detail;
    }


    public String getTowndesc() {
        return towndesc;
    }

    public void setTowndesc(String towndesc) {
        this.towndesc = towndesc;
    }

    public List<Townpic> getTownpic() {
        return townpic;
    }

    public void setTownpic(List<Townpic> townpic) {
        this.townpic = townpic;
    }

    public String getTownname() {
        return townname;
    }

    public void setTownname(String townname) {
        this.townname = townname;
    }

    public class Detail {
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

    public class Townpic {
        @Expose
        private String pic;
        @Expose
        private String desc;

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
    }
}
