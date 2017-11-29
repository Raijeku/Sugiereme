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
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static co.edu.udea.compumovil.gr10_20172.sugiereme.Utility.getBytes;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreaCategoriaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreaCategoriaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreaCategoriaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference newCategoriesDatabaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference categoryStorageReference;

    FirebaseUser firebaseUser;
    DatabaseReference usersDatabaseReference;
    DatabaseReference categoryVotersDatabaseReference;
    ChildEventListener usersChildEventListener;
    ValueEventListener categoryVotersValueEventListener;

    Bitmap bitmap;
    byte[] imagenSeleccionada;
    Uri selectedImage;
    EditText title, description;
    ImageView image;
    Button create;

    boolean hasVoted;
    int categoryVoters;

    public CreaCategoriaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreaCategoriaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreaCategoriaFragment newInstance(String param1, String param2) {
        CreaCategoriaFragment fragment = new CreaCategoriaFragment();
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
        View view= inflater.inflate(R.layout.fragment_crea_categoria, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        firebaseDatabase=FirebaseDatabase.getInstance();

        usersDatabaseReference=firebaseDatabase.getReference().child("users");
        categoryVotersDatabaseReference=firebaseDatabase.getReference().child("votingCategoryUsers");

        usersChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User addedUser = dataSnapshot.getValue(User.class);
                if (firebaseUser.getUid().equals(addedUser.getId())) {
                    if (addedUser.getCategoryVote()==null){
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

        categoryVotersValueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryVoters=dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        categoryVotersDatabaseReference.addValueEventListener(categoryVotersValueEventListener);

        newCategoriesDatabaseReference=firebaseDatabase.getReference().child("new_categories");
        firebaseStorage=FirebaseStorage.getInstance();
        categoryStorageReference=firebaseStorage.getReference().child("category_images");

        title=(EditText)view.findViewById(R.id.category_title);
        title.setText(mParam1);
        description=(EditText)view.findViewById(R.id.category_description);
        image=(ImageView)view.findViewById(R.id.category_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        create=(Button)view.findViewById(R.id.category_button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateCategoryClick(view);
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

    public void onCreateCategoryClick(View view){
        boolean create=true;
        if (TextUtils.isEmpty(title.getText().toString())){
            create=false;
        }
        if (TextUtils.isEmpty(description.getText().toString())){
            create=false;
        }
        if (selectedImage==null){
            create=false;
        }

        if (create) {
            StorageReference imageRef=categoryStorageReference.child(selectedImage.getLastPathSegment());
            imageRef.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri=taskSnapshot.getDownloadUrl();
                    String refId = newCategoriesDatabaseReference.push().getKey();

                    Category createdCategory = new Category();
                    createdCategory.setAmountVotes(1);
                    createdCategory.setTitle(title.getText().toString());
                    createdCategory.setDescription(description.getText().toString());
                    createdCategory.setId(refId);
                    createdCategory.setImage(downloadUri.toString());
                    createdCategory.setElements(new ArrayList<Element>());
                    Calendar cal=Calendar.getInstance();
                    cal.add(Calendar.DATE,1);
                    SimpleDateFormat simpleFormat=new SimpleDateFormat("yyyy/MM/dd");
                    createdCategory.setPostulationDate(simpleFormat.format(cal.getTime()));
                    createdCategory.setCreationDate("");
                    List<String> userIdList=new ArrayList<String>();
                    userIdList.add(firebaseUser.getUid());
                    createdCategory.setVoterIds(userIdList);

                    newCategoriesDatabaseReference.child(refId).setValue(createdCategory);

                    if (!hasVoted){
                        categoryVotersDatabaseReference.setValue(categoryVoters+1);
                    }
                    usersDatabaseReference.child(firebaseUser.getUid()+"/categoryVote").setValue(createdCategory);


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
                    if (addedUser.getCategoryVote()==null){
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

        categoryVotersValueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryVoters=dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        categoryVotersDatabaseReference.addValueEventListener(categoryVotersValueEventListener);
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
        } if (categoryVotersValueEventListener!=null) {
            categoryVotersDatabaseReference.removeEventListener(categoryVotersValueEventListener);
            categoryVotersValueEventListener=null;
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
