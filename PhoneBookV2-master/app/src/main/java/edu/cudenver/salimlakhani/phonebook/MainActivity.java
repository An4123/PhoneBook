package edu.cudenver.salimlakhani.phonebook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.cudenver.salimlakhani.phonebook.databinding.ActivityMainBinding;
import edu.cudenver.salimlakhani.phonebook.databinding.DialogViewContactBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ArrayList<Contact> list;
    private ContactAdapter contactAdapter;
    private RecyclerView recyclerView;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private DataManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("phonebook", Context.MODE_PRIVATE);
        editor = prefs.edit();

        setSupportActionBar(binding.toolbar);

        list = new ArrayList<Contact>();
        dm = new DataManager(this);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddContactDialog addContactDialog = new AddContactDialog();
                addContactDialog.show(getSupportFragmentManager(), "");
            }
        });

        String type = prefs.getString ("type", "name");

        recyclerView = findViewById(R.id.recyclerView);
        contactAdapter = new ContactAdapter(this, list);
        contactAdapter.setType(type);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(contactAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            AddContactDialog addContactDialog = new AddContactDialog();
            addContactDialog.show(getSupportFragmentManager(), "");
            return true;
        }
        else if (id == R.id.action_name) {
            contactAdapter.setType("name");
            editor.putString("type", "name");
            editor.commit();
            contactAdapter.notifyDataSetChanged();
            return true;

        }
        else if (id == R.id.action_phone) {
            contactAdapter.setType("phone");
            editor.putString("type", "phone");
            editor.commit();
            contactAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addContact (Contact contact) {
        //list.add(contact);
        String name = contact.getName();
        String phone = contact.getPhone();
        String email = contact.getEmail();
        String street = contact.getAddress();
        String city = contact.getCity();
        String state = contact.getState();
        String zip = contact.getZip();
        String contactType = contact.getContacttype();

        dm.insert(name, phone, email, street, city, state, zip, contactType);
        //Log.i ("info", "Number of Contacts " + list.size());
        //contactAdapter.notifyDataSetChanged();
        loadData();
    }

    public void showContact (int contactToShow) {
        //Create object for ViewContact
        DialogViewContactBinding binding;
        binding = DialogViewContactBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.textViewName.setText(list.get(contactToShow).getName());
        binding.textViewPhone.setText(list.get(contactToShow).getPhone());
        binding.textViewEmail.setText(list.get(contactToShow).getEmail());
        binding.textViewStreet.setText(list.get(contactToShow).getAddress());
        binding.textViewCity.setText(list.get(contactToShow).getCity());
        binding.textViewState.setText(list.get(contactToShow).getState());
        binding.textViewZip.setText(list.get(contactToShow).getZip());
        binding.textViewType.setText(list.get(contactToShow).getContacttype());


        // get ID
        int id = dm.getID(list.get(contactToShow).getName(), list.get(contactToShow).getPhone());
//        int id = dm.search(list.get(contactToShow));
        binding.buttonMainMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);            }
        });

        binding.buttonDelete.setOnClickListener(new View.OnClickListener(){
            // IF USER CLICKS DELETE THEN DELETE THE CONTACT FROM DM AND LIST.
            @Override
            public void onClick(View view){
                list.remove(contactToShow);
                dm.delete(id);
                loadData();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);            }
        });

        binding.buttonEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Contact contact = list.get(contactToShow);
                AddContactDialog addContactDialog = new AddContactDialog(contact.getName(),
                        contact.getPhone(), contact.getEmail(),contact.getAddress(),
                        contact.getCity(), contact.getState(), contact.getZip(),
                        contact.getContacttype());
                addContactDialog.show(getSupportFragmentManager(), "");
                dm.delete(id);
                list.remove(contactToShow);
                loadData();
            }
        });
    }


    public void loadData () {
        Cursor cursor = dm.selectAll();

        int contactCount = cursor.getCount();

        list.clear();

        if (contactCount > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String phone = cursor.getString(2);
                String email = cursor.getString(3);
                String street = cursor.getString(4);
                String city = cursor.getString(5);
                String state = cursor.getString(6);
                String zip = cursor.getString(7);
                String contactType = cursor.getString(8);

                Contact contact = new Contact(name, phone, email, street, state, city, zip, contactType);

                list.add(contact);
            }

            contactAdapter.notifyDataSetChanged();


        }
    }

    public void onResume () {
        super.onResume();
        loadData();
    }
}