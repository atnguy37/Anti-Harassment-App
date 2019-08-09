package com.asu.cse535.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * @author mario padilla
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder>  {

    //  private HashMap<String, Object> emergencyContactStructuer;
    public ArrayList<String> keys;
    public ArrayList<Object> values;

    private OnUserListener mOnUserListener;

    public UserAdapter(ArrayList<String> keys, ArrayList<Object> values, OnUserListener onUserListener) {
        //this.emergencyContactStructuer = list;

        this.keys = keys;
        this.values = values;
        this.mOnUserListener = onUserListener;

        //this.aryList = aryList;
    }

    public ArrayList<String> getKeys(){
        return keys;
    }

    public ArrayList<Object> getValues(){
        return values;
    }

    @Override
    public void onBindViewHolder(UserAdapter.UserHolder viewHolder, int position) {


        String phoneKey = keys.get(viewHolder.getAdapterPosition());
        String nameValue = values.get(viewHolder.getAdapterPosition()).toString();

        // Set item views based on your views and data model
        TextView nameTextView = viewHolder.name;
        nameTextView.setText(nameValue);
        TextView phoneText = viewHolder.phoneNumber;
        phoneText.setText(phoneKey);
//        String key = model.getEmergencyContacts().keySet().toString();
        //noteHolder.name.setText(model.getName());

    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.contact_list_view, parent, false);
        UserHolder viewHolder = new UserHolder(v, mOnUserListener);

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return keys.size();
        //return model.getEmergencyContacts().size();
    }



    public class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        TextView phoneNumber;

        OnUserListener onNoteListener;

        public UserHolder(View itemView, OnUserListener onNoteListener)  {

            super(itemView);
            name = itemView.findViewById(R.id.name_view_title);
            phoneNumber = itemView.findViewById(R.id.phone_view_title);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onUserClick(getAdapterPosition());
        }
    }

    public interface OnUserListener{
        void onUserClick(int position);
    }
}

