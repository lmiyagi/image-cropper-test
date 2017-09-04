package br.com.leonardomiyagi.imagecroppertest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.soundcloud.android.crop.Crop;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class MainActivity extends AppCompatActivity {

    private Button button, saveBtn;
    private ImageView imageView;
    private DrawView drawView;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        imageView = (ImageView) findViewById(R.id.image_view);
        drawView = (DrawView) findViewById(R.id.draw_view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openChooserWithGallery(MainActivity.this, "Selecione", 0);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap originalImage = BitmapFactory.decodeFile(fileUri.getPath());
                Bitmap drawing = Bitmap.createScaledBitmap(drawView.getDrawingCache(), originalImage.getWidth(), originalImage.getHeight(), false);
                drawing.setHasAlpha(true);
                Bitmap combinedImages = overlay(originalImage, drawing);
                try {
                    Date now = new Date();
                    File folder = new File(Environment.getExternalStorageDirectory() + "/ImageCropper");
                    if (!folder.exists()) {
                        if (folder.mkdir()) {
                            System.out.println("DEU ERRO PRA CRIAR DIRETORIO");
                        }
                    }
                    File file = new File(folder.getPath() + "/" + now.getTime() + ".jpg");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    combinedImages.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    final Uri contentUri = Uri.fromFile(file);
                    scanIntent.setData(contentUri);
                    sendBroadcast(scanIntent);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        });
        drawView.draw(new Canvas(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, MainActivity.this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                fileUri = Uri.fromFile(imageFiles.get(0));
                UCrop.Options options = new UCrop.Options();
                options.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                options.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                options.setActiveWidgetColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                UCrop.of(fileUri, fileUri)
                        .withOptions(options)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(500, 500)
                        .start(MainActivity.this);
            }
        });
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            imageView.setImageURI(UCrop.getOutput(data));
        }
    }

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        try {
            int maxWidth = (bmp1.getWidth() > bmp2.getWidth() ? bmp1.getWidth() : bmp2.getWidth());
            int maxHeight = (bmp1.getHeight() > bmp2.getHeight() ? bmp1.getHeight() : bmp2.getHeight());
            Bitmap bmOverlay = Bitmap.createBitmap(maxWidth, maxHeight, bmp1.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(bmp2, 0, 0, null);
            return bmOverlay;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }
}
