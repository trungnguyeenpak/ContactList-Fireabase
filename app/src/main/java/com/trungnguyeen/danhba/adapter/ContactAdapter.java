package com.trungnguyeen.danhba.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trungnguyeen.danhba.R;
import com.trungnguyeen.danhba.model.Contact;
import com.trungnguyeen.danhba.ultis.Utils;

import java.util.ArrayList;

/**
 * Created by trungnguyeen on 4/9/18.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private ArrayList<Contact> contactList;

    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.contact_item, parent, false);

        return new ContactViewHolder(itemView);
    }

    public void setContactList(ArrayList<Contact> contactList) {
        this.contactList = contactList;
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ContactViewHolder holder, int position) {
        Contact item = this.contactList.get(position);
        holder.bindView(item);
    }

    @Override
    public int getItemCount() {
        return this.contactList.size();
    }

    public void removeItem(int position) {
        contactList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Contact item, int position) {
        contactList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgAvatar;
        public TextView avatarText;
        public TextView username;
        public TextView phoneNumber;
        public ConstraintLayout viewForeground;
        public ConstraintLayout viewBackground;


        public ContactViewHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgName);
            avatarText = itemView.findViewById(R.id.tv_avatar);
            username = itemView.findViewById(R.id.tv_name);
            phoneNumber = itemView.findViewById(R.id.tv_phoneNumber);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            viewBackground = itemView.findViewById(R.id.view_background);
        }

        public void bindView(Contact contact) {
            username.setText(contact.getName());
            phoneNumber.setText(contact.getPhoneNumber());
            avatarText.setText(contact.getCharOfName(contact.getName()));
            imgAvatar.setBackgroundColor(Utils.GeneratorColor());
        }
    }
}
