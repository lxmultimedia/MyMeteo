package ch.ffhs.esa.mymeteo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.LruCache;
import android.widget.GridView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import ch.ffhs.esa.mymeteo.imagecrawler.ImageAdapter;
import ch.ffhs.esa.mymeteo.imagecrawler.ImageResult;


public class ImageCrawlerActivity extends AppCompatActivity {

    public final int MAX_RESULT_COUNT = 10;
    private final int PAGE_SIZE = 8;
    private final String SEARCH_ENDPOINT = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?count=10&offset=0&mkt=en-us&safeSearch=Moderate&";

    private ImageAdapter resultsImageAdapter;
    private GridView resultsGrid;
    public LruCache<String, Bitmap> imageCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crawler);

        resultsGrid = (GridView) findViewById(R.id.grid_view);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String query = intent.getStringExtra("locationName");
        doSearch(query);
    }

    protected void asyncJson(String url, final int start, final ImageAdapter searchResultsAdapter){

        // TODO: Set cache to be big enough to handle 50 images at a time
        // TODO: Figure out if and how that will fail if it exceeds max memory for the app
        //tutorial default cache size - 8th of allowed memory size
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        imageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        final AQuery aq = new AQuery(findViewById(R.id.mainLayoutRoot));
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                /*
                 * TODO: Extend AjaxCallback so this line written once and can be reused for other AJAX
                 * queries that should only continue if they are results for the "current" search
                 */
                if(!isAttached(searchResultsAdapter)) return;

                if(json != null){
                    try{
                        JSONArray results = json.getJSONArray("value");
                        for(int i=0; i<results.length(); i++){
                            final ImageResult imageResult = new ImageResult();
                            imageResult.imgUrl = results.getJSONObject(i).getString("thumbnailUrl");
                            imageResult.resultIndex = start + i;

                            if(imageResult.resultIndex >= 10){
                                return;
                            }

                            //Load the image for this result in background before adding to adaptor
                            aq.ajax(imageResult.imgUrl, Bitmap.class, new AjaxCallback<Bitmap>() {
                                @Override
                                public void callback(String url, Bitmap bitmap, AjaxStatus status) {

                                    if(!isAttached(searchResultsAdapter)) return;

                                    imageCache.put(imageResult.imgUrl, bitmap);
                                    resultsImageAdapter.addResult(imageResult);
                                    resultsGrid.invalidateViews();
                                }
                            });
                        }
                    }catch (JSONException e){
                        //TODO: handle errors
                    }
                }else{
                    //TODO: handle errors
                }
            }
        };
        cb.header("Ocp-Apim-Subscription-Key", "5847911c6a3d4048a0e68cbba5f806f3");
        aq.ajax(url, JSONObject.class, cb);
    }

    protected boolean isAttached(ImageAdapter adapter) {
        return resultsGrid.getAdapter() == adapter;
    }

    protected void runSearch(String query) {

        try {
            asyncJson(SEARCH_ENDPOINT + "&q=" + URLEncoder.encode(query, "UTF-8"),0, resultsImageAdapter);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void doSearch(String query) {

        //Trash the last query results if there was one
        resultsImageAdapter = new ImageAdapter(this);
        resultsGrid.setAdapter(resultsImageAdapter);
        resultsGrid.invalidateViews();

        new SearchTask(this).execute(query);
    }
}

class SearchTask extends AsyncTask<String, Void, Void> {

    private ImageCrawlerActivity context;

    public SearchTask(ImageCrawlerActivity context) {
        this.context = context;
    }

    protected Void doInBackground(String... args) {
        context.runSearch(args[0]);
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(Long result) {
    }
}
