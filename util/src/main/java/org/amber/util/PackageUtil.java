package org.amber.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageUtil {
    /**
     * 获取包下所有的类.
     * @param packageName
     * @return
     * @throws IOException
     */
    public static List<String> getClasses(final String packageName) throws IOException {
        Enumeration<URL> resources =
                Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", File.separator));

        List<String> list = new ArrayList<>();
        URL url = null;
        File file = null;
        while (resources.hasMoreElements()) {
            url = resources.nextElement();
            if ("file".equals(url.getProtocol())) {
                file = new File(url.getPath());
                List<String> allFiles = FileUtil.getAllFiles(file);
                list.addAll(allFiles);
            } else if ("jar".equals(url.getProtocol())) {
                //如果是jar包文件
                //定义一个JarFile
                JarFile jar;
                try {
                    //获取jar
                    jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    //从此jar包 得到一个枚举类
                    Enumeration<JarEntry> entries = jar.entries();
                    List<String> clsList = new ArrayList<>();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();
                        if (name.endsWith(".class")) {
                            clsList.add(name.replace("/", "."));
                        }
                    }
                    clsList.removeIf(e -> !e.startsWith(packageName));
                    clsList.forEach(e -> {
                        list.add(e.replace(packageName + ".", ""));
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //过滤
        List<String> classList = new ArrayList<>();
        list.forEach(e -> {
            if (!e.contains("$")) {
                String cls = String.format("%s.%s", packageName, e.replaceAll("/", ".").replace(".class", ""));
                classList.add(cls);
            }
        });
        return classList;
    }
}
