package com.example.juliod07_laptop.firebasecurso.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.juliod07_laptop.firebasecurso.AsyncTasks;
import com.example.juliod07_laptop.firebasecurso.R;
import com.example.juliod07_laptop.firebasecurso.models.Reports;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    //SI TIENES QUE QUITAR LOS CODIGOS COMENTADOS, PONE EL IMPLEMENT implements NavigationView.OnNavigationItemSelectedListener

    private DatabaseReference mDatabaseRef;
    private RecyclerView reportList;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;
    private AsyncTasks isInternet;
    private DatabaseReference mDatabaseUsers;
    private boolean isLikeChecked = false;
    private DatabaseReference mDatabase4Like;
    private FloatingActionButton mFabProfile,mFabLogout,mFabAbout, mFabAddPost;



    //FOR FUTURE STUDIES PORPUSES, HERE IS WHERE I USE THE RECYCLERVIEW , WELL THE FIREBASEUI RECYCLERVIEW...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_content);


        mFabAddPost = findViewById(R.id.addpost);
        mFabProfile = findViewById(R.id.profile);
        mFabLogout = findViewById(R.id.logout);
        mFabAbout = findViewById(R.id.about);



        mFabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));



            }
        });
        mFabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PostActivity.class));
                finish();

            }
        });

        mFabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();

            }
        });

        mFabAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AboutActivity.class));
                finish();
            }
        });








        //EL MENU TRADICIONAL SE CAMBIARA POR UN NAVIGATION DRAWER O SIMILAR(VER LOS FAVORITOS)


        //----------------------------------------------------------------------------------


        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent login = new Intent(MainActivity.this, LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);

                }
            }
        };
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Reportes");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        mDatabase4Like = FirebaseDatabase.getInstance().getReference().child("Likes");

        mDatabaseUsers.keepSynced(true);
        mDatabase4Like.keepSynced(true);
        mDatabaseRef.keepSynced(true);


        reportList = (RecyclerView) findViewById(R.id.report_list);
        reportList.setHasFixedSize(true); //is used to let the RecyclerView keep the same size


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);//en estas dos lineas hago que el recyclerview empiece al reves , ya que la data en Firebase
        layoutManager.setStackFromEnd(true);//se lee o agrega a lo ultimo con el recyclerview, so yo lo puse reverse...
        reportList.setLayoutManager(layoutManager);


        checkIfUserExists(); //METODO PARA CHEQUEAR SI EXISTE EL USER...


    }

    @Override
    protected void onStart() {
        super.onStart();


        mAuth.addAuthStateListener(authStateListener);

        FirebaseRecyclerAdapter<Reports, ReportViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Reports, ReportViewHolder>(

                //1.aqui le paso la clase que tiene los datos igual que en la BD en FireBase.
                //2.Tambien el layout a utilizar
                //3.El viewHolder
                //4.Referencia de la BD
                Reports.class,
                R.layout.report_list_row,
                ReportViewHolder.class,
                mDatabaseRef
        ) {


            @Override
            protected void populateViewHolder(ReportViewHolder viewHolder, Reports model, int position) {

                final String postId = getRef(position).getKey(); //PARA OBTENER LA KEY DE UN POST PARA EL ONCLICKLISTENER


                viewHolder.setTitle(model.getTitulo());
                viewHolder.setDesc(model.getDescripcion());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setUserImage(model.getUserImage(), getApplicationContext());
                viewHolder.setmLikeBtn(postId); //ESTO ES PARA SABER SI HAY LIKES DE UN USUARIO EN UN POST


                //CUANDO EL USUARIO PRESIONA EL BOTON DE LIKE
                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        isLikeChecked = true;
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUsName = user.getDisplayName();

                        mDatabase4Like.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (isLikeChecked) {
                                    //PARA SABER SI EL LIKE TIENE EL ID DEL USER PARA SABER SI YA DIO LIKE
                                    if (dataSnapshot.child(postId).hasChild(mAuth.getCurrentUser().getUid())) {
                                        //SI EXISTE, ENTOCES QUITA EL LIKE AL PULSARLO

                                        mDatabase4Like.child(postId).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        isLikeChecked = false;


                                    } else {
                                        mDatabase4Like.child(postId).child(mAuth.getCurrentUser().getUid()).setValue(currentUsName);
                                        isLikeChecked = false;

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });


            }
        };

        reportList.setAdapter(firebaseRecyclerAdapter);
    }

    private void logout() {
        mAuth.signOut(); //logout from firebase
        LoginManager.getInstance().logOut(); //logout from fb api

    }


    private void checkIfUserExists() {

        if (mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent SetupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        SetupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(SetupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    @Override
    public void onBackPressed() {
        /*
        startActivity(new Intent(MainActivity.this,MainActivity.class));*/

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Reportalo App")
                .setMessage("Seguro que quieres salir?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(MainActivity.this);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();




    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        View mView;

        CheckBox mLikeBtn;
        DatabaseReference mDatabaseLike; //Para usarse en el metodo de los likes
        FirebaseAuth mAuth;  //Para usarse en el metodo de los likes


        public ReportViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mLikeBtn = (CheckBox) mView.findViewById(R.id.checkbox_para_votar);
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();

            mDatabaseLike.keepSynced(true);

        }

        public void setmLikeBtn(final String postId) {

            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //AQUI DIRECTAMENTE VEO SI ESTA LIKEADO O NO EL POST POR EL CURRENT USER
                    if (dataSnapshot.child(postId).hasChild(mAuth.getCurrentUser().getUid())) {
                        mLikeBtn.setChecked(true);

                    } else {
                        mLikeBtn.setChecked(false);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void setTitle(String Titulo) {
            TextView postTitle = (TextView) mView.findViewById(R.id.title_report);
            postTitle.setText(Titulo);


        }

        public void setDesc(String Descripcion) {
            TextView postDesc = (TextView) mView.findViewById(R.id.desc_report);
            postDesc.setText(Descripcion);


        }

        public void setUsername(String username) {
            TextView post_username = (TextView) mView.findViewById(R.id.idUserOnPost);
            post_username.setText(username);

        }

        public void setImage(Context cx, String image) {
            ImageView postImage = (ImageView) mView.findViewById(R.id.img_report);

            Picasso.with(cx)
                    .load(image)
                    .fit()
                    .error(R.drawable.error)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(postImage);


        }

        public void setUserImage(String userImage, Context cx) {
            ImageView postUserImage = (ImageView) mView.findViewById(R.id.postUserImage);
            Picasso.with(cx)
                    .load(userImage)
                    .fit()
                    .error(R.drawable.error)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(postUserImage);

        }
    }


}


