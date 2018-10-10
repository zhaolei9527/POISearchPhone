package com.poisearchphone.Activity;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.poisearchphone.Base.BaseActivity;
import com.poisearchphone.R;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * com.poisearchphone.Activity
 *
 * @author 赵磊
 * @date 2018/10/9
 * 功能描述：
 */
public class HomeActivity extends BaseActivity {

    @Override
    protected void ready() {
        super.ready();
       /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected int setthislayout() {
        return R.layout.home_layout;
    }

    @Override
    protected void initview() {

    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        getAllCall();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private void getAllCall() {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursorUser = resolver.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID}, null, null, null);
        while (cursorUser.moveToNext()) {
            int id = cursorUser.getInt(0); // 按上面数组的声明顺序获取
            String name = cursorUser.getString(1);
            int rawContactsId = cursorUser.getInt(2);
            Log.e("HomeActivity", name + "---" + id + "---" + rawContactsId);
            if (name.startsWith("SAK")) {
                deleteCall(id);
            }
        }
    }

    private void deleteCall(int id) {
        try {
            // data表中对应的id值
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(id)})
                    .build());
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }

}
