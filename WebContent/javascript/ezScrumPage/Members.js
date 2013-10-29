var MembersPage = new Ext.Panel({
	id 			: 'Members_Page',
	layout 		: 'fit',
	autoScroll	: true,
	items : [
        { ref: 'Members_MembersGrid_ID', xtype : 'MembersGrid' }
    ],
	listeners : {
		'show' : function() {
			this.Members_MembersGrid_ID.loadDataModel();
		}
	}
});