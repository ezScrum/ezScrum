Ext.ns('ezScrum');

/**
 * 尚未將版本寫成讀取後端版本號
 */

ezScrum.FooterPanel = new Ext.Panel({
	region			: 'south',		// position
	
	id				: 'foot_panel',
	collapsible		: false,
	animCollapse	: false,
	animate			: false,
	hideCollapseTool: true,
	rootVisible		: false,
	lines			: false,
	frame			: false,
	border			: false,
    height			: 60,
    minSize			: 40,
    maxSize			: 40,
    lines			: false,
    items : [{
    	html:'<center><font size="2">ezScrum v1.8.0 Alpha2, the ezScrum Team, Software Systems Lab, NTUT</font></center>' +
    		 '<center><font size="2">contact[at]scrum[dot]tw</font></center>'+
    		 '<center><font size="2">Science and Research Building Room 1321, No.1, Sec. 3, Jhongsiao E. Rd., Taipei City, Taiwan. 886 2 27712171 to 4263 &copy;2009-2015</font></center>'
    }]
});