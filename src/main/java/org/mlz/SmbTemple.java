package org.mlz;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import org.mlz.config.SmbConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


@Component
@AutoConfigureBefore(
        value = {SmbConfiguration.class},
        name = {"org.mlz.config.SmbConfiguration"}
)
public class SmbTemple {
    @Autowired
    private SmbConfiguration smbConfiguration;

    Logger logger = LoggerFactory.getLogger(getClass());
    final static String AGREEMENT = "smb://";

    /**
     * @param path     存SMB服务器的路径
     * @param in       multipartFile文件
     * @param fileName 存入的文件名
     */
    public String smbPut(String path, String fileName, InputStream in) {
        Assert.isNull(in, "文件流错误");
        long startupDate = System.currentTimeMillis();
        OutputStream out = null;
        String url;
        try {
            url = new StringBuilder(AGREEMENT)
                    .append(smbConfiguration.properties.getSmbIp())
                    .append(smbConfiguration.properties.getPathDir())
                    .append(path).append(fileName).toString();
            SmbFile remoteFile = new SmbFile(url, smbConfiguration.getAuth());
            out = new SmbFileOutputStream(remoteFile);
            long copy = this.copy(in, out);
            logger.info("传输完成,文件大小{}，用时：{}ms", copy, System.currentTimeMillis() - startupDate);
        } catch (Exception e) {
            e.printStackTrace();
            url = null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return url;
    }


    /**
     * 获取附件
     *
     * @param path 附件路径
     * @return
     */
    public void getFile(String path, OutputStream sink) {
        InputStream bis = null;
        OutputStream os = null;
        try {
            this.read(new SmbFile(path, smbConfiguration.getAuth()).getInputStream(), sink);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }


    private static final int BUFFER_SIZE = 4096;

    private static long copy(InputStream source, OutputStream sink)
            throws IOException {
        long nread = 0L;
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
            nread += n;
        }
        return nread;
    }

    private static void read(InputStream source, OutputStream sink)
            throws IOException {
        byte[] buff = new byte[BUFFER_SIZE];
        int i = source.read(buff);
        while (i != -1) {
            sink.write(buff, 0, buff.length);
            sink.flush();
            i = source.read(buff);
        }
    }
}
