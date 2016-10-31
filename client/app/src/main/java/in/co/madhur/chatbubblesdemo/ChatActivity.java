package in.co.madhur.chatbubblesdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;
import com.generallycloud.nio.common.ThreadUtil;
import com.generallycloud.nio.extend.RESMessage;
import com.likemessage.BaseActivity;
import com.likemessage.bean.B_Contact;
import com.likemessage.bean.T_MESSAGE;
import com.likemessage.client.LMClient;
import com.likemessage.common.LConstants;
import com.likemessage.database.DBUtil;
import com.likemessage.network.ConnectorManager;
import com.likemessage.network.MessageReceiver;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import in.co.madhur.chatbubblesdemo.widgets.Emoji;
import in.co.madhur.chatbubblesdemo.widgets.EmojiView;
import in.co.madhur.chatbubblesdemo.widgets.SizeNotifierRelativeLayout;


public class ChatActivity extends BaseActivity implements SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutDelegate, NotificationCenter.NotificationCenterDelegate {

//    private RefreshListView chatListView;
    private ListView chatListView;
    private EditText chatEditText1;
    private List<T_MESSAGE> chatList;
    private ImageView enterChatView1, emojiButton;
    private ChatListAdapter chatListAdapter;
    private EmojiView emojiView;
    private SizeNotifierRelativeLayout sizeNotifierRelativeLayout;
    private boolean showingEmoji;
    private int keyboardHeight;
    private boolean keyboardVisible;
    private WindowManager.LayoutParams windowLayoutParams;
    private Logger logger = LoggerFactory.getLogger(ChatActivity.class);
    private Integer toUserID = null;

//    private Handler update = new Handler() {
//
//        public void handleMessage(Message msg) {
//
//            if (msg.what == 1){
//                chatListAdapter.notifyDataSetChangedSelectTop();
//            }else if(msg.what == 0){
//                chatListAdapter.notifyDataSetChanged();
//            }else{
//                chatListAdapter.notifyDataSetChangedSelect(msg.what);
//            }
//            super.handleMessage(msg);
//        }
//    };
//
//    public void notifyDataSetChanged(int value) {
//        update.sendEmptyMessage(value);
//    }

    /* ---------------------------------    ------------------------------*/

    /* ---------------------------------    ------------------------------*/

    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if (v == chatEditText1) {
                    sendMessage(editText.getText().toString(), toUserID);
                }

                chatEditText1.setText("");

