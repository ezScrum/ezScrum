Ext.ns('ezScrum');
Ext.ns('ezScrum.window');
Ext.ns('ezScrum.layout');

// form 的 sprint plan store
var SprintPlanStore_ForSprintWindow = new Ext.data.Store({
    fields : [
        { name : 'Id', type : 'int'}, 
        { name : 'Goal'}, 
        { name : 'StartDate'},
        { name : 'Interval'}, 
        { name : 'EndDate'}, 
        { name : 'Members'}, 
        { name : 'AvaliableDays'},
        { name : 'FocusFactor'}, 
        { name : 'DailyScrum'},
        { name : 'DemoDate'},
        { name : 'DemoPlace'}
    ],
    reader : SprintPlanJsonReader
});


/*-----------------------------------------------------------
 * 
 * Sprint Detail裡面的元件
 * 
 -------------------------------------------------------------*/
var SprintDetailItems = [
    {	// Id
		fieldLabel : 'ID',
		name       : 'Id',
		xtype	   : 'hidden',
	    readOnly   : true,
	    allowBlank : false,
	    anchor     : '95%',
	    value		: 456
    },{
    	// Sprint Goal
		fieldLabel : 'Sprint Goal',
        name       : 'Goal',
        xtype	   : 'textarea',
        allowBlank : false,
        anchor     : '95% 15%'
    },{
    	// Start Date
    	fieldLabel : 'Start Date',
    	name       : 'StartDate',
    	xtype	   : 'datefield',
    	allowBlank : false,
    	format     : 'Y/m/d',
    	altFormats : 'Y/m/d',
    	anchor     : '95%'
    },{
    	// Interval
    	fieldLabel : 'Interval (weeks)',
    	name       : 'Interval',
    	xtype	   : 'numberfield',
    	allowBlank : false,
    	anchor     : '50%',
    	allowNegative: false,
    	allowDecimals: false,
    	regex: /^(?!0*(\.0+)?$)(\d+|\d*\.\d+)$/
    },{
    	//End day
    	fieldLabel : 'End Date',
    	name       : 'EndDate',
    	xtype	   : 'datefield',
    	format     : 'Y/m/d',
    	altFormats : 'Y/m/d',
    	readOnly   : true,
    	anchor     : '95%'
    },{
    	// Members
    	fieldLabel : 'Team size',
    	name       : 'Members',
    	xtype	   : 'numberfield',
    	allowBlank : false,
    	anchor     : '50%',
    	allowNegative: false
    },{
    	// Man-Days
    	fieldLabel : 'Hours to Commit',
    	name       : 'AvaliableDays',
    	xtype	   : 'numberfield',
    	allowBlank : false,
    	anchor     : '50%',
    	allowNegative: false,
        emptyText  : 'Totally focused hours of the Team.'
    },{
    	// Focus Factor
    	fieldLabel : 'Focus Factor (%)',
    	name       : 'FocusFactor',
    	xtype	   : 'numberfield',
    	allowBlank : false,
    	anchor     : '95%',
    	allowNegative: false,
        emptyText  : 'The suggestion is 100.'
	},{
		// Demo Date
		fieldLabel : 'Demo Date',
		name       : 'DemoDate',
		xtype	   : 'datefield',
		allowBlank : false,
        format     : 'Y/m/d',
        altFormats : 'Y/m/d',
        anchor     : '95%'
	},{
		// Demo Place
		fieldLabel : 'Demo Place',
        name       : 'DemoPlace',
        xtype	   : 'textfield',
        anchor     : '95%'
	},{
		// Daily Scrum
		fieldLabel : 'Time and Place for Daily Scrum',
		name       : 'DailyScrum',
		xtype	   : 'textfield',
		anchor     : '95%'
	},{
    	// Story Point - 隱藏版
//    	fieldLabel : 'StoryPoint',
    	name       : 'StoryPoint',
    	xtype	   : 'numberfield',
    	readOnly   : true,
    	hidden	   : true,
    	anchor     : '50%',
    	allowNegative: false
    },{
    	// isCreate	- 隱藏版
    	name       : 'isCreate',
    	xtype	   : 'textfield',
    	hidden	   : true,
    	value	   : 'true',
    	anchor     : '95%'
    },{
    	xtype      : 'RequireFieldLabel'
    }
];

