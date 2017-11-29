package co.edu.udea.compumovil.gr10_20172.sugiereme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.junit.experimental.categories.Categories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostulaCategoriaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostulaCategoriaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostulaCategoriaFragment extends Fragment implements CategoryAdapter.OnRecyclerItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    RecyclerView recyclerView;
    List<Category> newCategories;
    List<Category> filteredNewCategories;
    ChildEventListener childEventListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference newCategoriesDatabaseReference;
    CategoryAdapter mAdapter;
    Category posultatedCategory;

    FirebaseUser firebaseUser;
    DatabaseReference usersDatabaseReference;
    DatabaseReference categoryVotersDatabaseReference;
    ChildEventListener usersChildEventListener;
    ValueEventListener categoryVotersValueEventListener;

    EditText categoryEditText;
    Button postulate;

    boolean hasVoted;
    int categoryVoters;
    public String votedId;

    public PostulaCategoriaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostulaCategoriaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostulaCategoriaFragment newInstance(String param1, String param2) {
        PostulaCategoriaFragment fragment = new PostulaCategoriaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_postula_categoria, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        votedId=" ";

        posultatedCategory=null;
        newCategories=new ArrayList<>();
        filteredNewCategories=new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();

        usersDatabaseReference=firebaseDatabase.getReference().child("users");
        categoryVotersDatabaseReference=firebaseDatabase.getReference().child("votingCategoryUsers");

        usersChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User addedUser = dataSnapshot.getValue(User.class);
                if (firebaseUser.getUid().equals(addedUser.getId())) {
                    if (addedUser.getCategoryVote()==null){
                        votedId=" ";
                        hasVoted=false;
                    } else {
                        Log.d("usersChildEventListener",addedUser.getCategoryVote().getId());
                        votedId=addedUser.getCategoryVote().getId();
                        hasVoted=true;
                    }
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
        usersDatabaseReference.removeEventListener(usersChildEventListener);
        usersDatabaseReference.addChildEventListener(usersChildEventListener);

        categoryVotersValueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null) {
                    categoryVoters = dataSnapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        categoryVotersDatabaseReference.removeEventListener(categoryVotersValueEventListener);
        categoryVotersDatabaseReference.addValueEventListener(categoryVotersValueEventListener);

        newCategoriesDatabaseReference=firebaseDatabase.getReference().child("new_categories");
        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Category addedCategory=dataSnapshot.getValue(Category.class);
                newCategories.add(addedCategory);
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
        newCategoriesDatabaseReference.removeEventListener(childEventListener);
        newCategoriesDatabaseReference.addChildEventListener(childEventListener);

        recyclerView = (RecyclerView) view.findViewById(R.id.postulate_category_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        mAdapter=new CategoryAdapter(new ArrayList<Category>(), getContext(),this);
        mAdapter.setCategoryList(new ArrayList<Category>());
        recyclerView.setAdapter(mAdapter);

        categoryEditText=(EditText)view.findViewById(R.id.postulate_category_enter_text);
        categoryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!newCategories.isEmpty()) {
                    filteredNewCategories.clear();
                    for (Category category : newCategories) {
                        if (category.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filteredNewCategories.add(category);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    mAdapter.setCategoryList(filteredNewCategories);
                    recyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        postulate=(Button)view.findViewById(R.id.postulate_category_fragment_button);
        postulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPostulateCategoryClick(view);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        votedId=" ";
        newCategories=new ArrayList<>();
        attachDatabaseListener();
    }

    private void attachDatabaseListener() {
        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Category addedCategory=dataSnapshot.getValue(Category.class);
                newCategories.add(addedCategory);
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
        newCategoriesDatabaseReference.removeEventListener(childEventListener);
        newCategoriesDatabaseReference.addChildEventListener(childEventListener);

        usersChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User addedUser = dataSnapshot.getValue(User.class);
                if (firebaseUser.getUid().equals(addedUser.getId())) {
                    if (addedUser.getCategoryVote()==null||addedUser.getCategoryVote().getId().equals("")){
                        votedId="";
                        hasVoted=false;
                    } else {
                        votedId=addedUser.getCategoryVote().getId();
                        hasVoted=true;
                    }
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
        usersDatabaseReference.removeEventListener(usersChildEventListener);
        usersDatabaseReference.addChildEventListener(usersChildEventListener);

        categoryVotersValueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryVoters=dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        categoryVotersDatabaseReference.removeEventListener(categoryVotersValueEventListener);
        categoryVotersDatabaseReference.addValueEventListener(categoryVotersValueEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseListener();
        mAdapter.setCategoryList(new ArrayList<Category>());
    }

    private void detachDatabaseListener() {
        if (childEventListener!=null) {
            newCategoriesDatabaseReference.removeEventListener(childEventListener);
            childEventListener=null;
        } if (usersChildEventListener!=null) {
            usersDatabaseReference.removeEventListener(usersChildEventListener);
            usersChildEventListener=null;
        } if (categoryVotersValueEventListener!=null) {
            categoryVotersDatabaseReference.removeEventListener(categoryVotersValueEventListener);
            categoryVotersValueEventListener=null;
        }
    }

    @Override
    public void onItemClicked(View v, int position) {
        posultatedCategory=newCategories.get(position);
    }

    public void onPostulateCategoryClick(View view){
        if (posultatedCategory!=null){
            if (true){
            //}
                usersDatabaseReference.child(firebaseUser.getUid()+"/categoryVote").setValue(posultatedCategory);
                Log.d("postulatedCategory",posultatedCategory.getId());
                Log.d("votedId",votedId);
            //if (!votedId.equals(posultatedCategory.getId())){
                categoryVotersDatabaseReference.setValue(categoryVoters+1);
                HashMap<String, Object> childUpdates = new HashMap<String, Object>();
                childUpdates.put("amountVotes", posultatedCategory.getAmountVotes()+1);
                newCategoriesDatabaseReference.child(posultatedCategory.getId()).updateChildren(childUpdates);
                newCategoriesDatabaseReference.child(posultatedCategory.getId()+"/voterIds/"+posultatedCategory.getAmountVotes()).setValue(firebaseUser.getUid());
            }

            //String refId=newCategoriesDatabaseReference.child(posultatedCategory.getId()+"/voterIds").push().getKey();
        } else {
            posultatedCategory=new Category();
            posultatedCategory.setTitle(categoryEditText.getText().toString());
            mListener.postularCategoria(posultatedCategory);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void postularCategoria(Category postulatedCategory);
    }
}