                return true;
            }
            return false;

        }
    };

    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == enterChatView1) {
                sendMessage(chatEditText1.getText().toString(), toUserID);
            }

            chatEditText1.setText("");

        }
    };

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {

            } else {
                enterChatView1.setImageResource(R.drawable.ic_chat_send);

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 0) {
                enterChatView1.setImageResource(R.drawable.ic_chat_send);
            } else {
                enterChatView1.setImageResource(R.drawable.ic_chat_send_active);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();

        this.toUserID = (Integer) intent.getIntExtra("toUserID", -1);

        logger.info("___________________________,onCreate,{}", toUserID);

        AndroidUtilities.statusBarHeight = getStatusBarHeight();

        chatListView = (ListView) findViewById(R.id.chat_list_view);

        chatEditText1 = (EditText) findViewById(R.id.chat_edit_text1);
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);

        // Hide the emoji on click of edit text
        chatEditText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingEmoji)
                    hideEmojiPopup();
            }
        });

        emojiButton = (ImageView) findViewById(R.id.emojiButton);

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmojiPopup(!showingEmoji);
            }
        });

        chatListAdapter = new ChatListAdapter(this, chatListView);

        chatListAdapter.getMoreMessage(toUserID);

        chatListAdapter.setChatUserID(toUserID);

        chatListView.setAdapter(chatListAdapter);

        chatEditText1.setOnKeyListener(keyListener);

        enterChatView1.setOnClickListener(clickListener);

        chatEditText1.addTextChangedListener(watcher1);

        sizeNotifierRelativeLayout = (SizeNotifierRelativeLayout) findViewById(R.id.chat_layout);
        sizeNotifierRelativeLayout.delegate = this;

        MessageReceiver.getInstance().setChatListView(chatListView);

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);

        MessageReceiver.getInstance().setChatActivity(this);

        ChatActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                chatListAdapter.notifyDataSetChanged();
            }
        });

        B_Contact contact = LConstants.getBContactByUserID(toUserID);

        this.setTitle(contact.getBackupName());

        TextView chat_with = (TextView) findViewById(R.id.txt_chat_with);

        chat_with.setText(" < " + contact.getBackupName());

        logger.info("____________________title:{}", contact.getBackupName());

        chatListView.setOnScrollListener(new AbsListView.OnScrollListener() {

           public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                logger.info("___________________________scroll:{},{},{}",new Object[]{i,i1,i2});

                if(i == 0){
                    //chatListAdapter.getMoreMessage(toUserID);
                }

            }
        });

    }

    private void sendMessage(final String messageText, final Integer toUserID) {
        if (messageText.trim().length() == 0)
            return;

        final B_Contact contact = LConstants.getBContactByUserID(toUserID);

        final T_MESSAGE message = new T_MESSAGE();
        message.setDeleted(false);
        message.setFromUserID(LConstants.THIS_USER_ID);
        message.setMessage(messageText);
        message.setMsgDate(new Date().getTime());
        message.setMsgType(0);
        message.setSend(true);
        message.setToUserID(toUserID);
        chatListAdapter.addChat(message);
        sendMessage(new MessageHandle() {
            @Override
            public void handle(BaseActivity activity) {
                chatListAdapter.notifyDataSetChangedSelectTop();
            }
        });

        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {


//                MessageProducer messageProducer = LConstants.messageProducer;
//
//                MapByteMessage _message = new MapByteMessage("mmm",contact.getUUID(),messageText.getBytes(Encoding.UTF8));
//
//                _message.put("eventName","lMessage");
//                _message.put("fromUserID",message.getFromUserID());

                try {

//                    logger.info("________________sendMessage,message:{}",_message);
//                    if(messageProducer.offer(_message)){
//                        DBUtil.getDbUtil().saveMsg(message);
//                    }

                    LMClient client = LConstants.LM_CLIENT;
                    logger.info("________________________________toUserID:{}", toUserID);
                    logger.info("________________sendMessage,message:{}", message);
                    logger.info("________________sendMessage,contact:{}", contact);

                    ConnectorManager manager = LConstants.getConnectorManager();

                    RESMessage resMessage = client.addMessage(manager.getFixedIOSession(), message, contact.getUUID());
                    if (resMessage.getCode() == 0) {
                        DBUtil.getDbUtil().saveMsg(message);
                    } else {
                        Toast.makeText(getActivity(), resMessage.getDescription(), Toast.LENGTH_SHORT);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

//        chatListAdapter.notifyDataSetChanged();

//        chatListView.setSelection(chatListAdapter.getCount() - 1);

        // Mark message as delivered after one second

        /*
        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

        exec.schedule(new Runnable(){
            @Override
            public void run(){
               message.setMessageStatus(Status.DELIVERED);

                final ChatMessage message = new ChatMessage();
                message.setMessageStatus(Status.SENT);
                message.setMessageText(messageText);
                message.setUserType(UserType.SELF);
                message.setMessageTime(new Date().getTime());
                chatList.add(message);

                ChatActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        chatListAdapter.notifyDataSetChanged();
                    }
                });


            }
        }, 1, TimeUnit.SECONDS);
*/
    }

    private Activity getActivity() {
        return this;
    }


    /**
     * Show or hide the emoji popup
     *
     * @param show
     */
    private void showEmojiPopup(boolean show) {
        showingEmoji = show;

        if (show) {
            if (emojiView == null) {
                if (getActivity() == null) {
                    return;
                }
                emojiView = new EmojiView(getActivity());

                emojiView.setListener(new EmojiView.Listener() {
                    public void onBackspace() {
                        chatEditText1.dispatchKeyEvent(new KeyEvent(0, 67));
                    }

                    public void onEmojiSelected(String symbol) {
                        int i = chatEditText1.getSelectionEnd();
                        if (i < 0) {
                            i = 0;
                        }
                        try {
                            CharSequence localCharSequence = Emoji.replaceEmoji(symbol, chatEditText1.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20));
                            chatEditText1.setText(chatEditText1.getText().insert(i, localCharSequence));
                            int j = i + localCharSequence.length();
                            chatEditText1.setSelection(j, j);
                        } catch (Exception e) {
                            Log.e(Constants.TAG, "Error showing emoji");
                        }
                    }
                });


                windowLayoutParams = new WindowManager.LayoutParams();
                windowLayoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                if (Build.VERSION.SDK_INT >= 21) {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                } else {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                    windowLayoutParams.token = getActivity().getWindow().getDecorView().getWindowToken();
                }
                windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }

            final int currentHeight;

            if (keyboardHeight <= 0)
                keyboardHeight = App.getInstance().getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200));

            currentHeight = keyboardHeight;

            WindowManager wm = (WindowManager) App.getInstance().getSystemService(Activity.WINDOW_SERVICE);

            windowLayoutParams.height = currentHeight;
            windowLayoutParams.width = AndroidUtilities.displaySize.x;

            try {
                if (emojiView.getParent() != null) {
                    wm.removeViewImmediate(emojiView);
                }
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
            }

            try {
                wm.addView(emojiView, windowLayoutParams);
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
                return;
            }

            if (!keyboardVisible) {
                if (sizeNotifierRelativeLayout != null) {
                    sizeNotifierRelativeLayout.setPadding(0, 0, 0, currentHeight);
                }

                return;
            }

        } else {
            removeEmojiWindow();
            if (sizeNotifierRelativeLayout != null) {
                sizeNotifierRelativeLayout.post(new Runnable() {
                    public void run() {
                        if (sizeNotifierRelativeLayout != null) {
                            sizeNotifierRelativeLayout.setPadding(0, 0, 0, 0);
                        }
                    }
                });
            }
        }


    }


    /**
     * Remove emoji window
     */
    private void removeEmojiWindow() {
        if (emojiView == null) {
            return;
        }
        try {
            if (emojiView.getParent() != null) {
                WindowManager wm = (WindowManager) App.getInstance().getSystemService(Context.WINDOW_SERVICE);
                wm.removeViewImmediate(emojiView);
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, e.getMessage());
        }
    }


    /**
     * Hides the emoji popup
     */
    public void hideEmojiPopup() {
        if (showingEmoji) {
            showEmojiPopup(false);
        }
    }

    /**
     * Check if the emoji popup is showing
     *
     * @return
     */
    public boolean isEmojiPopupShowing() {
        return showingEmoji;
    }


    /**
     * Updates emoji views when they are complete loading
     *
     * @param id
     * @param args
     */
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded) {
            if (emojiView != null) {
                emojiView.invalidateViews();
            }

            if (chatListView != null) {
                chatListView.invalidateViews();
            }
        }
    }

    @Override
    public void onSizeChanged(int height) {

        Rect localRect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);

        WindowManager wm = (WindowManager) App.getInstance().getSystemService(Activity.WINDOW_SERVICE);
        if (wm == null || wm.getDefaultDisplay() == null) {
            return;
        }


        if (height > AndroidUtilities.dp(50) && keyboardVisible) {
            keyboardHeight = height;
            App.getInstance().getSharedPreferences("emoji", 0).edit().putInt("kbd_height", keyboardHeight).commit();
        }


        if (showingEmoji) {
            int newHeight = 0;

            newHeight = keyboardHeight;

            if (windowLayoutParams.width != AndroidUtilities.displaySize.x || windowLayoutParams.height != newHeight) {
                windowLayoutParams.width = AndroidUtilities.displaySize.x;
                windowLayoutParams.height = newHeight;

                wm.updateViewLayout(emojiView, windowLayoutParams);
                if (!keyboardVisible) {
                    sizeNotifierRelativeLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if (sizeNotifierRelativeLayout != null) {
                                sizeNotifierRelativeLayout.setPadding(0, 0, 0, windowLayoutParams.height);
                                sizeNotifierRelativeLayout.requestLayout();
                            }
                        }
                    });
                }
            }
        }


        boolean oldValue = keyboardVisible;
        keyboardVisible = height > 0;
        if (keyboardVisible && sizeNotifierRelativeLayout.getPaddingBottom() > 0) {
            showEmojiPopup(false);
        } else if (!keyboardVisible && keyboardVisible != oldValue && showingEmoji) {
            showEmojiPopup(false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    /**
     * Get the system status bar height
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();

        hideEmojiPopup();
    }
}
