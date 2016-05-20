package in.co.madhur.chatbubblesdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gifisan.nio.common.DebugUtil;
import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.likemessage.common.LConstants;
import com.likemessage.database.DBUtil;
import com.likemessage.database.LMessage;

public class MessageListAct extends Activity {

	private ListView mlv;
	private MessageAdpter madpter;
	private Logger logger = LoggerFactory.getLogger(MessageListAct.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LConstants.init(this.getApplicationContext());

		setContentView(R.layout.activity_message);

		madpter = new MessageAdpter(this);
		mlv = (ListView) findViewById(R.id.mlv);
		mlv.setAdapter(madpter);
		mlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Intent intent = new Intent(MessageListAct.this,MainActivity.class);
				intent.putExtra("request_text_for_main", "从Main传递到SecondActivity");
				startActivityForResult(intent, 1);
			}
		});
		madpter.setMlist(mlist());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		logger.info("========================"+requestCode);
		logger.info("========================"+resultCode);
	}

	private ArrayList<MessageList> mlist() {
		String name[] = { "妈妈", "爸爸", "姐姐", "哥哥", "弟弟" };
		String message[] = { "起床了么？", "今天准备做什么？", "快给我回个电话。。。", "你在干嘛？",
				"哥哥带我出去玩。。。" };
		ArrayList<MessageList> list = new ArrayList<MessageList>();
		for (int i = 0; i < name.length; i++) {
			MessageList messageList = new MessageList();
			messageList.setName(name[i]);
			messageList.setMessage(message[i]);
			list.add(messageList);
		}

		List<LMessage> lists1 = DBUtil.getDbUtil().findTop(1);

		if (lists1.size() > 0){
			LMessage lMessage = lists1.get(0);
			MessageList messageList = new MessageList();
			messageList.setName(lMessage.getToNo());
			messageList.setMessage(lMessage.getMessage());
			list.add(messageList);
		}
		return list;
	};

}
