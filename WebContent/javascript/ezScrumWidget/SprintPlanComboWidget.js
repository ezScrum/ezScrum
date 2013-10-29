Ext.ns('ezScrum')

var SprintPlanComboStore = new Ext.data.Store({
	fields:[
	    {name : 'Id'}, 
	    {name : 'Info'},
	    {name : 'Edit'}
    ],
    reader : SprintPlanJsonReader,
    sortInfo: {field: "Id", direction: "DESC"}
});

ezScrum.SprintComboWidget = Ext.extend(Ext.form.ComboBox, {
	editable: false,
	url: 'GetSprintsComboInfo.do',
	triggerAction	: 'all',
	forceSelection	: true,
	mode	: 'local',
	store	: SprintPlanComboStore,
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
				ThisSprintStore.loadData(Ext.decode(response.responseText));		// get this sprint info
				SprintPlanComboStore.loadData(Ext.decode(response.responseText));	// get all sprints info
				obj.setCurrentSprintCombo(ThisSprintStore, SprintPlanComboStore);	// set this sprint info to show
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
			
			this.selectedIndex = SprintsStore.indexOfId(thisRecord.get('Id'));
			this.originalValue = SprintsStore.getAt(this.selectedIndex).get('Info');
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

Ext.reg('SprintComboWidget', ezScrum.SprintComboWidget);