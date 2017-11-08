package co.edu.udea.compumovil.gr10_20172.sugiereme;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class GustosActivity extends AppCompatActivity implements ElementAdapter.OnRecyclerItemClickListener {
    String categoryTitle;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference categoryDatabaseReference;
    ChildEventListener childEventListener;
    List<Element> elements;
    ElementAdapter mAdapter;
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

        categoryTitle=getIntent().getStringExtra("categoryTitle");
        elements=new ArrayList<>();

        firebaseDatabase=FirebaseDatabase.getInstance();
        categoryDatabaseReference=firebaseDatabase.getReference().child("categories");

        recyclerView=(RecyclerView) findViewById(R.id.gustos_lista);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getBaseContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        mAdapter=new ElementAdapter(elements,getBaseContext(),this);
        recyclerView.setAdapter(mAdapter);

        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Category addedCategory=dataSnapshot.getValue(Category.class);
                if (addedCategory.getTitle().equals(categoryTitle)){
                    for (DataSnapshot postSnapshot: dataSnapshot.child("elements").getChildren()){
                        elements.add(postSnapshot.getValue(Element.class));
                    }
                    mAdapter.setElementList(elements);
                    recyclerView.setAdapter(mAdapter);
                }
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
        categoryDatabaseReference.addChildEventListener(childEventListener);
    }

    public void onSalirClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        elements=new ArrayList<>();
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (childEventListener==null){
            childEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Category addedCategory=dataSnapshot.getValue(Category.class);
                    if (addedCategory.getTitle().equals(categoryTitle)){
                        for (DataSnapshot postSnapshot: dataSnapshot.child("elements").getChildren()){
                            elements.add(postSnapshot.getValue(Element.class));
                        }
                        mAdapter.setElementList(elements);
                        recyclerView.setAdapter(mAdapter);
                    }
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
            categoryDatabaseReference.addChildEventListener(childEventListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        mAdapter.setElementList(new ArrayList<Element>());
    }

    private void detachDatabaseReadListener() {
        if (childEventListener!=null){
            categoryDatabaseReference.removeEventListener(childEventListener);
            childEventListener=null;
        }
    }

    @Override
    public void onItemClicked(View v, int position) {
        Intent intent=new Intent(GustosActivity.this,DetailActivity.class);
        intent.putExtra("name",elements.get(position).getName());
        intent.putExtra("image",elements.get(position).getImage());
        intent.putExtra("description",elements.get(position).getDescription());
        startActivity(intent);
    }
}
