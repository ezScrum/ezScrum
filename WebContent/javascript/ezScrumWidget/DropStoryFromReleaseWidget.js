Ext.ns('ezScrum');

/* Drop Story From Release Widget */
ezScrum.DropStoryFromReleaseWidget = Ext.extend(Ext.Window, {
	title: 'Drop Story',
	height: 140,
	width: 450,
	modal: false,
	issueId: '-1',
	releaseId: '-1',
	closeAction: 'hide',
	initComponent: function() {
		var config = {
			// Delete Retrospective action url
			url: 'ajaxRemoveReleaseBacklog.do',
			items: {
				xtype: 'label',
				html: 'Make sure you sure to drop the story from this release!<br/>'
			},
			buttons: [{
				text: 'Drop',
				scope: this,
				handler: this.onDrop
			}, {
				text: 'Cancel',
				scope: this,
				handler: this.onCancel
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.DropStoryFromReleaseWidget.superclass.initComponent.apply(this, arguments);

		this.addEvents('DropStorySuccess', 'DropStoryFailure');
	},
	dropStory: function(issueId, releaseId) {
		this.issueId = issueId;
		this.releaseId = releaseId;
		this.show();
	},
	// Drop action
	onDrop: function() {
		// 顯示 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {
			msg: "Please wait..."
		});
		myMask.show();

		// Ajax request
		var obj = this;
		Ext.Ajax.request({
			url: this.url,
			success: function(response) {
				obj.onSuccess(response);
			},
			failure: function(response) {
				obj.onFailure(response);
			},
			params: {
				issueID: this.issueId,
				releaseId: this.releaseId
			}
		});
	},
	// 按下取消按鈕 關閉刪除Retrospective視窗
	onCancel: function() {
		this.hide();
	},
	// Ajax request 成功
	onSuccess: function(response) {
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {
			msg: "Please wait..."
		});
		myMask.hide();

		// check action permission
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			// 讀取回應資料
			var rs = myReader.readRecords(response.responseXML);
			// 顯示回應資料
			var record = rs.records[0];
			if (record) {
				this.fireEvent('DropStorySuccess', this, response, record.data['Id']);
			} else this.fireEvent('DropStoryFailure', this, response, this.issueId);
		}
	},
	onFailure: function(response) {
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {
			msg: "Please wait..."
		});
		myMask.hide();

		this.fireEvent('DropStoryFailure', this, response, this.issueId);
	}
});