/*******************************************************************************
 * 
 * Detail頁面，列出此Sprint的詳細動作，並且可針對欄位進行編輯
 * 
 ******************************************************************************/
ezScrum.SprintDetailForm = Ext.extend(Ext.FormPanel, {
	url				: 'saveSprintPlan.do',
    isCreate		: true,
    notifyPanel		: undefined,    
    monitorValid    : true,
    bodyStyle     	: 'padding:15px',
    labelAlign  	: 'right',
    frame           : false,
    autoScroll		: true,
    labelWidth      : 110, 
    initComponent   : function() {
        var config = {
            items	: [
                 SprintDetailItems
            ],
            buttons	: [{
                formBind : true,
                text     : 'Submit',
                scope    : this,
                handler  : this.Submit
            }, {
                text    : 'Cancel',
                scope   : this,
                handler : function()
                {
                    this.ownerCt.hide();
                }
            }]
        }

        Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.SprintDetailForm.superclass.initComponent.apply(this, arguments);
        
        /*-----------------------------------------------------------
         *  Interval與StartDate的Value改變事件處理(計算EndDate)
         *-------------------------------------------------------------*/
        var obj = this;
        this.id_CS = this.getItem_Field(0);
        this.startDate_CS = this.getItem_Field(2);
        this.interval_CS = this.getItem_Field(3);
        this.demoDate_CS = this.getItem_Field(8);
        this.endDate_CS = this.getItem_Field(4);
        
        this.interval_CS.addListener('change', function(interval, newValue, oldValue) {
            if (obj.startDate_CS.isValid()) {
            	// end date generated logic
            	var tempEndDate = obj.startDate_CS.getValue().add(Date.DAY, (newValue ^ 0) * 7 -1);
            	obj.endDate_CS.setValue(tempEndDate);
            	
            	// demo date generated logic
            	var tempDemoDate = tempEndDate;
            	if(tempEndDate.getDay() == 0){
            		tempDemoDate = tempEndDate.add(Date.DAY, -2);
                } else if (tempEndDate.getDay() == 6) {
                	tempDemoDate = tempEndDate.add(Date.DAY, -1);
                } 
            	obj.demoDate_CS.setMinValue(obj.startDate_CS.getValue());
            	obj.demoDate_CS.setValue(tempDemoDate);
            }
            
            
        }, this);
        
        this.startDate_CS.addListener('select', function(interval, newValue, oldValue) {
            if (obj.interval_CS.isValid()) {
            	obj.endDate_CS.setValue(newValue.add(Date.DAY, (obj.interval_CS.getValue() ^ 0) * 7 -1));
            	obj.demoDate_CS.setMinValue(obj.startDate_CS.getValue());
            	obj.demoDate_CS.setValue(obj.endDate_CS.getValue());
            }
        });
    },
    /*-----------------------------------------------------------
     *   處理當Submit按下去之後的事件
     -------------------------------------------------------------*/
    Submit	: function() {
        var obj = this;
        var form = this.getForm();
        
        // set isCreate attribute
        this.getForm().setValues({
        	isCreate: obj.isCreate
        });
        
        this.checkDate();			
    },
    /*-----------------------------------------------------------
     *  上傳成功時候的處理方式   
     *-------------------------------------------------------------*/
    onSubmitSuccess : function(response) {
    	var success = false;
    	var record = undefined;
    	
        // check action permission
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
        	if (eval(response)) {
        		success = response;
        	}
        }
		
		if (this.isCreate) {
			this.notifyPanel.notify_CreateSprint(success);
		} else {
			this.notifyPanel.notify_EditSprint(success);
		}
    },
    resetAllField   : function() {
    	this.getForm().reset();
    },
    checkDate: function(){
    	var form = this.getForm();
    	var obj = this;
    	Ext.Ajax.request({
    		url: 'checkSprintDate.do',
    		params: form.getValues(),
    		success: function(response){
    			ConfirmWidget.loadData(response);
				if (ConfirmWidget.confirmAction()) {
					if (response.responseText == 'legal') {
						obj.saveSprintPlan();
					} else {// illegal
						Ext.MessageBox.alert('Invalid Date!!', 'Sorry, the Start Date or End Date is overlap with the other sprint.');
					}
				}
    		},
    		failure: function(response) {
				Ext.MessageBox.alert('Failure');
			}
    	});
    },
    saveSprintPlan: function(){
    	var form = this.getForm();
		var obj = this;
		Ext.Ajax.request({
			url     : obj.url,
			params  : form.getValues(),
			success : function(response) { obj.onSubmitSuccess(response); },
			failure : function(response) { /* notify logon form, not finish yet*/  }
        });
    },
    LoadTheRecord	: function(sprintID) {
    	var obj = this;
		Ext.Ajax.request({
            url		: 'GetSprintPlan.do',
            params  : {SprintID : sprintID},
            success : function(response) {
            	SprintPlanStore_ForSprintWindow.loadData(Ext.decode(response.responseText));
            	
            	var record = SprintPlanStore_ForSprintWindow.getAt(0);
            	obj.SetTheRecord(record);
            	obj.resetMaxMinDate();
            	
				// append issueID to window title
            	SprintPlan_Window.setTitle(SprintPlan_Window.title + ' #' + record.get('Id'));
            },
            failure : function(){
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
        });
    },
    resetMaxMinDate:function(){
    	var startDate_CS = this.getItem_Field(2);
    	var demoDate_CS = this.getItem_Field(8);
    	var interval_CS = this.getItem_Field(3);
    	var endDate_CS = this.getItem_Field(4);
        if (startDate_CS.isValid()) {
            // generate EndDate
            var demoDateValue = demoDate_CS.getValue();
            var startDateValue = startDate_CS.getValue();
            var intervalValue = interval_CS.getValue();
            
            this.demoDate_CS.setMinValue(startDateValue);
            this.demoDate_CS.setValue(demoDateValue);
            this.endDate_CS.setValue( startDateValue.add(Date.DAY, (intervalValue ^ 0) * 7 -1) );//auto produce endDay
            
            var tempDemoDate = this.endDate_CS.getValue();
            
            // 6 means saturday and 0 means sunday, so it will add 1 or 2 more days based on weekend days
            if (tempDemoDate.getDay() == 0) {
            	tempDemoDate = tempDemoDate.add(Date.DAY, -2);
            } else if (tempDemoDate.getDay() == 6){
            	tempDemoDate = tempDemoDate.add(Date.DAY, -1);
            }
            this.demoDate_CS.setValue(tempDemoDate);
        }else{
        	alert('start date is invalid');
        }
    },
    SetTheRecord	: function(record) {
    	this.getForm().setValues({
    		Id			: record.get('Id'),
    		Goal		: SpecialChar_Translate(record.get('Goal')),
    		StartDate	: record.get('StartDate'),
    		Interval	: record.get('Interval'),
    		Members		: record.get('Members'),
    		StoryPoint	: record.get('StoryPoint'),
    		AvaliableDays:record.get('AvaliableDays'),
    		FocusFactor	: record.get('FocusFactor'),
    		DemoDate	: record.get('DemoDate'),
    		DemoPlace	: SpecialChar_Translate(record.get('DemoPlace')),
    		EndDate		: record.get('EndDate'),
    		DailyScrum	: SpecialChar_Translate(record.get('DailyScrum'))
    	});
    },
    loadDataForNewSprint : function() {
    	var obj = this;
		Ext.Ajax.request({
            url		: 'GetSprintPlan.do',
            params	: { lastsprint: true },
            success : function(response) {
            	SprintPlanStore_ForSprintWindow.loadData(Ext.decode(response.responseText));
            	var record = SprintPlanStore_ForSprintWindow.getAt(0);
            	obj.setNewSprintRecord(record);
            },
            failure : function(){
				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
    			loadmask.hide();
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
        });
    },
    setNewSprintRecord : function(record) {
		// init all fields
		this.getForm().reset();
		
    	if ( (record != null) && (record.get('Id')>0) ) {
    		// get StartDate
            var preStartDate = Date.parseDate(record.get('StartDate'), 'Y/m/d');
            
            // generate EndDate
            var temp = preStartDate.add(Date.DAY, parseInt(record.get('Interval')) * 7 - 1); 
            var preEndDate;
            
            // 6 means saturday and 0 means sunday, so it will add 1 or 2 more days based on weekend days
            if(temp.getDay() == 0){
               preEndDate = temp.add(Date.DAY, -2);
            } else if(temp.getDay() == 6) {
               preEndDate = temp.add(Date.DAY, -1);
            } else {
               preEndDate = temp;
            };
            
            // set boundary of demoDate
            this.demoDate_CS.setMinValue(this.startDate_CS.getValue());
            
            var newID = (record.get('Id') ^ 0) + 1;
			var newStartDate;
			
			if(record.get('Goal') == ""){
			   newStartDate = Date.parseDate(record.get('StartDate'), 'Y/m/d');
	        } else {
	           if(preEndDate.getDay() == 5){
	        	  newStartDate = preEndDate.add(Date.DAY, 3); 
	           } else {
	        	  newStartDate = preEndDate.add(Date.DAY, 1);
	           }
	        };
            
			// 設定初始值
            this.getForm().setValues({
            	Id			: newID,
            	StartDate	: newStartDate
            });
        } else {
        	// first sprint
        	this.id_CS.setValue(1);
        }
    },
    getItem_Field: function(index) {
    	return this.items.items[index];
    }
});

Ext.reg('SprintForm', ezScrum.SprintDetailForm);

/*******************************************************************************
 * 
 * 負責顯示Sprint Detail Form的Windows
 * 
 ******************************************************************************/
ezScrum.window.SprintWindow = Ext.extend(ezScrum.layout.Window, {
    title			: ' ',
    bodyStyle		: 'padding: 5px',
    initComponent   : function() {
        var config = {
            items : [{ xtype : 'SprintForm' }]
        };
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.window.SprintWindow.superclass.initComponent.apply(this, arguments);
        
        this.SprintForm = this.items.get(0);
    },
    showTheWindow_Add	: function(panel) {
    	// initial form info
    	this.SprintForm.resetAllField();
    	this.SprintForm.isCreate = true;
        this.SprintForm.notifyPanel = panel;
        
        // set form initial data
        this.SprintForm.loadDataForNewSprint();
        
        // initial window info 
        this.setTitle('Add New Sprint');        
        this.show();
    },
    showTheWindow_Edit	: function(panel, sprintID) {
    	// initial form info
    	this.SprintForm.resetAllField();
    	this.SprintForm.isCreate = false;
        this.SprintForm.notifyPanel = panel;
        
    	if (sprintID > 0) {
    		this.SprintForm.LoadTheRecord(sprintID);
            // initial window info 
            this.setTitle('Edit Sprint');  
            this.show();
    	}
    }
});

/*
 * call method
 * 		1. showTheWindow_Add: function(panel)
 * 		2. showTheWindow_Edit: function(panel, sprintID)
 * 
 * notify method
 * 		1. notify_CreateSprint: function(success)
 * 		2. notify_EditSprint: function(success)
 * 
 * shared with: 
 * 		1. Release Plan
 * 		2. Sprint Plan
 * 		3. Sprint Backlog
 * */
var SprintPlan_Window = new ezScrum.window.SprintWindow();