package edu.uoc.pec3.android.imageapp.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Miquel Casals on 15/04/2016.
 */
public class ImageHelper {
    private File file;
    private Bitmap image;


    /*
     * The constructor creates the file (and folters) if they don't exist and assign it to the class
     * variable "file" for next uses. We know that the app will storage only one image file and his
     * name and folder.
     */
    public ImageHelper() {
        String folder  = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "/UOCImageApp";
        File appFolder = new File(folder);
        appFolder.mkdirs();
        file = new File(appFolder, "imageapp.jpg");
    }


    // Image Getter & Setter.
    public void setImage(Bitmap img) {
        image = img;
    }

    public Bitmap getImage() {
        return image;
    }

    /*
     * Returns true if the external storage is aviable.
     */
    public boolean isAviable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    /*
     * Returns true if the file of the image exists.
     */
    public boolean exists() {
        return file.exists();
    }

    /*
     * Returns the absolute path of the class variable "file".
     */
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    /*
     * Save the value of the class variable "image" to the class variable "file" File.
     */
    public void save() throws IOException {
        FileOutputStream os = new FileOutputStream(file);
        //Compress the image to the stream with full quality and jpg format.
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        os.close();
    }

    /*
     * The method delete() erase the file stored on external storage. The path of the file is
     * stored in file class variable.
     */
    public void delete() {
        file.delete();
    }
}
