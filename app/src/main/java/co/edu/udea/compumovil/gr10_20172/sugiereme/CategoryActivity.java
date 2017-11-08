package co.edu.udea.compumovil.gr10_20172.sugiereme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity implements CategoryAdapter.OnRecyclerItemClickListener {
    RecyclerView recyclerView;
    CategoryAdapter mAdapter;
    List<Category> categories;
    ChildEventListener childEventListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference categoriesDatabaseReference;
    List<Category> selectedCategories;
    FirebaseAuth firebaseAuth;
    DatabaseReference userCategoriesDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categories=new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.category_activity_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getBaseContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        mAdapter=new CategoryAdapter(categories, getBaseContext(),this);
        recyclerView.setAdapter(mAdapter);

        firebaseDatabase=FirebaseDatabase.getInstance();
        categoriesDatabaseReference=firebaseDatabase.getReference().child("categories");
        firebaseAuth=FirebaseAuth.getInstance();
        userCategoriesDatabaseReference=firebaseDatabase.getReference().child(firebaseAuth.getCurrentUser().getUid()).child("categories");

        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Category addedCategory=dataSnapshot.getValue(Category.class);
                categories.add(addedCategory);
                mAdapter.setCategoryList(categories);
                recyclerView.setAdapter(mAdapter);
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
        };
        categoriesDatabaseReference.addChildEventListener(childEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        categories=new ArrayList<>();
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (childEventListener==null){
            childEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Category addedCategory = dataSnapshot.getValue(Category.class);
                    categories.add(addedCategory);
                    mAdapter.setCategoryList(categories);
                    recyclerView.setAdapter(mAdapter);
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
            };
            categoriesDatabaseReference.addChildEventListener(childEventListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        mAdapter.setCategoryList(new ArrayList<Category>());
    }

    private void detachDatabaseReadListener() {
        if (childEventListener!=null){
            categoriesDatabaseReference.removeEventListener(childEventListener);
            childEventListener=null;
        }
    }

    public void onContinuarClick(View view){
        for (Category category:
             selectedCategories) {
            userCategoriesDatabaseReference.push().setValue(category);
        }
        Intent intent = new Intent(this,CategorySelectActivity.class);
        startActivity(intent);
    }

    public void onRegresarClick(View view){
        firebaseAuth.signOut();
        this.finish();
    }

    @Override
    public void onItemClicked(View v, int position) {
        selectedCategories.add(categories.get(position));
    }
}
