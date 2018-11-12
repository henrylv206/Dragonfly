#! /bin/bash

# usage
echo "docker run --privileged -d -v /etc/hosts:/etc/node/hosts -e SUPERNODE_IPS=$supernode_ips -e REGISTRY=$registry -p 80:80 henrylv206@dragonfly-client:latest"

# dfget config
echo [node] > /etc/dragonfly.conf
echo address=$SUPERNODE_IPS >> /etc/dragonfly.conf

# update hosts
# TODO default registry j-hub.jd.com
# 1. 127.0.0.1
echo 127.0.0.1 j-hub.jd.com >> /etc/node/hosts

# TODO 2. set pod ip 


# dfdaemon config
# TODO default port 80

dfdaemon --port 80


