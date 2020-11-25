package com.raghav.gfgtextsticker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import petrov.kristiyan.colorpicker.ColorPicker;

//import all necessary classess.
public class MainActivity extends AppCompatActivity {

    TextView textSticker;
    EditText editTextSticker;
    ImageButton fontchange;
    ImageButton colorPickerText;
    Button createSticker;
    //this will work as a counter to change the font of TextView
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textSticker=(TextView)findViewById(R.id.stickerTextview);
        editTextSticker=(EditText) findViewById(R.id.stickerEditText);
        colorPickerText=(ImageButton) findViewById(R.id.changeColor);
        fontchange=(ImageButton) findViewById(R.id.changeFont);
        createSticker=(Button) findViewById(R.id.convert);

        createSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    executeSticker();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

      /*
      	Here we have added a TextWatcher. The onTextChanged() method will change the text in TextView
        as we type, in the EditText. This makes app more interactive.
      */

        editTextSticker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textSticker.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

      /*
      		Here we have implemented a small logic which changes the font of the TextView
            whenver we click this button. The counter increments by one and reset to zero when it
            reaches value 6.
      */
        fontchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (i)
                {
                    case 0:
                        i=1;
                    /*
                    	This is a very important method of this example.
                    	The setTypeFace() method sets the font of the TextView at runtime.

                    */
                        textSticker.setTypeface(ResourcesCompat.getFont(MainActivity.this,R.font.summer));
                        break;
                    case 1:
                        i=2;
                        textSticker.setTypeface(ResourcesCompat.getFont(MainActivity.this,R.font.angel));
                        break;
                    case 2:
                        i=3;
                        textSticker.setTypeface(ResourcesCompat.getFont(MainActivity.this,R.font.cute));
                        break;
                    case 3:
                        i=4;
                        textSticker.setTypeface(ResourcesCompat.getFont(MainActivity.this,R.font.mandala));
                        break;
                    case 4:
                        i=5;
                        textSticker.setTypeface(ResourcesCompat.getFont(MainActivity.this,R.font.painter));
                        break;
                    case 5:
                        i=6;
                        textSticker.setTypeface(ResourcesCompat.getFont(MainActivity.this,R.font.newfont));
                        break;
                    case 6:
                        i=0;
                        textSticker.setTypeface(ResourcesCompat.getFont(MainActivity.this,R.font.orange));
                        break;

                }
            }
        });

        //create an instance of ColorPicker and invoke the ColorPicker dialog onClick.

        colorPickerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ColorPicker colorPicker=new ColorPicker(MainActivity.this);
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        //get the integer value of color selected from the dialog box and
                        // the color of the TextView.
                        textSticker.setTextColor(color);

                    }

                    @Override
                    public void onCancel() {

                        colorPicker.dismissDialog();
                    }
                })
                        //set the number of color columns you want  to show in dialog.
                        .setColumns(5)
                        //set a default color selected in the dialog
                        .setDefaultColorButton(Color.parseColor("#000000"))
                        .show();
            }
        });
    }


    //This method creates a Bitmap from the TextView and saves that into the storage
    private void executeSticker() throws IOException {
        //Create an OututStream to write the file in storage
        OutputStream imageOutStream;
        //Although the ProgressDialog is not necessary but there may be cases when
        //it might takes 2-3seconds in creating the bitmap.(This happens only when there is a
        // large chunk of cache and also when app is running multiple threads)
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();

      /*
      	All the three methods are discussed later in this article.
        destroyDrawingCache(),buildDrawingCache(),getDrawingCache().
      */
        textSticker.destroyDrawingCache();
        textSticker.buildDrawingCache();
        Bitmap textStickerBitmap=textSticker.getDrawingCache();

        //From Android 10 onwards using the former method gives error, because
        // there is a security/privacy update in Android Q which doesn't allow accss to third
        // party files.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            //In order to create a new image file in storage we do the following steps.
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "gfg.png");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            imageOutStream = getContentResolver().openOutputStream(uri);

            //this method writes the file in storage. And finally our sticker has been created and
            // successfully saved in our storage
            textStickerBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);

            //close the output stream after use.
            imageOutStream.close();
            progressDialog.dismiss();

            //Now, incase you want to use that bitmap(sticker) at the very moment it is created
            //, we do the following steps.
            //Open a Inputstream to get the data from file
            final InputStream imageStream;
            try {
                //use the same uri which we previoulsy used in writing the file, as it contains
                //the path to that file.
                imageStream = getContentResolver().openInputStream(uri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                //create a drawable from bitmap.
                Drawable drawable = new BitmapDrawable(getResources(), selectedImage);
                // You can do anything with this drawable.
                //This drawable contains sticker png.


            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this, "File not found!!", Toast.LENGTH_SHORT).show();

            }

            //The else condition is executed if the device Android version is less than Android 10
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File image = new File(imagesDir, "stisdcker.jpg");
            imageOutStream = new FileOutputStream(image);

            textStickerBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);
            imageOutStream.close();

            final Uri imageUri = Uri.fromFile(image);
            final InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Drawable drawable = new BitmapDrawable(getResources(), selectedImage);
                // You can do anything with this drawable.
                //This drawable contains sticker png.


            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this, "File not found!!", Toast.LENGTH_SHORT).show();

            }

        }

        //Finally, print a success message.
        Toast.makeText(this, "Sticker created successfully!!", Toast.LENGTH_SHORT).show();
    }
}

