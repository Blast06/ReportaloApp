package com.example.juliod07_laptop.firebasecurso.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juliod07_laptop.firebasecurso.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private TextInputEditText email, password;
    private Button loginBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private LoginButton loginFB;
    private ProgressBar mProgressBar;
    private TextView idCreateHere;
    private ImageView loginGoogleBtn;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;

    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_login);
        hideProgressBar();

        progressDialog = new ProgressDialog(this);


        loginGoogleBtn = (ImageView) findViewById(R.id.login_google);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();


        loginFB = (LoginButton) findViewById(R.id.login_facebook);

        idCreateHere = (TextView) findViewById(R.id.create_here);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        mDatabaseUsers.keepSynced(true);


        email = (TextInputEditText) findViewById(R.id.username);
        password = (TextInputEditText) findViewById(R.id.password);

        loginBtn = (Button) findViewById(R.id.login_btn);

        ///PARA SABER EL CURRENT USER EN FB
        //AccessToken.getCurrentAccessToken();

        loginFB.setReadPermissions("email", "public_profile");
        loginFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(LoginActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "ERROR EN LOGIN DE FB: " + error, Toast.LENGTH_SHORT).show();

            }
        });

       





        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

        loginGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        // Configuracion de Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    //REMEMBER TO ADD AN EMAIL VALIDATION METHOD !!

    private void signIn() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient); //PRIMERO CIERRO SESION DEL ANTERIOR PARA QUE SALGA EL DIALOG PA ELEGIR USERS
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            /*progressDialog.setMessage("Iniciando sesion...");
            progressDialog.show();*/

            if (result.isSuccess()) {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                progressDialog.setMessage("Iniciando sesion...");
                progressDialog.show();


            } else {
                // Google Sign In failed, update UI appropriately
                // ...

                Toast.makeText(this, "Selecciona una cuenta!", Toast.LENGTH_SHORT).show();

            }


        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            /*updateUI(user);*/

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Error al iniciar sesion con google2.",
                                    Toast.LENGTH_SHORT).show();
                            /*updateUI(null);*/

                        }
                        checkIfUserExists();
                        progressDialog.dismiss();


                        // ...
                    }
                });
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }


    private void checkLogin() {
        String correo = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (!TextUtils.isEmpty(correo) && !TextUtils.isEmpty(pass)) {
            showProgressBar();
            mAuth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        checkIfUserExists();
                        hideProgressBar();

                    } else {
                        Toast.makeText(LoginActivity.this, "Error al iniciar sesion. Verificar clave y usuario", Toast.LENGTH_SHORT).show();
                        hideProgressBar();
                    }
                }
            });


        } else {
            Toast.makeText(this, "POR FAVOR LLENAR TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();
        }
    }


    private void checkIfUserExists() {
        if (mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {

                        Intent intentToMain = new Intent(LoginActivity.this, MainActivity.class);
                        intentToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToMain);


                    } else {
                        Intent setupIntent = new Intent(LoginActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    /*private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(this, "Inicio de sesion correcto", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "Error al iniciar con Google", Toast.LENGTH_SHORT).show();

        }
    }*/


    public void goCreateAcount(View view) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        /*updateUI(currentUser);*/

    }
}
