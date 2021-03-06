package guate.armandos20.com.mangaso.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.victor.loading.book.BookLoading;

import java.util.ArrayList;

import guate.armandos20.com.mangaso.AdaptadorFirestore.AllAnimesRecyclerViewAdapter;
import guate.armandos20.com.mangaso.AdaptadorFirestore.HomeRecyclerViewAdapter;
import guate.armandos20.com.mangaso.Entidades.Home;
import guate.armandos20.com.mangaso.MainActivity;
import guate.armandos20.com.mangaso.R;

public class HomeFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BookLoading mBookLoading;

    //vars
    private View mParentLayout;
    private ArrayList<Home> mNotes = new ArrayList<>();
    private HomeRecyclerViewAdapter mRecyclerViewAdapter;
    private DocumentSnapshot mLastQueriedDocument;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no1, container, false);
        mParentLayout = view.findViewById(android.R.id.content);
        mRecyclerView = view.findViewById(R.id.recycler_View);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mBookLoading = view.findViewById(R.id.bookloading);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mBookLoading.start();

        initRecyclerView();
        getNotes();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Hola que hace XD?", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(),MangaDetalleActivity.class));
                //NewNoteDialog dialog = new NewNoteDialog();
                //dialog.show(getSupportFragmentManager(), getString(R.string.dialog_new_note));
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        return view;
    }

    @Override
    public void onRefresh() {
        getNotes();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void getNotes(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference notesCollectionRef = db.collection("Cartelera");

        Query noteQuery = null;
        if (mLastQueriedDocument != null){
            noteQuery = notesCollectionRef
                    .whereEqualTo("usuarios", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    /*.orderBy("timestamp", Query.Direction.DESCENDING)*/
                    .startAfter(mLastQueriedDocument);
        }else{
            noteQuery = notesCollectionRef
                    .whereEqualTo("usuarios", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    /*.orderBy("timestamp", Query.Direction.DESCENDING);*/
        }

        noteQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    for (QueryDocumentSnapshot document : task.getResult()){
                        Home note = document.toObject(Home.class);
                        mNotes.add(note);
                    }

                    if (task.getResult().size() != 0){
                        mLastQueriedDocument = task.getResult()
                                .getDocuments()
                                .get(task.getResult().size() - 1);
                    }

                    mRecyclerViewAdapter.notifyDataSetChanged();

                }else{
                    makeSnackBarMessage("Query Failed. Check Logs.");
                }
            }
        });
    }

    private void initRecyclerView() {
        if(mRecyclerViewAdapter == null){
            mRecyclerViewAdapter = new HomeRecyclerViewAdapter(getContext(), mNotes);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

}
