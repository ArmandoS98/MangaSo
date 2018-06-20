package guate.armandos20.com.mangaso.AdaptadorFirestore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import guate.armandos20.com.mangaso.Entidades.Home;
import guate.armandos20.com.mangaso.Interfaz.IMainActivity;
import guate.armandos20.com.mangaso.R;

public class PopularesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "AllAnimesRecyclerViewAdapter";
    private ArrayList<Home> mNotes = new ArrayList<>();
    private IMainActivity mIMainActivity;
    private Context mContext;
    private int mSelectedNoteIndex;

    public PopularesRecyclerViewAdapter(Context context, ArrayList<Home> notes) {
        mNotes = notes;
        mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_populares_item, parent, false);

        holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            Glide.with(mContext).load(mNotes.get(position).getUrl_portada()).into(((ViewHolder)holder).preview);
            ((ViewHolder)holder).title.setText(mNotes.get(position).getTitulo());

            ((ViewHolder)holder).ranking.setText("Estado: " + mNotes.get(position).getEstado());
            /*SimpleDateFormat spf = new SimpleDateFormat("MMM dd, yyyy");
            String date = spf.format(mNotes.get(position).getTimestamp());
            ((ViewHolder)holder).timestamp.setText(date);*/
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.opciones_cards, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new opciones_cardview());
        popupMenu.show();
    }

    class opciones_cardview implements PopupMenu.OnMenuItemClickListener{

        public opciones_cardview() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()){
                case R.id.menu_item_ver_mas:
                    Toast.makeText(mContext, "Ver mas...", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.menu_item_add_to_favorite:
                    Toast.makeText(mContext, "Añadiendo a favorito!", Toast.LENGTH_SHORT).show();
                    return true;
                    default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void updateNote(Home note){
        mNotes.get(mSelectedNoteIndex).setTitulo(note.getTitulo());
        mNotes.get(mSelectedNoteIndex).setDescripcion(note.getDescripcion());
        notifyDataSetChanged();
    }

    public void removeNote(Home note){
        mNotes.remove(note);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIMainActivity = (IMainActivity) mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, timestamp, ranking;
        ImageView preview, imgoverflow;

        public ViewHolder(View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.imagen_preview);
            title = itemView.findViewById(R.id.title);
            ranking = itemView.findViewById(R.id.ranking);

            //timestamp = itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mSelectedNoteIndex = getAdapterPosition();
            mIMainActivity.onNoteSelected(mNotes.get(mSelectedNoteIndex));
        }
    }
}
