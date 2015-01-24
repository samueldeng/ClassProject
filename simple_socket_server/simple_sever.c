/*
 * simple_sever.c
 *
 *  Created on: Jun 1, 2013
 *      Author: samuel
 */


#include <stdio.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <string.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <unistd.h>
#include <time.h>
#include <pthread.h>
#include "simple_sever.h"

#define MAX_BUFF_SIZE  256
#define MAX_SEND_PACKET_SIZE  512
#define MAX_CLIENT_CONNECTIONS 32
#define MAX_PACKET_SIZE 128

struct client_sock_record cli_sock_rcd_lst[MAX_CLIENT_CONNECTIONS];
int lst_next = 0;

void Die(char* mess)
{
	perror(mess);
	exit(1);
}

int send_server_time(int sockfd)
{
	char packet[MAX_SEND_PACKET_SIZE];
	size_t packet_len;

	time_t t = time(NULL);
	struct tm time = *localtime(&t);

	packet_len = (size_t)sprintf(packet,"Server Time: %d-%d-%d %d:%d:%d\n",
			time.tm_year + 1900, time.tm_mon + 1, time.tm_mday, time.tm_hour, time.tm_min, time.tm_sec);

	if(send(sockfd, (const void *) packet, packet_len, 0) == packet_len)
		return 1;
	else{
		Die("Failed to send the time packet to the client");
		return -1;
	}
}

int send_server_name(int sockfd)
{
	char packet[MAX_SEND_PACKET_SIZE];
	size_t packet_len;

	packet_len = (size_t)sprintf(packet,"Welcome to Samuel's word\n");

	if(send(sockfd, (const void *) packet, packet_len, 0) == packet_len)
		return 1;
	else
		Die("Failed to send the time packet to the client");

	return -1;
}

int send_usr_list(int sockfd)
{
	char packet[MAX_SEND_PACKET_SIZE];
	size_t packet_len;
	char *ip_addr;
	uint16_t  port;
	int i;

	for(i = 0; i<lst_next; i++){
		ip_addr = inet_ntoa(cli_sock_rcd_lst[i].cli_addr.sin_addr);
		port = cli_sock_rcd_lst[i].cli_addr.sin_port;

		packet_len = (size_t)sprintf(packet,"%d:%s:%d\n",i,ip_addr,port);

		if(send(sockfd, (const void *) packet, packet_len, 0) == packet_len)
			continue;
		else
			Die("Failed to send the time packet to the client");
	}
	return 1;
}

int trans_mess(int sockfd_sndr)
{
	char buff_recv[MAX_BUFF_SIZE];
	char packet_to_send[MAX_PACKET_SIZE];
	int packet_len_to_send;
	int recv_nr,sockfd_recvr;

	/*
	 * 	Get the message from the sockfd_sndr.
	 * 	Parse the message
	 * 	make them available to the sender.
	 */
	memset(buff_recv, 0, sizeof buff_recv);
	memset(packet_to_send, 0, sizeof packet_len_to_send);

	//FIXME if the true chars is not so long than the MAX_BUFF_SIZE, it will be block?
	if((recv(sockfd_sndr, buff_recv, MAX_BUFF_SIZE, 0)) < 0){
		Die("Failed to get the chars in the function:'trans_mess'");
		return -1;
	}

	char *recv_nr_p = strtok(buff_recv,"\n");//Skip the "S\n" in the buff_recv.
	char *mess_to_send = strtok(NULL, "\n");
	recv_nr = atoi(recv_nr_p);

	/*
	 * 	Send the message to the "sockfd_recvr"
	 */

	sockfd_recvr = cli_sock_rcd_lst[recv_nr].sock;

	packet_len_to_send = (size_t) sprintf( packet_to_send,"%s\n",mess_to_send);
	if(send(sockfd_recvr, (const void *) packet_to_send, packet_len_to_send, 0) == packet_len_to_send){
		return 1;
	}
	else{
		Die("Failed to transfer the message to the receiver.");
		return -1;
	}

}
void* branch_from_input (void *sockfd_p)
{
	char pkt_header[2];

	/*
	 *	notice that the packet's header is made up of 2 Byte
	 *	and it can be use to branch according to the header 2 bytes.
	 *
	 */
	int sockfd =  *((int*)sockfd_p);

	memset(pkt_header, 0, sizeof pkt_header);

	while(1){
		if (recv(sockfd, pkt_header, sizeof pkt_header, 0) < 0 || pkt_header[1] != '\n')
			Die("packet header format error");
		else{
			switch(pkt_header[0]){
			case 'T':
				send_server_time(sockfd);
				break;
			case 'N':
				send_server_name(sockfd);
				break;
			case 'L':
				send_usr_list(sockfd);
				break;
			case 'S':
				trans_mess(sockfd);
				break;
			default:
				Die("packet head format error");
				break;
			}
		}
	}
}

int main( int argc, char *argv[] )
{
	int serv_sockfd;
	struct sockaddr_in serv_addr;

	/* First call to socket() function */
	if ((serv_sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
		Die("ERROR opening socket");

	/* Initialize socket structure */
	memset(&serv_addr, 0, sizeof serv_addr);
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = INADDR_ANY;
	serv_addr.sin_port = htons(atoi(argv[1]));

	/* Now bind the host address using bind() call.*/
	if (bind(serv_sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0)
		Die("ERROR on binding");

	/* Now start listening for the clients, here
	 * process will go in sleep mode and will wait
	 * for the incoming connection
	 */
	listen(serv_sockfd,5);

	while (1){
		//FIXME problems
		cli_sock_rcd_lst[lst_next].client_len = sizeof cli_sock_rcd_lst[lst_next].cli_addr;
		cli_sock_rcd_lst[lst_next].sock = accept(
				serv_sockfd,
				(struct sockaddr *) &cli_sock_rcd_lst[lst_next].cli_addr,
				(socklen_t*) &cli_sock_rcd_lst[lst_next].client_len
		);

		if ( cli_sock_rcd_lst[lst_next].sock < 0)
			Die("ERROR on accept");
		/*
		 * 	make a new thread to process the request.
		 */
		pthread_t ntid;
		int err = pthread_create(&ntid, NULL, &branch_from_input, &cli_sock_rcd_lst[lst_next].sock);

		/*
		 * 	must ensure the lst_netxt has been ++, then the thread can be execute,
		 * 	because, once the ls_next can't be ++, can thread can't realize there is another client connect.
		 *	which means that, when main process be block in the  accept, the child thread can be execute.
		 */

		//FIXME the reason is  bellow.
		lst_next++;
		if(err <  0)
			Die("thread create error");
	}
	return 0;
}
