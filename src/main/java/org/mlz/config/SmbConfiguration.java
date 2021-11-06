package org.mlz.config;

import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "jcifs.smb.client")
@EnableConfigurationProperties({SmbProperties.class,SmbDfsProperties.class})
public class SmbConfiguration {
    public static final String PREFIX = "jcifs.smb.client";

    private int responseTimeout = 50000;
    private boolean signingEnforced = false;
    private boolean signingPreferred = false;
    private boolean ipcSigningEnforced = false;


    public final SmbProperties properties;
    public final SmbDfsProperties dfsProperties;

    public SmbConfiguration(SmbProperties properties, SmbDfsProperties dfsProperties) {
        this.properties = properties;
        this.dfsProperties = dfsProperties;
    }

    /**
     * 密码验证
     *
     * @return
     * @throws CIFSException
     */
    @Bean
    public CIFSContext getAuth() throws CIFSException {
        NtlmPasswordAuthenticator auth = new NtlmPasswordAuthenticator(this.properties.getHost(),
                this.properties.getUsername(), this.properties.getPassword());
        // set config
        Properties properties = new Properties();
        //超时时间
        properties.setProperty("jcifs.smb.client.responseTimeout", String.valueOf(responseTimeout));
        properties.setProperty("jcifs.smb.client.signingEnforced", String.valueOf(signingEnforced));
        properties.setProperty("jcifs.smb.client.signingPreferred", String.valueOf(signingPreferred));
        properties.setProperty("jcifs.smb.client.ipcSigningEnforced", String.valueOf(ipcSigningEnforced));
        properties.setProperty("jcifs.smb.client.dfs.disabled", String.valueOf(dfsProperties.isDisabled()));

        PropertyConfiguration configuration = new PropertyConfiguration(properties);
        return new BaseContext(configuration).withCredentials(auth);
    }

    public int getResponseTimeout() {
        return responseTimeout;
    }

    public void setResponseTimeout(int responseTimeout) {
        this.responseTimeout = responseTimeout;
    }

    public boolean isSigningEnforced() {
        return signingEnforced;
    }

    public void setSigningEnforced(boolean signingEnforced) {
        this.signingEnforced = signingEnforced;
    }

    public boolean isSigningPreferred() {
        return signingPreferred;
    }

    public void setSigningPreferred(boolean signingPreferred) {
        this.signingPreferred = signingPreferred;
    }

    public boolean isIpcSigningEnforced() {
        return ipcSigningEnforced;
    }

    public void setIpcSigningEnforced(boolean ipcSigningEnforced) {
        this.ipcSigningEnforced = ipcSigningEnforced;
    }
}
