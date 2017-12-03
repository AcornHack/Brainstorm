package com.optimism.brainstorm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements ChildEventListener {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        adapter.addSubject(new Subject(dataSnapshot.getKey(), (String) dataSnapshot.getValue()));
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

    private static class Subject {
        String name;
        StorageReference image;

        Subject(String name, String image) {
            this.name = name;
            this.image = FirebaseStorage.getInstance().getReference("/" + image);
        }
    }

    private static final Subject[] SUBJECTS = new Subject[] {
            new Subject("Maths", "#03A9F4"),
            new Subject("English", "#795548"),
            new Subject("French", "#E91E63"),
            new Subject("Geography", "#4CAF50")
    };

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private SubjectsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.home_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter = new SubjectsAdapter());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference subjectsReference = database.getReference("subjects");
        subjectsReference.addChildEventListener(this);

        return view;
    }

    private static class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {
        private ArrayList<Subject> subjects;

        SubjectsAdapter() {
            subjects = new ArrayList<>();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView;
            CardView cardView;

            ViewHolder(View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.card_subject_image_view);
                textView = itemView.findViewById(R.id.card_subject_text_view);
                cardView = itemView.findViewById(R.id.card_view);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View root = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_subject, parent, false);

            return new ViewHolder(root);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Subject subject = subjects.get(position);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), SubjectActivity.class);
                    i.putExtra("subject", subject.name);
                    v.getContext().startActivity(i);
                }
            });

            GlideApp.with(holder.itemView.getContext())
                    .load(subject.image)
                    .into(holder.imageView);

            holder.textView.setText(subject.name);
        }

        @Override
        public int getItemCount() {
            return subjects.size();
        }

        void addSubject(Subject subject) {
            subjects.add(subject);
            notifyItemInserted(subjects.size() - 1);
        }
    }
}
