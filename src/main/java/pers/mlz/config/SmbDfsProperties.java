package pers.mlz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "jcifs.smb.client.dfs"
)
public class SmbDfsProperties {
    public static final String PREFIX_DFS = "jcifs.smb.client.dfs";

    /**
     * 是否关闭检索
     */
    private boolean disabled = false;

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
