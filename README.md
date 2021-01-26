---
title: "[en] ssh connection"
tags: []
---

## SSH Connections

- [Add new user](#add-user)
- [Login without password](#login)
- [Instalar Jenkins](#install-jenkins)

[](#add-user)
### Add new user


To add a new user inside a linux machine we have to do next commands

#### 

```sh
sudo useradd [username]
````

After add user, you have to setup a password for the user recently created.
```sh
sudo passwd [username]
```


After that, you could add the user to the sudoers group

```sh
sudo usermod -aG sudo [username]
```

### Install SSH

To install ssh we do throught next commands
```sh
sudo apt-get install ssh
```

After install ssh, we can verify service status
```sh
sudo systemctl status ssh
```

*To add exceptions from firewall on Ubuntu we could add next command

```sh
sudo ufw allow ssh
```

And check the firewall status

```sh
sudo ufw status
```
[](#login)
### Login virtual machines without password


From the machine where we want to connect we also need to install ssh
```sh
sudo apt-get install ssh
````
But now we need to create our ssh keys 
```sh
ssh-keygen -b 4096 -t rsa
```


Next we need to copy keys that we generated before to the server that we want to connect
```sh
ssh-copy-id [user]@[server_or_domain]

```

So the for the first time to copy we are going to need to input the user and password, and after that the connection will be automatic.

```sh
ssh [user]@[server_or_domain]
```


[](#install-jenkins)

### Install Jenkins

To install jenkins we follow oficial documentation at [jenkins](https://www.jenkins.io/)

We are going to add jenkins to the list at the next direction with any editor.

```sh
deb https://pkg.jenkins.io/debian-stable binary/

```

After that we update the system
```sh
sudo apt-get update
```

Next we install jenkins

```sh
sudo apt-get install jenkins
```
