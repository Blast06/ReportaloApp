package com.example.juliod07_laptop.firebasecurso.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.juliod07_laptop.firebasecurso.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private static final String TAG = "POSTACTIVITY";
    private ImageButton imageButtonSelect;
    private EditText titleofImg, descImg;
    private Button submitBtn;
    private Uri imageUri;
    private StorageReference storageReference;
    private ProgressDialog mProgressbar;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabasaUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        mProgressbar = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Reportes");
        mDatabasaUsers = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(currentUser.getUid());


        titleofImg = (EditText) findViewById(R.id.title_of_img);
        descImg = (EditText) findViewById(R.id.desc_of_img);

        submitBtn = (Button) findViewById(R.id.submitBtn);

        imageButtonSelect = (ImageButton) findViewById(R.id.imageButton);

        imageButtonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }


    private void startPosting() {

        final String title = titleofImg.getText().toString().trim();
        final String desc = descImg.getText().toString().trim();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && imageUri != null) {
            mProgressbar.setMessage("Subiendo publicacion..");
            mProgressbar.show();

            StorageReference filepath = storageReference.child("Imagenes_reportes").child(imageUri.getLastPathSegment());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") final
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = mDatabase.push(); //Crea la entrada el child "reportes" donde iran title,imagen y desc


                    //AQUI EN ESTOS PASOS SE REALIZAN PARA POSTEAR LA INFORMACION, SUBIRLA A LA BD, Y RELACIONAR EL ID DEL
                    //USUARIO CON EL POST, mDatabasaUsers se encarga de ello. EN los ultimos dos newPost, en el primero obtengo
                    //el id del usuario actual, asi va dentro de la jerarquia Reportes y dentro busca el id del usuario del post y  donde estara tambien username
                    //entro a el id del post(el "id" del post es aquel que es hijo de Reportes) y asi dentro de este, uso el ultimo newPost para obtener el nombre
                    //del usuario para ponerlo en cada post/publicacion. Ver BD en Firebase para entender mejor..


                    mDatabasaUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("Titulo").setValue(title);
                            newPost.child("Descripcion").setValue(desc);
                            newPost.child("image").setValue(downloadUrl.toString());
                            newPost.child("userImage").setValue(dataSnapshot.child("image").getValue());
                            newPost.child("userId").setValue(currentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(PostActivity.this, MainActivity.class));

                                    } else {
                                        Toast.makeText(PostActivity.this, "Error al postear informacion", Toast.LENGTH_SHORT).show();
                                        Log.w(TAG, "ERROR EN POSTEO DE INFORMACION", task.getException());
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mProgressbar.dismiss();

                }
            });

        } else {
            Toast.makeText(this, "POR FAVOR LLENE TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            imageUri = data.getData(); //obtiene la imagen para despues setearla en el imgButton
            Picasso.with(this).load(imageUri).fit().centerCrop().into(imageButtonSelect);

        }
    }


}
