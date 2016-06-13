# 【Android】アプリにファイルストア機能をつけよう！

## 概要

* 今回はAndroidで、ファイルストアの基本的な使い方(保存、取得)について説明します。
* イメージ的は以下のようになります。

![画像1](/readme-img/OverView.png)

## 準備

* Android Studio
* mBaaSの[アカウント作成](http://mb.cloud.nifty.com/signup.htm)

## 手順

* テンプレートプロジェクトをダウンロード
* SDKを追加（済み・最新SDKを利用したい場合、更新作業として行ってください）
* アプリ作成し、キーを設定
* 動作確認

## STEP 1. テンプレートプロジェクト

* プロジェクトの[Githubページ](https://github.com/ncmbadmin/android_camera_demo.git)から「Clone or download」＞「Download ZIP」をクリックします。
* プロジェクトを解凍します。
* AndroidStudioを開き、解凍したプロジェクトを選択します。

![画像2](/readme-img/SelectProject.png)

* 選択プロジェクトを開きます。

![画像3](/readme-img/ProjectDesign.png)


## STEP 2. SDKを追加と設定 (済み)

Android SDKとは、ニフティクラウドmobile backendが提供している「データストア」「プッシュ通知」などの機能を簡単まコードで利用できるものです。

![画像4](/readme-img/SdkTypes.png)

mBaaSでは、Android, iOS, Unity, JavaScript SDKを提供しています。
今回Android SDKの追加し方と設定を紹介します。

* ダウンロードしたプロジェクトには既に設定済みですが、最新ではない場合、ご自身で入れ替えてください！
* またご自身のプロジェクトにもSDKを追加したい場合も同じく実装必要です。

* SDKダウンロード
SDKはここ（SDK[リリースページ](https://github.com/NIFTYCloud-mbaas/ncmb_android/releases)）から取得してください.
  - NCMB.jarファイルがダウンロードします。
* SDKをインポート
  - app/libsフォルダにNCMB.jarをコピーします。
* 設定追加
  - app/build.gradleファイルに以下を追加します。

```
dependencies {
    compile 'com.google.code.gson:gson:2.3.1'
    compile files('libs/NCMB.jar')
}
```
  - androidManifestの設定

<application>タグの直前に以下のpermissionを追加します。

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```


## STEP 3. アプリキー設定

* 会員登録（無料）をし、登録ができたらログインをすると下図のように「アプリの新規作成」画面出るのでアプリを作成します。

![画像5](/readme-img/mBassNewProject.png)

* アプリ作成されると下図のような画面になります。
* この２種類のAPIキー（アプリケーションキーとクライアントキー）は先ほどインポートしたAndroidStudioで作成するAndroidアプリにニフティクラウドmobile backendの紐付けるため、あとで使います。

![画像6](/readme-img/mBassAPIkey.png)

* この後動作確認でデータが保存される場所も確認しておきましょう。

![画像7](/readme-img/mBassData.png)

* AndroidStudioで取得APIキー(アプリケーションキー、クライントキー)を設定する。

![画像8](/readme-img/AndroidAPIkey.png)

* AndroidStudioからビルドする。
    * 「プロジェクト場所」\app\build\outputs\apk\ ***.apk ファイルが生成される

## STEP 4. 確認

* アプリにてボタンをタブし、画像(今回はファイル名固定：`test.png`)を無事撮った後、アプリにて取得・表示する事が出来ます。

![画像9](/readme-img/AndroidCamera.png)

* mBaaS側のファイルストアにアップロードされた画像を確認出来ます。

![画像10](/readme-img/mBassFileStore.png)


## コード説明

* SDKおよび必要なライブラリーをインポートします。

```java:

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.FetchFileCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBAcl;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBFile;
```

* SDKを初期化

 * `MainActivity.java`のOnCreateメソッドに実装、ここでAPIキーを渡します。

```java:

 @Override
    protected void onCreate(Bundle savedInstanceState) {
       <省略>
        //**************** APIキーの設定とSDKの初期化 **********************
        NCMB.initialize(this, "YOUR_APPLICATION_KEY", "YOUR_CLIENT_KEY");
    }
```

１）ファイルストアへのアップロード

* mBaaSのAndroid SDKが提供する、ファイルストア機能を利用する場合はNCMBFileを使用します。
* ファイルストアへのアップロードするには、このクラスが提供するsaveInBackgroundメソッドを利用します。
* saveInBackground()を実施することで、非同期に保存が行われます。非同期実施するため、DoneCallBack()を使って、成功・失敗処理を指定します。
 - ファイル保存に成功した場合は、ファイルの取得を行います。(今回保存したファイル名：`test.png`)
 - ファイル保存に失敗する場合、アラートで保存失敗を表示します。
* アップロードするファイル名が重複する場合は、新ファイルが上書き保存になります。

２）アップロードしたファイルを取得

* ファイルストアに保存したものを取得するには、fetchメソッドを利用します。
* fetchInBackgroundメソッドでの非同期処理も可能です。(今回利用)
 - ファイルの取得時には、ファイル名を先に取得する必要があります。(今回保存したファイル名：`test.png`)
 - ファイル取得に成功した場合は、ファイル取得・表示します。
 - ファイル取得に失敗する場合、アラートで失敗を表示します。
 
```java:

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
            final NCMBFile file = new NCMBFile("test.png", dataByte, acl);
            file.saveInBackground(new DoneCallback() {
                @Override
                public void done(NCMBException e) {
                    String result;
                    if (e != null) {
                        //保存失敗
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Notification from Nifty")
                                .setMessage("Error:" + e.getMessage())
                                .setPositiveButton("OK", null)
                                .show();
                    } else {
                        //******* NCMB file download *******
                        NCMBFile file = new NCMBFile("test.png");
                        file.fetchInBackground(new FetchFileCallback() {
                            @Override
                            public void done(byte[] dataFetch, NCMBException er) {
                                if (er != null) {
                                    //失敗処理
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Notification from Nifty")
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


                    }
                }
            });
        }
    }
```

## もっと深く知りたい方へ
* ニフティクラウドmobile backend の[ドキュメント（ファイルストア（Android））](http://mb.cloud.nifty.com/doc/current/filestore/basic_usage_android.html)をご用意していますので、ご活用ください。

## 参考

サンプルコードをカスタマイズすることで、様々な機能を実装できます！
データ保存・データ検索・会員管理・プッシュ通知などの機能を実装したい場合には、
以下のドキュメントもご参考ください。

* [ドキュメント](http://mb.cloud.nifty.com/doc/current/)
* [ドキュメント・データストア](http://mb.cloud.nifty.com/doc/current/datastore/basic_usage_android.html)
* [ドキュメント・会員管理](http://mb.cloud.nifty.com/doc/current/user/basic_usage_android.html)
* [ドキュメント・プッシュ通知](http://mb.cloud.nifty.com/doc/current/push/basic_usage_android.html)

## 最後に

データを保存するってサーバを立てたり、自分でサーバ運用とか、設計とか、アプリからサーバーとのやりとりも色々考慮しなければなりません。
最短方法というのは、このようにmBaaSサービスを使って、運用、設計など自分でやらなくて済む、開発も数行コード書けばいいという便利なものはいかがでしょうか？

## Contributing

*    Fork it!
*    Create your feature branch: git checkout -b my-new-feature
*    Commit your changes: git commit -am 'Add some feature'
*    Push to the branch: git push origin my-new-feature
*    Submit a pull request :D

## License

    MITライセンス
    NIFTY Cloud mobile backendのAndroid SDKのライセンス
