#!/bin/bash

(
echo HELO darkness; sleep 0.1;
echo MAIL FROM: superman@gmail.com; sleep 0.1;
echo RCPT TO: isaac@inverse; sleep 0.1;
echo DATA; sleep 0.1;
echo " "
echo Subject: Super Mail
echo Este é um e-mail de teste desenvolvido após muitos testes frustrados. 
echo Enfim eu decidi escrever este e-mail.
echo ""
echo "."; sleep 0.1;
echo QUIT
) | telnet $1 $2
