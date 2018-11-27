#! /bin/bash

trap cleanup 15

# gracefully stop container and restore hosts
cleanup() {
    echo "gracefully stop dragonfly-client..."

    sed '/##dragonfly##$/ d' /etc/node/hosts > host.tmp
    cat host.tmp > /etc/node/hosts
}

# usage
echo "docker run --privileged --net=host -d -v /etc/hosts:/etc/node/hosts -e SUPERNODE_IPS=$supernode_ips -e REGISTRY=$registry -e PORT=$port j-hub.jd.com/jdevops/dragonfly-client:latest"

# delete last time's hosts
sed '/##dragonfly##$/ d' /etc/node/hosts > host.tmp
cat host.tmp > /etc/node/hosts

# dfget config
if [[ -z "${SUPERNODE_IPS}" ]]; then
  echo "${SUPERNODE_IPS} must be valid ip, eg: 1.2.3.4,5.6.7.8"
  exit 1
else
  # TODO valid check
  echo [node] > /etc/dragonfly.conf
  echo address=$SUPERNODE_IPS >> /etc/dragonfly.conf
fi

# check port
port=${PORT:-80}

netstat -ltnp | grep -w ":${port}"
if [ $? -eq 0 ]; then
  echo "port ${port} is in use."
  exit 1
fi

# update hosts
if [[ -z "${REGISTRY}" ]]; then
  echo "${REGISTRY} must be registry, eg: j-hub.jd.com"
  exit 1
else
  echo "127.0.0.1 ${REGISTRY} ##dragonfly##" >> /etc/node/hosts
fi

# dfdaemon config
dfdaemon --port ${port} &

wait

cleanup

