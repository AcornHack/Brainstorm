package com.optimism.brainstorm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class SubjectActivity extends AppCompatActivity implements View.OnClickListener, ChildEventListener {
    private String filterLevel = "No Filter", filterTopic = "No Filter", filterType = "No Filter";

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        String description = (String) dataSnapshot.child("description").getValue();
        String image = (String) dataSnapshot.child("image").getValue();
        String learningType = (String) dataSnapshot.child("learningType").getValue();
        String level = (String) dataSnapshot.child("level").getValue();
        String title = (String) dataSnapshot.child("title").getValue();
        String topics = (String) dataSnapshot.child("topics").getValue();
        String url = (String) dataSnapshot.child("url").getValue();
        
        LearningActivity activity = new LearningActivity(description, image, learningType, level, title, topics, url);

        if(!filterLevel.equals("No Filter")) {
            if(!filterLevel.equals(activity.level)) {
                return;
            }
        }

        if(!filterTopic.equals("No Filter")) {
            if(!filterTopic.equals(activity.topics)) {
                return;
            }
        }

        if(!filterType.equals("No Filter")) {
            if(!filterType.equals(activity.learningType)) {
                return;
            }
        }

        activityAdapter.addItem(activity);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public class LearningActivity {
        public String description;
        public String image;
        public String learningType;
        public String level;
        public String title;
        public String topics;
        public String url;

        public LearningActivity(String description, String image, String learningType, String level, String title, String topics, String url) {
            this.description = description;
            this.image = image;
            this.learningType = learningType;
            this.level = level;
            this.title = title;
            this.topics = topics;
            this.url = url;
        }

        @Override
        public String toString() {
            return title + ": " + url;
        }
    }

    private ActivityAdapter activityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        RecyclerView recyclerView = findViewById(R.id.subject_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(activityAdapter = new ActivityAdapter());

        String subject = getIntent().getStringExtra("subject");

        Button editFilterButton = findViewById(R.id.edit_filter_button);
        editFilterButton.setOnClickListener(this);

        //noinspection ConstantConditions
        getSupportActionBar().setTitle(subject);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("resources");
        updateData();
    }

    private static final String[] LEVELS = new String[] {
            "No Filter",
            "KS1",
            "KS2",
            "KS3",
            "KS4",
            "A Level"
    };

    private static final String[] TOPICS = new String[] {
            "No Filter",
            "Factorising",
            "Geometry",
            "Probability"
    };

    private static final String[] TYPES = new String[] {
            "No Filter",
            "Video",
            "Audio",
            "Interactive",
            "Text"
    };

    DatabaseReference reference;
    private void updateData() {
        activityAdapter.resetDataset();

        reference.removeEventListener(this);
        reference.addChildEventListener(this);
}

    @Override
    public void onClick(View v) {
        int p = (int) getResources().getDimension(R.dimen.fab_margin);

        LinearLayout root = new LinearLayout(this);
        root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(p, p, p, p);

        TextView levelView = new TextView(this); levelView.setText("Level");
        final Spinner levelSpinner = new Spinner(this);
        levelSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        levelSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, LEVELS));
        levelSpinner.setSelection(Arrays.asList(LEVELS).indexOf(filterLevel));

        TextView topicView = new TextView(this); topicView.setText("Topic");
        final Spinner topicSpinner = new Spinner(this);
        topicSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        topicSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, TOPICS));
        topicSpinner.setSelection(Arrays.asList(TOPICS).indexOf(filterTopic));

        TextView typeView = new TextView(this); typeView.setText("Type");
        final Spinner typeSpinner = new Spinner(this);
        typeSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        typeSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, TYPES));
        typeSpinner.setSelection(Arrays.asList(TYPES).indexOf(filterType));

        root.addView(levelView);
        root.addView(levelSpinner);

        root.addView(topicView);
        root.addView(topicSpinner);

        root.addView(typeView);
        root.addView(typeSpinner);

        Button button = new Button(this);
        button.setText("Edit");
        root.addView(button);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(root);

        final AlertDialog dialog = builder.create();
        dialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                filterLevel = levelSpinner.getSelectedItem().toString();
                filterTopic = topicSpinner.getSelectedItem().toString();
                filterType = typeSpinner.getSelectedItem().toString();
                updateData();
            }
        });
    }

    private static class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
        ArrayList<LearningActivity> dataset;

        ActivityAdapter() {
            dataset = new ArrayList<>();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            ImageView imageView;
            TextView titleView;
            TextView descriptionView;

            ViewHolder(View itemView) {
                super(itemView);

                cardView = itemView.findViewById(R.id.card_view);
                imageView = itemView.findViewById(R.id.card_activity_image_view);
                titleView = itemView.findViewById(R.id.card_activity_title_text_view);
                descriptionView = itemView.findViewById(R.id.card_activity_description_text_view);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View root = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_learning_activity, parent, false);

            return new ViewHolder(root);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final LearningActivity learningActivity = dataset.get(position);

            holder.titleView.setText(learningActivity.title);
            holder.descriptionView.setText(learningActivity.description);
            holder.imageView.setImageBitmap(null);
            new DownloadImageTask(holder.imageView)
            .execute(learningActivity.image);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(learningActivity.url));
                    v.getContext().startActivity(browserIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        void resetDataset() {
            this.dataset.clear();
            notifyDataSetChanged();
        }

        void addItem(LearningActivity activity) {
            this.dataset.add(activity);
            notifyItemInserted(dataset.size() - 1);
        }
    }

    //ew DownloadImageTask((ImageView) findViewById(R.id.imageView1))
            //.execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
