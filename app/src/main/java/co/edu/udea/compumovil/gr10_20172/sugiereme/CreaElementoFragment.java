package co.edu.udea.compumovil.gr10_20172.sugiereme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static co.edu.udea.compumovil.gr10_20172.sugiereme.Utility.getBytes;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreaElementoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreaElementoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreaElementoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";
    private static final String ARG_PARAM7 = "param7";
    private static final String ARG_PARAM8 = "param8";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private String mParam5;
    private String mParam6;
    private String mParam7;
    private String mParam8;

    private OnFragmentInteractionListener mListener;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference newElementsDatabaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference elementStorageReference;

    FirebaseUser firebaseUser;
    DatabaseReference usersDatabaseReference;
    DatabaseReference elementVotersDatabaseReference;
    ChildEventListener usersChildEventListener;
    ValueEventListener elementVotersValueEventListener;

    Bitmap bitmap;
    byte[] imagenSeleccionada;
    Uri selectedImage;
    EditText name, description;
    ImageView image;

    Button create;

    boolean hasVoted;
    int elementVoters;

    public CreaElementoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreaElementoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreaElementoFragment newInstance(String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8) {
        CreaElementoFragment fragment = new CreaElementoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);
        args.putString(ARG_PARAM8, param8);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
            mParam5 = getArguments().getString(ARG_PARAM5);
            mParam6 = getArguments().getString(ARG_PARAM6);
            mParam7 = getArguments().getString(ARG_PARAM7);
            mParam8 = getArguments().getString(ARG_PARAM8);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crea_elemento, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        firebaseDatabase=FirebaseDatabase.getInstance();

        usersDatabaseReference=firebaseDatabase.getReference().child("users");
        elementVotersDatabaseReference=firebaseDatabase.getReference().child("votingElementUsers");

        usersChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User addedUser = dataSnapshot.getValue(User.class);
                if (firebaseUser.getUid().equals(addedUser.getId())) {
                    if (addedUser.getElementVote()==null){
                        hasVoted=false;
                    } else {
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
                elementVoters=dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        elementVotersDatabaseReference.addValueEventListener(elementVotersValueEventListener);

        newElementsDatabaseReference=firebaseDatabase.getReference().child("new_elements");
        firebaseStorage=FirebaseStorage.getInstance();
        elementStorageReference=firebaseStorage.getReference().child("element_images");

        name=(EditText)view.findViewById(R.id.element_name);
        name.setText(mParam1);
        description=(EditText)view.findViewById(R.id.element_description);
        image=(ImageView)view.findViewById(R.id.element_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        create=(Button)view.findViewById(R.id.element_button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateElementClick(view);
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

    public void selectImage(View view){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();

            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(bitmap != null) {
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/2,bitmap.getHeight()/2,true);
                try {
                    imagenSeleccionada=getBytes(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                image.setImageBitmap(scaled);
            }

        }
    }

    public void onCreateElementClick(View view){
        boolean create=true;
        if (TextUtils.isEmpty(name.getText().toString())){
            create=false;
        }
        if (TextUtils.isEmpty(description.getText().toString())){
            create=false;
        }
        if (selectedImage==null){
            create=false;
        }

        if (create) {
            StorageReference imageRef=elementStorageReference.child(selectedImage.getLastPathSegment());
            imageRef.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri=taskSnapshot.getDownloadUrl();
                    String refId = newElementsDatabaseReference.push().getKey();

                    Element createdElement=new Element();
                    createdElement.setName(name.getText().toString());
                    createdElement.setImage(downloadUri.toString());
                    createdElement.setId(refId);
                    createdElement.setCreationDate("");
                    Calendar cal=Calendar.getInstance();
                    cal.add(Calendar.DATE,1);
                    SimpleDateFormat simpleFormat=new SimpleDateFormat("yyyy/MM/dd");
                    createdElement.setPostulationDate(simpleFormat.format(cal.getTime()));
                    createdElement.setAmountVotes(1);
                    createdElement.setDescription(description.getText().toString());
                    if (description.getText().length()>100) {
                        createdElement.setShortDescription(description.getText().toString().substring(0, 100));
                    } else {
                        createdElement.setShortDescription(description.getText().toString());
                    }

                    Category createdCategory = new Category();
                    createdCategory.setAmountVotes(1);
                    createdCategory.setTitle(mParam3);
                    createdCategory.setDescription(mParam4);
                    createdCategory.setId(mParam2);
                    createdCategory.setImage(mParam5);
                    List<Element> elementList=new ArrayList<Element>();
                    elementList.add(createdElement);
                    createdCategory.setElements(elementList);
                    List<String> userIdList=new ArrayList<String>();
                    userIdList.add(firebaseUser.getUid());
                    createdCategory.setVoterIds(userIdList);
                    createdCategory.setPostulationDate(mParam8);
                    createdCategory.setCreationDate(mParam7);

                    newElementsDatabaseReference.child(refId).setValue(createdCategory);

                    if (!hasVoted){
                        elementVotersDatabaseReference.setValue(elementVoters+1);
                    }
                    usersDatabaseReference.child(firebaseUser.getUid()+"/elementVote").setValue(createdCategory);

                    mListener.postulateClicked();
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        attachDatabaseListener();
    }

    private void attachDatabaseListener() {
        usersChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User addedUser = dataSnapshot.getValue(User.class);
                if (firebaseUser.getUid().equals(addedUser.getId())) {
                    if (addedUser.getElementVote()==null){
                        hasVoted=false;
                    } else {
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
                elementVoters=dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        elementVotersDatabaseReference.addValueEventListener(elementVotersValueEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseListener();
    }

    private void detachDatabaseListener() {
        if (usersChildEventListener!=null) {
            usersDatabaseReference.removeEventListener(usersChildEventListener);
            usersChildEventListener=null;
        } if (elementVotersValueEventListener!=null) {
            elementVotersDatabaseReference.removeEventListener(elementVotersValueEventListener);
            elementVotersValueEventListener=null;
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
        void postulateClicked();
    }
}
