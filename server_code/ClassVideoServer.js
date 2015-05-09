var WebSocketServer = require('ws').Server;
var UUID = require('node-uuid');
var events = require('events');
var util = require('util');
var errorCb = function(rtc) {
	return function(error) {
		if (error) {
			rtc.emit("error", error);
		}
	};
};

function CvideoRTC() {
	this.sockets = [];
	this.rooms = {};
	this.firstSock;
	this.mobileIds = [];
	
	this.on('__join', function(data, socket) {
		console.log(this.sockets.length);
		var ids = [],
			i, m,
			room = data.room || "__default",
			curSocket,
			curRoom;

		curRoom = this.rooms[room] = this.rooms[room] || [];
    if(this.firstSock == null && data.app === "browser"){
       this.firstSock = socket.id;
       console.log("teacher join online class");
     }
    
    if(!(data.app === "browser")){
    	console.log("mobile app join class");
      this.mobileIds.push(socket.id);  
      if(this.firstSock == null)
          return;
    	ids.push(this.firstSock);
			this.getSocket(this.firstSock).send(JSON.stringify({
				"eventName": "_new_peer",
				"data": {
					"socketId": socket.id
				}
			}), errorCb);
    }
    else {
			for (i = 0, m = curRoom.length; i < m; i++) {
				curSocket = curRoom[i];
				if (curSocket.id === socket.id) {
					continue;
				}
				
		    //dont send to mobile app except for first socket
				var app = this.mobileIds.indexOf(curSocket.id);
				if(app != -1)
				   continue;
				
				
				ids.push(curSocket.id);
				curSocket.send(JSON.stringify({
					"eventName": "_new_peer",
					"data": {
						"socketId": socket.id
					}
				}), errorCb);
			}
    }

		curRoom.push(socket);
		socket.room = room;

		socket.send(JSON.stringify({
			"eventName": "_peers",
			"data": {
				"connections": ids,
				"you": socket.id,
				"first": this.firstSock
			}
		}), errorCb);

		this.emit('new_peer', socket, room);
	});

	this.on('__ice_candidate', function(data, socket) {
		var soc = this.getSocket(data.socketId);

		if (soc) {
			soc.send(JSON.stringify({
				"eventName": "_ice_candidate",
				"data": {
					"label": data.label,
					"candidate": data.candidate,
          "id":data.id,
					"socketId": socket.id
				}
			}), errorCb);

			this.emit('ice_candidate', socket, data);
		}
	});

	this.on('__offer', function(data, socket) {
		var soc = this.getSocket(data.socketId);

		if (soc) {
			soc.send(JSON.stringify({
				"eventName": "_offer",
				"data": {
					"sdp": data.sdp,
					"socketId": socket.id
				}
			}), errorCb);
		}
		this.emit('offer', socket, data);
	});

	this.on('__answer', function(data, socket) {
		var soc = this.getSocket(data.socketId);
		if (soc) {
			soc.send(JSON.stringify({
				"eventName": "_answer",
				"data": {
					"sdp": data.sdp,
					"socketId": socket.id
				}
			}), errorCb);
			this.emit('answer', socket, data);
		}
	});
}

util.inherits(CvideoRTC, events.EventEmitter);

CvideoRTC.prototype.addSocket = function(socket) {
	this.sockets.push(socket);
};

CvideoRTC.prototype.removeSocket = function(socket) {
	var i = this.sockets.indexOf(socket),
		room = socket.room;
	this.sockets.splice(i, 1);
	if (room) {
		i = this.rooms[room].indexOf(socket);
		this.rooms[room].splice(i, 1);
		if (this.rooms[room].length === 0) {
			delete this.rooms[room];
		}
	}
	
};

CvideoRTC.prototype.broadcast = function(data, errorCb) {
	var i;
	for (i = this.sockets.length; i--;) {
		this.sockets[i].send(data, errorCb);
	}
};

CvideoRTC.prototype.broadcastInRoom = function(room, data, errorCb) {
	var curRoom = this.rooms[room],
		i;
	if (curRoom) {
		for (i = curRoom.length; i--;) {
			curRoom[i].send(data, errorCb);
		}
	}
};

CvideoRTC.prototype.getRooms = function() {
	var rooms = [],
		room;
	for (room in this.rooms) {
		rooms.push(room);
	}
	return rooms;
};

CvideoRTC.prototype.getSocket = function(id) {
	var i,
		curSocket;
	if (!this.sockets) {
		return;
	}
	for (i = this.sockets.length; i--;) {
		curSocket = this.sockets[i];
		if (id === curSocket.id) {
			return curSocket;
		}
	}
	return;
};

CvideoRTC.prototype.init = function(socket) {
	var that = this;
	socket.id = UUID.v4();
	that.addSocket(socket);
	socket.on('message', function(data) {
		var json = JSON.parse(data);
		if (json.eventName) {
			that.emit(json.eventName, json.data, socket);
		} else {
			that.emit("socket_message", socket, data);
		}
	});

	socket.on('close', function() {
		var i, m,
			room = socket.room,
			curRoom;
		if (room) {
			curRoom = that.rooms[room];
			for (i = curRoom.length; i--;) {
				if (curRoom[i].id === socket.id) {
					continue;
				}
				curRoom[i].send(JSON.stringify({
					"eventName": "_remove_peer",
					"data": {
						"socketId": socket.id
					}
				}), errorCb);
			}
		}

		that.removeSocket(socket);

		that.emit('remove_peer', socket.id, that);
	});
	that.emit('new_connect', socket);
};

module.exports.listen = function(server) {
	var CvideoRTCServer;
	if (typeof server === 'number') {
		CvideoRTCServer = new WebSocketServer({
			port: server
		});
	} else {
		CvideoRTCServer = new WebSocketServer({
			server: server
		});
	}

	CvideoRTCServer.rtc = new CvideoRTC();
	errorCb = errorCb(CvideoRTCServer.rtc);
	CvideoRTCServer.on('connection', function(socket) {
		this.rtc.init(socket);
	});

	return CvideoRTCServer;
};
