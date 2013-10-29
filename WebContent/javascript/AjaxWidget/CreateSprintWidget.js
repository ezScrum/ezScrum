Ext.ns('ezScrum');
/*-----------------------------------------------------------
 *   Sprint 的儲存結構
 -------------------------------------------------------------*/
// create the Data Store
var sprintStore = new Ext.data.Store({
            autoDestroy : true,
            fields : [{
                        name : 'Id',
                        type : 'int'
                    }, {
                        name : 'Goal'
                    }, {
                        name : 'StartDate'
                    }, {
                        name : 'Interval'
                    }, {
                        name : 'Members'
                    }, {
                        name : 'AvaliableDays'
                    }, {
                        name : 'FocusFactor'
                    }, {
                        name : 'DailyScrum'
                    }, {
                        name : 'DemoDate'
                    }, {
                        name : 'DemoPlace'
                    }],
            reader : SprintReader
        });
/*-----------------------------------------------------------
 * 
 * Sprint Detail裡面的元件
 * 
 -------------------------------------------------------------*/
var Id = new Ext.form.NumberField({
            readOnly   : true,
            fieldLabel : 'Id',
            readOnly   : true,
            name       : 'Id',
            allowBlank : false,
            anchor     : '95%'
        });
var SprintGoal = new Ext.form.TextArea({
            fieldLabel : 'Sprint Goal',
            name       : 'Goal',
            allowBlank : false,
            anchor     : '95% 15%'
        });

var DailyScrum = new Ext.form.TextField({
            fieldLabel : 'Time and Place for Daily Scrum',
            name       : 'DailyScrum',
            anchor     : '95%'
        });

var StartDate = new Ext.form.DateField({
            allowBlank : false,
            fieldLabel : 'Start Date',
            name       : 'StartDate',
            format     : 'Y/m/d',
            altFormats : 'Y/m/d',
            anchor     : '95%'
        });

var Members = new Ext.form.NumberField({
            allowBlank : false,
            fieldLabel : 'Members',
            name       : 'Members',
            anchor     : '50%',
            allowNegative: false
        });

var StoryPoint = new Ext.form.NumberField({
            readOnly   : true,
            fieldLabel : 'StoryPoint',
            readOnly   : true,
            name       : 'StoryPoint',
            anchor     : '50%',
            allowNegative: false
        });

var Interval = new Ext.form.NumberField({
            allowBlank : false,
            fieldLabel : 'Interval (weeks)',
            name       : 'Interval',
            anchor     : '50%',
            allowNegative: false
        });

var ManDays = new Ext.form.NumberField({
            allowBlank : false,
            fieldLabel : 'AvaliableDays',
            name       : 'AvaliableDays',
            anchor     : '50%',
            allowNegative: false
        });

var DemoDate = new Ext.form.DateField({
            allowBlank : false,
            fieldLabel : 'Demo Date',
            format     : 'Y/m/d',
            altFormats : 'Y/m/d',
            name       : 'DemoDate',
            anchor     : '95%'
        });

var DueDate = new Ext.form.DateField({
            disabled   : true,
            fieldLabel : 'Due Date',
            format     : 'Y/m/d',
            altFormats : 'Y/m/d',
            name       : 'DueDate',
            anchor     : '95%'
        });

var FocusFactor = new Ext.form.NumberField({
            allowBlank : false,
            fieldLabel : 'Focus Factor (%)',
            name       : 'FocusFactor',
            anchor     : '95%',
            allowNegative: false
        });

var DemoPlace = new Ext.form.TextField({
            fieldLabel : 'Demo Place',
            name       : 'DemoPlace',
            anchor     : '95%'
        });

/*******************************************************************************
 * 
 * 負責顯示Sprint Detail Form的Windows
 * 
 ******************************************************************************/
