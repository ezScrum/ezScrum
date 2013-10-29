Ext.ns('ezScrum');

ezScrum.ProjectLeftPanel = Ext.extend(Ext.tree.TreePanel, {
	collapsible		: true,
	collapseMode	:'mini',
	animCollapse	: false,
	animate			: false,
	hideCollapseTool: true,
	id				: 'projectleftside',
	ref				: 'projectleftside_refID',
	rootVisible		: false,
	lines			: false,
	autoScroll		: true,
	root: {
        nodeType	: 'node',
        text		: 'Root',
        expanded	: true,
        draggable	: false
    },
	loader: new Ext.tree.TreeLoader({
		dataUrl: 'GetProjectLeftTreeItem.do'
	}),
	initComponent : function() {
		ezScrum.ProjectLeftPanel.superclass.initComponent.apply(this, arguments);
		
		this.loadDataModel();
	},
	loadDataModel: function() {
		this.getLoader().load(this.root);
	}
});