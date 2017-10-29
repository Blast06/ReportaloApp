package com.example.juliod07_laptop.firebasecurso.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.juliod07_laptop.firebasecurso.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    private final static int GALLERY_REQUEST = 1;
    private TextInputEditText name;
    private Button submitBtn;
    private ImageButton imgBtn;
    private Uri imgUri = null;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //CREO LA CARPETA USER PROFILE PIC COMO CHILD DEL ROOT DEL STORAGE
        mStorage = FirebaseStorage.getInstance().getReference().child("User_profile_pic");

        mAuth = FirebaseAuth.getInstance();

        //CREO EL CHILD USUARIOS EN EL ROOT DE EL DATABASE
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        progressDialog = new ProgressDialog(this);


        name = (TextInputEditText) findViewById(R.id.setupNameField);
        submitBtn = (Button) findViewById(R.id.setupSubmitbtn);
        imgBtn = (ImageButton) findViewById(R.id.setup_image_button);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
            }
        });


        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });


    }

    private void startSetupAccount() {
        final String mNameField = name.getText().toString().trim();

        final String userId = mAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(mNameField) && imgUri != null) {
            progressDialog.setMessage("Guardando informacion...");
            progressDialog.show();

            StorageReference filePath = mStorage.child(imgUri.getLastPathSegment());

            filePath.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @VisibleForTesting
                    String downloadUrl = taskSnapshot.getDownloadUrl().toString(); //obtengo la url de la img subida

                    //Creo las ramas hijas de user id, siendo name e imagen sus hijas..
                    mDatabaseUsers.child(userId).child("Name").setValue(mNameField);
                    mDatabaseUsers.child(userId).child("image").setValue(downloadUrl);

                    //AQUI

                    progressDialog.dismiss();

                    Intent intentToMain = new Intent(SetupActivity.this, MainActivity.class);
                    intentToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentToMain);


                }
            });

            Toast.makeText(this, "PERFIL ACTUALIZADO CON EXITO!", Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(this, "POR FAVOR NO DEJE CAMPOS VACIOS!!", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imgUri = result.getUri(); //obtengo la imagen cropeada y la guardo en resulturi

                imgBtn.setImageURI(imgUri); //pongo la imagen cropeada en el imageButton de el setup_activity


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
