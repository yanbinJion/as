package com.wisdomrouter.app.fragment.bean;

import java.io.Serializable;
import java.util.List;

public class VoteDetailsDao implements Serializable {

    private String title;
    private String votepic;
    private String content;
    private String createtime;
    private String starttime;
    private String endtime;
    private String click;
    private String key;
    private String isvote;
    private String limititem;
    private List<Items> items;


    public String getLimititem() {
        return limititem;
    }

    public void setLimititem(String limititem) {
        this.limititem = limititem;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVotepic() {
        return votepic;
    }

    public void setVotepic(String votepic) {
        this.votepic = votepic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIsvote() {
        return isvote;
    }

    public void setIsvote(String isvote) {
        this.isvote = isvote;
    }

    public class Items implements Serializable{
        private String title;
        private String indexpic;
        private String contact;
        private String content;
        private String itemkey;
        private int count;
        private String no;


        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIndexpic() {
            return indexpic;
        }

        public void setIndexpic(String indexpic) {
            this.indexpic = indexpic;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getItemkey() {
            return itemkey;
        }

        public void setItemkey(String itemkey) {
            this.itemkey = itemkey;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

    }
}
