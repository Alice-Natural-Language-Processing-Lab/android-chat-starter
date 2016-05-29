package in.co.madhur.chatbubblesdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.likemessage.bean.T_MESSAGE;

import java.text.SimpleDateFormat;
import java.util.List;

import in.co.madhur.chatbubblesdemo.widgets.Emoji;

/**
 * Created by madhur on 17/01/15.
 */
public class ChatListAdapter extends BaseAdapter {


    private ListView chatListView = null;
    private List<T_MESSAGE> chatList;
    private Context context;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");
    private Integer chatUserID = null;

    public ChatListAdapter(Context context,ListView chatListView,List<T_MESSAGE> chatList) {
        this.chatListView = chatListView;
        this.chatList = chatList;
        this.context = context;
    }

    public List<T_MESSAGE> getChatList(){
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
        ViewHolder1 holder1;
        ViewHolder2 holder2;

        if (message.isSend()) {
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_user2_item, null, false);

                holder2 = new ViewHolder2();


                holder2.messageTextView = (TextView) v.findViewById(R.id.message_text);
                holder2.timeTextView = (TextView) v.findViewById(R.id.time_text);
                holder2.messageStatus = (ImageView) v.findViewById(R.id.user_reply_status);
                v.setTag(holder2);

            } else {
                v = convertView;
                holder2 = (ViewHolder2) v.getTag();

            }

            holder2.messageTextView.setText(Emoji.replaceEmoji(message.getMessage(), holder2.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16) ));
            //holder2.messageTextView.setText(message.getMessageText());
            holder2.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMsgDate()));

//            if (message.getMessageStatus() == Status.DELIVERED) {
//                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double_tick));
//            } else if (message.getMessageStatus() == Status.SENT) {
//                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_single_tick));
//
//            }
        } else {
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_user1_item, null, false);
                holder1 = new ViewHolder1();


                holder1.messageTextView = (TextView) v.findViewById(R.id.message_text);
                holder1.timeTextView = (TextView) v.findViewById(R.id.time_text);

                v.setTag(holder1);
            } else {
                v = convertView;
                holder1 = (ViewHolder1) v.getTag();

            }

            holder1.messageTextView.setText(Emoji.replaceEmoji(message.getMessage(), holder1.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16)));
            holder1.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMsgDate()));
        }
        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        T_MESSAGE message = chatList.get(position);
        return message.isSend() ? 0 :1;
        //FIXME .............
    }

    public Integer getChatUserID() {
        return chatUserID;
    }

    public void setChatUserID(Integer chatUserID) {
        this.chatUserID = chatUserID;
    }

    private class ViewHolder1 {
        public TextView messageTextView;
        public TextView timeTextView;
    }

    private class ViewHolder2 {
        public ImageView messageStatus;
        public TextView messageTextView;
        public TextView timeTextView;
    }

    public void addChat(T_MESSAGE chatMessage){
        this.chatList.add(chatMessage);
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        chatListView.setSelection(getCount()-1);
    }
}
