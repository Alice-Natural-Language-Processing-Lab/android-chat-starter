package in.co.madhur.chatbubblesdemo;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessageAdpter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<MessageList> mlist;

	public MessageAdpter(Context mc) {
		this.context = mc;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mlist == null ? 0 : mlist.size();
	}

	@Override
	public Object getItem(int position) {
		return mlist.get(position);
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

		holder.name.setText(mlist.get(position).getName());
		holder.message.setText(mlist.get(position).getMessage());

		return convertView;
	}

	public void setMlist(ArrayList<MessageList> mlist) {
		this.mlist = mlist;
		notifyDataSetChanged();
	}

	public void addMlist(ArrayList<MessageList> mlist) {
		this.mlist.addAll(mlist);
		notifyDataSetChanged();
	}

	protected static class MessageHolder {
		private TextView name;
		private TextView message;
	}

}
