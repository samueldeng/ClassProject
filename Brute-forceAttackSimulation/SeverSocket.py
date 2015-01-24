import socket

serverSocket = socket.socket( socket.AF_INET, socket.SOCK_STREAM )
serverSocket.bind( ('',12345) );
serverSocket.listen(5)

while 1:
	clientSocket, (remoteHost, remotePort) = serverSocket.accept()
	print 'Remote Socket is' + str(remoteHost) + ':' + str(remotePort)
	loginMessage = clientSocket.recv(100)
	username = loginMessage.split(' ')[0]
	password = loginMessage.split(' ')[1]
	print username;
	print password;
	if username =='Samuel' and password == '19920202':
		clientSocket.send('OK')
	else:
		clientSocket.send('FormatError or Auth failed')
