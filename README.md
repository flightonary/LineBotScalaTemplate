Play framework template of Line Bot in scala
========

はじめに
------
Line BOTアプリケーション用のPlay frameworkテンプレートです。  
基本的な機能は実装してます。

環境
---
play framework 2.5.x  
typesafe-activator 1.3.x

使い方
----
LineBotController.scala に励ましBOTサンプルを実装してます。  
文章末尾を「〜かな」と呟くと雑に励ましてくれます。

models.linebot.api.SendObjectsにあるAPIを使えばいろんな返答ができます。

Contributors
------------
tonary (nekomelife@gmail.com)
