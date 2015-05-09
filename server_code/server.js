var express = require('express');
var app = express();
var server = require('http').createServer(app);
var CvideoRTC = require('./ClassVideoServer').listen(server);
var path = require("path");

var port = process.env.PORT || 3000;
server.listen(port);

app.use(express.static(path.join(__dirname, 'public')));

app.get('/', function(req, res) {
	res.sendfile(__dirname + '/index.html');
});

CvideoRTC.rtc.on('new_connect', function(socket) {
	console.log('new connection');
});

CvideoRTC.rtc.on('remove_peer', function(socketId) {
	console.log(socketId + "user leave");
});

CvideoRTC.rtc.on('new_peer', function(socket, room) {
	console.log("new user" + socket.id + "join class" + room);
});

CvideoRTC.rtc.on('socket_message', function(socket, msg) {
	console.log("receive msg from" + socket.id + "：" + msg);
});

CvideoRTC.rtc.on('ice_candidate', function(socket, ice_candidate) {
	console.log("receive Candidate from" + socket.id);
});

CvideoRTC.rtc.on('offer', function(socket, offer) {
	console.log("receive Offer from" + socket.id);
});

CvideoRTC.rtc.on('answer', function(socket, answer) {
	console.log("receive answer from" + socket.id);
});

CvideoRTC.rtc.on('error', function(error) {
	console.log("error:" + error.message);
});