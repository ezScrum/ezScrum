<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
<link rel="stylesheet" type="text/css" href="css/Message.css"/>

<script type="text/javascript">
	var updateRecord = Ext.data.Record.create([
		'date', 'version', 'info', 'url'
	]);

	var updateJSReader = new Ext.data.JsonReader({
		root: 'update'
	}, updateRecord);
	
	var updateStore = new Ext.data.Store({
		idIndex: 0,
		id: 0,
		reader: updateJSReader,
		fields: [
			{name : 'url'},
			{name : 'date'},
			{name : 'version'},
			{name : 'info'}
		]
	});
	
	function showMask(targetId, msg) {
	    new Ext.LoadMask(Ext.get(targetId), {msg: msg}).show();
	}

	function hideMask(targetId) {
	    new Ext.LoadMask(Ext.get(targetId)).hide();
	}
	
	function downloadTolocal(target) {
		showMask('UpdateWidget', 'Downloading ... please wait');
		Ext.Ajax.request({
			url		: './UpdateDownload.do',
			params	: {link : target},
			success	: function(response) {
				hideMask('UpdateWidget');
				if (eval(response.responseText)) {
					Ext.example.msg('Download', 'Download Success');
				} else {
					Ext.example.msg('Download', 'Download Failure');
				}
          	}
		});
	}

	Ext.onReady(function() {
		Ext.Ajax.request({
			url: './CheckUpdateInfo.do',
			success: function(response) {
				if(response.responseText == '') {
					Ext.MessageBox.alert('Update Info', 'There is no new version or it cannot connect to ezScrum server.');
				} else {
					updateStore.loadData(Ext.decode(response.responseText));
				}
          	}
		});
		
		function UpdateDownload(val) {
			return '<center><span><img src="./images/download.png" /></span></center>';
		} 
		
		var grid = new Ext.grid.GridPanel({
			id: 'UpdateWidget',
			store: updateStore,
			sm: new Ext.grid.RowSelectionModel({
		    	singleSelect:true
		    }),
	        columns: [
	        	{header: 'Download', width: 100, sortable: false, dataIndex: 'url', renderer: UpdateDownload},
	            {header: 'Release Date', width: 150, sortable: true, dataIndex: 'date'},
	            {header: 'Version', width: 100, sortable: true, dataIndex: 'version'},
	            {header: 'Update Information',width: 350, sortable: true, dataIndex: 'info'}
	        ],
	        stripeRows: true,
	        height: 400,
	        title: 'ezScrum Update Info',
	        stateful: true,
	        stateId: 'grid'
	    });
	    
	    grid.getSelectionModel().on({
			selectionChange : {buffer:10, fn:function(sm) {
				var record = grid.getSelectionModel().getSelected();

				var info = 'Do you want to download ezScrum version ' + record.get('version') + ' ?';
				Ext.MessageBox.confirm('Download ezScrum', info, function(btn) {
					if (btn == 'yes') {
						downloadTolocal(record.get('url'));
					}
				});
			}
		}});
	    
		grid.render('content');
	});
</script>

<div id="content"></div>