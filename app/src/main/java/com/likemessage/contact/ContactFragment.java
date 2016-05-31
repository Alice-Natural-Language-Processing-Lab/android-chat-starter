package com.likemessage.contact;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.likemessage.AddContactActivity;
import com.likemessage.PhoneActivity;
import com.likemessage.common.LConstants;

import in.co.madhur.chatbubblesdemo.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactFragment extends Fragment {

    private Logger logger = LoggerFactory.getLogger(ContactFragment.class);
    private View view = null;
    private PhoneActivity activity;
    private ContactAdapter adapter;
    private ListView contactListView;

    public ContactFragment() {
    }

    View findViewById(int id) {
        return getActivity().findViewById(id);
    }


    public void setPhoneActivity(PhoneActivity activity){
        this.activity = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        contactListView = (ListView) findViewById(R.id.contact_list);

        adapter = new ContactAdapter(activity, contactListView);
        contactListView.setAdapter(adapter);

        Button btn_add_contact = (Button) findViewById(R.id.btn_add_contact);

        btn_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logger.info("________________add contact click");

                Intent intent = new Intent(activity,AddContactActivity.class);
                intent.putExtra("test","test");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_contact, container, false);
        }
        return view;
    }

    public void onResume() {

        if (LConstants.isNeedRefreshContact){
            LConstants.isNeedRefreshContact = false;
            adapter.refresh();
        }

        super.onResume();
    }
}