ezScrum.ShowSprintDetailWin = Ext.extend(Ext.Window, {
            title                : 'Sprint Detail',
            id                   : 'SprintDetailForm',
            width                : 600,
            modal                : true,
            closeAction          : 'hide',
            constrain	 		 : true,
            initComponent        : function()
            {
                var config = {
                    items : [{
                                xtype : 'SprintDetail'
                            }]
                }
                Ext.apply(this, Ext.apply(this.initialConfig, config));
                ezScrum.ShowSprintDetailWin.superclass.initComponent.apply(
                        this, arguments);
                var formItem = this.items.get(0);
                var winObj = this;
                // 註冊處理FormPanel會丟出來的事件
                // Success事件
                formItem.on('SubmitSuccess', function(obj, response, values)
                        {
                            winObj.items.get(0).hide;
                            winObj.hide();
                            winObj.fireEvent('Success', obj, response, values);
                        });
                // SubmitFailure事件
                formItem.on('SubmitFailure', function(obj, response, values)
                        {
                            winObj.fireEvent('Failure', obj, response, values);
                        });

                this.addEvents('Success', 'Failure');

            },
            /*-----------------------------------------------------------
             *  跳出視窗顯示Form
             *-------------------------------------------------------------*/
            showWidget           : function(titleString)
            {
                this.setTitle(titleString);
                this.show();
            },
            /*-----------------------------------------------------------
             *  將外部傳來的record給FormPanel
             *-------------------------------------------------------------*/
            loadData             : function(record)
            {
                if(record != null)
                    this.items.get(0).getForm().loadRecord(record);
                
                if (StartDate.isValid())
                {
                    // 計算DueDate
                    var tmp = StartDate.getValue().add(Date.DAY, parseInt(record
                                    .get('Interval'))
                                    * 7 - 1);
                    DueDate.setValue(tmp);
                    DemoDate.setMinValue(StartDate.getValue());
                    DemoDate.setMaxValue(tmp);
                }
            },
            /*-----------------------------------------------------------
            *	自動依照傳進來的Sprint ID去Server取得資料
            -------------------------------------------------------------*/
            autoLoadData:function(SprintID)
            {
            	var thisObj = this;
            	 /*-----------------------------------------------------------
                 * 讀取某個Sprint資訊
                 *-------------------------------------------------------------*/
                Ext.Ajax.request({
                            url : 'showEditSprintInfo.do?SprintID='+SprintID,
                            success : function(response) {
                                sprintStore.loadData(response.responseXML);
                                thisObj.loadData(sprintStore.getAt(0));
                            }
                        });
            }
            ,
            /*-----------------------------------------------------------
             *  新增Sprint所要顯示的一些基本資訊
             *-------------------------------------------------------------*/
            loadDataForNewSprint : function(record)
            {
            	if (record != null) {
	                // 設定ID
	                Id.setValue((record.get('Id') ^ 0) + 1);
	
	                // 取出StartDate
	                var preStartDate = Date.parseDate(record.get('StartDate'),
	                        'Y/m/d');
	                // 計算DueDate
	                var preDueDate = preStartDate.add(Date.DAY, parseInt(record
	                                .get('Interval'))
	                                * 7 - 1);
	                // DueDate的下一天就為下一個Sprint的預設的開始日期
	                StartDate.setValue(preDueDate.add(Date.DAY, 1));
	
	                // 設定DemoDate可選擇的最小範圍，也就是StartDate
	                DemoDate.setMinValue(StartDate.getValue());
	            } else {
	            	Id.setValue(1);
	            }
            },
            /*-----------------------------------------------------------
             *  清空FormPanel裡面的欄位
             *-------------------------------------------------------------*/
            resetForm            : function()
            {
                this.items.get(0).resetAllField();
            },
            // for checking this widget is doing what, for create sprint or edit sprint
            setIsCreate: function(isCreate) {
				Ext.getCmp('SprintDetail').isCreate = eval(isCreate);
            }
        });

