/*
 * number field 
 */
var NumberTest = /^[0-9]*$/i;
Ext.apply(Ext.form.VTypes, {
    
    Number: function(val, field) {
        return NumberTest.test(val);
    },
    NumberText: 'This field must be a number',
    NumberMask: /[\d]/i
});

/*
 * float field 
 */
var FloatTest = /^([0-9]*)(\.?[0-9]+)?$/i;
Ext.apply(Ext.form.VTypes, {

    Float: function(val, field) {
        return FloatTest.test(val);
    },

    FloatText: 'This field must be a float',

    FloatMask: /[\d\s:.]/i
});

/*
 * excel file field 
 */
Ext.apply(Ext.form.VTypes, {
	checkXLS : function(filename, field) {
        if (filename) {
        	// split filename
        	var token = filename.split(".");
        	if (token[1] && (token[1]=="xls" || token[1]=="xlsx") ) {
				return true;
			} else {
				return false;
			}			
        }
        return false;
    },
	checkXLSText : 'Please import an excel file.'
});

/*
 * email field 
 */
var mailTest = /^(\w+)([\-+.][\w]+)*@(\w[\-\w]*\.){1,5}([A-Za-z]){2,6}$/;
Ext.apply(Ext.form.VTypes, {
	email			: function(v){
		return mailTest.test(v);
	},
	emailText		: 'This field should be an e-mail address in the format "user@ezScrum.tw"',
	emailMask		: /[a-z0-9_\.\-@]/i
});

/*
 * password field ( admin 權限 ) 
 */
Ext.apply(Ext.form.VTypes, {
	password : function(val, field) {
		if (field.initialPassField) {
			var pwd = Ext.getCmp(field.initialPassField);
			return (val == pwd.getValue());
		}
		return true;
	},
	passwordText : 'Passwords do not match'
});

/*
 * password field ( 一般 user 權限 ) 
 */
Ext.apply(Ext.form.VTypes, {
	pwdvalid : function(val, field) {
        if (field.initialPassField) {
            var pwd = Ext.getCmp(field.initialPassField);
            
            if (val == pwd.getValue()) {
            	Ext.getCmp('UserInformationManagement_Page').getTopToolbar().get('UserInformation_UpdateAccountBtn').setDisabled(false);
            	return true;
            } else {
            	Ext.getCmp('UserInformationManagement_Page').getTopToolbar().get('UserInformation_UpdateAccountBtn').setDisabled(true);
            	return false;
            }
        }
        return true;
    },
	pwdvalidText : 'Passwords do not match'
});

/*
 * upload fileName field 
 */
Ext.apply(Ext.form.VTypes, {
	checkUploadFileName : function(fileName, field) {
		if (fileName.search("#") != -1){	   // #
		}else if (fileName.search(/%/) != -1){ // %
		}else if (fileName.search(/&/) != -1){ // &
		}else if (fileName.search(/\+/) != -1){// +
		}else if (fileName.search(/\//) != -1){// /
		}else if (fileName.search(/\*/) != -1){// *
		}else if (fileName.search(/\?/) != -1){// ?
		}else if (fileName.search(/\"/) != -1){// "
		}else if (fileName.search(/</) != -1){ // <
		}else if (fileName.search(/>/) != -1){ // >
		}else if (fileName.search(/\|/) != -1){// |
		/*
		 * Chrome 上傳欄位顯示的 path 為 C:\fakepath\fileName，Firefox 則只有 fileName
		 * 因此 \,: 不可擋
		 */
//		}else if (fileName.search(/\\/) != -1){// \ 
//		}else if (fileName.search(/:/) != -1){ // : 
        }else{
        	return true;
        }
        return false;
    },
    checkUploadFileNameText : 'Special characters #,%,&,+,/,*,?,",<,>,| are not allowed.'
});