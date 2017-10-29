package com.example.juliod07_laptop.firebasecurso.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.juliod07_laptop.firebasecurso.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText regNombre, regEmail, regPassowrd;
    private Button regBtn;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        regNombre = (TextInputEditText) findViewById(R.id.reg_nombre);
        regEmail = (TextInputEditText) findViewById(R.id.reg_email);
        regPassowrd = (TextInputEditText) findViewById(R.id.reg_password);

        regBtn = (Button) findViewById(R.id.btn_registrate);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });

    }

    private void registrar() {
        final String name = regNombre.getText().toString().trim();
        String email = regEmail.getText().toString().trim();
        String password = regPassowrd.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            if (password.length() > 6) {
                mProgress.setMessage("Creando cuenta...");
                mProgress.show();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String user_id = mAuth.getCurrentUser().getUid();

                            DatabaseReference current_user_db = mDatabaseUsers.child(user_id);
                            current_user_db.child("Nombre").setValue(name);
                            current_user_db.child("image").setValue("default");

                            mProgress.dismiss();
                            Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            Toast.makeText(SignUpActivity.this, "Usuario creado con exito!", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

            } else {
                Toast.makeText(this, "POR FAVOR, LA CLAVE DEBE TENER MAS DE 6 LETRAS", Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(this, "NO DEJE CAMPOS VACIOS, POR FAVOR", Toast.LENGTH_SHORT).show();

        }

    }


}