/*******************************************************************************
 * 
 * Detail頁面，列出此Sprint的詳細動作，並且可針對欄位進行編輯
 * 
 ******************************************************************************/
ezScrum.SprintDetail = Ext.extend(Ext.FormPanel, {
			id 				: 'SprintDetail',
            isCreate		: true,
            monitorValid    : true,
            margins         : '5 5 5 5',
            frame           : true,
            /*-----------------------------------------------------------
             *    初始化表單欄位
             *-------------------------------------------------------------*/
            initComponent   : function()
            {
                var config = {
                    autoScroll : true,
                    // 建立Form的表單
                    items      : [Id, SprintGoal, StartDate, Interval, DueDate,
                            Members, StoryPoint, ManDays, FocusFactor,
                            DemoDate, DemoPlace, DailyScrum],
                    buttons    : [{
                                formBind : true,
                                text     : 'Submit',
                                scope    : this,
                                handler  : this.submit
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
                ezScrum.SprintDetail.superclass.initComponent.apply(this,
                        arguments);
                this.addEvents('SubmitSuccess', 'SubmitFailure');

                /*-----------------------------------------------------------
                 *  Interval與StartDate的Value改變事件處理(計算DueDate)
                 *-------------------------------------------------------------*/
                Interval.addListener('change', function(interval, newValue,
                                oldValue)
                        {
                            if (StartDate.isValid())
                            {
                                DueDate.setValue(StartDate.getValue().add(
                                        Date.DAY, (newValue ^ 0) * 7 -1));
                                DemoDate.setMaxValue(DueDate.getValue());
                                DemoDate.setValue(DueDate.getValue());
                            }

                        }, this);

                StartDate.addListener('select', function(interval, newValue,
                                oldValue)
                        {
                            if (Interval.isValid())
                            {
                                DueDate.setValue(newValue.add(Date.DAY,
                                        (Interval.getValue() ^ 0) * 7 -1));
                                DemoDate.setMinValue(StartDate.getValue());
                                DemoDate.setMaxValue(DueDate.getValue());
                                DemoDate.setValue(DueDate.getValue());
                            }

                        });

            },
            /*-----------------------------------------------------------
             *   處理當Submit按下去之後的事件
             -------------------------------------------------------------*/
            submit          : function()
            {
                var form = this.getForm();
                var obj = this;
                
                var actionurl = 'saveSprintPlan.do';
                if ( ! this.isCreate) {
					actionurl = 'editSprintPlan.do';               
                }
                
                Ext.Ajax.request({
					url     : actionurl,
					success : function(response) {
						obj.onSubmitSuccess(response);
					},
					failure : function(response) {
						obj.onSubmitFailure(response);
					},
					params  : form.getValues()
                });
            },
            /*-----------------------------------------------------------
             *  上傳成功時候的處理方式   
             *-------------------------------------------------------------*/
            onSubmitSuccess : function(response)
            {
	            // check action permission
				ConfirmWidget.loadData(response);
				if (ConfirmWidget.confirmAction()) {
                	this.fireEvent('SubmitSuccess', this, response, this.getForm().getValues());
                }
                // this.ownerCt.hide();
            },
            /*-----------------------------------------------------------
             *  上傳失敗的時候的處理方式   
             *-------------------------------------------------------------*/
            onSubmitFailure : function(response)
            {
                this.fireEvent('SubmitFailure', this, response, this.getForm()
                                .getValues());
            },
            resetAllField   : function()
            {
                Id.setValue("");
                SprintGoal.setValue("");
                DailyScrum.setValue("");
                StartDate.setValue("");
                Members.setValue("");
                Interval.setValue("");
                ManDays.setValue("");
                DemoDate.setValue("");
                DemoPlace.setValue("");
                FocusFactor.setValue("");
                DueDate.setValue("");
                DueDate.reset();
            }
        })

Ext.reg('SprintDetail', ezScrum.SprintDetail);