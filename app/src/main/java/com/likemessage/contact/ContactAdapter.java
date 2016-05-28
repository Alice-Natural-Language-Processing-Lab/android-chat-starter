package com.likemessage.contact;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.likemessage.bean.B_Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import in.co.madhur.chatbubblesdemo.ChatActivity;
import in.co.madhur.chatbubblesdemo.R;

public class ContactAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<B_Contact> list;
	private HashMap<String, Integer> alphaIndexer; // 字母索引
	private String[] sections; // 存储每个章节
	private Activity activity; // 上下文

	private Logger logger = LoggerFactory.getLogger(ContactAdapter.class);

	public ContactAdapter(Activity activity, List<B_Contact> list,
						  QuickAlphabeticBar alpha) {
		this.activity = activity;
		this.inflater = LayoutInflater.from(activity);
		this.list = list;
		this.alphaIndexer = new HashMap<String, Integer>();
		this.sections = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			// 得到字母
			String name = getAlpha(list.get(i).getSortKey());
			if (!alphaIndexer.containsKey(name)) {
				alphaIndexer.put(name, i);
			}
		}

		Set<String> sectionLetters = alphaIndexer.keySet();
		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
		Collections.sort(sectionList); // 根据首字母进行排序
		sections = new String[sectionList.size()];
		sectionList.toArray(sections);

		alpha.setAlphaIndexer(alphaIndexer);

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void remove(int position) {
		list.remove(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.fragment_contact_item, null);
			holder = new ViewHolder();
			holder.contact_header = (ImageView) convertView
					.findViewById(R.id.contact_header);
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.startMessageChat = (ImageView) convertView.findViewById(R.id.startMessageChat);
			holder.startPhoneCall = (ImageView) convertView.findViewById(R.id.startPhoneCall);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		B_Contact contact = list.get(position);
		String backupName = contact.getBackupName();
		String nickname = contact.getNickname();
		holder.name.setText(backupName);
		holder.number.setText(nickname);
		//好像是打开手机号码名片
//		holder.quickContactBadge.assignContactUri(ContactsContract.Contacts.getLookupUri(
//				contact.getContactId(), contact.getLookUpKey()));
//		if (0 == contact.getPhotoId()) {
//			holder.quickContactBadge.setImageResource(R.drawable.ic_launcher);
//			holder.startMessageChat.setImageResource(R.drawable.ic_launcher);
//			holder.startPhoneCall.setImageResource(android.R.drawable.sym_action_call);
//		} else {
//			Uri uri = ContentUris.withAppendedId(
//					ContactsContract.Contacts.CONTENT_URI,
//					contact.getContactId());
//			InputStream input = ContactsContract.Contacts
//					.openContactPhotoInputStream(activity.getContentResolver(), uri);
//			Bitmap contactPhoto = BitmapFactory.decodeStream(input);
//			holder.quickContactBadge.setImageBitmap(contactPhoto);
//		}
		// 当前字母
		String currentStr = getAlpha(contact.getSortKey());
		// 前面的字母
		String previewStr = (position - 1) >= 0 ? getAlpha(list.get(
				position - 1).getSortKey()) : " ";

		if (!previewStr.equals(currentStr)) {
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentStr);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}

		holder.startMessageChat.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				ViewHolder holder1 = (ViewHolder) view.getTag();

				logger.info("___________________v:{}",view);
				logger.info("____________________tag:{}",holder1);

				String phoneNO = "";
				if (holder1 != null){
					phoneNO = holder1.number.getText().toString();
				}

				logger.info("__________________________________phoneNO:{}",phoneNO);

				Intent intent = new Intent(activity,ChatActivity.class);
				intent.putExtra("phoneNO", phoneNO);
				activity.startActivityForResult(intent, 1);

				logger.info("__________________________________message chat:{}",view);
			}
		});

		holder.startMessageChat.setTag(holder);

		holder.startPhoneCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				logger.info("__________________________________phone call:{}",view);
			}
		});


		return convertView;
	}

	private static class ViewHolder {
		ImageView contact_header;
		TextView alpha;
		TextView name;
		TextView number;
		ImageView startMessageChat;
		ImageView startPhoneCall;
	}

	/**
	 * 获取首字母
	 *
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式匹配
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // 将小写字母转换为大写
		} else {
			return "#";
		}
	}

}

