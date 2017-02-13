package com.ancun.up2yun.endpoint;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

/**
 * @author 摇光
 * @version 1.0
 * @Created on 2016/8/22
 * @Copyright 杭州安存网络科技有限公司 Copyright (c) 2016
 */
public abstract class FileBase {

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    /** 临时文件夹 */
    private final String tempDir;

    public FileBase(String tempDir) {
        this.tempDir = tempDir;
    }

    /**
     * 解析请求路径成本地文件路径
     *
     * @param uri   请求路径
     * @return  本地文件路径
     */
    protected String sanitizeUri(String uri) {
        // Decode the path.
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

        if (uri.isEmpty() || uri.charAt(0) != '/') {
            return null;
        }

        // Convert file separators.
        uri = uri.replace('/', File.separatorChar);

        // Simplistic dumb security check.
        // You will have to do something serious in the production environment.
        if (uri.contains(File.separator + '.') ||
                uri.contains('.' + File.separator) ||
                uri.charAt(0) == '.' || uri.charAt(uri.length() - 1) == '.' ||
                INSECURE_URI.matcher(uri).matches()) {
            return null;
        }

        // Convert to absolute path.
        return this.tempDir + File.separator + uri;
    }

}
