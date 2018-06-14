package guate.armandos20.com.mangaso;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;
import guate.armandos20.com.mangaso.AdaptadorFirestore.HomeRecyclerViewAdapter;
import guate.armandos20.com.mangaso.Dialog.NewNoteDialog;
import guate.armandos20.com.mangaso.Dialog.ViewNoteDialog;
import guate.armandos20.com.mangaso.Entidades.Home;
import guate.armandos20.com.mangaso.Fragments.HomeFragment;
import guate.armandos20.com.mangaso.Fragments.No1Fragment;
import guate.armandos20.com.mangaso.Fragments.No2Fragment;
import guate.armandos20.com.mangaso.Fragments.No3Fragment;
import guate.armandos20.com.mangaso.Interfaz.IMainActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        IMainActivity{

    private static final String TAG = "MainActivity";

    //Fragmento Padre
    private Fragment fragmentoGenerico = null;
    private View mParentLayout;
    private HomeRecyclerViewAdapter mHomeRecyclerViewAdapter;

    //firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mParentLayout = findViewById(android.R.id.content);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,  this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //Metodo de verificacion de las diferentes cuentas de acceso a la app
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            mFirebaseUser = null;

           startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MangaDetalleActivity.class));
                //NewNoteDialog dialog = new NewNoteDialog();
                //dialog.show(getSupportFragmentManager(), getString(R.string.dialog_new_note));
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (mFirebaseUser != null){

            //TODO: ESTO PERMITE CAMBIARLE EL TEXTO EN EL ENCABEZADO

            View hView = navigationView.getHeaderView(0);
            final TextView nav_user = hView.findViewById(R.id.tv_nombre);
            final CircleImageView profile = hView.findViewById(R.id.imageView);

            nav_user.setText(mFirebaseUser.getDisplayName());

            if (mFirebaseUser.getPhotoUrl() != null) {
                Glide.with(this).load(mFirebaseUser.getPhotoUrl()).into(profile);
            }
        }

        if (navigationView != null) {
            //prepararDrawer(navigationView);
            // Seleccionar item por defecto
            setTitle(navigationView.getMenu().getItem(0).getTitle());
            fragmentoGenerico = new HomeFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contenedorDeCosas,fragmentoGenerico).commit();
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
           /* MediaFragment mediaFragment = new MediaFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contenedorDeCosas,mediaFragment).commit();*/
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentoGenerico instanceof HomeFragment){
                super.onBackPressed();
            }else{
                showHome();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_camera) {
            fragmentoGenerico = new HomeFragment();
        } else if (id == R.id.nav_gallery) {
            fragmentoGenerico = new No1Fragment();
        } else if (id == R.id.nav_slideshow) {
            fragmentoGenerico = new No2Fragment();
        } else if (id == R.id.nav_manage) {
            fragmentoGenerico = new No3Fragment();
        } else if (id == R.id.nav_share) {
            
        } else if (id == R.id.nav_send) {
            if (mFirebaseUser != null){
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }else{
                Toast.makeText(this, "Emos tenido un error al momento de cerrar secion!", Toast.LENGTH_SHORT).show();
            }
        }

        if (fragmentoGenerico != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.contenedorDeCosas, fragmentoGenerico)
                    .commit();
        }

        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    //METODO encargado de cargar el fragment HOME
    private void showHome(){
        fragmentoGenerico = new HomeFragment();
        if (fragmentoGenerico != null){
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);// Colocas el id de tu NavigationView
            setTitle(navigationView.getMenu().getItem(0).getTitle());
            FragmentManager  manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.contenedorDeCosas, fragmentoGenerico, fragmentoGenerico.getTag()).commit();
        }
    }

    @Override
    public void createNewNote(String title, String content) {
        //Aqui me quede
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference newNoteRef = db.collection("Cartelera")
                .document();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Home note = new Home();
        note.setTitulo(title);
        note.setDescripcion(content);
        note.setCartelera_id(newNoteRef.getId());
        note.setId_pos("1");

        newNoteRef.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    makeSnackBarMessage("Created a new note");
                }else{
                    makeSnackBarMessage("Failed. Check Log");
                }
            }
        });
    }

    @Override
    public void onNoteSelected(Home note) {
        //startActivity(new Intent(this,MangaDetalleActivity.class));
        Intent intent = new Intent(this, MangaDetalleActivity.class);
        intent.putExtra("miLista", note);
        startActivity(intent);

        /*ViewNoteDialog dialog = ViewNoteDialog.newInstance(note);
        dialog.show(getSupportFragmentManager(),getString(R.string.dialog_view_note));*/
    }

    @Override
    public void updateNote(final Home note) {
        /*FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference noteRef = db.collection("Cartelera")
                .document(note.getCartelera_id());

        noteRef.update(
                "titulo", note.getTitulo(),
                "descripcion", note.getDescripcion()
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    makeSnackBarMessage("Updated Note ");
                    //mHomeRecyclerViewAdapter.updateNote(note);
                }else{
                    makeSnackBarMessage("Failed. Check Logs.");
                }
            }
        });*/
        Toast.makeText(this, "No es requerido para esta version", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void deleteNote(final Home note) {
        Toast.makeText(this, "No es requerido para esta version", Toast.LENGTH_SHORT).show();

        /* db = FirebaseFirestore.getInstance();

        DocumentReference noteRef = db.collection("Cartelera")
                .document(note.getCartelera_id());

        noteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    makeSnackBarMessage("Deleted Note.");
                    //mHomeRecyclerViewAdapter.removeNote(note);
                }else{
                    makeSnackBarMessage("Failed. Check Log.");
                }
            }
        });*/
    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
