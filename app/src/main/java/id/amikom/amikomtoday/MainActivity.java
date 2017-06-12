package id.amikom.amikomtoday;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;

    ArrayList<HashMap<String, String>> beritaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beritaList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        new GetBerita().execute();
    }

    private class GetBerita extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Fwww.amikom.ac.id%2Findex.php%2Ffeed%2Finfo_kampus&api_key=filc77uppyf1gjghknfn0bpw271xdejsva7jqcyr&order_by=pubDate&count=20";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray beritas = jsonObj.getJSONArray("items");

                    // looping through All beritas
                    for (int i = 0; i < beritas.length(); i++) {
                        JSONObject c = beritas.getJSONObject(i);
                        String title = c.getString("title");
                        String link = c.getString("link");
                        String content = c.getString("content");

                        // tmp hash map for single berita
                        HashMap<String, String> berita = new HashMap<>();

                        // adding each child node to HashMap key => value
                        berita.put("title", title);
                        berita.put("link", link);
                        berita.put("content", content);

                        // adding berita to berita list
                        beritaList.add(berita);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, beritaList,
                    R.layout.list_item, new String[]{ "title","link","content"},
                    new int[]{R.id.title, R.id.link, R.id.content});
            lv.setAdapter(adapter);
        }
    }
}