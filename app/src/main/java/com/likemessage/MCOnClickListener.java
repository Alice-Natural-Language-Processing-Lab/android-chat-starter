package com.likemessage;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.likemessage.layout.ContactFragment;
import com.likemessage.layout.MessageFragment;

import in.co.madhur.chatbubblesdemo.R;

/**
 * Created by wangkai on 2016/5/21.
 */
//public class MCOnClickListener implements View.OnClickListener{
public class MCOnClickListener implements View.OnClickListener {

    private Context mContext;
    private FragmentManager manager;
    private MessageFragment messageFragment;
    private ContactFragment contactFragment;
    private Logger logger = LoggerFactory.getLogger(MCOnClickListener.class);
    private Fragment from = null;

    public MCOnClickListener(Context context,MessageFragment messageFragment,ContactFragment contactFragment) {
        this.mContext = context;
        this.messageFragment = messageFragment;
        this.contactFragment = contactFragment;
        this.from = this.messageFragment;
        this.manager = ((Activity) mContext).getFragmentManager();
    }

    public void onClick(View v) {

        logger.info("____________________________________,{}",R.layout.fragment_message);
        logger.info("____________________________________,{}",R.layout.fragment_contact);
        logger.info("_____________________________v_id:{}",v.getId());

        logger.info("________________________________onCLick");

        switch (v.getId()) {
//            case R.id.intentmain:
//                Intent intent = new Intent();
//                intent.setClass(mContext, MainActivity.class);
//                mContext.startActivity(intent);
//                ((Activity) mContext).finish();
//                break;
            case R.id.btn_message:
                switchFragment(from, messageFragment);
                break;
            case R.id.btn_contact:
                switchFragment(from, contactFragment);
                break;
            default:
                break;
        }
    }

    public void switchFragment(Fragment from, Fragment to) {
        if (from == null || to == null)
            return;
        FragmentTransaction transaction = ((Activity) mContext)
                .getFragmentManager().beginTransaction();
        if (!to.isAdded()) {
            // 隐藏当前的fragment，add下一个到Activity中
            transaction.hide(from).add(R.id.fragment_wrapper, to).commit();
            Toast.makeText(mContext, "运行了", Toast.LENGTH_SHORT).show();
        } else {
            // 隐藏当前的fragment，显示下一个
            transaction.hide(from).show(to).commit();
            Toast.makeText(mContext, "没运行了", Toast.LENGTH_SHORT).show();
        }
        // 让menu回去
        Toast.makeText(mContext, "..........", Toast.LENGTH_SHORT).show();

        this.from = to;
    }
}
