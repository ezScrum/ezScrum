預設條件:
1.安裝與設定mysql
2.對ezScrum.zip解壓縮，以下用${ezScrum}代表解壓縮的路徑

使用方式:
1.修改${ezScrum}下的JettyServer.xml，將
<SystemProperty name="jetty.host" default="localhost"/>改成
<SystemProperty name="jetty.host" default="預設的IP"/>，例如:192.168.1.2
2.點擊${ezScrum}下的InstallApp-NT.bat將ezScrum安裝至XP的服務中
3.點擊${ezScrum}下的ServiceStart.bat將ezScrum的服務啟動
4.開啟瀏覽器輸入http://192.168.1.2:8080/ezScrum即可正常使用ezScrum

後續:
1. 更新 ezScrum 後請在client 端清除瀏覽器快取