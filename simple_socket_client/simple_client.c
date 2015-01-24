/*
 * simple_client.c
 *
 *  Created on: May 31, 2013
 *      Author: samuel
 */



#include <stdio.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <string.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <unistd.h>

#define MAX_SEND_PACKET_SIZE 256
#define MAX_SEND_MESSAGE_SIZE 128
#define MAX_CLIENT_COMMAND 128
#define MAX_DISPLAY_BUFF_SIZE 1024

void Die(char *mess)
{
	perror(mess);
	exit(1);
}

int get_timestamp(int sockfd)
{
	char packet[]="T\n";

	if(send(sockfd, (const void *) &packet, 2, 0) > 0)
		return (1);
	else
		return (-1);
}

int get_server_name(int sockfd)
{
	char packet[]="N\n";

	if(send(sockfd, (const void *) &packet, 2, 0) > 0)
		return (1);
	else
		return (-1);
}

int get_connection_list(int sockfd)
{
	char packet[]="L\n";

	if(send(sockfd, (const void *) &packet, 2, 0) > 0)
		return (1);
	else
		return (-1);
}

int send_message(int sockfd)
{
	char packet[MAX_SEND_PACKET_SIZE];
	int input_recv_nr;
	char input_send_mess[MAX_SEND_MESSAGE_SIZE];
	size_t packet_len;

	scanf("%d",&input_recv_nr);
	getchar();// to skip the \n in the stdin.
	scanf("%[^\n]", input_send_mess);// let the space can be identified in the input_send_mess.
	getchar();//to skip the \n in the end.

	packet_len = (size_t) sprintf( packet,"S\n%d\n%s\n",input_recv_nr,input_send_mess);
	if(send(sockfd, (const void *) packet, packet_len, 0) == packet_len)
		return 1;
	else
		return -1;
}

int setup_connection(int sockfd,char ip_addr[], char port_nr[])
{
	struct sockaddr_in server_sock_addr;

	memset(&server_sock_addr, 0, sizeof(server_sock_addr));
	server_sock_addr.sin_family = AF_INET;
	server_sock_addr.sin_addr.s_addr = inet_addr(ip_addr);
	server_sock_addr.sin_port = htons(atoi(port_nr));

	if(connect(sockfd,(struct sockaddr *)&server_sock_addr, sizeof(server_sock_addr)) < 0){
		return -1;
	}
	return 1;
}

int setup_close(int sockfd)
{
	if( close(sockfd) < 0)
		return -1;
	else
		return 1;
}

void show_messages(int sockfd)
{
	int rec_pkt_size = MAX_DISPLAY_BUFF_SIZE+1;
	char buff[MAX_DISPLAY_BUFF_SIZE];
	/*
	 * 	Best way:
	 * 	set timed single thread to handle the socket file descriptor, and put it into local  buffer.
	 * 	other function can only get their data from the local buffer.
	 *
	 */
	while (rec_pkt_size >= MAX_DISPLAY_BUFF_SIZE){
		rec_pkt_size = recv(sockfd, buff, MAX_DISPLAY_BUFF_SIZE, 0);
		write(STDOUT_FILENO, buff, rec_pkt_size);
	}

	if(rec_pkt_size < 0)
		Die("show messages function error");
}

int main(int argc, char *argv[])
{
	int sockfd;
	char usr_command[MAX_CLIENT_COMMAND];

	//create a socket
	if ((sockfd = socket(AF_INET, SOCK_STREAM,IPPROTO_TCP)) < 0){
		Die("Error: Couldn't create a socket");
	}


	//branch according to the usr_command
	while(1){

		//input the usr_command from the keyboard.
		fgets(usr_command, sizeof usr_command, stdin);

		if(usr_command[0] == 'c'){
			strtok(usr_command," ");
			char *ip_addr = strtok(NULL," ");//get the IP address.
			char *port_nr = strtok(NULL," ");//get the port number.
			if(setup_connection(sockfd, ip_addr, port_nr) < 0)
				Die("Failed to setup the connection.");

			int pid = fork();
			if(pid < 0)
				Die("Failed to fork");
			else if(pid > 0)
				continue;
			else{
				/*
				 * 	child process.
				 */
				while(1)
					show_messages(sockfd);
			}
		}

		if (usr_command[0] == 'd'){
			if(setup_close(sockfd) < 0)
				Die("Failed to close the socket");
			else
				continue;
		}

		if(usr_command[0] == 't'){
			if(get_timestamp(sockfd) < 0)
				Die("Failed to send the message of get_timestamp");
			else
				continue;
		}

		if(usr_command[0] == 'n'){
			if(get_server_name(sockfd) < 0)
				Die("Failed to send the message of get_server_name");
			else
				continue;
		}

		if(usr_command[0] == 'l'){
			if(get_connection_list(sockfd) < 0)
				Die("Failed to send the message of get_connection_list");
			else
				continue;
		}

		if(usr_command[0] == 's' && usr_command[1] != 'h'){
			if(send_message(sockfd) < 0)
				Die("Failed to send the message of get_connection_list");
			else
				continue;
		}

//		if(usr_command[0] == 's' && usr_command[1] == 'h'){
//			if(show_messages(sockfd) < 0)
//				Die("Failed to get the message from the socket.");
//			else
//				continue;
//		}

		//nothing match, Error.
		Die("Input  Error");
	}
}
