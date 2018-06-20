package guate.armandos20.com.mangaso.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;

import guate.armandos20.com.mangaso.Activities.MangaDetalleActivity;
import guate.armandos20.com.mangaso.AdaptadorFirestore.PeliculasRecyclerViewAdapter;
import guate.armandos20.com.mangaso.AdaptadorFirestore.PopularesRecyclerViewAdapter;
import guate.armandos20.com.mangaso.Entidades.Home;
import guate.armandos20.com.mangaso.Entidades.Peliculas;
import guate.armandos20.com.mangaso.R;

public class PeliculasFragment extends Fragment {
    private static final String TAG = "PeliculasFragment";
    //widgets
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RotateLoading mRotateLoading;

    //vars
    private View mParentLayout;
    private ArrayList<Peliculas> mNotes = new ArrayList<>();
    private PeliculasRecyclerViewAdapter mPopularesRecyclerViewAdapter;
    private DocumentSnapshot mLastQueriedDocument;

    public PeliculasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_peliculas, container, false);
        mParentLayout = view.findViewById(android.R.id.content);
        mRecyclerView = view.findViewById(R.id.recycler_View);
        mRotateLoading = view.findViewById(R.id.rotateloading);

        mRotateLoading.start();

        initRecyclerView();
        getNotes();

        //isRootTask();

        return view;
    }


    private void getNotes(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference notesCollectionRef = db.collection("Peliculas");

        Query noteQuery = null;
        if (mLastQueriedDocument != null){
            noteQuery = notesCollectionRef
                    .whereEqualTo("estado", "1")
                    .startAfter(mLastQueriedDocument);
        }else{
            noteQuery = notesCollectionRef
                    .whereEqualTo("estado", "1");
        }

        noteQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    for (QueryDocumentSnapshot document : task.getResult()){
                        Peliculas peliculas = document.toObject(Peliculas.class);
                        mNotes.add(peliculas);
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
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initRecyclerView() {
        if(mPopularesRecyclerViewAdapter == null){
            mPopularesRecyclerViewAdapter = new PeliculasRecyclerViewAdapter(getContext(), mNotes);
        }
        //StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mPopularesRecyclerViewAdapter);
    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }
    /*private boolean isRootTask(){
        if (((MangaDetalleActivity)getActivity()).getTask() == 0){
            return true;
        }else{
            return false;
        }
    }*/

}
