package edu.cudenver.salimlakhani.phonebook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter <ContactAdapter.ListItemHolder>{

    private MainActivity mainActivity;
    private ArrayList<Contact> list;
    private String type;

    public ContactAdapter (MainActivity mainActivity, ArrayList<Contact> list) {
        this.mainActivity = mainActivity;
        this.list = list;
        this.type = "name";
    }

    public ContactAdapter.ListItemHolder onCreateViewHolder (ViewGroup parent, int ViewType) {

        View listItem = LayoutInflater.from (parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ListItemHolder(listItem);
    }

    public void onBindViewHolder (ContactAdapter.ListItemHolder holder, int position) {

        Contact contact = list.get(position);

        if (type.equals("name"))
            holder.textViewName.setText(contact.getName() + " : " + contact.getPhone());
        else
            holder.textViewName.setText(contact.getPhone());

    }

    public void setType (String type) {
        this.type = type;
    }

    public int getItemCount() {
        return list.size();
    }



    public class ListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewName;

        public ListItemHolder (View view) {
            super (view);
            textViewName = view.findViewById(R.id.textViewName);
            view.setClickable(true);
            view.setOnClickListener(this);

        }

        public void onClick (View view) {

            mainActivity.showContact(getAdapterPosition());
        }


    }


}
