package com.objectvolatile.megacorev2.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class IOUtils {

    private IOUtils() {}

    public static void copyContents(InputStream is, OutputStream os) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeString(OutputStream os, String txt) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
            writer.write(txt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}