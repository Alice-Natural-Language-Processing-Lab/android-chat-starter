package com.likemessage.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;
import com.likemessage.PhoneActivity;
import com.likemessage.bean.B_Contact;
import com.likemessage.bean.T_MESSAGE;
import com.likemessage.common.BaseAdapter;
import com.likemessage.common.LConstants;

import java.util.List;

import in.co.madhur.chatbubblesdemo.R;

public class MessageListAdpter extends BaseAdapter {

	private PhoneActivity activity;
	private ListView messageListView;
	private LayoutInflater inflater;
	private List<T_MESSAGE> messageList;
	private Logger logger = LoggerFactory.getLogger(MessageListAdpter.class);

	public MessageListAdpter(PhoneActivity activity,ListView messageListView,List<T_MESSAGE> messageList) {
		this.messageListView = messageListView;
		this.activity = activity;
		this.messageList = messageList;
		inflater = LayoutInflater.from(this.activity);
	}

	public List<T_MESSAGE> getMessageList(){
		return messageList;
	}

	@Override
	public int getCount() {
		return messageList == null ? 0 : messageList.size();
	}

	@Override
	public T_MESSAGE getItem(int position) {
		return messageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MessageHolder holder = null;
		if (convertView == null) {
			holder = new MessageHolder();
			convertView = inflater.inflate(R.layout.fragment_message_item, parent, false);
			convertView.setTag(holder);
			holder.txt_message_user = (TextView) convertView.findViewById(R.id.txt_message_user);
			holder.txt_message_text = (TextView) convertView.findViewById(R.id.txt_message_text);
		} else {
			holder = (MessageHolder) convertView.getTag();
		}

		T_MESSAGE message = messageList.get(position);

		Integer userID = message.isSend() ? message.getToUserID() : message.getFromUserID();

		userID = message.getToUserID();

		logger.info("_____________________toUserID:{}",userID);

		B_Contact contact = LConstants.getBContactByUserID(userID);

		logger.info("_____________________contact:{}",contact);

		holder.txt_message_user.setText(contact.getBackupName());
		holder.txt_message_text.setText(message.getMessage());

		return convertView;
	}

	public void addMessage(T_MESSAGE messageBean) {
		this.messageList.add(messageBean);
		notifyDataSetChanged();
	}

	protected static class MessageHolder {
		private TextView txt_message_user;
		private TextView txt_message_text;
	}

	public ListView getListView() {
		return messageListView;
	}
}
