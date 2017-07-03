importScripts('https://www.gstatic.com/firebasejs/3.9.0/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/3.9.0/firebase-messaging.js');

fetch("/ezScrum/firebase.json")
  .then(function (response) {
	  response.json().then(function(data) {  
	   var config = {
			 apiKey: data.apiKey,
			 authDomain: data.authDomain,
			 databaseURL: data.databaseURL,
			 projectId: data.projectId,
			 storageBucket: data.storageBucket,
			 messagingSenderId: data.messagingSenderId
	   };
	   firebase.initializeApp(config);
	   const messaging = firebase.messaging();
	   messaging.setBackgroundMessageHandler(function(payload){
	   	var option = {
	   			title: payload.notification.title,
	   			body: payload.notification.body,
	   			icon: "./images/scrum_16.png",
	   			click_action : payload.notification.click_action
	   	}
	   	var notification = new Notification(payload.notification.title,option);
	   });
     });  
  })  
  .catch(function (error) {  
    console.log('Request failed', error);  
  });



