package com.machopiggies.gameloaderapi.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileUtil {
    public static List<Class<?>> getClassesOfPackage(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        if (stream == null) return new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> {
                    try {
                        return Class.forName(packageName + "." + line.substring(0, line.lastIndexOf('.')));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    public static List<Class<?>> getTest(ClassLoader classLoader, String packageName) {
        InputStream stream = classLoader
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        if (stream == null) return new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> {
                    try {
                        return Class.forName(packageName + "." + line.substring(0, line.lastIndexOf('.')));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    public static List<File> getFileList(File directory) {
        List<File> files = new ArrayList<>();
        getFileListOperation(directory, files);
        return files;
    }

    private static void getFileListOperation(File directory, List<File> files) {
        if (directory.isFile()) {
            files.add(directory);
        } else if (directory.isDirectory()) {
            String[] subList = directory.list();
            if (subList != null) {
                if (subList.length == 0) {
                    files.add(new File(directory.getPath()));
                }
                for (String child : subList) {
                    getFileListOperation(new File(directory, child), files);
                }
            }
        }
    }

    public static String getFileName(String headerPath, String filePath) {
        return filePath.substring(headerPath.length() + 1);
    }

    public static void deleteRecursively(File file, Logger logger) {
        if (file == null) return;

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File childFile : files) {
                    if (childFile.isDirectory()) {
                        deleteRecursively(childFile, logger);
                    } else {
                        if (!childFile.delete()) {
                            logger.severe("Could not delete file: " + childFile.getName());
                        }
                    }
                }
            }
        }

        if (!file.delete()) {
            logger.severe("Could not delete file: " + file.getName());
        }
    }
}
