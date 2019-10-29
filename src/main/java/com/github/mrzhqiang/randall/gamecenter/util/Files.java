package com.github.mrzhqiang.randall.gamecenter.util;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author mrzhqiang
 */
public enum Files {
    ;

    private static final Logger LOGGER = LoggerFactory.getLogger("common");

    public static void createOrExists(File file) {
        Preconditions.checkNotNull(file, "file == null");
        if (file.exists()) {
            return;
        }
        try {
            if (file.isDirectory() && file.mkdirs()) {
                LOGGER.info("创建新目录：{}", file.getCanonicalPath());
                return;
            }
            // file.isFile 必须要有文件格式后缀，否则返回 false 导致无法创建文件
            if (file.createNewFile()) {
                LOGGER.info("创建新文件：{}", file.getCanonicalPath());
            }
        } catch (SecurityException e) {
            String message = String.format("无法读写 [%s]", file.getPath());
            throw new RuntimeException(message, e);
        } catch (IOException e) {
            String message = String.format("创建失败 [%s]", file.getPath());
            throw new RuntimeException(message, e);
        }
    }

    public static void deleteOrNotExists(File file) {
        Preconditions.checkNotNull(file, "file == null");
        if (!file.exists()) {
            return;
        }
        try {
            if (file.delete()) {
                LOGGER.info("已删除：{}", file.getCanonicalPath());
            }
        } catch (IOException e) {
            String message = String.format("删除 [%s] 失败", file.getPath());
            throw new RuntimeException(message, e);
        }
    }

    public static void onceWrite(File file, String content) {
        createOrExists(file);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            String message = String.format("无法写入到 [%s]", file.getPath());
            throw new RuntimeException(message, e);
        }
    }

    public static void appleWrite(File file, String content) {
        createOrExists(file);
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            String message = String.format("无法追加到 [%s]", file.getPath());
            throw new RuntimeException(message, e);
        }
    }
}
