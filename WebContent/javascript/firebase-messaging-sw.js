importScripts('https://www.gstatic.com/firebasejs/3.9.0/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/3.9.0/firebase-messaging.js');

var config = {
	 apiKey: "AIzaSyAJJUz24w2bKfUqFNSTbelqyarDyvQ_cOE",
	 authDomain: "test-219bd.firebaseapp.com",
	 databaseURL: "https://test-219bd.firebaseio.com",
	 projectId: "test-219bd",
	 storageBucket: "test-219bd.appspot.com",
	 messagingSenderId: "197046125774"
};
firebase.initializeApp(config);

const messaging = firebase.messaging();