package guate.armandos20.com.mangaso.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import guate.armandos20.com.mangaso.R;

public class ViewMangaReadActivity extends AppCompatActivity {
    private static final String TAG = "ViewMangaReadActivity";
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_manga_read);


        pdfView = findViewById(R.id.pdfView);
        new RetrievePDFStream().execute("http://adrax.hol.es/BNHA/bnha%20131.pdf");
    }

    class RetrievePDFStream extends AsyncTask<String,Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try{
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == 200){
                    inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                }
            }catch (IOException e){
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }
}
