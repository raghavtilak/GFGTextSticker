package com.raghav.gfgtextsticker

import android.app.ProgressDialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import petrov.kristiyan.colorpicker.ColorPicker

class MainActivity : AppCompatActivity() {
    private lateinit var textSticker: TextView
    private lateinit var editTextSticker: EditText
    private lateinit var fontChange: ImageButton
    private lateinit var colorPickerText: ImageButton
    private lateinit var createSticker: Button
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textSticker = findViewById(R.id.stickerTextview)
        editTextSticker = findViewById(R.id.stickerEditText)
        colorPickerText = findViewById(R.id.changeColor)
        fontChange = findViewById(R.id.changeFont)
        createSticker = findViewById(R.id.convert)

        createSticker.setOnClickListener {
            try {
                executeSticker()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        editTextSticker.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textSticker.text = s
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing
            }
        })

        fontChange.setOnClickListener {
            when (i) {
                0 -> {
                    i = 1
                    textSticker.typeface = ResourcesCompat.getFont(this, R.font.summer)
                }
                1 -> {
                    i = 2
                    textSticker.typeface = ResourcesCompat.getFont(this, R.font.angel)
                }
                2 -> {
                    i = 3
                    textSticker.typeface = ResourcesCompat.getFont(this, R.font.cute)
                }
                3 -> {
                    i = 4
                    textSticker.typeface = ResourcesCompat.getFont(this, R.font.mandala)
                }
                4 -> {
                    i = 5
                    textSticker.typeface = ResourcesCompat.getFont(this, R.font.painter)
                }
                5 -> {
                    i = 6
                    textSticker.typeface = ResourcesCompat.getFont(this, R.font.newfont)
                }
                6 -> {
                    i = 0
                    textSticker.typeface = ResourcesCompat.getFont(this, R.font.orange)
                }
            }
        }

        colorPickerText.setOnClickListener {
            val colorPicker = ColorPicker(this)
            colorPicker.setOnFastChooseColorListener(object : ColorPicker.OnFastChooseColorListener {
                override fun setOnFastChooseColorListener(position: Int, color: Int) {
                    textSticker.setTextColor(color)
                }

                override fun onCancel() {
                    colorPicker.dismissDialog()
                }
            })
                .setColumns(5)
                .setDefaultColorButton(Color.parseColor("#000000"))
                .show()
        }
    }

    private fun executeSticker() {
        val imageOutStream: OutputStream
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait..")
        progressDialog.show()

        textSticker.destroyDrawingCache()
        textSticker.buildDrawingCache()
        val textStickerBitmap = textSticker.drawingCache

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "gfg.png")
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            imageOutStream = contentResolver.openOutputStream(uri)
            textStickerBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream)
            imageOutStream.close()
            progressDialog.dismiss()
            val imageStream: InputStream
            try {
                imageStream = contentResolver.openInputStream(uri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                val drawable = BitmapDrawable(resources, selectedImage)
            } catch (e: FileNotFoundException) {
                Toast.makeText(this, "File not found!!", Toast.LENGTH_SHORT).show()
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val image = File(imagesDir, "sticker.jpg")
            imageOutStream = FileOutputStream(image)
            textStickerBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream)
            imageOutStream.close()
            val imageUri = Uri.fromFile(image)
            val imageStream: InputStream
            try {
                imageStream = contentResolver.openInputStream(imageUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                val drawable = BitmapDrawable(resources, selectedImage)
            } catch (e: FileNotFoundException) {
                Toast.makeText(this, "File not found!!", Toast.LENGTH_SHORT).show()
            }
        }
        Toast.makeText(this, "Sticker created successfully!!", Toast.LENGTH_SHORT).show()
    }
}
