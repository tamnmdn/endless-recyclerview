package com.example.endlesslib;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.loadmore.EndlessRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EndlessRecyclerView endlessRecyclerView;
    MainAdapter mainAdapter;
    List<String> sampleData = new ArrayList<>();
    List<String> dataGet = new ArrayList<>();
    private int offsetLoad = 0;
    private int itemPerPage = 23;
    private int currentPage = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createSampleData();
        endlessRecyclerView = findViewById(R.id.rv_endless);
        mainAdapter = new MainAdapter();
        mainAdapter.setItems(dataGet);
        endlessRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        endlessRecyclerView.setAdapter(mainAdapter);

        endlessRecyclerView.setOnLoadMoreListener(new EndlessRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore(EndlessRecyclerView view) {
                getData(currentPage);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData(currentPage);
            }
        }, 5000);
    }

    private void createSampleData() {
        for (int i = 0; i < 243; i++) {
            sampleData.add("Item ----- " + i);
        }
    }

    private void getData(final int page) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> data = new ArrayList<>();
                for (int i = 0; i < itemPerPage; i++) {
                    data.add(sampleData.get(page * i));
                }
                initLoadMoreData(data);
            }
        }, 3000);
    }

    private void initLoadMoreData(List<String> results) {
        if (offsetLoad == 0) {
            dataGet.clear();
        }
        if (!results.isEmpty()) {
            int countResponse = results.size();
            dataGet.addAll(results);
            loadDataSuccess(countResponse, offsetLoad);

            if (countResponse < itemPerPage) {
                currentPage++;
                offsetLoad += countResponse;
                updateLoadingRecyclerView(EndlessRecyclerView.ScrollType.DONE);
            } else {
                currentPage++;
                offsetLoad += itemPerPage;
                updateLoadingRecyclerView(EndlessRecyclerView.ScrollType.IN_PROGRESS);
            }
        } else {
            loadDataSuccess(0, offsetLoad);
            updateLoadingRecyclerView(EndlessRecyclerView.ScrollType.DONE);
        }
    }

    public void loadDataSuccess(int countResponse, int offset) {
        if (offset == 0 && countResponse == 0) {
            //TODO mTvNoResult.setVisibility(View.VISIBLE);
            endlessRecyclerView.setVisibility(View.GONE);
        } else {
            //TODO mTvNoResult.setVisibility(View.GONE);
            endlessRecyclerView.setVisibility(View.VISIBLE);
            mainAdapter.notifyItemRangeInserted(offset, countResponse);
        }
    }

    public void updateLoadingRecyclerView(EndlessRecyclerView.ScrollType scrollType) {
        endlessRecyclerView.setLoading(scrollType);
    }
}
