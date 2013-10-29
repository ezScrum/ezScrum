Ext.ns('ezScrum');

var moveSprintStore = new Ext.data.ArrayStore({
	fields: ['Id', 'Goal']
});

/* Move Sprint Widget */
ezScrum.window.MoveSprintWindow = Ext.extend(ezScrum.layout.Window, {
	title : 'Move Sprint',
	layout: 'fit',
	width : 200,
	initComponent : function() {
		var config = {
			// Move Sprint action url
			url : 'AjaxMoveSprint.do',
			items : [ {
			    tpl           : '<tpl for="."><div ext:qtip="{Goal}" class="x-combo-list-item">Sprint #{Id}</div></tpl>',
			    store		  : moveSprintStore,
			    displayField  : 'Goal',
			    valueField	  : 'Id',
			    typeAhead     : true,
			    mode          : 'local',
			    triggerAction : 'all',
			    emptyText     : 'Select a sprint...',
			    selectOnFocus : true,
			    editable	  : false,
			    xtype		  : 'combo'
			} ],
			buttons : [{
				text : 'Move',
				scope : this,
				handler : this.onMove
			}, {
				text : 'Cancel',
				scope : this,
				handler : this.onCancel
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.MoveSprintWindow.superclass.initComponent.apply(this, arguments);

		this.SprintCombo = this.items.items[0];
		this.addEvents('MoveSuccess', 'MoveFailure');
	},

	/*-----------------------------------------------------------
	 *  外部Function要呼叫Move Sprint這個動作的話就是從這邊開始的啦
	 *-------------------------------------------------------------*/
	moveSprint : function(canMoveData, oldId) {
		moveSprintStore.loadData(canMoveData);
		
		this.SprintCombo.originalValue = '';
		this.SprintCombo.reset();
		this.oldId = oldId;
		this.show();
	},
	
	// Delete action
	onMove : function() {
		// 顯示 Mask
		var myMask = new Ext.LoadMask(this.getEl(), { msg : "Please wait..." });
		myMask.show();

		// Ajax request
		var obj = this;
		Ext.Ajax.request({
			url : this.url,
			success : function(response) {
				obj.onSuccess(response);
			},
			failure : function(response) {
				obj.onFailure(response);
			},
			params : {
				OldID : this.oldId,
				NewID : obj.SprintCombo.getValue()
			}
		});
	},
	// 按下取消按鈕 關閉刪除Retrospective視窗
	onCancel : function() {
		this.hide();
	},
	// Ajax request 成功
	onSuccess : function(response) {
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {
			msg : "Please wait..."
		});
		myMask.hide();

		// check action permission
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			this.fireEvent('MoveSuccess', this, response);
			this.hide();
		}
	},
	onFailure : function(response) {
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {
			msg : "Please wait..."
		});
		myMask.hide();

		this.fireEvent('MoveFailure', this, response);
		this.hide();
	}
});