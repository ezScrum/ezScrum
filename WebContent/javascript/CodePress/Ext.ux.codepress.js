/*
 * CodePress - Real Time Syntax Highlighting Editor written in JavaScript - http://codepress.org/
 * 
 * Copyright (C) 2006 Fernando M.A.d.S. <fermads@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation.
 * 
 * Read the full licence: http://www.opensource.org/licenses/lgpl-license.php
 */

Ext.namespace('Ext.ux');

Ext.ux.CodePress = Ext.extend(Ext.form.Field, {
	
    /**
     * @cfg {String} sourceEl The id of the element to pull code from
     */
	sourceEl : false
	
    /**
     * @cfg {String} code The code to use in the editor
     */
	, code : false
	
    /**
     * @cfg {String} language The language to render the code with
     */
	, language : false
	
	, url : false
	
	, height : false
	
	, width : false
	
	, autoResize : false
	
    // private
	, initialized : false
	
    // private
    , initComponent : function(){
		Ext.ux.CodePress.superclass.initComponent.call(this);
		
		// Hide the sourceEl if provided
		if(this.sourceEl){
			Ext.get(this.sourceEl).hide();
		}
		
        this.addEvents({
            /**
             * @event initialize
             * Fires when the editor is fully initialized (including the iframe)
             * @param {HtmlEditor} this
             */
            initialize: true
			
            /**
             * @event activate
             * Fires when the editor is first receives the focus. Any insertion must wait
             * until after this event.
             * @param {HtmlEditor} this
             */
            , activate: true
		
		});
	}
	
    // private (for BoxComponent)
    , adjustSize : Ext.BoxComponent.prototype.adjustSize
	
	, resize : function(){
		//console.log('resizing: ' + this.ownerCt.body.dom.clientWidth + 'x' + this.ownerCt.body.dom.clientHeight);
		var h = (this.height || this.ownerCt.body.dom.clientHeight) +'px';
		var w = (this.width || this.ownerCt.body.dom.clientWidth) +'px';
		this.editor.body.style.width = w;
		this.iframe.setStyle('height', h);
		this.iframe.setStyle('width', w);
    }
	
    , onRender : function(ct, position){
        Ext.ux.CodePress.superclass.onRender.call(this, ct, position);
		
		if(!Ext.ux.CodePress.path){
			this.getCodePath();
		}
		//Taken from Ext.form.HtmlEditor
        this.el.dom.style.border = '0 none';
        this.el.dom.setAttribute('tabIndex', -1);
        this.el.addClass('x-hidden');
		
		
        if(Ext.isIE){ // fix IE 1px bogus margin
            this.el.applyStyles('margin-top:-1px;margin-bottom:-1px;')
        }
        this.wrap = this.el.wrap({
            //cls:'x-html-editor-wrap', cn:{cls:'x-html-editor-tb'}
        });
		
		// Create the iframe
		this.iframe = Ext.get(document.createElement('iframe'));
        this.iframe.src = (Ext.SSL_SECURE_URL || 'javascript:false');
		
		// Create the textarea element if not created
		if(!this.sourceEl){
			this.textarea = Ext.get(document.createElement('textarea'));
		}else{
			this.textarea = Ext.get(this.sourceEl);
		}
		this.textarea.dom.disabled = true;
		this.textarea.dom.style.overflow = 'hidden';
		this.textarea.dom.style.overflow = 'auto';
		this.iframe.dom.frameBorder = 0; // remove IE internal iframe border
		this.iframe.setStyle('visibility', 'hidden');
		this.iframe.setStyle('position', 'absolute');
		this.options = this.textarea.dom.className;
		
        this.wrap.dom.appendChild(this.textarea.dom);
		this.textarea.dom.parentNode.insertBefore(this.iframe.dom, this.textarea.dom);
		
		this.edit();
	}
    
	, afterRender : function(){
        Ext.ux.CodePress.superclass.afterRender.call(this);
		
    }
	
	, focus : function(){
		
	}
	
	, getCodePath : function() {
		s = document.getElementsByTagName('script');
		for(var i=0,n=s.length;i<n;i++) {
			if(s[i].src.match('Ext.ux.codepress.js')) {
				Ext.ux.CodePress.path = s[i].src.replace('Ext.ux.codepress.js','');
				break;
			}
		}
	}
	
	, initialize : function() {
		if(Ext.isIE){
			this.doc = this.iframe.dom.contentWindow.document;
			this.win = this.iframe.dom.contentWindow;
		} else {
			this.doc = this.iframe.dom.contentDocument;
			this.win = this.iframe.dom.contentWindow;
		}
		this.editor = this.win.CodePress;
		this.editor.body = this.doc.getElementsByTagName('body')[0];
		if(this.url){
			Ext.Ajax.request({
				url: this.url
				, method:'get'
				, success:function(response, options){
					var code = response.responseText;
					this.code = code;
					this.editor.setCode(this.code);
				}.createDelegate(this)
			});
		}else{
			this.editor.setCode(this.code || this.textarea.dom.value);
		}
		this.resize();
		this.setOptions();
		this.editor.syntaxHighlight('init');
		this.textarea.dom.style.display = 'none';
		this.iframe.dom.style.position = 'static';
		this.iframe.dom.style.visibility = 'visible';
		this.iframe.dom.style.display = 'inline';
		
		this.initialized = true;
		//if(this.autoResize === true) this.resize();
		this.fireEvent('initialize', this);
	}
	
	// obj can by a textarea id or a string (code)
	, edit : function(obj,language) {
		if(obj) this.textarea.dom.value = document.getElementById(obj) ? document.getElementById(obj).value : obj;
		if(!this.textarea.dom.disabled) return;
		this.language = language ? language : this.getLanguage();
		this.iframe.dom.src = Ext.ux.CodePress.path+'codepress.html?language='+this.language+'&ts='+(new Date).getTime();
		this.iframe.removeListener('load', this.initialize);
		this.iframe.on('load', this.initialize, this);
	}

	, getLanguage : function() {
		if(this.language) return this.language;
		for (language in Ext.ux.CodePress.languages) 
			if(this.options.match('\\b'+language+'\\b')) 
				return Ext.ux.CodePress.languages[language] ? language : 'generic';
	}
	
	, setOptions : function() {
		if(this.options.match('autocomplete-off')) this.toggleAutoComplete();
		if(this.options.match('readonly-on')) this.toggleReadOnly();
		if(this.options.match('linenumbers-off')) this.toggleLineNumbers();
	}
	
	, getCode : function() {
		return this.textarea.dom.disabled ? this.editor.getCode() : this.textarea.dom.value;
	}

	, setCode : function(code) {
		this.textarea.dom.disabled ? this.editor.setCode(code) : this.textarea.dom.value = code;
		this.editor.syntaxHighlight();
	}

	, toggleAutoComplete : function() {
		this.editor.autocomplete = (this.editor.autocomplete) ? false : true;
	}
	
	, toggleReadOnly : function() {
		this.textarea.dom.readOnly = (this.textarea.dom.readOnly) ? false : true;
		if(this.iframe.dom.style.display != 'none') // prevent exception on FF + iframe with display:none
			this.editor.readOnly(this.textarea.dom.readOnly ? true : false);
	}
	
	, toggleLineNumbers : function() {
		var cn = this.editor.body.className;
		this.editor.body.className = (cn==''||cn=='show-line-numbers') ? 'hide-line-numbers' : 'show-line-numbers';
	}
	
	, toggleEditor : function() {
		if(this.textarea.dom.disabled) {
			this.textarea.dom.value = this.getCode();
			this.textarea.dom.disabled = false;
			this.iframe.dom.style.display = 'none';
			this.textarea.dom.style.display = 'inline';
		}
		else {
			this.textarea.dom.disabled = true;
			this.setCode(this.textarea.dom.value);
			this.editor.syntaxHighlight('init');
			this.iframe.domstyle.display = 'inline';
			this.textarea.dom.style.display = 'none';
		}
	}
});

Ext.reg('codepress', Ext.ux.CodePress);


Ext.ux.CodePress.languages = {	
	csharp : 'C#', 
	css : 'CSS', 
	generic : 'Generic',
	java : 'Java', 
	html : 'HTML',
	javascript : 'JavaScript', 
	perl : 'Perl', 
	ruby : 'Ruby',	
	php : 'PHP', 
	text : 'Text', 
	sql : 'SQL',
	vbscript : 'VBScript'
}