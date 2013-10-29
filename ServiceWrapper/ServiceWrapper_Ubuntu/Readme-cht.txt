預設條件:
1.安裝與設定mysql
2.對ezScrum.zip解壓縮，以下用${ezScrum}代表解壓縮的路徑
3.切換目錄到${ezScrum}下，指令︰cd ${ezScrum}

使用方式:
1.修改${ezScrum}下的JettyServer.xml，將
<SystemProperty name="jetty.host" default="localhost"/>改成
<SystemProperty name="jetty.host" default="預設的IP"/>，例如:192.168.1.2
2.將${ezScrum}下的ezScrum檔案改變權限，指令︰chmod 755 ezScrum
3.將${ezScrum}下的wrapper檔案改變權限，指令︰chmod 755 wrapper
4.指令︰ls -al 確認${ezScrum}下的 ezScrum 以及 wrapper 具有執行的權限
5.執行 ezScrum 怖建於後端程式，指令︰sudo ./ezScrum install（注意要有root權限）
6.開啟 ezSCrum 服務，指令︰sudo ./ezScrum start（注意要有root權限）
7. 開啟瀏覽器輸入 http://192.168.1.2:8080/ezScrum 即可正常使用ezScrum

後續:
1. 關閉 ezSCrum 服務，指令︰sudo ./ezScrum stop（注意要有root權限）
2. 移除 ezScrum 怖建於後端程式，指令︰sudo ./ezScrum remove（注意要有root權限）
3. 更新 ezScrum 後請在client 端清除瀏覽器快取