# 宝探しゲーム🍎🍏✨
Minecraft Java版で利用できるプラグイン「宝探しゲーム」です。

## ゲーム概要
- 宝箱(チェスト)にランダムに設置されるリンゴを見つけて、スコアを獲得するミニゲームです。  
- 制限時間はなく、リンゴの獲得時間によってスコアが変わります。
- 結果は、プレイヤー名、スコア、日時で登録されます。

## ゲーム詳細
- コマンドで`/treasurehunt`と入力しエンターキーを押すとゲームを開始します。※クリエイティブモード推奨（`/gamemode creative`）
- プレイヤーの現在位置を中心とし、円形に12個の宝箱が配置されます。  
- 宝箱には、リンゴ🍎と金のリンゴ🍏✨が1個ずつ、ランダムに設置されます。
- 宝箱をクリックすると中身がゲットできます。
  
>[!TIP]
>- プレイヤーを中心に、半径10ブロックとして宝箱が設置されます。
>- 周囲に建物や木など、高さがある場所ではその上に宝箱が設置されることがあるため、空を飛ばなくていいように、何もない十分な広さの場所でプレイしてください。

## 得点 
- リンゴ🍎 5秒以内 　・・・100点 
- リンゴ🍎 10秒以内　・・・ 50点
- リンゴ🍎 10秒超過　・・・　0点
  
>[!NOTE]
>- 金のリンゴ🍏✨　・・・取得時間に関わらずボーナス200点✨ （ボーナスを狙って、10秒経過後もあきらめないでね）

- 得点はデータベースに保存され、コマンドで`/treasurehunt list`を実行すると、過去の履歴が表示されます
　　

## プレイ説明動画
https://github.com/user-attachments/assets/328fb28c-6021-4626-aee5-f0ccc79fe693





### データベース設計
|属性|設定値|
|----|----|
|ユーザー名|※|
|パスワード|※|
|URL|※|
|データベース名|spigot_server|
|テーブル名|player_score|
### データベースの接続方法

1. ご自身のローカル環境でMySQLに接続してください。

2. 以下のコマンドを順に実行し、データベースを作成してください。
>```
>CREATE DATABASE spigot_server;
>```
>```
>USE spigot_server;
>```
>　//Macの場合
>```
>CREATE TABLE player_score(id int auto_increment, player_name varchar(100), score int, registered_at datetime, primary key(id));
>```
>
>　//Windowsの場合
>```
>CREATE TABLE player_score(id int auto_increment, player_name varchar(100), score int, registered_at datetime, primary key(id)) DEFAULT CHARSET=utf8;
>```

3. MySQLのurl,username,passwordはご自身のローカル環境に合わせてご使用ください。(mybatis-config.xmlで設定します。)
   
 

## 対応バージョン
- Java版 Minecraft 1.21
- Spigot 1.21
- Java(TM) SE（Java Standard Edition） 21.0.5
