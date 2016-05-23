package com.likemessage.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import in.co.madhur.chatbubblesdemo.R;

public class MessageListAdpter extends BaseAdapter {

	private Context context;
	private ListView messageListView;
	private LayoutInflater inflater;
	private ArrayList<MessageBean> messageList;

	public MessageListAdpter(Context mc,ListView messageListView,ArrayList<MessageBean> messageList) {
		this.messageListView = messageListView;
		this.context = mc;
		this.messageList = messageList;
		inflater = LayoutInflater.from(context);
	}

	public ArrayList<MessageBean> getMessageList(){
		return messageList;
	}

	@Override
	public int getCount() {
		return messageList == null ? 0 : messageList.size();
	}

	@Override
	public MessageBean getItem(int position) {
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

		holder.name.setText(messageList.get(position).getName());
		holder.message.setText(messageList.get(position).getMessage());

		return convertView;
	}

	public void addMessage(MessageBean messageBean) {
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
