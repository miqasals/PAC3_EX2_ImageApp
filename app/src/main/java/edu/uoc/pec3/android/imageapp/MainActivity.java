package edu.uoc.pec3.android.imageapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import edu.uoc.pec3.android.imageapp.utils.ImageHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // TAG logs
    private final String TAG = this.getClass().getSimpleName();

    // Request code
    static final int REQUEST_IMAGE_CAPTURE = 1;

    // Views
    private Button mButtonOpenImage;
    private ImageView mImageView;
    private TextView mTextView;

    // Helpers
    private ImageHelper ihelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set views
        mButtonOpenImage = (Button) findViewById(R.id.button);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mTextView = (TextView) findViewById(R.id.textView);

        // Set listeners
        mButtonOpenImage.setOnClickListener(this);

        // Set the helpers.
        ihelper = new ImageHelper();

        // Request permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permissions not garanted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, R.string.permissions_denied_msg,
                        Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        } else {
            // Permissions are allowed. Read the image from storage.
            if (ihelper.isAviable()) {
                if (ihelper.exists()) {
                    ihelper.setImage(BitmapFactory.decodeFile(ihelper.getAbsolutePath()));
                    // Sets the layout.
                    mImageView.setImageBitmap(ihelper.getImage());
                    mTextView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.saveImage:
                // If save button presed
                saveImage();
                return true;
            case R.id.deleteImage:
                // If delete button pressed.
                deleteImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
     * When Save appBar button is pressed the checks the writable access permission to external storage,
     * checks if the external storage is mounted and try to save the displayed in the device.
     */
    private void saveImage() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Permission not granteed.
            Toast.makeText(this, R.string.permissions_denied_msg,
                    Toast.LENGTH_LONG).show();
        } else {
            // Permission granteed. Proceed to saves the image.
            if (ihelper.isAviable()) {
                // Media mounted. The app try to save in storage the image.
                try {
                    ihelper.save();
                    // Send toast to user.
                    Toast.makeText(this, R.string.save_image_toast, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Send error message to user.
                Toast.makeText(this, R.string.storage_unmounted_toast, Toast.LENGTH_LONG).show();
            }
        }
    }


    /*
     * On Delete appBar button pressed it clears the image from layout and delete the it from the
     * storage.
     */
    private void deleteImage() {

        // Create a AlertDialog builder and sets message and buttons.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_dialog_message))
                .setTitle(getString(R.string.delete_dialog_title))
                // Manage behavior of positive response.
                .setPositiveButton(getString(R.string.delete_dialog_positive_btn),
                    new DialogInterface.OnClickListener() {
                            // Behaviour on press YES
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Clear the image in layout.
                                mImageView.setImageResource(android.R.color.transparent);
                                mTextView.setVisibility(View.VISIBLE);
                                // Delete image from external storage.
                                ihelper.delete();
                                // Send toast message to user.
                                Toast.makeText(getApplicationContext(), R.string.delete_image_toast,
                                        Toast.LENGTH_LONG).show();
                            }
                    }
                )
                // Negative response only dismiss the dialog.
                .setNegativeButton(getString(R.string.delete_dialog_negative_btn),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );

        // Create a Alert dialog based in the builder settings and show it.
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        if (v == mButtonOpenImage) {    // Quant es fa un click en la View mButtonOpenImage. ######
            // launching an intent to get an image from camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            /*
            We have created an implicit intent for start the camera application. If don't exist
            any app can handle the intent the app crashes. We must control it.
             */
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Start the activity and wait for result.

                // TODO: afegir arxiu per guardar la foto en gran format. Pendent de confirmar....

                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /*
    For get the taken image we must override the onActivityResult method. In this method we get
    the extra data and set the layout image. We must set visibility of textView to invisible too.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // Gets the image data.
            Bundle extras = data.getExtras();
            ihelper.setImage((Bitmap) extras.get("data"));

            // Sets the layout.
            mImageView.setImageBitmap(ihelper.getImage());
            mTextView.setVisibility(View.INVISIBLE);
        }
    }








}
