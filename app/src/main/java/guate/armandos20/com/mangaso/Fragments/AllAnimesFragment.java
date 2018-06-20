package guate.armandos20.com.mangaso.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import guate.armandos20.com.mangaso.Entidades.Home;
import guate.armandos20.com.mangaso.R;

public class AllAnimesFragment extends Fragment implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "AllAnimesFragment";

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RotateLoading mRotateLoading;

    //vars
    private View mParentLayout;
    private ArrayList<Home> mNotes = new ArrayList<>();
    private AllAnimesRecyclerViewAdapter mNoteRecyclerViewAdapter;
    private DocumentSnapshot mLastQueriedDocument;

    public AllAnimesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mParentLayout = view.findViewById(android.R.id.content);
        mRecyclerView = view.findViewById(R.id.recycler_View);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mRotateLoading = view.findViewById(R.id.rotateloading);

        mRotateLoading.start();

        mSwipeRefreshLayout.setOnRefreshListener(this);

        initRecyclerView();
        getNotes();

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
                    .whereEqualTo("id_pos", "1")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(mLastQueriedDocument);
        }else{
            noteQuery = notesCollectionRef
                    .whereEqualTo("id_pos", "1")
                    .orderBy("timestamp", Query.Direction.DESCENDING);
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
                    mNoteRecyclerViewAdapter.notifyDataSetChanged();

                }else{
                    makeSnackBarMessage("Query Failed. Check Logs.");
                }
            }
        });
    }

    private void initRecyclerView() {
        if(mNoteRecyclerViewAdapter == null){
            mNoteRecyclerViewAdapter = new AllAnimesRecyclerViewAdapter(getContext(), mNotes);
        }
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mNoteRecyclerViewAdapter);
    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem  = menu.findItem(R.id.action_search);
        SearchView searchView  = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mNoteRecyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }
}
