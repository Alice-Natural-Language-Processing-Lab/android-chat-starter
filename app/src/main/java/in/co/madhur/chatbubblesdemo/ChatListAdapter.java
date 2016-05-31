package in.co.madhur.chatbubblesdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.likemessage.bean.T_MESSAGE;
import com.likemessage.common.BaseAdapter;
import com.likemessage.database.DBUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import in.co.madhur.chatbubblesdemo.widgets.Emoji;

/**
 * Created by madhur on 17/01/15.
 */
public class ChatListAdapter extends BaseAdapter {


    private ListView chatListView = null;
    private List<T_MESSAGE> chatList;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
    private Integer chatUserID = null;
    private Logger logger = LoggerFactory.getLogger(ChatListAdapter.class);
    private int currentIndex = 0;
    private int pageSize = 15;
    private ChatActivity activity = null;

    public ChatListAdapter(ChatActivity activity, ListView chatListView) {
        this.activity = activity;
        this.chatListView = chatListView;
        this.chatList = new ArrayList<T_MESSAGE>();
    }

    public List<T_MESSAGE> getChatList() {
        return chatList;
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        T_MESSAGE message = chatList.get(position);

        if (message.getMsgType() == 9) {

            ViewHolderTimeline holderTimeline;

            if (convertView == null) {
                v = LayoutInflater.from(activity).inflate(R.layout.activity_chat_item_timeline, null, false);
                holderTimeline = new ViewHolderTimeline();
                holderTimeline.timeline = (TextView) v.findViewById(R.id.txt_timeline);
                v.setTag(holderTimeline);
            } else {
                v = convertView;
                holderTimeline = (ViewHolderTimeline) v.getTag();
            }

            holderTimeline.timeline.setText(SIMPLE_DATE_FORMAT.format(message.getMsgDate()));

            return v;
        }

        ViewHolder1 holder1;
        ViewHolder2 holder2;

        if (message.isSend()) {
            if (convertView == null) {
                v = LayoutInflater.from(activity).inflate(R.layout.activity_chat_item_user_from, null, false);

                holder2 = new ViewHolder2();


                holder2.messageTextView = (TextView) v.findViewById(R.id.message_text);
//                holder2.timeTextView = (TextView) v.findViewById(R.id.time_text);
                holder2.messageStatus = (ImageView) v.findViewById(R.id.user_reply_status);
                v.setTag(holder2);
            } else {
                v = convertView;
                holder2 = (ViewHolder2) v.getTag();
            }

            holder2.messageTextView.setText(Emoji.replaceEmoji(message.getMessage(), holder2.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16)));
            //holder2.messageTextView.setText(message.getMessageText());
//            holder2.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMsgDate()));

//            if (message.getMessageStatus() == Status.DELIVERED) {
//                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double_tick));
//            } else if (message.getMessageStatus() == Status.SENT) {
//                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_single_tick));
//
//            }
        } else {
            if (convertView == null) {
                v = LayoutInflater.from(activity).inflate(R.layout.activity_chat_item_user_to, null, false);
                holder1 = new ViewHolder1();


                holder1.messageTextView = (TextView) v.findViewById(R.id.message_text);
//                holder1.timeTextView = (TextView) v.findViewById(R.id.time_text);

                v.setTag(holder1);

            } else {
                v = convertView;
                holder1 = (ViewHolder1) v.getTag();
            }

            holder1.messageTextView.setText(Emoji.replaceEmoji(message.getMessage(), holder1.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16)));
//            holder1.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMsgDate()));
        }
        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        T_MESSAGE message = chatList.get(position);

        if (message.getMsgType() == 9){
            return 2;
        }
        return message.isSend() ? 0 : 1;
    }

    public Integer getChatUserID() {
        return chatUserID;
    }

    public void setChatUserID(Integer chatUserID) {
        this.chatUserID = chatUserID;
    }

    private class ViewHolder1 {
        public TextView messageTextView;
    }

    private class ViewHolder2 {
        public ImageView messageStatus;
        public TextView messageTextView;
    }

    private class ViewHolderTimeline {
        public TextView timeline;
    }

    public void addChat(T_MESSAGE chatMessage) {
        //TODO add timeline
        this.chatList.add(chatMessage);
    }

    public void getMoreMessage(Integer toUserID){
        this.currentIndex += pageSize;
        List<T_MESSAGE> messageList = DBUtil.getDbUtil().findChat(toUserID,currentIndex,0);
        List<T_MESSAGE> ml = new ArrayList<T_MESSAGE>();
        long lastDate = 0;
        long dateInterval = 5 * 60 * 1000;
        for (int i = 0; i < messageList.size(); i++) {

            T_MESSAGE message = messageList.get(i);

            long date = message.getMsgDate();

            if (date - lastDate > dateInterval) {
                lastDate = date;
                T_MESSAGE m = new T_MESSAGE();
                m.setMsgDate(lastDate);
                m.setMsgType(9);
                ml.add(m);
            }
            ml.add(message);
        }
        this.chatList.clear();
        this.chatList.addAll(ml);

        if (currentIndex == pageSize){
            this.activity.notifyDataSetChanged(1);
        }else{

            int y = currentIndex % pageSize;

            if (y == 0){
                this.activity.notifyDataSetChanged(pageSize);
            }else if(y == 1){
                this.activity.notifyDataSetChanged(2);
            }else{
                this.activity.notifyDataSetChanged(y);
            }
        }
    }

    @Override
    public ListView getListView() {
        return chatListView;
    }
}
