ezScrum.ProjectMaiUI = Ext.extend(Ext.Viewport, {
	id: 'ProjectInfoMainLayout',
	layout: 'border',
	initComponent: function() {
		this.items = [ezScrum.Project_TopPanel, ezScrum.LeftPanel, ezScrum.ContentPanel, ezScrum.FooterPanel];
		ezScrum.ProjectMaiUI.superclass.initComponent.call(this);
	}
});
