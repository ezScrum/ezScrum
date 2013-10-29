//顯示BC/SP值的欄位
var BC_SP_field = new Ext.form.NumberField({
	fieldLabel: 'Baseline Cost per Story Point', 
	name: 'BC_SP_field', anchor: '100%', 
	allowBlank: false,
	value: 0,
	listeners : {
		'beforerender': function() {
			this.setInitValue(); 
		}
	},
	setInitValue: function(){
		var obj = this;
		Ext.Ajax.request({
			url: 'show_BC_SP.do',
			success : function(response) {
				BC_SP_field.setValue( Ext.decode(response.responseText) );
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	}
});

//顯示BV值的欄位
var BV_field = new Ext.form.NumberField({
	fieldLabel: 'Baseline Velocity', 
	name: 'BV_field', anchor: '100%', 
	allowBlank: false,
	value: 0,
	listeners : {
		'beforerender': function() {
			this.setInitValue(); 
		}
	},
	setInitValue: function(){
		var obj = this;
		Ext.Ajax.request({
			url: 'show_BV.do',
			success : function(response) {
				//alert(Ext.decode(response.responseText));
				BV_field.setValue( Ext.decode(response.responseText) );
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	}
});

// 因排版才設計成如此，學 task board
var CPI_SPI_SettingField = [{
		monitorValid: true,
		layout:'column',
		defaults:{
			width: 150,
			layout: 'form',
			anchor: '100%',
			border: false
		},
		items: [{	//以 ref 取代 id(若寫死且原件共有可能會有問題)
			labelWidth: 180, width: 275,
			items: [BC_SP_field] // default
	    },{
			labelWidth: 180, width: 275, 
			items: [BV_field]// default
		}]
}];

//選擇要顯示哪個sprint以方便檢查該sprint actualCost的sprintCombobox
var SprintCombo_ActualCost = new ezScrum.SprintComboWidget({
	fieldLabel: 'Sprint ID',
    name: 'SprintID',
    anchor: '100%'
});

SprintCombo_ActualCost.addListener('select',function(){
	Ext.Ajax.request({
		url: 'showActualCost.do',
		params: { sprintID : SprintCombo_ActualCost.getStore().getAt(SprintCombo_ActualCost.selectedIndex).get('Id') },
		success : function(response) {
			actualCostField.setValue( Ext.decode(response.responseText) );
		},
		failure : function() {
			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
		}
	});
});

//儲存所有值的按鈕
var saveBtn = new Ext.Button({
	text: 'Save', 
	name: 'CPI_SPI_saveButton',
	anchor: '100%',
	formBind:true,
	saveAllValue: function(){
		this.saveBC_SP();
		this.saveBV();
		if( SprintCombo_ActualCost.selectedIndex != -1){
			this.saveActualCost(); 	
		}else{
		    alert('Save Actual Cost fail , Please add a sprint');
		}	
	},
	saveActualCost: function(){
		Ext.Ajax.request({
			url: 'editActualCost.do',
			params: { sprintID : SprintCombo_ActualCost.getStore().getAt(SprintCombo_ActualCost.selectedIndex).get('Id') ,actualCost : actualCostField.getValue() },
			success : function(response) {
				Ext.example.msg('ActualCost Saved','ActualCost Saved');
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	saveBC_SP: function(){
		Ext.Ajax.request({
			url: 'edit_BC_SP.do',
			params: { BC_SP : BC_SP_field.getValue() },
			success : function(response) {
				Ext.example.msg('BC/SP Saved','ActualCost Saved');
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	saveBV: function(){
		Ext.Ajax.request({
			url: 'edit_BV.do',
			params: { BV : BV_field.getValue() },
			success : function(response) {
				Ext.example.msg('BV Saved','ActualCost Saved');
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	}
});

//顯示actualCost值的欄位
var actualCostField = new Ext.form.NumberField({
	fieldLabel: 'Actual Cost', 
	name: 'AC_field', anchor: '100%', 
	allowBlank: false,
	initSprintID: 0,
	value: 0,
	listeners : {
		'beforerender': function() {
			this.setInitValue(); 
		}
	},
	setInitValue: function(){
		var obj = this;
		Ext.Ajax.request({
			url: 'GetSprintsComboInfo.do',
			success : function(response) {
				if( SprintCombo_ActualCost.selectedIndex != -1 ){
				    ThisSprintStore.loadData(Ext.decode(response.responseText));		// get this sprint info
				    obj.initSprintID = ThisSprintStore.getAt(0).get('Id');
				    obj.setValueBySprintID( obj.initSprintID );
				}
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	setValueBySprintID: function( sprintID ){
		Ext.Ajax.request({
			url: 'showActualCost.do',
			params: { sprintID : sprintID },
			success : function(response) {
				actualCostField.setValue( Ext.decode(response.responseText) );
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	}
});

// Group Actual cost 的combobox、field、button
var AC_SettingField = [{
	monitorValid: true,
	layout:'column',
	defaults:{
		width: 150,
		layout: 'form',
		anchor: '100%',
		border: false
	},
	items: [{	//以 ref 取代 id(若寫死且原件共有可能會有問題)
		labelWidth: 180, 
		width: 275, 
		items: SprintCombo_ActualCost
    }
	,{
		labelWidth: 180, 
		width: 275, 
		items: actualCostField // default
	},
	{
		width: 50, items: [{xtype:'textfield', fieldLabel: '', hidden: true, anchor: '100%'}] // 排版間隔用
	},{
		labelWidth: 180, 
		width: 60, 
		items: saveBtn
	}]
}];

// CPI/SPI Setting form
ezScrum.CPI_SPI_SettingForm = Ext.extend(Ext.form.FormPanel, {
	monitorValid: true,
	frame : true,
	labelAlign	: 'right',
//	height: 70,
	layout		: 'anchor',
    labelWidth	: 200,
	initComponent : function() {
		var config = {
				items: [ 
				         CPI_SPI_SettingField,
				         AC_SettingField
			           ]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CPI_SPI_SettingForm.superclass.initComponent.apply(this, arguments);
	}
});
Ext.reg('CPI_SPI_SettingForm', ezScrum.CPI_SPI_SettingForm);

// PV, EV, TAC Report
ezScrum.ValueChart = Ext.extend(Ext.Panel, {
	url			: 'show_EV_PV_TAC.do',
    layout	: 'anchor',
    collapsible : true,
    frame:true,
    initComponent : function() {
    	this.ValueChart_Store = new Ext.data.JsonStore({
    		root:'PV_EV_TAC_Data',
    		fields: ['SprintId', 'EV', 'PV', 'TAC']
    	});
    	
		var config = {
			items:[{
				xtype: 'linechart',
		        store: this.ValueChart_Store,
		        xField: 'SprintId',
		        yField: 'CPI',
		        
	        	anchor:'80%', // 可 width and height, but height 會有問題
	        	height: 600,  // 所以這裡高度固定
		        
		        xAxis: new Ext.chart.CategoryAxis({
	                title: 'Sprint #'
	            }),
	            
	            yAxis: new Ext.chart.NumericAxis({
	                title: 'Dollars'
	            }),
		        
		        series: [{
		            type:'line',
		            displayName: 'Plan Value',
		            yField: 'PV',
		            style: {
		                color: '#99bbe8'
		        	}
		        }, {
		            type: 'line',
		            displayName: 'Earn Value',
		            yField: 'EV',
		            style: {
		                color: '#00FF00'
		            }
		        }, {
		            type:'line',
		            displayName: 'Total Actual Cost',
		            yField: 'TAC',
		            style: {
		                color: '#FF0000'
		        	}
		        }],
		        extraStyle: {
		        	legend: {
		        		display: 'bottom'
					}	
	        	}
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.ValueChart.superclass.initComponent.apply(this, arguments);
	},
	loadDataModel: function() {
		MainLoadMaskShow();
		
		Ext.Ajax.request({
			scope	: this,
			url		: this.url,
			success	: function(response) {
				MainLoadMaskHide();
				this.ValueChart_Store.loadData(Ext.decode(response.responseText));				
			},
			failure: function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
				MainLoadMaskHide();
			}
		});
	}
});
Ext.reg('ValueChart', ezScrum.ValueChart);


// CPI/SPI Report
ezScrum.CPI_SPI_Chart = Ext.extend(Ext.Panel, {
	url			: 'showPerformanceIndex.do',
    layout	: 'anchor',
    frame:true,
    collapsible : true,
    initComponent : function() {
    	this.CPI_SPI_Store = new Ext.data.JsonStore({
    		root:'CPI_SPI_Data',
    		fields: ['SprintId', 'CPI', 'SPI', 'Ideal']
    	});
    	
		var config = {
			items:[{
				xtype: 'linechart',
		        store: this.CPI_SPI_Store,
		        xField: 'SprintId',
		        yField: 'CPI',
		        
	        	anchor:'80%', // 可 width and height, but height 會有問題
	        	height: 600,  // 所以這裡高度固定
		        
		        xAxis: new Ext.chart.CategoryAxis({
	                title: 'Sprint #'
	            }),
	            
//	            yAxis: new Ext.chart.NumericAxis({
//	                title: 'Value  '
//	            }),
		        
		        series: [{
		            type:'line',
		            displayName: 'Ideal Line',
		            yField: 'Ideal',
		            style: {
		                color: '#99bbe8'
		        	}
		        }, {
		            type: 'line',
		            displayName: 'Cost Performance Index',
		            yField: 'CPI',
		            style: {
		                color: '#00FF00'
		            }
		        }, {
		            type:'line',
		            displayName: 'Schedule Performance Index',
		            yField: 'SPI',
		            style: {
		                color: '#FF0000'
		        	}
		        }],
		        extraStyle: {
		        	legend: {
		        		display: 'bottom'
					}	
	        	}
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CPI_SPI_Chart.superclass.initComponent.apply(this, arguments);
	},
	loadDataModel: function() {
		MainLoadMaskShow();
		
		Ext.Ajax.request({
			scope	: this,
			url		: this.url,
			success	: function(response) {
				this.CPI_SPI_Store.loadData(Ext.decode(response.responseText));
				MainLoadMaskHide();
			},
			failure: function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
				MainLoadMaskHide();
			}
		});
	}
});
Ext.reg('CPI_SPI_Chart', ezScrum.CPI_SPI_Chart);

ezScrum.CPI_SPI_Panel = Ext.extend(Ext.Panel, {
	title		: 'CPI / SPI Report',
	autoHeight		: true,
	initComponent : function() {
		var config = {
			items: [{
				ref: 'CPI_SPI_SettingForm',
				xtype:'CPI_SPI_SettingForm'
			},
			{
				title: 'Value Chart',
				ref: 'ValueChart', // EV, PV, TAC Report
				xtype : 'ValueChart'
			},
			{
				title: 'Analysis Chart',
				ref: 'CPI_SPI_Chart',
				xtype : 'CPI_SPI_Chart'
			}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CPI_SPI_Panel.superclass.initComponent.apply(this, arguments);
		obj = this;
		saveBtn.addListener('click',function(){
			saveBtn.saveAllValue();
			obj.loadDataModel();
		});
	},
	loadDataModel: function() {
		this.ValueChart.loadDataModel();
		this.CPI_SPI_Chart.loadDataModel();
	},
	listeners : {
		'show' : function() {
			this.loadDataModel();
		}
	}
});
Ext.reg('CPI_SPI_Panel', ezScrum.CPI_SPI_Panel);
