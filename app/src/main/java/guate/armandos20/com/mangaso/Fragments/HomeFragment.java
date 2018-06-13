package guate.armandos20.com.mangaso.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import guate.armandos20.com.mangaso.AdaptadorFirestore.HomeRecyclerViewAdapter;
import guate.armandos20.com.mangaso.Adapters.MainAdapter;
import guate.armandos20.com.mangaso.Entidades.Home;
import guate.armandos20.com.mangaso.Entidades.SingleHorizontal;
import guate.armandos20.com.mangaso.Entidades.SingleVertical;
import guate.armandos20.com.mangaso.R;

public class HomeFragment extends Fragment implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "HomeFragment";
    private ArrayList<Object> objects = new ArrayList<>();

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //vars
    private View mParentLayout;
    private ArrayList<Home> mNotes = new ArrayList<>();
    private HomeRecyclerViewAdapter mNoteRecyclerViewAdapter;
    private DocumentSnapshot mLastQueriedDocument;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mParentLayout = view.findViewById(android.R.id.content);
        mRecyclerView = view.findViewById(R.id.recycler_View);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

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

                    mNoteRecyclerViewAdapter.notifyDataSetChanged();

                }else{
                    makeSnackBarMessage("Query Failed. Check Logs.");
                }
            }
        });
    }

    private void initRecyclerView() {
        if(mNoteRecyclerViewAdapter == null){
            mNoteRecyclerViewAdapter = new HomeRecyclerViewAdapter(getContext(), mNotes);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mNoteRecyclerViewAdapter);
    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private ArrayList<Object> getObject() {
        objects.add(getVerticalData().get(0));
        objects.add(getHorizontalData().get(0));
        return objects;
    }

    public static ArrayList<SingleVertical> getVerticalData() {
        ArrayList<SingleVertical> singleVerticals = new ArrayList<>();
        singleVerticals.add(new SingleVertical("Charlie Chaplin", "Sir Charles Spencer \"Charlie\" Chaplin, KBE was an English comic actor,....", R.drawable.prueba));
        singleVerticals.add(new SingleVertical("Mr.Bean", "Mr. Bean is a British sitcom created by Rowan Atkinson and Richard Curtis, and starring Atkinson as the title character.", R.drawable.prueba));
        singleVerticals.add(new SingleVertical("Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", R.drawable.prueba));
        return singleVerticals;
    }


    public static ArrayList<SingleHorizontal> getHorizontalData() {
        ArrayList<SingleHorizontal> singleHorizontals = new ArrayList<>();
        singleHorizontals.add(new SingleHorizontal(R.drawable.prueba, "Charlie Chaplin", "Sir Charles Spencer \"Charlie\" Chaplin, KBE was an English comic actor,....", "2010/2/1"));
        singleHorizontals.add(new SingleHorizontal(R.drawable.prueba, "Mr.Bean", "Mr. Bean is a British sitcom created by Rowan Atkinson and Richard Curtis, and starring Atkinson as the title character.", "2010/2/1"));
        singleHorizontals.add(new SingleHorizontal(R.drawable.prueba, "Jim Carrey", "James Eugene \"Jim\" Carrey is a Canadian-American actor, comedian, impressionist, screenwriter...", "2010/2/1"));
        return singleHorizontals;
    }

    @Override
    public void onClick(View v) {

    }
}
