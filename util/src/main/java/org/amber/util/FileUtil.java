package org.amber.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    /**
     * 获取目录下的
     *
     * @param directory 文件目录
     * @return 所有文件名.
     */
    public static List<String> getAllFiles(File directory) {
        List<String> list = new ArrayList<>();
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        String parentName = f.getName();
                        List<String> subFile = getAllFiles(f);
                        for (String subF : subFile) {
                            list.add(parentName + File.separator + subF);
                        }
                    } else {
                        String name = f.getName();
                        list.add(name);
                    }
                }
            }
        }
        return list;
    }
}
