package co.edu.udea.compumovil.gr10_20172.sugiereme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostulaElementoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostulaElementoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostulaElementoFragment extends Fragment implements CategoryAdapter.OnRecyclerItemClickListener, ElementAdapter.OnRecyclerItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    RecyclerView categoryRecyclerView;
    List<Category> newCategories;
    List<Category> filteredNewCategories;
    ChildEventListener categoryChildEventListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference newCategoriesDatabaseReference;
    CategoryAdapter mAdapter;
    RecyclerView elementRecyclerView;
    List<Element> newElements;
    List<Element> filteredNewElements;
    DatabaseReference newElementsDatabaseReference;
    DatabaseReference oldElementsDatabaseReference;
    ChildEventListener elementChildEventListener;
    ElementAdapter eAdapter;
    Category selectedCategory;
    Element postulatedElement;

    FirebaseUser firebaseUser;
    DatabaseReference usersDatabaseReference;
    DatabaseReference elementVotersDatabaseReference;
    ChildEventListener usersChildEventListener;
    ValueEventListener elementVotersValueEventListener;

    EditText categoryEditText, elementEditText;
    Button postulate;

    boolean hasVoted;
    int elementVoters;
    String votedId;
    List<Category> newPairs;

    public PostulaElementoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostulaElementoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostulaElementoFragment newInstance(String param1, String param2) {
        PostulaElementoFragment fragment = new PostulaElementoFragment();
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
        View view = inflater.inflate(R.layout.fragment_postula_elemento, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        votedId=" ";
        newPairs=new ArrayList<>();

        newCategories=new ArrayList<>();
        filteredNewCategories=new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();

        usersDatabaseReference=firebaseDatabase.getReference().child("users");
        elementVotersDatabaseReference=firebaseDatabase.getReference().child("votingElementUsers");

        usersChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User addedUser = dataSnapshot.getValue(User.class);
                if (firebaseUser.getUid().equals(addedUser.getId())) {
                    if (addedUser.getElementVote()==null){
                        votedId="";
                        hasVoted=false;
                    } /*else  if (addedUser.getElementVote().getId().equals("")){
                        votedId="";
                        hasVoted=false;
                    }*/
                    else {
                        votedId=addedUser.getElementVote().getId();
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
        usersDatabaseReference.addChildEventListener(usersChildEventListener);

        elementVotersValueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null) {
                    elementVoters = dataSnapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        elementVotersDatabaseReference.addValueEventListener(elementVotersValueEventListener);

        newCategoriesDatabaseReference=firebaseDatabase.getReference().child("categories");
        categoryChildEventListener=new ChildEventListener() {
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
        newCategoriesDatabaseReference.addChildEventListener(categoryChildEventListener);

        categoryRecyclerView = (RecyclerView) view.findViewById(R.id.postulate_element_recycler);
        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter=new CategoryAdapter(filteredNewCategories, getContext(),this);
        categoryRecyclerView.setAdapter(mAdapter);

        categoryEditText=(EditText)view.findViewById(R.id.postulate_element_enter_text);
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
                        else {
                            filteredNewCategories.remove(category);
                        }
                    }
                    mAdapter.setCategoryList(filteredNewCategories);
                    categoryRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        newElements=new ArrayList<>();
        filteredNewElements=new ArrayList<>();
        newElementsDatabaseReference=firebaseDatabase.getReference().child("new_elements");
        oldElementsDatabaseReference=firebaseDatabase.getReference().child("elements");

        elementChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Category addedPair=dataSnapshot.getValue(Category.class);
                newElements.add(addedPair.getElements().get(0));
                newPairs.add(addedPair);
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
        newElementsDatabaseReference.addChildEventListener(elementChildEventListener);
        oldElementsDatabaseReference.addChildEventListener(elementChildEventListener);

        elementRecyclerView = (RecyclerView) view.findViewById(R.id.postulate_element_recycler_element);
        elementRecyclerView.setHasFixedSize(true);
        elementRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eAdapter=new ElementAdapter(filteredNewElements, getContext(),this);
        elementRecyclerView.setAdapter(eAdapter);

        elementEditText=(EditText)view.findViewById(R.id.postulate_element_enter_text_element);
        elementEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!newElements.isEmpty()) {
                    filteredNewElements.clear();
                    for (Element element : newElements) {
                        if (element.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filteredNewElements.add(element);
                        }
                        else {
                            filteredNewCategories.remove(element);
                        }
                    }
                    eAdapter.setElementList(filteredNewElements);
                    elementRecyclerView.setAdapter(eAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        selectedCategory=null;
        postulatedElement=null;

        postulate=(Button)view.findViewById(R.id.postulate_element_fragment_button);
        postulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPostulateElementClick(view);
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
        newElements=new ArrayList<>();
        attachDatabaseListener();
    }

    private void attachDatabaseListener() {
        categoryChildEventListener=new ChildEventListener() {
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
        newCategoriesDatabaseReference.removeEventListener(categoryChildEventListener);
        newCategoriesDatabaseReference.addChildEventListener(categoryChildEventListener);

        usersChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User addedUser = dataSnapshot.getValue(User.class);
                if (firebaseUser.getUid().equals(addedUser.getId())) {
                    if (addedUser.getElementVote()==null){
                        votedId=" ";
                        hasVoted=false;
                    } else {
                        votedId=addedUser.getElementVote().getId();
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

        elementVotersValueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                elementVoters=dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        elementVotersDatabaseReference.removeEventListener(elementVotersValueEventListener);
        elementVotersDatabaseReference.addValueEventListener(elementVotersValueEventListener);

        elementChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Element addedElement=dataSnapshot.getValue(Element.class);
                newElements.add(addedElement);
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
        newElementsDatabaseReference.removeEventListener(elementChildEventListener);
        oldElementsDatabaseReference.removeEventListener(elementChildEventListener);
        newElementsDatabaseReference.addChildEventListener(elementChildEventListener);
        oldElementsDatabaseReference.addChildEventListener(elementChildEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseListener();
        mAdapter.setCategoryList(new ArrayList<Category>());
        eAdapter.setElementList(new ArrayList<Element>());
    }

    private void detachDatabaseListener() {
        if (categoryChildEventListener!=null){
            newCategoriesDatabaseReference.removeEventListener(categoryChildEventListener);
            categoryChildEventListener=null;
        }
        if (elementChildEventListener!=null){
            newElementsDatabaseReference.removeEventListener(elementChildEventListener);
            oldElementsDatabaseReference.removeEventListener(elementChildEventListener);
            elementChildEventListener=null;
        }
        if (usersChildEventListener!=null) {
            usersDatabaseReference.removeEventListener(usersChildEventListener);
            usersChildEventListener=null;
        }
        if (elementVotersValueEventListener!=null) {
            elementVotersDatabaseReference.removeEventListener(elementVotersValueEventListener);
            elementVotersValueEventListener=null;
        }
    }

    @Override
    public void onItemClicked(View v, int position) {
        selectedCategory=newCategories.get(position);
    }

    @Override
    public void onElementItemClicked(View v, int position) {
        postulatedElement=newElements.get(position);
    }

    public void onPostulateElementClick(View view){
        if (postulatedElement!=null){
            if (true){
                elementVotersDatabaseReference.setValue(elementVoters+1);
            //}
                usersDatabaseReference.child(firebaseUser.getUid()+"/elementVote").setValue(postulatedElement);

            //if (!votedId.equals(postulatedElement.getId())) {
                HashMap<String, Object> childUpdates = new HashMap<String, Object>();
                //Log.d("postulatedElement",postulatedElement.getId());
                childUpdates.put("amountVotes", postulatedElement.getAmountVotes() + 1);
                newElementsDatabaseReference.child(postulatedElement.getId()).updateChildren(childUpdates);
                newElementsDatabaseReference.child(postulatedElement.getId()+"/voterIds/"+postulatedElement.getAmountVotes()).setValue(firebaseUser.getUid());
            }
            //String refId=newElementsDatabaseReference.child(postulatedElement.getId()+"/voterIds").push().getKey();
        } else {
            Log.d("aqui else","hey");
            if (selectedCategory!=null) {
                Log.d("aqui else if", "heyy");
                postulatedElement=new Element();
                postulatedElement.setName(elementEditText.getText().toString());
                mListener.postularElemento(postulatedElement, selectedCategory);
            }
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
        void postularElemento(Element postulatedElement, Category selectedCategory);
    }
}
