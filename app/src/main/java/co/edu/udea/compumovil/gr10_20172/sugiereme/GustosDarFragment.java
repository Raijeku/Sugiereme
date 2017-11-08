package co.edu.udea.compumovil.gr10_20172.sugiereme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GustosDarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GustosDarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GustosDarFragment extends Fragment implements ElementAdapter.OnRecyclerItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button continuarButton, regresarButton;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference categoriesDatabaseReference;
    private ChildEventListener childEventListener;
    private RecyclerView recyclerView;
    private List<Element> elements;
    private ElementAdapter mAdapter;

    public GustosDarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GustosDarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GustosDarFragment newInstance(String param1, String param2) {
        GustosDarFragment fragment = new GustosDarFragment();
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
        View view = inflater.inflate(R.layout.fragment_gustos_dar, container, false);

        elements=new ArrayList<>();
        mAdapter=new ElementAdapter(elements,getContext(),this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        categoriesDatabaseReference=firebaseDatabase.getReference().child("categories");

        recyclerView=(RecyclerView) view.findViewById(R.id.gustos_dar_lista);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(mAdapter);

        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Category addedCategory=dataSnapshot.getValue(Category.class);
                if (addedCategory.getTitle().equals(mParam1)){
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
        categoriesDatabaseReference.addChildEventListener(childEventListener);

        continuarButton = (Button) view.findViewById(R.id.gustos_dar_continuar);
        continuarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null){
                    mListener.onGDContinuarClick();
                }
            }
        });
        regresarButton = (Button) view.findViewById(R.id.gustos_dar_regresar);
        regresarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null){
                    mListener.onGDRegresarClick();
                }
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
    public void onItemClicked(View v, int position) {
        mListener.onGDItemSelect(elements.get(position));
        /*Intent intent=new Intent(GustosActivity.this,DetailActivity.class);
        intent.putExtra("name",elements.get(position).getName());
        intent.putExtra("image",elements.get(position).getImage());
        intent.putExtra("description",elements.get(position).getDescription());
        startActivity(intent);*/
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
        void onGDItemSelect(Element element);
        void onGDContinuarClick();
        void onGDRegresarClick();
    }
}
