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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;

import guate.armandos20.com.mangaso.AdaptadorFirestore.AllAnimesRecyclerViewAdapter;
import guate.armandos20.com.mangaso.AdaptadorFirestore.PopularesRecyclerViewAdapter;
import guate.armandos20.com.mangaso.Entidades.Home;
import guate.armandos20.com.mangaso.R;

public class PopularesFragment extends Fragment implements
        View.OnClickListener {
    private static final String TAG = "PopularesFragment";
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RotateLoading mRotateLoading;

    //vars
    private View mParentLayout;
    private ArrayList<Home> mNotes = new ArrayList<>();
    private PopularesRecyclerViewAdapter mPopularesRecyclerViewAdapter;
    private DocumentSnapshot mLastQueriedDocument;

    public PopularesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no3, container, false);
        mParentLayout = view.findViewById(android.R.id.content);
        mRecyclerView = view.findViewById(R.id.recycler_View);
        mRotateLoading = view.findViewById(R.id.rotateloading);

        mRotateLoading.start();

        initRecyclerView();
        getNotes();

        return view;
    }

    private void getNotes(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference notesCollectionRef = db.collection("Cartelera");

        Query noteQuery = null;
        if (mLastQueriedDocument != null){
            noteQuery = notesCollectionRef
                    .whereEqualTo("tendencia", "si")
                    .startAfter(mLastQueriedDocument);
        }else{
            noteQuery = notesCollectionRef
                    .whereEqualTo("tendencia", "si");
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

                    mRotateLoading.stop();
                    mPopularesRecyclerViewAdapter.notifyDataSetChanged();

                }else{
                    makeSnackBarMessage("Query Failed. Check Logs.");
                }
            }
        });
    }

    private void initRecyclerView() {
        if(mPopularesRecyclerViewAdapter == null){
            mPopularesRecyclerViewAdapter = new PopularesRecyclerViewAdapter(getContext(), mNotes);
        }
        //StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mPopularesRecyclerViewAdapter);
    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }
}
