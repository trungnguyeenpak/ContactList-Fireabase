package com.trungnguyeen.danhba.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trungnguyeen.danhba.R;
import com.trungnguyeen.danhba.adapter.ContactAdapter;
import com.trungnguyeen.danhba.model.Contact;
import com.trungnguyeen.danhba.ultis.RecyclerItemTouchHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private ArrayList<Contact> contacts;
    private ContactAdapter contactAdapter;


    //Controls
    private EditText edtName;
    private EditText edtPhoneNumber;
    private Button  btnAdd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvContact;
    private Toolbar toolbar;
    private ConstraintLayout contraConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();
        contacts = new ArrayList<>();

        dataChangeListener();
        setupControls();
        setupRecyclerContact();
        setupSwipeController();
    }

    private void setupSwipeController() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvContact);
    }

    private void setupRecyclerContact() {

        rvContact = findViewById(R.id.recycler_contact);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvContact.setLayoutManager(linearLayoutManager);
        contactAdapter = new ContactAdapter();
        contactAdapter.setContactList(contacts);
        rvContact.setAdapter(contactAdapter);
        rvContact.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadRecyclerView();
            }
        });
    }

    private void reloadRecyclerView() {
        Toast.makeText(this, "Reload data", Toast.LENGTH_SHORT).show();
        new ReloadData().execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2500);
    }

    private void setupControls() {
        contraConstraintLayout = findViewById(R.id.main_layout);
        edtName = (EditText) findViewById(R.id.edt_name);
        edtPhoneNumber = (EditText) findViewById(R.id.edt_phone_number);
        btnAdd = (Button) findViewById(R.id.btn_addContact);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Contacts");

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                String phoneNumber = edtPhoneNumber.getText().toString().trim();

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(MainActivity.this, "Please input Name and Phone Number", Toast.LENGTH_SHORT).show();
                }
                else{
                    Contact contact = new Contact(name, phoneNumber);
                    edtPhoneNumber.setText("");
                    edtName.setText("");
                    writeNewContact(contact);
                    hideSoftKeyboard();
                }
            }
        });
    }

    public void writeNewContact(Contact contact){
        String contactID = myRef.push().getKey();
        contact.setId(contactID);
        myRef.child(contactID).setValue(contact);
    }

    public void dataChangeListener(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contacts.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    contacts.add(ds.getValue(Contact.class));
                    Log.i(TAG, "onDataChange: " + dataSnapshot.getChildren().toString());
                }
                contactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) MainActivity.this.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                MainActivity.this.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ContactAdapter.ContactViewHolder) {
            // get the removed item name to display it in snack bar
            String name = contacts.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final Contact deletedItem = contacts.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            contactAdapter.removeItem(viewHolder.getAdapterPosition());
            myRef.child(deletedItem.getId()).removeValue();
            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(contraConstraintLayout, "Do you want to undo contact?", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myRef.removeValue();
                    // undo is selected, restore the deleted item
                    contactAdapter.restoreItem(deletedItem, deletedIndex);
                    for(Contact item: contacts){
                        writeNewContact(item);
                    }
                    contactAdapter.notifyDataSetChanged();
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();


        }

    }

    public class ReloadData extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contacts.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        contacts.add(ds.getValue(Contact.class));
                        Log.i(TAG, "onDataChange: " + dataSnapshot.getChildren().toString());
                    }
                    publishProgress();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            contactAdapter.notifyDataSetChanged();
        }
    }
}
