package com.npdevelopment.geoguessswipe;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener {

    List<GeoObject> mGeoObjects;
    private GestureDetector mGestureDetector;
    private GeoObjectAdapter mAdapter;
    private TextView mScoreText;

    private String mImageAnswer;
    private int score = 0;
    private int totalCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScoreText = findViewById(R.id.scoreText);

        mGeoObjects = new ArrayList<>();

        for (int i = 0; i < GeoObject.PRE_DEFINED_GEO_OBJECT_NAMES.length; i++) {
            mGeoObjects.add(new GeoObject(GeoObject.PRE_DEFINED_GEO_OBJECT_NAMES[i],
                    GeoObject.PRE_DEFINED_GEO_OBJECT_IMAGE_IDS[i]));
        }

        final RecyclerView mGeoRecyclerView = findViewById(R.id.recycleView);
        totalCountries = mGeoObjects.size();

        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);

        // Set layout in the parent layout with just one item in a row
        mGeoRecyclerView.setLayoutManager(mLayoutManager);
        mGeoRecyclerView.setHasFixedSize(true);

        // Add list to recycler view
        mAdapter = new GeoObjectAdapter(this, mGeoObjects);
        mGeoRecyclerView.setAdapter(mAdapter);

        mGeoRecyclerView.addOnItemTouchListener(this);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        /*
        Add a touch helper to the RecyclerView to recognize when a user swipes to delete a list entry.
        An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
        and uses callbacks to signal when a user is performing these actions.
        */
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    //Called when a user swipes left or right on a ViewHolder
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        //Get the index corresponding to the selected position
                        int position = (viewHolder.getAdapterPosition());

                        checkAnswer(position, swipeDir);

                        mGeoObjects.remove(position);
                        mAdapter.notifyItemRemoved(position);

                        if (position == mGeoObjects.size()) {
                            mScoreText.setText(getString(R.string.quiz_results) + " " + score + "/" + totalCountries);
                            mScoreText.setVisibility(View.VISIBLE);
                        }
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mGeoRecyclerView);
    }

    /**
     * Check if answer is correct based on the yes or no word in the file name
     *
     * @param index          of the object in the list
     * @param swipeDirection swipe direction of the user, left or right
     */
    public void checkAnswer(int index, int swipeDirection) {
        mImageAnswer = extractAnswerFromImageName(index);

        switch (swipeDirection) {
            case ItemTouchHelper.LEFT:
                if (mImageAnswer.equals("yes")) {
                    score++;
                    addTextForToast(getString(R.string.correct_txt), index, getString(R.string.european_country));
                } else {
                    addTextForToast(getString(R.string.incorrect_txt), index, getString(R.string.not_european_country));
                }
                break;
            case ItemTouchHelper.RIGHT:
                if (mImageAnswer.equals("no")) {
                    score++;
                    addTextForToast(getString(R.string.correct_txt), index, getString(R.string.not_european_country));
                } else {
                    addTextForToast(getString(R.string.incorrect_txt), index, getString(R.string.european_country));
                }
                break;
            default:
                break;
        }
    }

    /**
     * @param index of the object in the list
     * @return yes or no, that is tagged in the file name
     */
    public String extractAnswerFromImageName(int index) {
        String mAnswer = getResources().getResourceName(mGeoObjects.get(index).getmGeoImageName());
        mAnswer = mAnswer.substring(mAnswer.indexOf("_") + 1, mAnswer.lastIndexOf("_"));
        return mAnswer;
    }

    public void addTextForToast(String status, int countryIndex, String text) {
        Toast.makeText(getApplicationContext(),
                status + "! " + mGeoObjects.get(countryIndex).getmGeoName() +
                        " " + text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        int mAdapterPosition = rv.getChildAdapterPosition(child);

        if (child != null && mGestureDetector.onTouchEvent(e)) {
            Toast.makeText(this, mGeoObjects.get(mAdapterPosition).getmGeoName(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {
    }
}
