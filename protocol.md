# Protocol version 1.0

## General schema
KEYWORD param1 param2
The general schema describes strings that start with a keyword, followed by parameters separated by a single space. If one parameter is an array of e.g. commands to subscribe to, they are separated by simple comma.
The communication is not symmetric - meaning commands which can be send to the server are not equal the ones the server can send to the client.

## Commands

### Client -> Server

### Client <- Server
