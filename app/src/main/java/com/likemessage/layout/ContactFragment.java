package com.likemessage.layout;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;

import in.co.madhur.chatbubblesdemo.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactFragment extends Fragment {

    private Logger logger = LoggerFactory.getLogger(ContactFragment.class);

    private View view = null;

    public ContactFragment() {
    }

    View findViewById(int id){
        return getActivity().findViewById(id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null){
            view = inflater.inflate(R.layout.fragment_contact, container, false);
        }
        return view;
    }



}
