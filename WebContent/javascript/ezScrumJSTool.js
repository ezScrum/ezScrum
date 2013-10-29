Ext.ns('ezScrum');

/**
 * 拿取URL上面的PARAMETER
 * @author Zam
 */
function getURLParameter(paramName) {
  	var searchString = decodeURI(window.location.search).substring(1);
  	var val;
  	var params = searchString.split("&");

  	for (var i = 0; i < params.length; i++) {
    val = params[i].split("=");
	    if (val[0] == paramName) {
	      	return unescape(val[1]);
	    }
  	}
  	return null;
}

/**
 * 替每一個表單的field的必填欄位加上星號(*)
 * @author SPARK
 */
Ext.override(Ext.form.Field, {
	initComponent: Ext.form.Field.prototype.initComponent.createSequence(function(){
		if (this.allowBlank != undefined && !this.allowBlank && this.hidden != true) {
			var composite_field = this.findParentByType('compositefield');
			if (composite_field) {
				composite_field.setFieldLabel('<font style="color:red">* </font>' + composite_field.fieldLabel);
			} else {
				this.setFieldLabel(' <font style="color:red">* </font>' + this.fieldLabel);
			}
		}
	}),
	setFieldLabel : function(text) {
		if (this.rendered) {
			this.el.up('.x-form-item', 10, true).child('.x-form-item-label').update(text);
		}
		this.fieldLabel = text;
	}
});

// 提示使用者星號為必填欄位
ezScrum.RequireFieldLabel = Ext.extend(Ext.form.Label, {
    forId      : 'RequireField',
    text       : '( Note : * denotes a required field )',
    style      : 'margin-left:15px;color:red;',
    anchor     : '100%'
});

Ext.reg('RequireFieldLabel', ezScrum.RequireFieldLabel);
