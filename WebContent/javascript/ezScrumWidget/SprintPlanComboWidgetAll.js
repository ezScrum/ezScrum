Ext.ns('ezScrum')

var SprintPlanComboAllStore = new Ext.data.Store({
	fields:[
	    {name : 'Id'}, 
	    {name : 'Info'}
    ],
    reader : SprintPlanJsonReader
});

ezScrum.SprintComboWidgetWithAll = Ext.extend(Ext.form.ComboBox, {
	editable: false,
	url: 'GetSprintsComboInfoAll.do',
	triggerAction	: 'all',
	forceSelection	: true,
	mode	: 'local',
	store	: SprintPlanComboAllStore,
	valueField	: 'Info',
	displayField: 'Info',
	listeners : {
		'expand' : function(combo) {
			var blurField = function(el) {
				el.blur();
			}
			blurField.defer(10, this, [ combo.el ]);
		},
		'collapse' : function(combo) {
			var blurField = function(el) {
				el.blur();
			}
			blurField.defer(10, this, [ combo.el ]);
		},
		'render': function() {
			this.loadDataModel();
		}
	},
	loadDataModel: function() {
		var obj = this;
		Ext.Ajax.request({
			url: obj.url,
			success : function(response) {
				ThisSprintStore.loadData(Ext.decode(response.responseText));			// get this sprint info
				SprintPlanComboAllStore.loadData(Ext.decode(response.responseText));	// get all sprints info
				
				obj.setCurrentSprintCombo(ThisSprintStore, SprintPlanComboAllStore);	// set this sprint info to show
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	setCurrentSprintCombo: function(thisSprintStore, SprintsStore) {
		var thisSprintID = thisSprintStore.getAt(0).get('Id');
				
		if (thisSprintID > 0) {
			var thisRecord;
			SprintsStore.each(function (record) {
				if (record.get('Id') == thisSprintID) {
					thisRecord = record;
					return;
				}
			});
			
			this.selectedIndex = this.getStore().indexOfId(thisRecord.get('Id'));
			this.originalValue = this.getStore().getAt(this.selectedIndex).get('Info');
			
		} else {
			this.originalValue = thisSprintStore.getAt(0).get('Info');
		}
		
		this.reset();
	},
	setCurrentSprintID: function(sprintID) {
		if (sprintID > 0) {
			this.selectedIndex = this.getStore().indexOfId(sprintID);
			this.originalValue = this.getStore().getAt(this.selectedIndex).get('Info');
		} else {
			this.originalValue = thisSprintStore.getAt(0).get('Info');
		}
		
		this.reset();
	}
});
Ext.reg('SprintComboAllWidget', ezScrum.SprintComboWidgetWithAll);