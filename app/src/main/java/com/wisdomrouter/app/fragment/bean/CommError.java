/**
 * @FILE:CommError.java
 * @AUTHOR:baifan
 * @DATE:2015-4-28 下午2:14:10
 **/
package com.wisdomrouter.app.fragment.bean;

/**
 * 通用的错误类
 */
public class CommError {
    private int state;
    private String message;
    private String pic;
    private String count;
    private int score;
    private Errors errors;
    private Data data;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public class Errors {
        private String[] info_key;
        private String[] title;
        private String[] user_openid;
        private String[] content;
        private String[] platform;
        private String[] pic1;
        private String[] pic2;

        public String[] getPic1() {
            return pic1;
        }

        public void setPic1(String[] pic1) {
            this.pic1 = pic1;
        }

        public String[] getPic2() {
            return pic2;
        }

        public void setPic2(String[] pic2) {
            this.pic2 = pic2;
        }

        public String[] getPlatform() {
            return platform;
        }

        public void setPlatform(String[] platform) {
            this.platform = platform;
        }

        public String[] getTitle() {
            return title;
        }

        public void setTitle(String[] title) {
            this.title = title;
        }

        public String[] getUser_openid() {
            return user_openid;
        }

        public void setUser_openid(String[] user_openid) {
            this.user_openid = user_openid;
        }

        public String[] getContent() {
            return content;
        }

        public void setContent(String[] content) {
            this.content = content;
        }

        public String[] getInfo_key() {
            return info_key;
        }

        public void setInfo_key(String[] info_key) {
            this.info_key = info_key;
        }
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }


    public class Data {
        private String vcode;
        private String time;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getVcode() {
            return vcode;
        }

        public void setVcode(String vcode) {
            this.vcode = vcode;
        }
    }
}
