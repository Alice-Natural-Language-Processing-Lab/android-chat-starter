package com.likemessage.layout;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.likemessage.MCOnClickListener;

import in.co.madhur.chatbubblesdemo.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MessageFragment extends Fragment {

    private Logger logger = LoggerFactory.getLogger(MessageFragment.class);

    private View view = null;

    public MessageFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {


        Button m_btn_message = (Button) findViewById(R.id.m_btn_message);
        Button m_btn_contact = (Button) findViewById(R.id.m_btn_contact);
        Button c_btn_message = (Button) findViewById(R.id.c_btn_message);
        Button c_btn_contact = (Button)  findViewById(R.id.c_btn_contact);

        logger.info("______________________m_btn_message:{}",m_btn_message);
        logger.info("______________________m_btn_contact:{}",m_btn_contact);



        super.onCreate(savedInstanceState);
    }

    View findViewById(int id){

        return getActivity().findViewById(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        logger.info("______________________");

        if (view == null){
            view = inflater.inflate(R.layout.fragment_message, container, false);
        }

        Button m_btn_message = (Button) findViewById(R.id.m_btn_message);
        Button m_btn_contact = (Button) findViewById(R.id.m_btn_contact);

        MCOnClickListener mcOnClickListener = new MCOnClickListener();

        m_btn_contact.setOnClickListener(mcOnClickListener);
        m_btn_message.setOnClickListener(mcOnClickListener);

        return inflater.inflate(R.layout.fragment_message, container, false);
    }



}
