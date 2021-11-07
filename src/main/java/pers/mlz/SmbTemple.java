package pers.mlz;

import jcifs.CIFSException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import pers.mlz.config.SmbConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;


@Component
@AutoConfigureBefore(
        value = {SmbConfiguration.class},
        name = {"pers.mlz.config.SmbConfiguration"}
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
            long copy = copy(in, out);
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
     */
    public void getFile(String path, OutputStream sink) {
        InputStream bis = null;
        try (SmbFile ignored = new SmbFile(path, smbConfiguration.getAuth())) {
            read(ignored.getInputStream(), sink);
        } catch (MalformedURLException e) {
            logger.error("访问远程地址失败", e);
        } catch (CIFSException e) {
            logger.error("认证信息错误", e);
        } catch (IOException e) {
            logger.error("io", e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
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
