package com.likemessage.contact;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;
import com.likemessage.BaseActivity;
import com.likemessage.CallActivity;
import com.likemessage.PhoneActivity;
import com.likemessage.bean.B_Contact;
import com.likemessage.common.BaseAdapter;
import com.likemessage.common.LConstants;

import java.util.List;

import in.co.madhur.chatbubblesdemo.ChatActivity;
import in.co.madhur.chatbubblesdemo.R;

public class ContactAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<B_Contact> contactList;
    private PhoneActivity activity; // 上下文
    private ListView contactListView;

    private Logger logger = LoggerFactory.getLogger(ContactAdapter.class);

    public ContactAdapter(PhoneActivity activity,ListView contactListView) {
        this.contactListView = contactListView;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.contactList = LConstants.fixdContacts;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        B_Contact contact = contactList.get(position);
        if (contact.getUserID() == -1){
            return 1;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int position) {
        contactList.remove(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final B_Contact contact = contactList.get(position);

        if (contact.getUserID() == -1){
            ViewHoldAlpha holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.fragment_contact_item_alpha, null);
                holder = new ViewHoldAlpha();

                holder.txt_alpha = (TextView) convertView.findViewById(R.id.txt_alpha);
                convertView.setTag(holder);
            } else {
                holder = (ViewHoldAlpha) convertView.getTag();
            }
            holder.txt_alpha.setText(contact.getPinyin());
            return convertView;
        }

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_contact_item, null);
            holder = new ViewHolder();
            holder.contact_header = (ImageView) convertView
                    .findViewById(R.id.contact_header);
            holder.txt_backkupName = (TextView) convertView.findViewById(R.id.txt_backupName);
            holder.txt_nickname = (TextView) convertView.findViewById(R.id.txt_nickname);
            holder.startMessageChat = (ImageView) convertView.findViewById(R.id.startMessageChat);
            holder.startPhoneCall = (ImageView) convertView.findViewById(R.id.startPhoneCall);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String backupName = contact.getBackupName();
        String nickname = contact.getNickname();
        holder.txt_backkupName.setText(backupName);
        holder.txt_nickname.setText(nickname);
        holder.userID = contact.getUserID();

        holder.startMessageChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                ViewHolder holder1 = (ViewHolder) view.getTag();

                logger.info("___________________v:{}", view);
                logger.info("____________________tag:{}", holder1);

                Integer toUserID = holder1.userID;

                logger.info("__________________________________toUserID:{}", toUserID);

                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("toUserID", toUserID);
                activity.startActivityForResult(intent, 1);

                logger.info("__________________________________message chat:{}", view);
            }
        });

        holder.startPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder holder1 = (ViewHolder) view.getTag();

                logger.info("___________________v:{}", view);
                logger.info("____________________tag:{}", holder1);

                Integer toUserID = holder1.userID;

                logger.info("__________________________________toUserID:{}", toUserID);

                Intent intent = new Intent(activity, CallActivity.class);
                intent.putExtra("isCall",true);
                intent.putExtra("uuid",contact.getUUID());
                activity.startActivityForResult(intent, 1);

                logger.info("__________________________________message chat:{}", view);
            }
        });

        holder.startMessageChat.setTag(holder);
        holder.startPhoneCall.setTag(holder);

        return convertView;
    }

    private static class ViewHolder {
        ImageView contact_header;
        TextView txt_backkupName;
        TextView txt_nickname;
        ImageView startMessageChat;
        ImageView startPhoneCall;
        Integer userID;
    }

    private  static class ViewHoldAlpha{
        TextView txt_alpha;
    }

    public ListView getListView(){
        return contactListView;
    }

    public void refresh(){
        LConstants.initizlizeContact();
        this.contactList = LConstants.fixdContacts;

        BaseActivity.sendMessage(new BaseActivity.MessageHandle() {
            @Override
            public void handle(BaseActivity activity) {
                notifyDataSetChangedSelectTop();
                logger.info("___________________________message handle execute");
            }
        });
    }
}

