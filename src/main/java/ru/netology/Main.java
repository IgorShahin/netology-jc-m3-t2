package ru.netology;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    private static final String SAVE_DIR =
            System.getProperty("user.home") + File.separator + "Games" + File.separator + "savegames";

    public static void main(String[] args) {
        GameProgress gp1 = new GameProgress(90, 2, 10, 254.7);
        GameProgress gp2 = new GameProgress(70, 5, 14, 512.3);
        GameProgress gp3 = new GameProgress(50, 8, 21, 1024.9);

        new File(SAVE_DIR).mkdirs();
        String save1 = SAVE_DIR + File.separator + "save1.dat";
        String save2 = SAVE_DIR + File.separator + "save2.dat";
        String save3 = SAVE_DIR + File.separator + "save3.dat";

        saveGame(save1, gp1);
        saveGame(save2, gp2);
        saveGame(save3, gp3);

        String zipPath = SAVE_DIR + File.separator + "zip.zip";
        List<String> filesToZip = new ArrayList<>();
        filesToZip.add(save1);
        filesToZip.add(save2);
        filesToZip.add(save3);

        zipFiles(zipPath, filesToZip);

        deleteFiles(filesToZip);

        System.out.println("Готово. Архив: " + zipPath);
    }

    public static void saveGame(String filePath, GameProgress progress) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(progress);
            System.out.println("Сохранение создано: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка сохранения " + filePath + ": " + e.getMessage());
        }
    }

    public static void zipFiles(String zipPath, List<String> files) {
        File zipFile = new File(zipPath);
        File parent = zipFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (String filePath : files) {
                File file = new File(filePath);
                if (!file.exists() || !file.isFile()) {
                    System.err.println("Пропущен (не найден файл): " + filePath);
                    continue;
                }
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry entry = new ZipEntry(file.getName());
                    zos.putNextEntry(entry);

                    int b;
                    while ((b = fis.read()) != -1) {
                        zos.write(b);
                    }

                    zos.closeEntry();
                    System.out.println("Добавлен в архив: " + file.getName());
                } catch (IOException e) {
                    System.err.println("Ошибка при добавлении в архив " + filePath + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка создания архива " + zipPath + ": " + e.getMessage());
        }
    }

    public static void deleteFiles(List<String> files) {
        for (String filePath : files) {
            File f = new File(filePath);
            if (f.exists() && f.isFile()) {
                boolean deleted = f.delete();
                System.out.println((deleted ? "Удалён: " : "Не удалось удалить: ") + f.getAbsolutePath());
            }
        }
    }
}
