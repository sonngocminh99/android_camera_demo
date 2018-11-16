package mbaas.com.nifcloud.ncmbcameraquickstart;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.nifcloud.mbaas.core.DoneCallback;
import com.nifcloud.mbaas.core.FetchFileCallback;
import com.nifcloud.mbaas.core.NCMB;
import com.nifcloud.mbaas.core.NCMBAcl;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBFile;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    Button b1;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //**************** APIキーの設定とSDKの初期化 **********************
        NCMB.initialize(this.getApplicationContext(), "YOUR_APP_KEY", "YOUR_CLIENT_KEY");

        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.button);
        iv = (ImageView) findViewById(R.id.imageView);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || data.getExtras() == null) {
            return;
        } else {
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            //******* NCMB file upload *******
            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            bp.compress(Bitmap.CompressFormat.PNG, 0, byteArrayStream);
            byte[] dataByte = byteArrayStream.toByteArray();

            //ACL 読み込み:可 , 書き込み:可
            NCMBAcl acl = new NCMBAcl();
            acl.setPublicReadAccess(true);
            acl.setPublicWriteAccess(true);

            //通信実施
            final NCMBFile file;
            try {
                file = new NCMBFile("test.png", dataByte, acl);
                file.saveInBackground(new DoneCallback() {
                    @Override
                    public void done(NCMBException e) {
                        String result;
                        if (e != null) {
                            //保存失敗
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Notification from NifCloud")
                                    .setMessage("Error:" + e.getMessage())
                                    .setPositiveButton("OK", null)
                                    .show();
                        } else {
                            //******* NCMB file download *******
                            NCMBFile file = null;
                            try {
                                file = new NCMBFile("test.png");
                                file.fetchInBackground(new FetchFileCallback() {
                                    @Override
                                    public void done(byte[] dataFetch, NCMBException er) {
                                        if (er != null) {
                                            //失敗処理
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("Notification from NifCloud")
                                                    .setMessage("Error:" + er.getMessage())
                                                    .setPositiveButton("OK", null)
                                                    .show();
                                        } else {
                                            //成功処理
                                            Bitmap bMap = BitmapFactory.decodeByteArray(dataFetch, 0, dataFetch.length);
                                            iv.setImageBitmap(bMap);
                                        }
                                    }
                                });
                            } catch (NCMBException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            } catch (NCMBException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}