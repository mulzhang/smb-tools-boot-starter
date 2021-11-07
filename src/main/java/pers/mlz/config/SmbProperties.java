package pers.mlz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "smb.tools"
)
public class SmbProperties {
    public static final String PREFIX = "smb.tools";
    /**
     * smbIp
     */
    private String smbIp;
    /**
     * smb所在域
     */
    private String host;
    /**
     * 文件夹
     */
    private String pathDir;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;


    public SmbProperties() {
    }


    public String getSmbIp() {
        return smbIp;
    }

    public void setSmbIp(String smbIp) {
        this.smbIp = smbIp;
    }

    public String getPathDir() {
        return pathDir;
    }

    public void setPathDir(String pathDir) {
        this.pathDir = pathDir;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}