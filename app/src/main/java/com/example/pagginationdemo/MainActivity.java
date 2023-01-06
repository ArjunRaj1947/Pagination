package com.example.pagginationdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<MainData> dataArrayList = new ArrayList<>();
    MyAdapter adapter;
    int page = 1, limit = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nestedScrollView = findViewById(R.id.scroll_view);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        //initialize adapter
        adapter = new MyAdapter(MainActivity.this, dataArrayList);
        //set layout manger
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //set adapter
        recyclerView.setAdapter(adapter);

        //create get data method
        getData(page, limit);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //check condition
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    //when reach last item position
                    //increase page Size
                    page++;
                    //show progress bar
                    progressBar.setVisibility(View.VISIBLE);
                    //call method
                    getData(page, limit);

                }
            }
        });
    }

    private void getData(int page, int limit) {
        //initialize retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://picsum.photos/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        //create main interface
        MainInterface mainInterface = retrofit.create(MainInterface.class);
        //Initialize call
        Call<String> call = mainInterface.STRING_CALL(page, limit);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //check condition
                if (response.isSuccessful() && response.body() != null) {
                    //when response is successful and not empty
                    //Hide progress bar
                    progressBar.setVisibility(View.GONE);

                    try {
                        //Initialize json array
                        JSONArray jsonArray = new JSONArray(response.body());
                        //parse json array
                        parseResult(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });

    }

    private void parseResult(JSONArray jsonArray) {
        //for loop
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                //Initialize json object
                JSONObject object = jsonArray.getJSONObject(i);
                //Initialize main data
                MainData data = new MainData();
                //set Id
                data.setId("Image: " + object.getString("id"));
                //set image
                data.setImage(object.getString("download_url"));
                //set name
                data.setName("Click By: " + object.getString("author"));
                //add data in array list
                dataArrayList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Initialize adapter
            adapter = new MyAdapter(MainActivity.this, dataArrayList);
            //set adapter
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_manu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search Here!");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}