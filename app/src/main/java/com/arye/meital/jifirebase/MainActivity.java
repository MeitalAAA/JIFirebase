package com.arye.meital.jifirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.arye.meital.jifirebase.adapters.MyAdapter;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyAdapter.AdapterInteraction {

    private MyAdapter adapter;

    private ArrayList<String> keysSet = new ArrayList<>();
    private ArrayList<String> dataSet = new ArrayList<>();
    MenuItem login;
    MenuItem logout;
    static final int ADD_ITEM = 1;
    static final int EDIT_ITEM = 2;
    private static final int SIGN_IN = 3;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseCurrentUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpRcyclerView();

        setUpFirebase();

    }

    private void setUpFirebase() {

        //  User authentication
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseCurrentUser = firebaseAuth.getCurrentUser();

        getUserData();

    }

    private void getUserData() {

        if(firebaseCurrentUser != null) {
            // Reference to table location in db. Create if not exist
            databaseReference = FirebaseDatabase.getInstance().getReference().child(firebaseCurrentUser.getUid().toString());

            // Listen to children events
            databaseReference.addChildEventListener(new ChildEventListener() {
                // onChildAdded is called first for every existing child when listener is attached
                // and again whenever child is added
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String name = dataSnapshot.getValue(String.class);
                    dataSet.add(name);
                    keysSet.add(dataSnapshot.getKey());
                    adapter.notifyItemInserted(dataSet.size() - 1);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    int itemIndex = keysSet.indexOf(dataSnapshot.getKey());
                    dataSet.set(itemIndex, dataSnapshot.getValue().toString());
                    adapter.notifyItemChanged(itemIndex);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    int itemIndex = keysSet.indexOf(dataSnapshot.getKey());
                    dataSet.remove(itemIndex);
                    keysSet.remove(itemIndex);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
            login();
    }

    private void setUpRcyclerView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new MyAdapter(this, dataSet);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_login) {
            login();
            return true;
        }
        else if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return false;
    }

    private void login() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),new AuthUI.IdpConfig.FacebookBuilder().build());
        // Create and launch sign-in intent
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),SIGN_IN);
    }
    private void logout() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // Clear recyclerView
                dataSet.clear();
                keysSet.clear();
                adapter.notifyDataSetChanged();

                login();
            }
        });
        Toast.makeText(this, firebaseAuth.getCurrentUser() == null ? "null" : firebaseAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        login = menu.findItem(R.id.action_login);
        logout = menu.findItem(R.id.action_logout);

        if(firebaseCurrentUser == null)
        {
            login.setVisible(true);
            logout.setVisible(false);
        }
        else
        {
            logout.setVisible(true);
            login.setVisible(false);
        }
        return true;
    }

    public void fabIconPressed(View view) {
        if (firebaseCurrentUser != null) {
            Intent addItemIntent = new Intent(this, ModifyListActivity.class);
            startActivityForResult(addItemIntent, ADD_ITEM);
        }
        else
            Toast.makeText(this, "You need to sign in first", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_ITEM) {
            if (resultCode == RESULT_OK) {
                addToList(data.getStringExtra("name"));
            }
            else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == EDIT_ITEM) {
            if (resultCode == RESULT_OK) {
                editInList(data.getStringExtra("name"), data.getIntExtra("position", -1));
            }
            else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == SIGN_IN) {
            if (resultCode == RESULT_OK) {
                firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                getUserData();
                Toast.makeText(this, "Successfully signed in", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void addToList(String name) {
        databaseReference.push().setValue(name);
        //dataSet.add(name);
    }

    public void editInList(String name, int pos) {
        if(pos != -1) {
            databaseReference.child(keysSet.get(pos)).setValue(name);
        }
    }

    @Override
    public void onDeleteItem(String name, int pos) {
        databaseReference.child(keysSet.get(pos)).removeValue();
    }

    @Override
    public void onEditItem(String name, int pos) {
        Intent editItemIntent = new Intent(this, ModifyListActivity.class);
        editItemIntent.putExtra("position", pos);
        editItemIntent.putExtra("name", name);
        startActivityForResult(editItemIntent, EDIT_ITEM);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
