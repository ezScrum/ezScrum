Ext.ns('ezScrum')
/*
 * =========================================================== Grid元件
 * ============================================================
 */
// Grid 欄位
var cm = new Ext.grid.ColumnModel( [ {
	header : 'Name',
	dataIndex : 'name',
	width : 150
}, {
	id : 'value',
	header : 'Value',
	dataIndex : 'value'
} ]);

var ds = new Ext.data.JsonStore( {
	autoDestory : true,
	url : "AjaxGetServerInfoAction.do",
	root : 'Datas',
	fields : [ 'name', 'value' ]
});

function restartServer() {

	var restart = true;
	var timeout = 5;
	Ext.MessageBox.show( {
		title:'Count Down',
		msg:'Restart the Server',
		buttons:Ext.MessageBox.CANCEL,
		width:300,
		progress:true,
		closable:true,
		fn:function(btn)
		{
			restart = false;
		}
	});
	var countdown = function(v) {
		return function() {
			if (v == timeout) {
				Ext.MessageBox.hide();
				if(restart)
				{
					Ext.Ajax.request({
					url:"restartServer.do",
					success:function(response)
					{
						
						
					},
					failure:function(response)
					{
						Ext.MessageBox.alert('You Can not Restart the Server','You need have the administrator permission');
					}
					});
				}
			} else {
				Ext.MessageBox.updateProgress(v / timeout,
						'Will Restart the server after ' + (timeout-v) + ' second...');
			}
		}
	}
	for ( var i = 1; i <=timeout; i++) {
		setTimeout(countdown(i), i * 1000);
	}
}
/*
 * =========================================================== ToolBar
 * ============================================================
 */

/*
 * ===========================================================
 * 當網頁讀取完畢之後，會開始進行這邊的初始化動作
 * ============================================================
 */
Ext.onReady(function() {
	ds.load();
	var toolbar = new Ext.Toolbar();
	toolbar.add( {
		id : 'restartBtn',
		icon : 'images/refresh.png',
		text : 'Restart Server',
		handler : function() {
			Ext.MessageBox.confirm('Restart Server',
					'Do you want to restart the server ?', function(btn) {
						// 如果確定要Restart Server
					if (btn == 'yes') {
						restartServer();
					}
				});
		}
	});
	var restartManager = new Ext.grid.GridPanel( {
		frame : true,
		stripeRows : true,
		loadMask : true,
		region : 'center',
		cm : cm,
		store : ds,
		// 設定讓Value列自動延伸
		autoExpandColumn : 'value',
		tbar : toolbar
	});

	var mainPanel = new Ext.Panel( {
		title : "Server Manager",
		renderTo : 'Restart_Manager',
		height : 600,
		layout : 'border',
		items : [ restartManager ]
	});

});