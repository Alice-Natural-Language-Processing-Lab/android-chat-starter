package com.likemessage.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.likemessage.bean.B_Contact;
import com.likemessage.bean.T_MESSAGE;
import com.likemessage.common.LConstants;

import java.util.List;

import in.co.madhur.chatbubblesdemo.R;

public class MessageListAdpter extends BaseAdapter {

	private Context context;
	private ListView messageListView;
	private LayoutInflater inflater;
	private List<T_MESSAGE> messageList;

	public MessageListAdpter(Context mc,ListView messageListView,List<T_MESSAGE> messageList) {
		this.messageListView = messageListView;
		this.context = mc;
		this.messageList = messageList;
		inflater = LayoutInflater.from(context);
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
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.message = (TextView) convertView.findViewById(R.id.message);
		} else {
			holder = (MessageHolder) convertView.getTag();
		}

		T_MESSAGE message = messageList.get(position);

		Integer userID = message.isSend() ? message.getToUserID() : message.getFromUserID();

		userID = message.getToUserID();

		B_Contact contact = LConstants.getBContactByUserID(userID);

		holder.name.setText(contact.getBackupName());
		holder.message.setText(message.getMessage());

		return convertView;
	}

	public void addMessage(T_MESSAGE messageBean) {
		this.messageList.add(messageBean);
		notifyDataSetChanged();
	}

	protected static class MessageHolder {
		private TextView name;
		private TextView message;
	}

	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		messageListView.setSelection(getCount()-1);
	}
}
