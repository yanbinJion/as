package com.wisdomrouter.app.fragment.bean;


public class ArticleList2Dao {
    private String title; // 标题
    private String author;// 作者
    private String source;// 来源
    private String indexpic; // 列表图片
    private String createtime; // 日期
    private String click; // 点击次数
    private String desc; // 简介
    private String key;
    private String template;
    private Long comment;
    private String info_class;
    private String state;
    private String message;
    private String father;
    private String videopath;
    private String image;
    private String id;
    private String showtype;
    private String votepic;
    private String actaddress;
    private String starttime;
    private String endtime;
    private int countimage;
    private Client client;
    private String[] articlepic;
    private String adpic;
    private String content;// 直播简介
    private Boolean isRead = false;// 是否已读
    private String limitItem;
    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getLimitItem() {
        return limitItem;
    }

    public void setLimitItem(String limitItem) {
        this.limitItem = limitItem;
    }

    public String getAdpic() {
        return adpic;
    }

    public void setAdpic(String adpic) {
        this.adpic = adpic;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getActaddress() {
        return actaddress;
    }

    public void setActaddress(String actaddress) {
        this.actaddress = actaddress;
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }


    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getComment() {
        return comment;
    }

    public void setComment(Long comment) {
        this.comment = comment;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getShowtype() {
        return showtype;
    }

    public void setShowtype(String showtype) {
        this.showtype = showtype;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCountimage() {
        return countimage;
    }

    public String[] getArticlepic() {
        return articlepic;
    }

    public void setArticlepic(String[] articlepic) {
        this.articlepic = articlepic;
    }

    public void setCountimage(int countimage) {
        this.countimage = countimage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getVideopath() {
        return videopath;
    }

    public void setVideopath(String videopath) {
        this.videopath = videopath;
    }

    public String getInfo_class() {
        return info_class;
    }

    public void setInfo_class(String info_class) {
        this.info_class = info_class;
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

    public class Client {
        private String $id;

        public String get$id() {
            return $id;
        }

        public void set$id(String $id) {
            this.$id = $id;
        }
    }

    public String getVotepic() {
        return votepic;
    }

    public void setVotepic(String votepic) {
        this.votepic = votepic;
    }
}