Ext.ns('ezScrum');
/*-----------------------------------------------------------
 * 
 * Release Detail 中的元件
 * 
 -------------------------------------------------------------*/
var ReleaseId = new Ext.form.Hidden({
	fieldLabel: 'Id',
	name: 'Id',
	allowBlank: false,
	anchor: '95%'
});

var Name = new Ext.form.TextField({
	fieldLabel: 'Name',
	name: 'Name',
	allowBlank: false,
	anchor: '95%'
});

var ReleaseStartDate = new Ext.form.DateField({
	allowBlank: false,
	fieldLabel: 'Start Date',
	name: 'StartDate',
	format: 'Y/m/d',
	altFormats: 'Y/m/d',
	anchor: '95%',
	editable: true
});

var ReleaseEndDate = new Ext.form.DateField({
	allowBlank: false,
	fieldLabel: 'End Date',
	name: 'EndDate',
	format: 'Y/m/d',
	altFormats: 'Y/m/d',
	anchor: '95%',
	editable: true
});

var Description = new Ext.form.TextArea({
	fieldLabel: 'Description',
	name: 'Description',
	allowBlank: false,
	anchor: '95%',
	autoScroll: true
});
var action = new Ext.form.TextField({
	name: 'action',
	hidden: true
});

/*******************************************************************************************************************************************************************************************************
 * 
 * 負責顯示Release Detail Form的Windows
 * 
 ******************************************************************************************************************************************************************************************************/
