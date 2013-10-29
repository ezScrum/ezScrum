ezScrum.ManagementMaiUI = Ext.extend(Ext.Viewport, {
	id: 'ManagementMainLayout',
	layout: 'border',
	initComponent: function() {
        this.items = [
			ezScrum.Management_TopPanel,
			ezScrum.Management_LeftPanel,
			ezScrum.Management_ContentPanel,
			ezScrum.FooterPanel  
        ];
        
        ezScrum.ManagementMaiUI.superclass.initComponent.call(this);
    }
});
