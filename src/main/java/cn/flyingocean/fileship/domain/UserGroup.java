package cn.flyingocean.fileship.domain;

public class UserGroup {

    // 自增id
    private byte id;
    private String groupname;
    // 持有文件仓库上限
    private int maxWareHouses;
    // 持有文件数上限
    private int maxFiles;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public int getMaxWareHouses() {
        return maxWareHouses;
    }

    public void setMaxWareHouses(int maxWareHouses) {
        this.maxWareHouses = maxWareHouses;
    }

    public int getMaxFiles() {
        return maxFiles;
    }

    public void setMaxFiles(int maxFiles) {
        this.maxFiles = maxFiles;
    }
}
