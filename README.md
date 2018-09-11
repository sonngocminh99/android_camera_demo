# 【Android】写真をクラウドに保存しよう！

![画像1](/readme-img/OverView.png)

## 概要

* [ニフクラ mobile backend](https://mbaas.nifcloud.com/)の『ファイルストア機能』を利用して、「撮った写真をクラウドに保存する」内容を実装したサンプルプロジェクトです
* 簡単な操作ですぐに [ニフクラ mobile backend](https://mbaas.nifcloud.com/)の機能を体験いただけます

## ニフクラ mobile backendって何？？
スマートフォンアプリのバックエンド機能（プッシュ通知・データストア・会員管理・ファイルストア・SNS連携・位置情報検索・スクリプト）が**開発不要**、しかも基本**無料**(注1)で使えるクラウドサービス！今回はデータストアを体験します

注1：詳しくは[こちら](https://mbaas.nifcloud.com/price.htm)をご覧ください

![画像2](/readme-img/SdkTypes.png)

## 動作環境
* Windows 7 Professional
* Android Studio 1.5
* Android ver 4x,5x
 * このサンプルアプリは、端末のカメラを使用するため、実機が必要です

※上記内容で動作確認をしています


## 手順
### 1. [ニフクラ mobile backend](https://mbaas.nifcloud.com/)の会員登録とログイン→アプリ作成

* 上記リンクから会員登録（無料）をします登録ができたらログインをすると下図のように「アプリの新規作成」画面が出るのでアプリを作成します

![画像3](/readme-img/mBassNewProject.png)

* アプリ作成されると下図のような画面になります
* この２種類のAPIキー（アプリケーションキーとクライアントキー）は先ほどインポートしたAndroidStudioで作成するAndroidアプリにニフクラ mobile backendの紐付けるため、あとで使います

![画像4](/readme-img/mBassAPIkey.png)

* 動作確認後に写真（画像）が保存される場所も確認しておきましょう

![画像5](/readme-img/mBassData.png)

### 2. [GitHub](https://github.com/ncmbadmin/android_camera_demo.git)からサンプルプロジェクトのダウンロード

* プロジェクトの[Githubページ](https://github.com/ncmbadmin/android_camera_demo.git)から「Clone or download」＞「Download ZIP」をクリックします
* プロジェクトを解凍します

### 3. AndroidStudioでアプリを起動

* AndroidStudioを開き、解凍したプロジェクトを選択します

![画像6](/readme-img/SelectProject.png)

* プロジェクトを開きます

![画像7](/readme-img/ProjectDesign.png)

### 4. APIキーの設定

* AndroidStudioでMainActivity.javaにあるAPIキー（アプリケーションキーとクライアントキー）の設定をします

![画像8](/readme-img/AndroidAPIkey.png)

* それぞれ`YOUR_APP_KEY`と`YOUR_CLIENT_KEY`の部分を書き換えます
 * このとき、ダブルクォーテーション（`"`）を消さないように注意してください！

* AndroidStudioからビルドする。
    * 「プロジェクト場所」\app\build\outputs\apk\ ***.apk ファイルが生成される

### 5. 動作確認

* アプリが起動したら、①「CAMERA」ボタンをタップして、写真を撮影します
* 次に、②「保存」ボタンをタップして、保存します(今回はファイル名固定：`test.png`)
* 保存された画像が起動画面に表示されます

![画像9](/readme-img/AndroidCamera.png)

-----

* 保存に成功したら、[ニフクラ mobile backend](https://mbaas.nifcloud.com/)のダッシュボードから「ファイルストア」を確認してみましょう！

![画像10](/readme-img/mBassFileStore.png)

* 簡単に写真がクラウドに保存できました☆★

## 解説
サンプルプロジェクトに実装済みの内容のご紹介

#### SDKのインポートと初期設定
* ニフクラ mobile backend の[ドキュメント（クイックスタート）](https://mbaas.nifcloud.com/doc/current/introduction/quickstart_android.html#/Android/)をご用意していますので、ご活用ください

#### ロジック
 * `activity_main.xml`でデザインを作成し、`MainActivity.java`にロジックを書いています
 * 写真をクラウドに保存する処理は以下のように記述されます

１）ファイルストアへのアップロード

* mBaaSのAndroid SDKが提供する、ファイルストア機能を利用する場合はNCMBFileを使用します
* ファイルストアへのアップロードするには、このクラスが提供するsaveInBackgroundメソッドを利用します
* saveInBackground()を実施することで、非同期に保存が行われます非同期実施するため、DoneCallBack()を使って、成功・失敗処理を指定します
 - ファイル保存に成功した場合は、ファイルの取得を行います(今回保存したファイル名：`test.png`)
 - ファイル保存に失敗した場合、アラートで保存失敗を表示します
* ファイル名を固定しているため、新しくファイル（画像）を保存すると上書きされます

２）アップロードしたファイルを取得

* ファイルストアに保存したものを取得するには、fetchメソッドを利用します
* fetchInBackgroundメソッドでの非同期処理も可能です(今回利用)
 - ファイルの取得時には、ファイル名を先に取得する必要があります(今回保存したファイル名：`test.png`)
 - ファイル取得に成功した場合は、ファイル取得・表示します
 - ファイル取得に失敗した場合、アラートで失敗を表示します
 
```java

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
```

## 参考
* ニフクラ mobile backend の[ドキュメント（ファイルストア（Android））](https://mbaas.nifcloud.com/doc/current/filestore/basic_usage_android.html)をご用意していますので、ご活用ください

