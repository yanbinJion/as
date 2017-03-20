package com.wisdomrouter.app.fragment.bean;

public class ImageInfo {
    public String path;
    public String name;
    public long time;
    public String source_image; //源图
    public boolean isAddButton = false; //是否是添加按钮

    public ImageInfo() {

    }

    public ImageInfo(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSource_image() {
        return source_image;
    }

    public void setSource_image(String source_image) {
        this.source_image = source_image;
    }

    public boolean isAddButton() {
        return isAddButton;
    }

    public void setIsAddButton(boolean isAddButton) {
        this.isAddButton = isAddButton;
    }

    @Override
    public boolean equals(Object o) {
        try {
            ImageInfo other = (ImageInfo) o;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
