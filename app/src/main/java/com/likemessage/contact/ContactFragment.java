package com.likemessage.contact;

import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        contactList = (ListView) findViewById(R.id.contact_list);
        alphabeticBar = (QuickAlphabeticBar) findViewById(R.id.fast_scroller);

        // 实例化
        asyncQueryHandler = new MyAsyncQueryHandler(getActivity().getContentResolver());
        init();

        ImageView btn_startMessageChat = (ImageView) findViewById(R.id.startMessageChat);
        ImageView btn_startPhoneCall = (ImageView) findViewById(R.id.startPhoneCall);


        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                logger.info("________________setOnItemClickListener,{}",view.toString());

                Toast.makeText(getActivity(),view.toString(),Toast.LENGTH_LONG);

            }
        });

        logger.info("______________________________onActivityCreated,{}",btn_startMessageChat);


//        btn_startMessageChat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                v.getParent();
//
//
//
//
//            }
//        });




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_contact, container, false);
        }
        return view;
    }


    private ContactAdapter adapter;
    private ListView contactList;
    private List<ContactBean> list;
    private AsyncQueryHandler asyncQueryHandler; // 异步查询数据库类对象
    private QuickAlphabeticBar alphabeticBar; // 快速索引条

    private Map<Integer, ContactBean> contactIdMap = null;

    /**
     * 初始化数据库查询参数
     */
    private void init() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人Uri；
        // 查询的字段
        String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY };
        // 按照sort_key升序查詢
        asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");

    }

    /**
     *
     * @author Administrator
     *
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                contactIdMap = new HashMap<Integer, ContactBean>();
                list = new ArrayList<ContactBean>();
                cursor.moveToFirst(); // 游标移动到第一项
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String sortKey = cursor.getString(3);
                    int contactId = cursor.getInt(4);
                    Long photoId = cursor.getLong(5);
                    String lookUpKey = cursor.getString(6);

                    if (contactIdMap.containsKey(contactId)) {
                        // 无操作
                    } else {
                        // 创建联系人对象
                        ContactBean contact = new ContactBean();
                        contact.setDisplayName(name);
                        contact.setPhoneNum(number);
                        contact.setSortKey(sortKey);
                        contact.setPhotoId(photoId);
                        contact.setLookUpKey(lookUpKey);
                        list.add(contact);

                        contactIdMap.put(contactId, contact);
                    }
                }
                if (list.size() > 0) {
                    setAdapter(list);
                }
            }

            super.onQueryComplete(token, cookie, cursor);
        }

    }

    private void setAdapter(List<ContactBean> list) {
        adapter = new ContactAdapter(getActivity(), list, alphabeticBar);
        contactList.setAdapter(adapter);
        alphabeticBar.init(getActivity());
        alphabeticBar.setListView(contactList);
        alphabeticBar.setHight(alphabeticBar.getHeight());
        alphabeticBar.setVisibility(View.VISIBLE);
    }


}
