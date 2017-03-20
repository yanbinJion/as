package com.wisdomrouter.app.fragment.bean;

/**
 * Created by Administrator on 2016/3/16.
 */
public class CustomDao{
    private String title;
    private String info_class;
    private int state;
    private String createtime;
    private String indexpic;
    private String desc;
    private String list_key;
    private String info_key;
    private String link;
    private int sort;
    private String[] pic;
    private String source;
    private String click;

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }



    public String[] getPic() {
        return pic;
    }

    public void setPic(String[] pic) {
        this.pic = pic;
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


    public void setTitle(String title) {
        this.title = title;
    }

    public void setInfo_class(String info_class) {
        this.info_class = info_class;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setList_key(String list_key) {
        this.list_key = list_key;
    }

    public void setInfo_key(String info_key) {
        this.info_key = info_key;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo_class() {
        return info_class;
    }

    public int getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }

    public String getList_key() {
        return list_key;
    }

    public String getInfo_key() {
        return info_key;
    }

    public int getSort() {
        return sort;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
