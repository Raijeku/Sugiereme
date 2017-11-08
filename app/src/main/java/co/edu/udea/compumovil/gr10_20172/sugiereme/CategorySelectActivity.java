package co.edu.udea.compumovil.gr10_20172.sugiereme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectActivity extends AppCompatActivity implements CategoryAdapter.OnRecyclerItemClickListener {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userCategoriesDatabaseReference;
    ChildEventListener childEventListener;
    FirebaseAuth firebaseAuth;
    CategoryAdapter mAdapter;
    List<Category> categories;
    RecyclerView recyclerView;
    Button salirButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gustos);

        salirButton=(Button) findViewById(R.id.gustos_button);
        salirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSalirClick(view);
            }
        });

        categories=new ArrayList<>();
        recyclerView=(RecyclerView) findViewById(R.id.gustos_lista);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getBaseContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        mAdapter=new CategoryAdapter(categories,getBaseContext(),this);
        recyclerView.setAdapter(mAdapter);

        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        userCategoriesDatabaseReference=firebaseDatabase.getReference().child(firebaseAuth.getCurrentUser().getUid()).child("categories");

        childEventListener = new ChildEventListener() {
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
        userCategoriesDatabaseReference.addChildEventListener(childEventListener);
    }

    public void onSalirClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
            userCategoriesDatabaseReference.addChildEventListener(childEventListener);
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
            userCategoriesDatabaseReference.removeEventListener(childEventListener);
            childEventListener=null;
        }
    }

    @Override
    public void onItemClicked(View v, int position) {
        Intent intent=new Intent(CategorySelectActivity.this,GustosActivity.class);
        intent.putExtra("categoryTitle",categories.get(position).getTitle());
        startActivity(intent);
    }
}