ezScrum.AddNewReleaseWidget = Ext.extend(Ext.Window, {
	title: 'Release Detail',
	id: 'ReleaseDetailForm',
	width: 600,
	modal: true,
	constrain: true,
	closeAction: 'hide',
	initComponent: function() {
		var config = {
			items: [{
				xtype: 'ReleaseDetail'
			}]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddNewReleaseWidget.superclass.initComponent.apply(this, arguments);
		var formItem = this.items.get(0);
		var winObj = this;

		// 註冊處理FormPanel會丟出來的事件
		// Success事件
		formItem.on('SubmitSuccess', function(obj, response, values) {
			winObj.items.get(0).hide;
			winObj.hide();
			winObj.fireEvent('Success', obj, response, values);

		});
		// SubmitFailure事件
		formItem.on('SubmitFailure', function(obj, response, values) {
			winObj.fireEvent('Failure', obj, response, values);
		});

		this.addEvents('Success', 'Failure');

	},
	/*-----------------------------------------------------------
	 *  跳出視窗顯示Form
	 *-------------------------------------------------------------*/
	showWidget: function(titleString) {
		this.setTitle(titleString);
		this.show();
	},
	/*-----------------------------------------------------------
	 *  將外部傳來的record給FormPanel
	 *-------------------------------------------------------------*/
	loadData: function(record) {
		var e_id = record.attributes['ID'];
		var e_name = record.attributes['Name'];
		var e_sDate = record.attributes['StartDate'];
		var e_eDate = record.attributes['EndDate'];
		var e_des = record.attributes['Description'];

		e_name = e_name.replace(/&lt;/ig, "<");
		e_name = e_name.replace(/&gt;/ig, ">");
		e_name = e_name.replace(/&apos;/ig, "'");
		e_name = e_name.replace(/&quot;/ig, "\"");
		e_name = e_name.replace(/&amp;/ig, "&");

		e_des = e_des.replace(/&lt;/ig, "<");
		e_des = e_des.replace(/&gt;/ig, ">");
		e_des = e_des.replace(/&apos;/ig, "'");
		e_des = e_des.replace(/&quot;/ig, "\"");
		e_des = e_des.replace(/&amp;/ig, "&");

		ReleaseId.setValue(e_id);
		Name.setValue(e_name);
		ReleaseStartDate.setValue(e_sDate);
		ReleaseEndDate.setValue(e_eDate);
		Description.setValue(e_des);
		action.setValue("edit");

		// set date range
		ReleaseEndDate.setMinValue(e_sDate);
		ReleaseEndDate.isValid();

		return e_id;
	},
	/*-----------------------------------------------------------
	 *  新增Release所要顯示的一些基本資訊
	 *-------------------------------------------------------------*/
	loadIDForNewRelease: function() {
		Ext.Ajax.request({
			url: 'AjaxGetNewReleaseID.do',
			success: function(response) {
				ConfirmWidget.loadData(response);
				if (ConfirmWidget.confirmAction()) {
					var rs = newReleaseIDReader.readRecords(response.responseXML);
					if (rs.success) {
						var record = rs.records[0];
						var newID = record.data['ID'];
						ReleaseId.setValue(newID);
					}
				}
			}
		});
		action.setValue("save");
	},
	/*-----------------------------------------------------------
	 *  清空FormPanel裡面的欄位
	 *-------------------------------------------------------------*/
	resetForm: function() {
		this.items.get(0).resetAllField();
	}
});

/*******************************************************************************************************************************************************************************************************
 * 
 * Detail頁面，列出此Release的詳細動作，並且可針對欄位進行編輯
 * 
 ******************************************************************************************************************************************************************************************************/
ezScrum.ReleaseDetail = Ext.extend(Ext.FormPanel, {
	monitorValid: true,
	margins: '5 5 5 5',
	frame: true,
	/*-----------------------------------------------------------
	 *    初始化表單欄位
	 *-------------------------------------------------------------*/
	initComponent: function() {
		var config = {
			autoScroll: true,
			items: [ReleaseId, Name, ReleaseStartDate, ReleaseEndDate, Description, action, {
				xtype: 'RequireFieldLabel'
			}],
			buttons: [{
				formBind: true,
				text: 'Submit',
				scope: this,
				handler: this.submit
			}, {
				text: 'Cancel',
				scope: this,
				handler: function() {
					this.ownerCt.hide();
				}
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.ReleaseDetail.superclass.initComponent.apply(this, arguments);
		this.addEvents('SubmitSuccess', 'SubmitFailure');

		ReleaseStartDate.addListener('select', function(interval, newValue, oldValue) {
			ReleaseEndDate.setMinValue(ReleaseStartDate.getValue());
			ReleaseEndDate.isValid();
		});
	},
	/*-----------------------------------------------------------
	 *   處理當Submit按下去之後的事件
	 -------------------------------------------------------------*/
	submit: function() {
		var form = this.getForm();

		// check the Release Plan date legal or illegal
		this.checkDate();
	},
	/*-----------------------------------------------------------
	 *  上傳成功時候的處理方式   
	 *-------------------------------------------------------------*/
	onSubmitSuccess: function(response) {
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			this.fireEvent('SubmitSuccess', this, response, this.getForm().getValues());
		}
		// this.ownerCt.hide();
	},
	/*-----------------------------------------------------------
	 *  上傳失敗的時候的處理方式   
	 *-------------------------------------------------------------*/
	onSubmitFailure: function(response) {
		this.fireEvent('SubmitFailure', this, response, this.getForm().getValues());
	},
	resetAllField: function() {
		// 讓所有欄位回復初始狀態
		this.getForm().reset();
	},
	checkDate: function() {
		var form = this.getForm();
		var obj = this;
		Ext.Ajax.request({
			url: 'checkReleaseDate.do',
			params: form.getValues(),
			success: function(response) {
				ConfirmWidget.loadData(response);
				if (ConfirmWidget.confirmAction()) {
					if (response.responseText == 'legal') {
						obj.saveReleasePlan();
					} else {// illegal
						Ext.MessageBox.alert('Invalid Date!!', 'Sorry, the Start Date or End Date is overlap with the other Release Plan.');
					}
				}
			},
			failure: function(response) {
				Ext.MessageBox.alert('Failure');
			}
		});
	},
	saveReleasePlan: function() {
		var form = this.getForm();
		var obj = this;
		Ext.Ajax.request({
			url: 'saveReleasePlan.do',
			success: function(response) {
				obj.onSubmitSuccess(response);
			},
			failure: function(response) {
				obj.onSubmitFailure(response);
			},
			params: form.getValues()
		});
	}
});

Ext.reg('ReleaseDetail', ezScrum.ReleaseDetail);