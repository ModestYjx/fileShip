package cn.flyingocean.fileship.domain;

import javax.persistence.*;

/**
 * 文件认领表
 */
@Entity
public class FileClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 32位UUID
    @Column(length = 32)
    private String claimCode;

    // 文件ID
    private int fileId;

    public FileClaim() {
    }

    public FileClaim(String claimCode, int fileId) {
        this.claimCode = claimCode;
        this.fileId = fileId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClaimCode() {
        return claimCode;
    }

    public void setClaimCode(String claimCode) {
        this.claimCode = claimCode;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
}
