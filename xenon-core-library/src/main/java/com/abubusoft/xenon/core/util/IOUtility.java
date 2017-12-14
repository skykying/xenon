package com.abubusoft.xenon.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Utility varie
 *
 * @author Francesco Benincasa
 */
public abstract class IOUtility {

    /**
     * Legge un file di testo a partire dal suo percorso.
     *
     * @param fullfilename
     * @return
     */
    public static String readTextFile(String fullfilename) {
        String everything = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fullfilename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append('\n');
                line = br.readLine();
            }
            everything = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return everything;
    }

    /**
     * Legge un file di testo a partire dal suo percorso.
     *
     * @param fullfilename
     * @return
     */
    public static ArrayList<String> readTextFileAsStrings(String fullfilename) {
        ArrayList<String> everything = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fullfilename));
            String line = br.readLine();

            everything = new ArrayList<String>();

            while (line != null) {
                everything.add(line);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return everything;
    }

    /**
     * Legge un file di testo da una risorsa raw.
     *
     * @param context
     * @param resourceId
     * @return
     * @throws IOException
     */
    public static String readTextFileFromAssets(Context context, String fileName) throws IOException {
        StringBuilder buffer = new StringBuilder();
        InputStream inputStream = context.getAssets().open(fileName);
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String read = in.readLine();
        while (read != null) {
            buffer.append(read + "\n");
            read = in.readLine();
        }

        inputStream.close();

        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    /**
     * Legge un file di testo da una risorsa raw.
     *
     * @param context
     * @param resourceId
     * @return
     * @throws IOException
     */
    public static String readRawTextFile(Context context, int rawResourceId) throws IOException {
        StringBuilder buffer = new StringBuilder();
        InputStream inputStream = context.getResources().openRawResource(rawResourceId);
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String read = in.readLine();
        while (read != null) {
            buffer.append(read + "\n");
            read = in.readLine();
        }

        inputStream.close();

        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    /**
     * Crea un file vuoto nella cartella cache dell'applicazione con l'estensione desiderata e con un
     *
     * @param ctx
     * @param extension
     * @return
     */
    public static File createEmptyFile(Context ctx, String prefix, String extension) {
        File outputDir = ctx.getCacheDir();
        File outputFile = null;
        try {
            prefix = prefix == null ? "" : prefix;
            outputFile = File.createTempFile(prefix, "." + extension, outputDir);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile;
    }

    /**
     * Salva una bitmap in formato png nella cartella cache dell'applicazione.
     *
     * @param photo
     * @return
     */
    public static String saveTempPngFile(Context context, String prefix, Bitmap photo) {
        File outputDir = context.getCacheDir();
        return saveTempPngFile(context, prefix, outputDir, photo);
    }

    /**
     * Cancella tutti i file temporanei che iniziano con un dato prefisso
     *
     * @param context
     * @param prefix
     * @return numero di file cancellati
     */
    public static int deleteTempFiles(Context context, final String prefix) {
        int i = 0;
        File cacheDir = context.getCacheDir();
        File file;

        String[] filesToDelete = cacheDir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                if (filename.startsWith(prefix)) {
                    return true;
                }
                return false;

            }
        });

        for (String item : filesToDelete) {
            file = new File(cacheDir + "/" + item);

            //	Logger.debug("Delete cached file %s", file.getAbsolutePath());

            file.delete();
            i++;
        }

        return i;
    }

    /**
     * Cancella tutt le preference fatte dall'utente
     *
     * @param context
     * @return
     */
    public static int deleteAllSavedUserPreferences(Context context) {
        final ArrayList<String> listName = new ArrayList<String>();

        File sharedPrefsDir = new File(context.getCacheDir().getAbsoluteFile() + "/../shared_prefs/");

        sharedPrefsDir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                if (filename.startsWith("userprefs")) {
                    listName.add(filename.substring(0, filename.lastIndexOf(".")));
                }
                return false;
            }
        });

        File file;
        for (String item : listName) {
            file = new File(sharedPrefsDir.getAbsolutePath() + "/" + item + ".xml");

            //     Logger.debug("Delete SavedUserPreference file %s", file.getAbsolutePath());
            file.delete();
        }

        return listName.size();
    }

    /**
     * cancella il salvataggio fatto dall'utente
     *
     * @param context
     * @param preferenceName
     * @return
     */
    public static boolean deleteSavedUserPreference(Context context, String preferenceName) {

        File cacheDir = context.getCacheDir();
        File sharedPrefsDir = new File(cacheDir.getAbsoluteFile() + "/../shared_prefs");

        File file = null;
        try {

            file = new File(sharedPrefsDir.getAbsolutePath() + "/" + preferenceName + ".xml");
            file.delete();
            //     Logger.debug("Deleted SavedUserPreference file %s", file.getAbsolutePath());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // Logger.warn("Can not delete user-preference-file %s", file.getAbsolutePath());
        }

        return false;

    }

    /**
     * Recupera l'elenco delle preferenze utente.
     *
     * @param context
     * @return
     */
    public static ArrayList<String> readSavedUserPreference(Context context) {
        final ArrayList<String> listName = new ArrayList<String>();

        File cacheDir = context.getCacheDir();
        File sharedPrefsDir = new File(cacheDir.getAbsoluteFile() + "/../shared_prefs/");

        sharedPrefsDir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                if (filename.startsWith("user")) {
                    listName.add(filename.substring(0, filename.lastIndexOf(".")));
                }
                return false;
            }
        });

        return listName;
    }

    /**
     * Salva un png in un dato percorso dato sottoforma di file.
     *
     * @param photo
     * @return
     */
    public static String saveTempPngFile(Context context, String prefix, File outputDir, Bitmap photo) {
        File outputFile;
        String outputFileName = null;
        try {
            outputFile = File.createTempFile(prefix, ".png", outputDir);
            FileOutputStream out = new FileOutputStream(outputFile);
            photo.compress(Bitmap.CompressFormat.PNG, 90, out);

            outputFileName = outputFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFileName;
    }

    /**
     * Salva un file di testo nella cartella temporanea
     *
     * @param photo
     * @return
     */
    public static boolean writeTempRawTextFile(Context context, String filename, String text) {
        File outputDir = context.getCacheDir();
        return writeRawTextFile(context, filename, outputDir, text);
    }

    /**
     * Salva un file di testo in un dato percorso dato sottoforma di file.
     *
     * @param photo
     * @return
     */
    public static boolean writeRawTextFile(Context context, String filename, File outputDir, String text) {
        File outputFile;
        BufferedWriter writer = null;
        try {
            outputFile = new File(outputDir, filename);
            FileWriter fw = new FileWriter(outputFile);
            writer = new BufferedWriter(fw);
            writer.write(text);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                }
        }

        return true;
    }

    /**
     * Salva un'immagine in formato
     *
     * @param ctx
     * @param outputFileName
     * @param photo
     * @return
     */
    public static boolean savePngFile(Context ctx, String outputFileName, Bitmap photo) {
        File outputFile = null;
        try {
            outputFile = new File(outputFileName);
            FileOutputStream out = new FileOutputStream(outputFile);
            photo.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (outputFile != null) {
                    outputFile.delete();
                    // Logger.warn("File deleted %s", outputFileName);
                }
            } catch (Exception f) {

            }

            return false;
        }

        return true;
    }

    /**
     * Copia un file
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    public static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }
}
