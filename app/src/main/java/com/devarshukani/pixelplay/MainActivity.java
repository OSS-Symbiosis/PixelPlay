package com.devarshukani.pixelplay;

import static android.graphics.BitmapFactory.decodeResource;
import static com.devarshukani.pixelplay.R.id.btnOriginal;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    final int REQUESTCODE = 1000;
    Button btnSelectImage, btnOriginal;
    ImageButton btnHeart, btnSquare, btnCircle, btnRectangle;
    ImageView imgShowImage, imgDialogPreview;
    TextView okay_text, btnUseThisImage;
    Dialog dialog;
    Bitmap results, maskbitmap;
    String lastSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Add Image/Icon");

        btnSelectImage = findViewById(R.id.btnSelectImage);
        imgShowImage = findViewById(R.id.imgShowImage);


        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Title"),REQUESTCODE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void ShowDialog(Uri resultUri, Bitmap photo){
        dialog = new Dialog(MainActivity.this);

        dialog.setContentView(R.layout.dialog_select_mask);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;


        imgDialogPreview = dialog.findViewById(R.id.imgDialogPreview);
        btnOriginal = dialog.findViewById(R.id.btnOriginal);
        btnHeart = dialog.findViewById(R.id.btnHeart);
        btnSquare = dialog.findViewById(R.id.btnSquare);
        btnCircle = dialog.findViewById(R.id.btnCircle);
        btnRectangle = dialog.findViewById(R.id.btnRectangle);
        btnUseThisImage = dialog.findViewById(R.id.btnUseThisImage);

        btnOriginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgDialogPreview.setImageBitmap(photo);
                lastSelected = "original";
            }
        });

        btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaskingProcess(photo, "heart");
                lastSelected = "heart";
            }
        });

        btnSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaskingProcess(photo, "square");
                lastSelected = "square";
            }
        });

        btnCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaskingProcess(photo, "circle");
                lastSelected = "circle";
            }
        });

        btnRectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaskingProcess(photo, "rectangle");
                lastSelected = "rectangle";
            }
        });

        btnUseThisImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(lastSelected == "original"){
                    imgShowImage.setImageBitmap(photo);
                }
                else{
                    imgShowImage.setImageBitmap(results);
                }
//                Toast.makeText(MainActivity.this, "Btn Use this image pressed", Toast.LENGTH_SHORT).show();
            }
        });

//        System.out.println(resultUri.toString());
        dialog.show();
        imgDialogPreview.setImageURI(resultUri);

    }


    private void MaskingProcess(Bitmap photo, String maskname) {
        try {

            Bitmap original, mask = null;
//            original = BitmapFactory.decodeResource(getResources(), R.drawable.original);
            original = photo;
            boolean isOriginal = false;

            if(maskname.equals("heart")){
                mask = BitmapFactory.decodeResource(getResources(), R.drawable.user_image_frame_1);
            }
            else if(maskname.equals("square")){
                mask = BitmapFactory.decodeResource(getResources(), R.drawable.user_image_frame_2);
            }
            else if(maskname.equals("circle")){
                mask = BitmapFactory.decodeResource(getResources(), R.drawable.user_image_frame_3);
            }
            else if(maskname.equals("rectangle")){
                mask = BitmapFactory.decodeResource(getResources(), R.drawable.user_image_frame_4);
//                isOriginal = true;
            }

            if (original != null){
                int iv_width = original.getWidth();
                int iv_height = original.getHeight();
                if(!isOriginal){
                    if(iv_height > iv_width){
                        iv_height = iv_width;
                    }
                    else{
                        iv_width = iv_height;
                    }
                }

                results = Bitmap.createBitmap(iv_width, iv_height, Bitmap.Config.ARGB_8888);
                maskbitmap = Bitmap.createScaledBitmap(mask, iv_width, iv_height, true);
                Canvas canvas = new Canvas(results);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                canvas.drawBitmap(original, 0, 0, null);
                canvas.drawBitmap(maskbitmap, 0, 0, paint);

                paint.setXfermode(null);
                paint.setStyle(Paint.Style.STROKE);
            }

        } catch (OutOfMemoryError outOfMemoryError) {
            outOfMemoryError.printStackTrace();
        }
        imgDialogPreview.setImageBitmap(results);
//        return results;

    }


    private void startCropActivity(Uri imageUri){
        CropImage.activity(imageUri)
//                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESTCODE){
                startCropActivity(data.getData());
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap photo = null;
                try {
                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ShowDialog(resultUri, photo);
//                imgShowImage.setImageURI(resultUri);
            }
        }

    }


}